package tech.qeedji.tablet.proximity_sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Proximity";

    private SensorManager sensorManager;
    private Sensor mProximity;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mTextView = (TextView) findViewById(R.id.textView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float distance = event.values[0];
        Log.i(TAG, "distance : " + distance);
        if (distance == 0) {
            mTextView.setText("Hello Qeedji");
        } else {
            mTextView.setText("Nobody detected");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used.
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "registerListener proximity sensor");
        sensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "unregisterListener proximity sensor");
        sensorManager.unregisterListener(this);
    }
}
