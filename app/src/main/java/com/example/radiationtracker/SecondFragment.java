package com.example.radiationtracker;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SecondFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private Camera mCamera;
    private CameraPreview mPreview;
    private View mCameraView;

    // newInstance constructor for creating fragment with arguments
    public static SecondFragment newInstance(int page, String title) {
        SecondFragment fragmentFirst = new SecondFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    private boolean safeCameraOpenInView(View view){
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        mCameraView = view;
        qOpened = (mCamera != null);

        if(qOpened == true) {
            mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera, view);
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open(); //Attempt to get camera instance
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return c;
    }

    private void releaseCameraAndPreview(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private Context mContext;
        private View mCameraView;

        public CameraPreview(Context context, Camera camera, View cameraView){
            super(context);

            mCameraView = cameraView;
            mCamera = camera;
            //Install a SurfaceHolder.Callback so we get notified when the underlying
            //surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setKeepScreenOn(true);
            //Deprecated setting, but required on android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void startCameraPreview()
        {
            try{
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }


        /*
        * Surface has been created, now tell the camera where to draw the preview
        * @param holder
        * */
        public void surfaceCreated(SurfaceHolder holder){
            try{
                mCamera.setPreviewDisplay(holder);
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder){
            //empty. Hoidetaan cameran vapautus aktiviteetissä.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
            //If your preview can change or rotate, take care of those events here.
            //Make sure to stop the preview before resizing or reformatting it.

            if(mHolder.getSurface() == null){
                //preview surface does not exist
                return;
            }

            try{
                mCamera.stopPreview();
            }catch (Exception e){
                //Ignore: tried to stop a non-existant preview
            }

            try{
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        boolean opened = safeCameraOpenInView(view);

        if(opened == false){
            Log.d("CameraGuide", "Error, camera failed to open");
            return view;
        }
        return view;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback(){
        @Override
    public void onPictureTaken(byte[] data, Camera camera){
            File pictureFile = getOutputMediaFile();
            if(pictureFile == null){
                Toast.makeText(getActivity(), "Image retrieval failed.", Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    };

    private File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UltimateCameraGuideApp");

        if(! mediaStorageDir.exists()){
            if(! mediaStorageDir.mkdirs()){
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }
    //Create a media file name.
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
    return mediaFile;
    }
}