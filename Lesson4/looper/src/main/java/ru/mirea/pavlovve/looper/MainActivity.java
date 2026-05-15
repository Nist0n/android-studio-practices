package ru.mirea.pavlovve.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.pavlovve.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result");
                binding.textView.setText(result);
            }
        };

        myLooper = new MyLooper(mainHandler);
        myLooper.start();

        binding.buttonSend.setOnClickListener(v -> {

            Message msg = Message.obtain();
            Bundle bundle = new Bundle();

            bundle.putInt("age", 21);
            bundle.putString("job", "Студент");

            msg.setData(bundle);

            if (myLooper.mHandler != null) {
                myLooper.mHandler.sendMessage(msg);
            }
        });
    }
}