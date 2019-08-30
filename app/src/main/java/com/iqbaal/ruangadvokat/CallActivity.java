package com.iqbaal.ruangadvokat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class CallActivity extends AppCompatActivity {
 SinchClient sinchClient;
 Call call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId("-LmHnf7OvvR3qAaRWF9m")
                .applicationKey("8ba14f01-9d34-40e9-aca3-65d389a991fe")
                .applicationSecret("Ijy+OTZI30y0idF5Tc0IrQ==")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call call) {

            }
        });
        sinchClient.start();

        Button telpon = findViewById(R.id.nelpon);
        telpon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinchClient.getCallClient().callUser("-LmxJ3ckUxsDpYtO4GUx").addCallListener(new SinchCallListener());

            }
        });
    }
    private class SinchCallListener implements CallListener{

        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(getApplicationContext(), "Ringing ...", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCallEstablished(Call call) {
            Toast.makeText(getApplicationContext(), "Call Established", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCallEnded(Call endcall) {
            Toast.makeText(getApplicationContext(), "Call Ended", Toast.LENGTH_LONG).show();
            call = null;
            endcall.hangup();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }
}
