package ru.mirea.pavlovve.httpurlconnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.pavlovve.httpurlconnection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLoad.setOnClickListener(v -> checkInternetAndLoad());
    }

    @SuppressWarnings("MissingPermission")
    private void checkInternetAndLoad() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;

        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadIpTask()
                    .execute("https://ipinfo.io/json");

        } else {

            Toast.makeText(this,
                    "Нет интернета",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadIpTask
            extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                return downloadUrl(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject =
                        new JSONObject(result);

                String ip =
                        jsonObject.getString("ip");

                String city =
                        jsonObject.getString("city");

                String region =
                        jsonObject.getString("region");

                String country =
                        jsonObject.getString("country");

                String loc =
                        jsonObject.getString("loc");

                binding.ipText.setText(ip);
                binding.cityText.setText(city);
                binding.regionText.setText(region);
                binding.countryText.setText(country);
                binding.locationText.setText(loc);

                String[] coordinates = loc.split(",");

                String latitude = coordinates[0];
                String longitude = coordinates[1];

                new WeatherTask().execute(
                        latitude,
                        longitude
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class WeatherTask
            extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String latitude = strings[0];
            String longitude = strings[1];

            String weatherUrl =
                    "https://api.open-meteo.com/v1/forecast?latitude="
                            + latitude
                            + "&longitude="
                            + longitude
                            + "&current_weather=true";

            try {
                return downloadUrl(weatherUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject =
                        new JSONObject(result);

                JSONObject weather =
                        jsonObject.getJSONObject("current_weather");

                String temperature =
                        weather.getString("temperature");

                binding.weatherText.setText(
                        temperature + " °C"
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadUrl(String address)
            throws Exception {

        InputStream inputStream = null;

        String data = "";

        try {

            URL url = new URL(address);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);

            connection.setRequestMethod("GET");

            connection.setDoInput(true);

            connection.connect();

            int responseCode =
                    connection.getResponseCode();

            if (responseCode ==
                    HttpURLConnection.HTTP_OK) {

                inputStream =
                        connection.getInputStream();

                ByteArrayOutputStream buffer =
                        new ByteArrayOutputStream();

                int read;

                while ((read = inputStream.read()) != -1) {
                    buffer.write(read);
                }

                data = buffer.toString();

                buffer.close();
            }

            connection.disconnect();

        } finally {

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return data;
    }
}