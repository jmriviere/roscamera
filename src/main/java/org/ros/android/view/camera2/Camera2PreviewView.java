package org.ros.android.view.camera2;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.base.Preconditions;

import org.ros.exception.RosRuntimeException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by jmr12 on 11/03/15.
 */
public class Camera2PreviewView extends ViewGroup {

    private SurfaceView previewSurface;
    private ImageReader imageReader;
    private CameraCaptureSession captureSession;
    private CameraDevice camera;
    private Size previewSize;
    private RawImageListener rawImageListener;
    private Context context;

    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null != context) {
                Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public Camera2PreviewView(Context context) {
        super(context);
        init(context);
    }

    public Camera2PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Camera2PreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    public void setCamera(final CameraDevice camera) {
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

        try {
            camera.createCaptureSession(Arrays.asList(previewSurface.getHolder().getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == camera) {
                        return;
                    }

                    captureSession = cameraCaptureSession;

                    CaptureRequest.Builder previewRequestBuilder = null;
                    try {
                        previewRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                    }
                    catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    previewRequestBuilder.addTarget(previewSurface.getHolder().getSurface());

                    CameraCaptureSession.CaptureCallback captureCallback =
                            new CameraCaptureSession.CaptureCallback() {};

                    try {
                        // Auto focus should be continuous for camera preview.
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // Flash is automatically enabled when necessary.
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // Finally, we start displaying the camera preview.
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        captureSession.setRepeatingRequest(previewRequest,
                                captureCallback, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    showToast("Failed: " + previewSurface.getHolder().getSurface() + " " + imageReader.getSurface());
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void releaseCamera() {
        if (null == camera) {
            return;
        }
        camera.close();
        camera = null;
    }

    private void init(Context context) {
        this.context = context;
        previewSurface = new SurfaceView(context);
        previewSurface.getHolder().setFixedSize(1920,1080);
        addView(previewSurface);
        imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, /* max images on queue*/2);
    }

    public void setRawImageListener(RawImageListener rawImageListener) {
        this.rawImageListener = rawImageListener;
    }

    private void showToast(String text) {
// We show a Toast by sending request message to mMessageHandler. This makes sure that the
// Toast is shown on the UI thread.
        Message message = Message.obtain();
        message.obj = text;
        mMessageHandler.sendMessage(message);
    }

}