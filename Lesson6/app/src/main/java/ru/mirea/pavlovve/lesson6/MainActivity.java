package ru.mirea.pavlovve.lesson6;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editGroup;
    private EditText editNumber;
    private EditText editMovie;
    private Button buttonSave;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editGroup = findViewById(R.id.editGroup);
        editNumber = findViewById(R.id.editNumber);
        editMovie = findViewById(R.id.editMovie);
        buttonSave = findViewById(R.id.buttonSave);

        preferences = getSharedPreferences("mirea_settings", MODE_PRIVATE);

        loadData();

        buttonSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("GROUP", editGroup.getText().toString());
        editor.putString("NUMBER", editNumber.getText().toString());
        editor.putString("MOVIE", editMovie.getText().toString());

        editor.apply();
    }

    private void loadData() {

        String group = preferences.getString("GROUP", "");
        String number = preferences.getString("NUMBER", "");
        String movie = preferences.getString("MOVIE", "");

        editGroup.setText(group);
        editNumber.setText(number);
        editMovie.setText(movie);
    }
}