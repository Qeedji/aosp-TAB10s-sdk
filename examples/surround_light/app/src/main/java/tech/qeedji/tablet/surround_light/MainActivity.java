package tech.qeedji.tablet.surround_light;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import tech.qeedji.system.lib.SurroundLight;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Light";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        SurroundLight surroundlight = new SurroundLight(this);

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked) {
                    Log.i(TAG, "Set red");
                    surroundlight.setColor(SurroundLight.RED);
                }
                break;
            case R.id.radioButton2:
                if (checked) {
                    Log.i(TAG, "Set green");
                    surroundlight.setColor(SurroundLight.GREEN);
                }
                break;
            case R.id.radioButton3:
                if (checked) {
                    Log.i(TAG, "Set orange");
                    surroundlight.setColor(SurroundLight.ORANGE);
                }
                break;
            case R.id.radioButton4:
                if (checked) {
                    Log.i(TAG, "Set off");
                    surroundlight.setColor(SurroundLight.OFF);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurroundLight surroundlight = new SurroundLight(this);
        int id = R.id.radioButton4;
        switch (surroundlight.getColor()) {
            case SurroundLight.RED:
                id = R.id.radioButton1;
                break;
            case SurroundLight.GREEN:
                id = R.id.radioButton2;
                break;
            case SurroundLight.ORANGE:
                id = R.id.radioButton3;
                break;
            }
        RadioButton button = findViewById(id);
        button.setChecked(true);
        Log.d(TAG, "resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "pause");
    }
}
