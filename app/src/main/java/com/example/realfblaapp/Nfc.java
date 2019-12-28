package com.example.realfblaapp;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

public class Nfc extends Activity
        implements CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    TextView textInfo;
    TextView textOut;
    TextView msg;

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_main);

        textInfo = findViewById(R.id.textOut);
        textOut = findViewById(R.id.textInfo);
        msg = findViewById(R.id.msg);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            Toast.makeText(Nfc.this,
                    "nfcAdapter==null, no NFC adapter exists",
                    Toast.LENGTH_LONG).show();
        }else{

//            Toast.makeText(Nfc.this,
//                    "Set Callback(s)",
//                    Toast.LENGTH_LONG).show();
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action != null) {
            if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
                Parcelable[] parcelables =
                        intent.getParcelableArrayExtra(
                                NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage inNdefMessage = (NdefMessage) parcelables[0];
                NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
                NdefRecord NdefRecord_0 = inNdefRecords[0];
                String inMsg = new String(NdefRecord_0.getPayload());
                textInfo.setText(inMsg);
                Toast.makeText(Nfc.this,
                        inMsg,
                        Toast.LENGTH_LONG).show();
                parse(inMsg);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

        final String eventString = "onNdefPushComplete\n" + event.toString();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        eventString,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        String stringOut = textOut.getText().toString();
        byte[] bytesOut = stringOut.getBytes();

        NdefRecord ndefRecordOut = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(),
                new byte[] {},
                bytesOut);

        NdefMessage ndefMessageout = new NdefMessage(ndefRecordOut);
        return ndefMessageout;
    }

    public void parse(String message) {
        String string = message;
        int left = string.indexOf("(");
        int right = string.indexOf(")");

// pull out the text inside the parens
        String sub = string.substring(left+1, right);

        String idNum = string.substring(0, left);
        String timeDate = sub;
            int leftTime = timeDate.indexOf("_");
            int rightTime = timeDate.indexOf("_");

            String time = timeDate.substring(0, leftTime);
            String Date = timeDate.substring(rightTime+1);

        String checkBoolean = string.substring(right+1);
        Toast.makeText(getApplicationContext(), checkBoolean, Toast.LENGTH_SHORT).show();

        if (checkBoolean.equals("0")) {
            msg.setText(idNum + " has checked in at " + time);
        } else {
            msg.setText(idNum + " has checked out " + time);
        }
    }

}