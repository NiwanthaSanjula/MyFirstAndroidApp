package com.s23010177.niwantha;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TextView temperatureDisplay, warningMessage;
    private MediaPlayer mediaPlayer;
    private boolean hasPlayed = false;
    private Animation blinkAnimation;

    private final float THRESHOLD = 77f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.sensorToolbar);
        Drawable backArrow = ContextCompat.getDrawable(this, androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationIcon(backArrow);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // UI elements
        temperatureDisplay = findViewById(R.id.temperatureDisplay);
        warningMessage = findViewById(R.id.warningMessage);
        blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);

        // Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (temperatureSensor == null) {
            Toast.makeText(this, "No Ambient Temperature Sensor!", Toast.LENGTH_LONG).show();
            finish();
        }

        // Audio alert
        mediaPlayer = MediaPlayer.create(this, R.raw.alertsound);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float currentTemp = event.values[0];
        temperatureDisplay.setText("Current Temp: " + currentTemp + " °C");

        if (currentTemp > THRESHOLD) {
            warningMessage.setVisibility(TextView.VISIBLE);
            if (warningMessage.getAnimation() == null) {
                warningMessage.startAnimation(blinkAnimation);
            }
            if (!hasPlayed) {
                hasPlayed = true;
                mediaPlayer.start();
            }
        } else {
            warningMessage.clearAnimation();
            warningMessage.setVisibility(TextView.GONE);
            hasPlayed = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
