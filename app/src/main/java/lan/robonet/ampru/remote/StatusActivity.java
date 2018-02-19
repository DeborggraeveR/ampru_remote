package lan.robonet.ampru.remote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StatusActivity extends AppCompatActivity {

    private BroadcastReceiver m_receiver_end_signal;
    private BroadcastReceiver m_receiver_test_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Intent intent = getIntent();
        //String order_status = intent.getStringExtra("order_status");
        //TextView tv = (TextView)findViewById(R.id.order_status);
        //tv.setText(order_status);

        // Register the end signal listener
        IntentFilter pkgFilter_end_signal = new IntentFilter("lan.robonet.ampru.remote.end_signal");
        m_receiver_end_signal = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("AMPRU_REMOTE_APP", "[StatusActivity][onReceive][end_signal]");
                finish();
            }
        };
        registerReceiver(m_receiver_end_signal, pkgFilter_end_signal);

        // Register the test message listener
        IntentFilter pkgFilter_test_message = new IntentFilter("lan.robonet.ampru.remote.test_message");
        m_receiver_test_message = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("AMPRU_REMOTE_APP", "[ApplicationActivity][onReceive][remote_test]"+intent.getStringExtra("test_data"));

            }
        };
        registerReceiver(m_receiver_test_message, pkgFilter_test_message);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(m_receiver_test_message);
        unregisterReceiver(m_receiver_end_signal);

        super.onDestroy();
    }
}
