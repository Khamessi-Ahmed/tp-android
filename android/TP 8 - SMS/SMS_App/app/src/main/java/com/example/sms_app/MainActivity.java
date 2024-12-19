package com.example.sms_app;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText phone;
    private EditText message;
    private Button envoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initActivity();
    }

    /**
     * Initialisations
     */
    private void initActivity() {
        // Récupération des objets graphiques
        phone = (EditText) findViewById(R.id.txtPhone);
        message = (EditText) findViewById(R.id.txtMessage);
        envoi = (Button) findViewById(R.id.btnEnvoi);
        // gestion de l'événement clic sur bouton envoi
        createOnClickEnvoiButton();
    }

    /**
     * Clique sur bouton envoi: envoi SMS
     */
    private void createOnClickEnvoiButton() {
        envoi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SmsManager.getDefault().sendTextMessage(phone.getText().toString(), null,
                                                        message.getText().toString(), null, null);
            }
        });
    }
}