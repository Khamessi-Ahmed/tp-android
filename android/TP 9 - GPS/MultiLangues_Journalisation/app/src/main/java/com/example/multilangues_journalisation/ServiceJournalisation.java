package com.example.multilangues_journalisation;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceJournalisation extends Service {
    private LocationManager locationManager;
    private TelephonyManager telephonyManager;
    private BroadcastReceiver batteryReceiver;
    private int lastBatteryLevel = -1;
    private int lastSignalStrength = -1;
    private Location lastLocation;

    private static final long MIN_TIME_BETWEEN_UPDATES = 1000;
    private static final float MIN_DISTANCE_CHANGE = 5;


    /**
     * Called when the service is created. Initializes the location manager, telephony manager,
     * and sets up listeners for location, signal strength, and battery updates.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") int networkType = telephonyManager.getNetworkType();
        Log.i("ServiceJournalisation", "Network Type: " + networkType);
        setupLocationListener();
        setupSignalStrengthListener();
        setupBatteryListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Sets up the location listener to receive location updates from the GPS provider.
     * The updates will be triggered at a minimum interval of 1 second or when the location
     * changes by at least 5 meters.
     */
    private void setupLocationListener() {
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BETWEEN_UPDATES,
                        MIN_DISTANCE_CHANGE,
                        new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                if (lastLocation == null || location.distanceTo(lastLocation) >= MIN_DISTANCE_CHANGE) {
                                    lastLocation = location;
                                    logData();
                                }
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {}

                            @Override
                            public void onProviderEnabled(String provider) {}

                            @Override
                            public void onProviderDisabled(String provider) {}
                        }
                );
            } else {
                Log.e("ServiceErrors", "GPS provider is not enabled");
            }
        } catch (SecurityException e) {
            Log.e("ServiceErrors", "Permissions not granted for location access", e);
        }
    }

    /**
     * Sets up the signal strength listener using the TelephonyManager.
     * The signal strength is logged whenever it changes.
     * The GSM signal strength is used if valid; otherwise, the signal strength is
     * obtained by invoking the {@link #getSignalStrength()} method to retrieve the value.
     */
    private void setupSignalStrengthListener() {
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                Log.d("ServiceJournalisation", String.valueOf(signalStrength.getGsmSignalStrength()));
                int dbm = (signalStrength.getGsmSignalStrength() != 99) ? (-113 + signalStrength.getGsmSignalStrength() * 2) : getSignalStrength();
                if (dbm != lastSignalStrength && dbm != -1) {
                    lastSignalStrength = dbm;
                    logData();
                }
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * Fetches signal strength from all available cell info (GSM, LTE, WCDMA).
     * Returns the signal strength in dBm.
     *
     * @return The signal strength in dBm, or -1 if it cannot be determined.
     */
    // FIXME: cellInfos returning empty list (tested only on AVD - API 24)
    private int getSignalStrength() {
        try {
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
            Log.d("ServiceJournalisation", "CellInfos List: " + cellInfos);
            Log.i("ServiceJournalisation", "SDK Build Version : " + Build.VERSION.SDK_INT);
            if (cellInfos != null && !cellInfos.isEmpty()) {
                CellInfo cellInfo = cellInfos.get(0);
                Log.d("ServiceJournalisation", "CellInfo Type: " + cellInfo.getClass());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    CellSignalStrength cellSignalStrength = cellInfo.getCellSignalStrength();
                    return cellSignalStrength.getDbm();
                } else {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellSignalStrengthGsm gsmSignalStrength = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        return gsmSignalStrength.getDbm();
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellSignalStrengthLte lteSignalStrength = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        Log.d("ServiceJournalisation", "Cell info LTE signal is : " + lteSignalStrength.getDbm());
                        return lteSignalStrength.getDbm();
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        CellSignalStrengthWcdma wcdmaSignalStrength = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                        return wcdmaSignalStrength.getDbm();
                    }
                }
            }
        } catch (SecurityException e) {
            Log.e("ServiceErrors", "SecurityException when getting cell info", e);
        }
        return -1;
    }

    /**
     * Sets up a battery listener to track the battery level and log it if there is a change.
     */
    private void setupBatteryListener() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int batteryLevel = (int) ((level / (float) scale) * 100);
                if (Math.abs(batteryLevel - lastBatteryLevel) >= 1) {
                    lastBatteryLevel = batteryLevel;
                    logData();
                }
            }
        };
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if (lastBatteryLevel == -1) {
            BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            int level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            lastBatteryLevel = level;
            logData();  // Log the initial battery level once
        }
    }

    /**
     * Logs the location, signal strength, and battery data into a CSV file.
     * It also sends a broadcast with the updated data to allow the UI to be updated.
     */
    private void logData() {
        if (lastLocation == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String data = String.format(Locale.US, "%s;%.6f;%.6f;%.2f;%d;%d\n",
                timestamp,
                lastLocation.getLatitude(),
                lastLocation.getLongitude(),
                lastLocation.getAltitude(),
                lastSignalStrength,
                lastBatteryLevel);

        Log.d("ServiceJournalisation", "Writing on csv file: " + data);

        FileOutputStream fos = null;
        try {
            File file = new File(getExternalFilesDir(null), "LogTracking.csv");
            fos = new FileOutputStream(file, true);
            fos.write(data.getBytes());
            fos.close();

            // Send broadcast to update UI
            Intent intent = new Intent("com.example.multilangues_journalisation.UPDATE_UI");
            intent.putExtra("latitude", String.format(Locale.US, "%.6f", lastLocation.getLatitude()));
            intent.putExtra("longitude", String.format(Locale.US, "%.6f", lastLocation.getLongitude()));
            intent.putExtra("altitude", String.format(Locale.US, "%.2f", lastLocation.getAltitude()));
            intent.putExtra("signalStrength", String.valueOf(lastSignalStrength));
            intent.putExtra("batteryLevel", String.valueOf(lastBatteryLevel));
            sendBroadcast(intent);
        } catch (IOException e) {
            Log.e("ServiceErrors", "Error writing to log file", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("ServiceErrors", "Error closing the csv file", e);
                }
            }
        }
    }

    /**
     * Cleans up resources when the service is destroyed.
     * Removes location updates, stops listening for signal strength changes,
     * and unregisters the battery receiver.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(location -> {});
            } catch (SecurityException e) {
                Log.e("ServiceErrors", "Error removing location updates", e);
            }
        }
        if (telephonyManager != null) {
            telephonyManager.listen(null, PhoneStateListener.LISTEN_NONE);
        }
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
    }
}