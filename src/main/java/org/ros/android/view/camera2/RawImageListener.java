package org.ros.android.view.camera2;

import android.util.Size;

/**
 * Created by jmr12 on 11/03/15.
 */
interface RawImageListener {

    void onNewRawImage(byte[] data, Size size);

}
