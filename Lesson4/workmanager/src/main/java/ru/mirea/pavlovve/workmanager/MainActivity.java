package ru.mirea.pavlovve.workmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import ru.mirea.pavlovve.workmanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(v -> {
            OneTimeWorkRequest request =
                    new OneTimeWorkRequest.Builder(MyWorker.class)
                            .build();

            WorkManager
                    .getInstance(this)
                    .enqueue(request);
        });
    }
}