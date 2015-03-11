package doc.ic.ac.uk.roscamera.roscamera;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.android.view.camera2.RosCamera2PreviewView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;


public class MainActivity extends RosActivity {

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private RosCamera2PreviewView rosCamera2PreviewView;
    private int cameraId;

    public MainActivity() {

        super("ROSCamera", "ROSCamera");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        rosCamera2PreviewView = (RosCamera2PreviewView) findViewById(R.id.ros_camera_preview_view);
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
        startBackgroundThread();
        cameraId = 0;
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            manager.openCamera(Integer.toString(cameraId), new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice cameraDevice) {
                    rosCamera2PreviewView.setCamera(cameraDevice);
                }

                @Override
                public void onDisconnected(CameraDevice cameraDevice) {
                    rosCamera2PreviewView.releaseCamera();
                }

                @Override
                public void onError(CameraDevice cameraDevice, int i) {

                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(rosCamera2PreviewView, nodeConfiguration);
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }
}
