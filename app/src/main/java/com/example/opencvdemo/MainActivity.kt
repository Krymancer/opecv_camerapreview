package com.example.opencvdemo

import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraActivity
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.SurfaceView
import android.view.WindowManager
import java.util.Collections


const val PERMISSION_REQUEST_CAMERA = 0

class MainActivity : CameraActivity(), CvCameraViewListener2 {

    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    private var rgba: Mat? = null
    private val TAG = "OCVSample::Activity"
    private val mIsJavaCamera = true
    private val mItemSwitchCamera: MenuItem? = null

     private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(TAG,"OpenCV loaded successfully")
                    mOpenCvCameraView!!.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)

        mOpenCvCameraView = findViewById(R.id.java_camera_view)
        mOpenCvCameraView!!.visibility = CameraBridgeViewBase.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(this)

    }

    override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        }else{
            Log.d(TAG, "OpenCV library found inside package. Using it!")
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun getCameraViewList(): List<CameraBridgeViewBase?>? {
        return listOf(mOpenCvCameraView)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) mOpenCvCameraView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {}

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat? {
        return inputFrame.rgba()
    }
}