package ru.mirea.pavlovve.data_thread;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import ru.mirea.pavlovve.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonStart.setOnClickListener(v -> {
            final Runnable runn1 = () -> {
                binding.tvInfo.setText(
                        "1. runOnUiThread — выполняется сразу в UI потоке\n"
                );
            };

            final Runnable runn2 = () -> {
                binding.tvInfo.append(
                        "2. post — ставится в очередь и выполняется после\n"
                );
            };

            final Runnable runn3 = () -> {
                binding.tvInfo.append(
                        "3. postDelayed — выполняется с задержкой\n"
                );
            };

            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);

                    runOnUiThread(runn1);

                    TimeUnit.SECONDS.sleep(1);

                    binding.tvInfo.postDelayed(runn3, 2000);

                    binding.tvInfo.post(runn2);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}