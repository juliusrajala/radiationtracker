package com.example.radiationtracker;

import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Julius on 23.9.2014.
 * Kolmas näkymä joka tulee piirtämään GL:n avulla texturesurfacen kamera-feedistä.
 * TODO: Katso mallia secondFragmentistä ja netistä
 */
public class ThirdFragment extends Fragment {

    public static ThirdFragment newInstance(){
        ThirdFragment fragmentThird = new ThirdFragment();
        Bundle args = new Bundle();
        fragmentThird.setArguments(args);
        return fragmentThird;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        return view;
    }

    public void setupcamera(){
        if (mCamera != null){
            VideoCapture camera = mCamera;
            mCamera = null;
            camera.release();

        }
        mCamera = new VideoCapture(mCameraId);

        List<Camera.Size> previewSizes = mCamera.getSupportedPreviewSizes();
        double smallestPreviewSize = 1280*720;
        double smallestWidth = 480;
        for(Camera.Size previewSize : previewSizes){
            if(previewSize.area() < smallestPreviewSize && previewSize.width >= smallestWidth){
                mPreviewSize = previewSize
            }
        }
        mCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, mPreviewSize.width);
        mCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, mPreviewSize.height);

        while(mDoProcess && mCamera != null){
            boolean grabbed = mCamera.grab();
            if(grabbed){
                mCamera.retrieve(mCurrentFrame,
                        Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGB);
                //Process mCurrentFrame...
            }
        }
    }


}
