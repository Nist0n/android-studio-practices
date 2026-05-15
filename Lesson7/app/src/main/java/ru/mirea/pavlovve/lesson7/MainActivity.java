package ru.mirea.pavlovve.lesson7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import ru.mirea.pavlovve.lesson7.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final String host = "time.nist.gov";
    private final int port = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(v -> {
            GetTimeTask task = new GetTimeTask();
            task.execute();
        });
    }

    private class GetTimeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String result = "";

            try {
                Socket socket = new Socket(host, port);

                BufferedReader reader =
                        SocketUtils.getReader(socket);

                reader.readLine();

                result = reader.readLine();

                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null && !s.isEmpty()) {

                String[] parts = s.split(" ");

                String date = parts[1];
                String time = parts[2];

                binding.dateTextView.setText(date);
                binding.timeTextView.setText(time);
            }
        }
    }
}