package org.ros.android.view.camera2;

import android.content.Context;
import android.util.AttributeSet;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;

/**
 * Created by jmr12 on 11/03/15.
 */
public class RosCamera2PreviewView extends Camera2PreviewView implements NodeMain {

    public RosCamera2PreviewView(Context context) {
        super(context);
    }

    public RosCamera2PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RosCamera2PreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ros_camera2_preview_view");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        setRawImageListener(new CompressedImagePublisher(connectedNode));
    }

    @Override
    public void onShutdown(Node node) {

    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {

    }
}
