package ru.mirea.pavlovve.securesharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class MainActivity extends AppCompatActivity {

    private EditText editPoet;
    private Button buttonSave;

    private SharedPreferences securePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPoet = findViewById(R.id.editPoet);
        buttonSave = findViewById(R.id.buttonSave);

        try {

            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            securePreferences = EncryptedSharedPreferences.create(
                    this,
                    "secure_shared_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        loadData();

        buttonSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {

        securePreferences.edit()
                .putString("POET", editPoet.getText().toString())
                .apply();
    }

    private void loadData() {

        String poet = securePreferences.getString("POET", "");

        editPoet.setText(poet);
    }
}