package ru.mirea.pavlovve.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MyLooper extends Thread {

    public Handler mHandler;
    private Handler mainHandler;

    public MyLooper(Handler mainThreadHandler) {
        this.mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {

        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {

                int age = msg.getData().getInt("age");
                String job = msg.getData().getString("job");

                try {
                    // Задержка = возраст * 100
                    Thread.sleep(age * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String result = "Возраст: " + age + ", Профессия: " + job;

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                message.setData(bundle);

                mainHandler.sendMessage(message);
            }
        };

        Looper.loop();
    }
}