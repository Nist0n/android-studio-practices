package ru.mirea.pavlovve.notebook;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private EditText editFileName;
    private EditText editQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editFileName = findViewById(R.id.editFileName);
        editQuote = findViewById(R.id.editQuote);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonLoad = findViewById(R.id.buttonLoad);

        buttonSave.setOnClickListener(v -> saveFile());

        buttonLoad.setOnClickListener(v -> loadFile());
    }

    private void saveFile() {

        try {

            String fileName =
                    editFileName.getText().toString() + ".txt";

            String quote =
                    editQuote.getText().toString();

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(path, fileName);

            FileOutputStream stream =
                    new FileOutputStream(file);

            stream.write(quote.getBytes());

            stream.close();

            Toast.makeText(this,
                    "Файл сохранен",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void loadFile() {

        try {

            String fileName =
                    editFileName.getText().toString() + ".txt";

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path, fileName);

            if (!file.exists()) {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
                return;
            }

            FileInputStream stream =
                    new FileInputStream(file);

            InputStreamReader reader =
                    new InputStreamReader(stream);

            BufferedReader bufferedReader =
                    new BufferedReader(reader);

            StringBuilder stringBuilder =
                    new StringBuilder();

            String line;

            while ((line = bufferedReader.readLine()) != null) {

                stringBuilder.append(line).append("\n");
            }

            editQuote.setText(stringBuilder.toString());

            bufferedReader.close();

            Toast.makeText(this,
                    "Файл загружен",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}