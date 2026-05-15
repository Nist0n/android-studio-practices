package ru.mirea.pavlovve.employeedb;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editHero;
    private EditText editRealName;
    private EditText editPower;

    private TextView textResult;

    private SuperheroDao superheroDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editHero = findViewById(R.id.editHero);
        editRealName = findViewById(R.id.editRealName);
        editPower = findViewById(R.id.editPower);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonLoad = findViewById(R.id.buttonLoad);

        textResult = findViewById(R.id.textResult);

        superheroDao = App.getInstance()
                .getDatabase()
                .superheroDao();

        buttonSave.setOnClickListener(v -> saveHero());

        buttonLoad.setOnClickListener(v -> loadHeroes());
    }

    private void saveHero() {

        Superhero superhero = new Superhero();

        superhero.heroName =
                editHero.getText().toString();

        superhero.realName =
                editRealName.getText().toString();

        superhero.powerLevel =
                Integer.parseInt(editPower.getText().toString());

        superheroDao.insert(superhero);
    }

    private void loadHeroes() {

        List<Superhero> superheroes =
                superheroDao.getAll();

        StringBuilder builder =
                new StringBuilder();

        for (Superhero superhero : superheroes) {

            builder.append("ID: ")
                    .append(superhero.id)
                    .append("\nГерой: ")
                    .append(superhero.heroName)
                    .append("\nНастоящее имя: ")
                    .append(superhero.realName)
                    .append("\nСила: ")
                    .append(superhero.powerLevel)
                    .append("\n\n");
        }

        textResult.setText(builder.toString());
    }
}