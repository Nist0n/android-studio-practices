package ru.mirea.pavlovve.thread;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ru.mirea.pavlovve.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread mainThread = Thread.currentThread();

        binding.textViewResult.setText("Имя потока: " + mainThread.getName());

        mainThread.setName("МОЯ ГРУППА: 09, НОМЕР: 17");

        binding.textViewResult.append("\nНовое имя: " + mainThread.getName());

        Log.d("THREAD", "Stack: " + Arrays.toString(mainThread.getStackTrace()));

        Log.d("THREAD", "Group: " + mainThread.getThreadGroup());

        binding.buttonCalculate.setOnClickListener(v -> {
            String lessonsStr = binding.editTextLessons.getText().toString();
            String daysStr = binding.editTextDays.getText().toString();

            if (lessonsStr.isEmpty() || daysStr.isEmpty()) {
                binding.textViewResult.setText("Введите данные");
                return;
            }

            new Thread(() -> {
                int numberThread = counter++;
                Log.d("THREAD", "Запущен поток № " + numberThread);

                try {

                    int lessons = Integer.parseInt(lessonsStr);
                    int days = Integer.parseInt(daysStr);

                    double result = (double) lessons / days;

                    Thread.sleep(3000);

                    runOnUiThread(() -> {
                        binding.textViewResult.setText("Среднее: " + result);
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        binding.textViewResult.setText("Ошибка ввода");
                    });
                }

                Log.d("THREAD", "Завершён поток № " + numberThread);

            }).start();
        });

        binding.buttonCalculate.setOnLongClickListener(v -> {

            long endTime = System.currentTimeMillis() + 5000;

            while (System.currentTimeMillis() < endTime) {
            }

            return true;
        });
    }
}