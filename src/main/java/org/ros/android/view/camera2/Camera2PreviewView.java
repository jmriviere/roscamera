package org.ros.android.view.camera2;

import android.content.Context;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.media.ImageReader;
import android.util.Size;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import java.nio.ByteBuffer;

/**
 * Created by jmr12 on 11/03/15.
 */
public class Camera2PreviewView extends ViewGroup {

    private SurfaceView previewSurface;
    private ImageReader imageReader;
    private CameraDevice camera;
    private Size previewSize;
    private RawImageListener rawImageListener;

    public Camera2PreviewView(Context context) {
        super(context);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);
            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (previewSize != null) {
                previewWidth = previewSize.getWidth();
                previewHeight = previewSize.getHeight();
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void setCamera(CameraDevice camera) {
        Preconditions.checkNotNull(camera);
        this.camera = camera;
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                Image im = imageReader.acquireNextImage();
                ByteBuffer buffer = im.getPlanes()[0].getBuffer();
                if (rawImageListener != null) {
                    rawImageListener.onNewRawImage(buffer.array(), previewSize);
                }
            }
        }, null);
//        setupCameraParameters();
//        setupBufferingPreviewCallback();
//        camera.;
    }

    public void releaseCamera() {
        if (camera == null) {
            return;
        }
        camera.close();
        camera = null;
    }

    private void init(Context context) {
        previewSurface = new SurfaceView(context);
        addView(previewSurface);
    }

}