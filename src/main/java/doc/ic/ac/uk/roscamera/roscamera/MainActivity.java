package doc.ic.ac.uk.roscamera.roscamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;


public class MainActivity extends RosActivity {

    public MainActivity() {
        super("ROSCamera", "ROSCamera");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        return;
//        cameraId = 0;
//        rosCameraPreviewView.setCamera(Camera.open(cameraId));
//        NodeConfiguration nodeConfiguration =
//                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
//        nodeConfiguration.setMasterUri(getMasterUri());
//        nodeMainExecutor.execute(rosCameraPreviewView, nodeConfiguration);
    }
}
