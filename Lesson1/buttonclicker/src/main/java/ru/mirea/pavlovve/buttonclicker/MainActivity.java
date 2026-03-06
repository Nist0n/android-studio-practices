package ru.mirea.pavlovve.buttonclicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView textViewStudent;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewStudent = findViewById(R.id.textViewStudent);
        Button btnWhoAmI = findViewById(R.id.btnWhoAmI);
        checkBox = findViewById(R.id.checkBox);

        View.OnClickListener oclBtnWhoAmI = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewStudent.setText("Мой номер по списку, не помню, может №18");
            }
        };

        btnWhoAmI.setOnClickListener(oclBtnWhoAmI);
    }

    public void OnMyButtonClick(View view){
        Toast.makeText(this, "Ещё один способ!", Toast.LENGTH_SHORT).show();
        textViewStudent.setText("Номер классный");
        checkBox.setChecked(!checkBox.isChecked());
    }
}