package ru.mirea.pavlovve.lesson4;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.pavlovve.lesson4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.buttonPlay.setOnClickListener(v -> {
            binding.title.setText("Работает");
        });

        binding.buttonPause.setOnClickListener(v -> {
            binding.title.setText("Остановлен");
        });
    }
}