package com.example.multilangues_journalisation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
// import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.app.AppCompatActivity;

// import com.example.multilangues_journalisation.Utils.LanguageUtils;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };
    private TextView latitudeText, longitudeText, altitudeText, signalStrengthText, batteryLevelText;
    private BroadcastReceiver updateReceiver;
    private Spinner languageSpinner;
    // private static final String PREFS_NAME = "AppPrefs";
    // private static final String LANGUAGE_KEY = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        // String savedLanguage = LanguageUtils.getPersistedLanguage(this);
        // LanguageUtils.setLocale(this, savedLanguage);
        setContentView(R.layout.activity_main);

        languageSpinner = findViewById(R.id.spn_language_spinner);
        latitudeText = findViewById(R.id.latitudeText);
        longitudeText = findViewById(R.id.longitudeText);
        altitudeText = findViewById(R.id.altitudeText);
        signalStrengthText = findViewById(R.id.signalStrengthText);
        batteryLevelText = findViewById(R.id.batteryLevelText);


        /*
         * this intent allows the values to get displayed correctly even
         * after changing the app language.
         */
        Intent intent = getIntent();
        latitudeText.setText(String.format("%s: %s", getString(R.string.latitude), intent.getStringExtra("latitude")));
        longitudeText.setText(String.format("%s: %s", getString(R.string.longitude), intent.getStringExtra("longitude")));
        altitudeText.setText(String.format("%s: %s", getString(R.string.altitude), intent.getStringExtra("altitude")));
        signalStrengthText.setText(String.format("%s: %s", getString(R.string.signal_strength), intent.getStringExtra("signalStrength")));
        batteryLevelText.setText(String.format("%s: %s", getString(R.string.battery_level), intent.getStringExtra("batteryLevel")));

        setupLanguageSpinner();
        setupUpdateReceiver();

        checkAndRequestPermissions();
    }

    /**
     * Sets up the language selection spinner and handles language changes.
     * This method initializes a spinner with language options (English, Arabic, and French),
     * and sets the default selection based on the current locale. When a new language is
     * selected from the spinner, it updates the app's locale accordingly.
     * <p>
     * The method performs the following steps:
     * <ul>
     *     <li>Creates an array of language strings.</li>
     *     <li>Initializes an ArrayAdapter with the language options and sets it to the spinner.</li>
     *     <li>Sets the default spinner selection based on the current system language.</li>
     *     <li>Listens for language selection changes and updates the locale when a new language is selected.</li>
     * </ul>
     * </p>
     * @see #updateLocale(String)
     */
    private void setupLanguageSpinner() {
        String[] languages = {getString(R.string.english), getString(R.string.arabic), getString(R.string.french)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set the default selection based on the current locale
        String currentLanguage = Locale.getDefault().getLanguage();
        // String currentLanguage = LanguageUtils.getPersistedLanguage(this);
        int position;
        switch (currentLanguage) {
            case "ar":
                position = 1;
                break;
            case "fr":
                position = 2;
                break;
            default:
                position = 0;
                break;
        }
        languageSpinner.setSelection(position);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage;
                switch (position) {
                    case 1:
                        selectedLanguage = "ar";
                        break;
                    case 2:
                        selectedLanguage = "fr";
                        break;
                    default:
                        selectedLanguage = "en";
                        break;
                }
                if (!selectedLanguage.equals(currentLanguage)) {
                    updateLocale(selectedLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Updates the app's locale to the specified language code.
     * This method changes the app's locale based on the provided language code and
     * applies the new locale configuration to the app's resources. It then recreates
     * the current activity to ensure that the new locale is applied throughout the app.
     *
     * @param languageCode The language code to set the new locale.
     *                     For example, "en" for English, "ar" for Arabic, or "fr" for French.
     */
    private void updateLocale(String languageCode) {
        // LanguageUtils.setLocale(this, languageCode);
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Restart the activity to apply the new locale
        recreate();
    }

    /**
     * Sets up a BroadcastReceiver to listen for UI update broadcasts.
     * The receiver listens for the action "com.example.multilangues_journalisation.UPDATE_UI" and
     * updates the UI with the received data (latitude, longitude, altitude, signal strength, battery level).
     */
    private void setupUpdateReceiver() {
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals("com.example.multilangues_journalisation.UPDATE_UI")) {
                    updateUI(intent);
                }
            }
        };
    }

    /**
     * Updates the UI with the received data from the broadcast intent.
     * The data includes latitude, longitude, altitude, signal strength, and battery level.
     * This method updates the respective text views with the formatted information
     * and registers the information in the intent to be used for displaying them in case
     * of an app language change.
     *
     * @param intent The intent containing the data to update the UI with.
     */
    private void updateUI(Intent intent) {
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");
        String altitude = intent.getStringExtra("altitude");
        String signalStrength = intent.getStringExtra("signalStrength");
        String batteryLevel = intent.getStringExtra("batteryLevel");

        getIntent().putExtra("latitude", latitude);
        getIntent().putExtra("longitude", longitude);
        getIntent().putExtra("altitude", altitude);
        getIntent().putExtra("signalStrength", signalStrength);
        getIntent().putExtra("batteryLevel", batteryLevel);

        latitudeText.setText(String.format("%s: %s", getString(R.string.latitude), latitude));
        longitudeText.setText(String.format("%s: %s", getString(R.string.longitude), longitude));
        altitudeText.setText(String.format("%s: %s", getString(R.string.altitude), altitude));
        signalStrengthText.setText(String.format("%s: %s", getString(R.string.signal_strength), signalStrength));
        batteryLevelText.setText(String.format("%s: %s", getString(R.string.battery_level), batteryLevel));
    }

    /**
     * Checks whether the necessary permissions are granted and requests them if not.
     * If permissions are granted, starts the {@link ServiceJournalisation} service.
     * If not, requests the required permissions.
     */
    private void checkAndRequestPermissions() {
        boolean allPermissionsGranted = true;
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            startServiceJournalisation();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Handles the result of the permissions request.
     * If permissions are granted, starts the {@link ServiceJournalisation} service.
     * If permissions are not granted, shows a toast message to the user.
     *
     * @param requestCode The request code passed in requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                startServiceJournalisation();
            } else {
                Toast.makeText(this, "Not all permissions are granted, your app may not work properly", Toast.LENGTH_LONG).show();
                /*
                new AlertDialog.Builder(this)
                        .setTitle(R.string.permission_denied_title)
                        .setMessage(R.string.permission_denied_message)
                        .setPositiveButton(R.string.request_again, (dialog, which) -> checkAndRequestPermissions())
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            // Optionally disable features that require permissions
                            disableFeaturesThatRequirePermissions();
                        })
                        .show();
                 */
            }
        }
    }

    /**
     * Starts the {@link ServiceJournalisation} service to collect data.
     */
    private void startServiceJournalisation() {
        startService(new Intent(this, ServiceJournalisation.class));
    }

    /**
     * Registers the update receiver when the activity is resumed.
     * This receiver listens for the action "com.example.multilangues_journalisation.UPDATE_UI"
     * and triggers the UI update when a broadcast with this action is received.
     */
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.example.multilangues_journalisation.UPDATE_UI");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(updateReceiver, filter);
        }
    }

    /**
     * Unregisters the update receiver when the activity is paused.
     * This prevents memory leaks by ensuring that the receiver is no longer active when the activity is not visible.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateReceiver);
    }
}