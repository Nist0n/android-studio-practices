package ru.mirea.pavlovve.mireaproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CompassFragment extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private final float[] gravity = new float[3];
    private final float[] geomagnetic = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    private ImageView compassArrow;
    private TextView directionText;
    private TextView degreesText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        compassArrow = view.findViewById(R.id.compassArrow);
        directionText = view.findViewById(R.id.textDirection);
        degreesText = view.findViewById(R.id.textDegrees);

        sensorManager = (SensorManager) requireContext().getSystemService(android.content.Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        if (accelerometer == null || magnetometer == null) {
            directionText.setText(R.string.compass_sensor_unavailable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager == null || accelerometer == null || magnetometer == null) {
            return;
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravity, 0, event.values.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagnetic, 0, event.values.length);
        }

        if (!SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
            return;
        }

        SensorManager.getOrientation(rotationMatrix, orientation);
        float azimuthRad = orientation[0];
        float azimuthDeg = (float) Math.toDegrees(azimuthRad);
        azimuthDeg = (azimuthDeg + 360f) % 360f;

        updateCompass(azimuthDeg);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Toast.makeText(requireContext(), R.string.compass_low_accuracy, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCompass(float azimuthDeg) {
        compassArrow.setRotation(-azimuthDeg);
        directionText.setText(getDirectionLabel(azimuthDeg));
        degreesText.setText(getString(R.string.compass_degrees_format, azimuthDeg));
    }

    private String getDirectionLabel(float azimuth) {
        String[] labels = getResources().getStringArray(R.array.compass_directions);
        int index = Math.round(azimuth / 45f) % 8;
        return labels[index];
    }
}
