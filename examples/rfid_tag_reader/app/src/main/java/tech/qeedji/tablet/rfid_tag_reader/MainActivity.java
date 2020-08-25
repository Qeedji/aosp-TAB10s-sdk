package tech.qeedji.tablet.rfid_tag_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tech.qeedji.system.lib.Rfid125KHz;

public class MainActivity extends AppCompatActivity implements KeyEvent.Callback {

    private TextView textView;
    private Button button;
    private String tagID = "";
    private NfcAdapter nfcAdapter;
    private Rfid125KHz rfid;
    private PendingIntent nfcPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button.setOnClickListener(onClick);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        rfid = new Rfid125KHz(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    /* Called when the activity will start interacting with the user. */
    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null);
        rfid.setEnabled(true);
    }
    /* Called when the system is about to start resuming a previous activity. */
    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);
        rfid.setEnabled(false);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)
                || intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String tag = byteArrayToString(id);
            addText(textView, "Nfc : " + tag);
        }
    }

    static String byteArrayToString(byte[] array) {
        String hex = "";
        for (int i = 0; i < array.length ; i++) {
            hex += String.format("%02X", array[i]);
        }
        return hex;
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        public void onClick(View v) {
            textView.setText(null);
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean isRfidDevice = "rfid".equals(event.getDevice().getName());
        if (isRfidDevice && isKeyboardWedgeKey(keyCode)) {
            tagID = tagID + KeyEvent.keyCodeToString(keyCode).substring(8);
            button.setOnClickListener(null);
            return true;
        } else if (isRfidDevice && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            addText(textView, "Rfid125KHz : " + tagID);
            tagID = "";
            button.setOnClickListener(onClick);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private boolean isKeyboardWedgeKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_B:
            case KeyEvent.KEYCODE_C:
            case KeyEvent.KEYCODE_D:
            case KeyEvent.KEYCODE_E:
            case KeyEvent.KEYCODE_F:
                return true;
        }
        return false;
    }

    private void addText(TextView textView, String text) {
        if (textView.length() == 0) {
            textView.setText(text);
        } else {
            textView.setText(textView.getText() + "\n" + text);
        }
    }
}
