package ru.mirea.pavlovve.internalfilestorage;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText editDate;
    private EditText editDescription;
    private TextView textResult;

    private final String fileName = "history.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editDate = findViewById(R.id.editDate);
        editDescription = findViewById(R.id.editDescription);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonRead = findViewById(R.id.buttonRead);

        textResult = findViewById(R.id.textResult);

        buttonSave.setOnClickListener(v -> saveFile());

        buttonRead.setOnClickListener(v -> readFile());
    }

    private void saveFile() {

        String date = editDate.getText().toString();
        String description = editDescription.getText().toString();

        String text = "Дата: " + date + "\nОписание: " + description;

        try {

            FileOutputStream fileOutputStream =
                    openFileOutput(fileName, Context.MODE_PRIVATE);

            fileOutputStream.write(text.getBytes());

            fileOutputStream.close();

            Toast.makeText(this,
                    "Файл сохранен",
                    Toast.LENGTH_SHORT).show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    private void readFile() {

        try {

            FileInputStream fileInputStream =
                    openFileInput(fileName);

            byte[] bytes = new byte[fileInputStream.available()];

            fileInputStream.read(bytes);

            String text = new String(bytes);

            textResult.setText(text);

            fileInputStream.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}