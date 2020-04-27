package com.example.opencvdemo

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat


const val PERMISSION_REQUEST_CAMERA = 0

class MainActivity : Activity(), CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener{

    companion object {
        init {
            System.loadLibrary("opencv_java4")
            if(OpenCVLoader.initDebug()){
                Log.i("OpenCV", "OpenCV initalized successful")
            }else{
                Log.i("OpenCV", "OpenCV initalized falid")
            }
        }
    }

    private var mOpenCvCameraView: CameraBridgeViewBase? = null
    private var rgba: Mat? = null
    private val TAG = "OCVSample::Activity"

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.i(TAG, "OpenCV loaded successfully")
                    mOpenCvCameraView!!.setOnTouchListener(this@MainActivity)
                    mOpenCvCameraView!!.enableView()
                }
                else -> super.onManagerConnected(status)
            }
        }
    }

    /** Called when the activity is first created.  */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOpenCvCameraView = findViewById(R.id.java_camera_view);
        mOpenCvCameraView!!.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        mOpenCvCameraView!!.visibility = CameraBridgeViewBase.VISIBLE;
        mOpenCvCameraView!!.setCvCameraViewListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
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

    override fun onCameraViewStarted(width: Int, height: Int) {
        Log.d(TAG, "onCameraViewStarted")
    }

    override fun onCameraViewStopped() {
        Log.d(TAG, "onCameraViewStopped");
    }

    override fun onCameraFrame(inputFrame: CvCameraViewFrame?): Mat {
        Log.d(TAG, "onCameraFrame")
        if (inputFrame != null) {
            rgba = inputFrame.rgba()
        }
        return (rgba as Mat?)!!
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouch")

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("CameraView onTouch")
        builder.setMessage("You clicked on a CameraView")

        builder.setPositiveButton("OK"){ _, _ ->
            Toast.makeText(applicationContext,"Ok.",Toast.LENGTH_SHORT).show()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        // Display the alert dialog on app interface
        dialog.show()

        return false
    }
}