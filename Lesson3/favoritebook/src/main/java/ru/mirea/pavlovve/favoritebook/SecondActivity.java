package ru.mirea.pavlovve.favoritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    EditText userEditText;
    Button comeBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userEditText = findViewById(R.id.userEditView);
        comeBackButton = findViewById(R.id.comeBackButton);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userEditText.getText().toString().isEmpty()) {
                    ReturnToMainActivity();
                }
                else {
                    Toast.makeText(SecondActivity.this, "Сначале напишите название своей любимой книги", Toast.LENGTH_SHORT).show();
                }
            }
        };
        comeBackButton.setOnClickListener(onClickListener);

        TextView textView = findViewById(R.id.developerBookTextView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String developerFavoriteBook = extras.getString(MainActivity.KEY);
            textView.setText(String.format("Любимая книга разработчика - \n%s", developerFavoriteBook));
        }
    }

    private void ReturnToMainActivity() {
        Intent data = new Intent();
        data.putExtra(MainActivity.USER_MESSAGE, userEditText.getText().toString());
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}