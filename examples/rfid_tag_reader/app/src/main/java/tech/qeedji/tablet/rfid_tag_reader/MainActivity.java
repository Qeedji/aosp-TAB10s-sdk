package tech.qeedji.tablet.rfid_tag_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import tech.qeedji.system.lib.Rfid125KHz;

public class MainActivity extends AppCompatActivity implements KeyEvent.Callback {

    private TableLayout table;
    private TextView textType;
    private TextView textSN;
    private String tagID = "";
    private NfcAdapter nfcAdapter;
    private Rfid125KHz rfid;
    private PendingIntent nfcPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        table = findViewById(R.id.table);
        table.setVisibility(View.INVISIBLE);
        textType = findViewById(R.id.type);
        textSN = findViewById(R.id.id);
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
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String type = "NFC";
            if ((tag != null) && (tag.getTechList().length > 0)) {
                type = techtoType(tag.getTechList()[0]);
            }
            toast("NFC Tag detected");
            textType.setText(type);
            textSN.setText(byteArrayToString(id));
            table.setVisibility(View.VISIBLE);
        }
    }

    static String byteArrayToString(byte[] array) {
        String hex = "";
        for (int i = 0; i < array.length ; i++) {
            if(i>0)
                hex += ":";
            hex += String.format("%02X", array[i]);
        }
        return hex;
    }

    static String techtoType(String tech) {
        if ("android.nfc.tech.NfcA".equals(tech))
            return "ISO 14443-3A";
        else if ("android.nfc.tech.NfcB".equals(tech))
            return "ISO 14443-3B";
        else if ("android.nfc.tech.IsoDep".equals(tech))
            return "ISO DEP";
        else if ("android.nfc.tech.NfcF".equals(tech))
            return "JIS 6319-4";
        else if ("android.nfc.tech.NfcV".equals(tech))
            return "ISO 15693";
        else if ("android.nfc.tech.Ndef".equals(tech))
            return "NFC Forum Type 1, 2, 3 or 4";
        return "NFC";
    }

    static String idtoSN(String id) {
        int len = id.length();
        String sn = "";
        for (int i=0; i<len; i++) {
            if(i>0 && ((i%2)==0))
                sn += ":";
            sn += id.charAt(i);
        }
        return sn;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean isRfidDevice = "rfid".equals(event.getDevice().getName());
        if (isRfidDevice && isKeyboardWedgeKey(keyCode)) {
            tagID = tagID + KeyEvent.keyCodeToString(keyCode).substring(8);
            return true;
        } else if (isRfidDevice && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            toast("RFID 125KHz Tag detected");
            textType.setText("RFID 125KHz");
            textSN.setText(idtoSN(tagID));
            table.setVisibility(View.VISIBLE);
            tagID = "";
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

    private void toast(final CharSequence text) {
        ToastCountDownTimer c = new ToastCountDownTimer(getApplicationContext(), text);
        c.start();
    }

    class ToastCountDownTimer extends CountDownTimer {
        private Toast mToast = null;
        public ToastCountDownTimer(Context context, CharSequence text) {
            super(1000,500);
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.show();
        }
        public void onTick(long millisUntilFinished) {
        }
        public void onFinish() {
            mToast.cancel();
        }
    }
}
