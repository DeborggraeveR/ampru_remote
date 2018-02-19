package lan.robonet.ampru.remote;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.address.InetAddressFactory;
import org.ros.android.view.VirtualJoystickView;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

public class MainActivity extends RosAppActivity {

    static {
        System.loadLibrary("native-lib");
    }

    private RemoteApp m_remote_app = new RemoteApp();
    private BroadcastReceiver m_receiver_test_message = null;
    private VirtualJoystickView m_virtualJoystickView;

    public MainActivity() {
        super("AMPRU Remote", "AMPRU Remote");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("AMPRU_REMOTE_APP", "[ApplicationActivity][onCreate]: start");

        setDefaultMasterName("ampru_remote");
        setDashboardResource(R.id.top_bar);
        setMainWindowResource(R.layout.activity_main);

        super.onCreate(savedInstanceState);

        m_virtualJoystickView = (VirtualJoystickView) findViewById(R.id.virtual_joystick);

        Log.d("AMPRU_REMOTE_APP", "[ApplicationActivity][onCreate]: end");
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        Log.d("AMPRU_REMOTE_APP", "[ApplicationActivity][init]: start");

        super.init(nodeMainExecutor);

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(), getMasterUri());
        nodeMainExecutor.execute(m_remote_app, nodeConfiguration);
        nodeMainExecutor.execute(m_virtualJoystickView, nodeConfiguration.setNodeName("virtual_joystick"));

        Log.d("AMPRU_REMOTE_APP", "[ApplicationActivity][init]: end");


        //startActivity(new Intent(this, StatusActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("AMPRU_REMOTE_APP", "[ApplicationActivity][onCreateOptionsMenu]");

        menu.add(0, 0, 0, "test");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("AMPRU_REMOTE_APP", "[ApplicationActivity][onOptionsItemSelected]"+item.getItemId());

        boolean result = super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case 0:
                finish();
                break;
        }

        return result;
    }

    public native String stringFromJNI();

    public class RemoteApp extends AbstractNodeMain {

        private Publisher<std_msgs.String> m_test_publisher = null;

        @Override
        public GraphName getDefaultNodeName() {
            return GraphName.of("ampru_remote");
        }

        public void publishTestMessage() {
            std_msgs.String message = m_test_publisher.newMessage();
            message.setData("hello from android");
            m_test_publisher.publish(message);
        }

        @Override
        public void onStart(ConnectedNode connectedNode) {
            m_test_publisher = connectedNode.newPublisher(getMasterNameSpace().resolve("test_pub").toString(), std_msgs.String._TYPE);

            Subscriber<std_msgs.String> subscriber_test = connectedNode.newSubscriber(getMasterNameSpace().resolve("remote_test").toString(), std_msgs.String._TYPE);
            subscriber_test.addMessageListener(new MessageListener<std_msgs.String>() {
                @Override
                public void onNewMessage(std_msgs.String message) {
                    Log.d("AMPRU_REMOTE_APP", "[ApplicationActivity][onNewMessage][remote_test]"+message.getData());

                    Intent intent = new Intent("lan.robonet.ampru.remote.test_message");
                    intent.putExtra("test_data", message.getData());
                    sendBroadcast(intent);
                }
            });
        }
    }
}
