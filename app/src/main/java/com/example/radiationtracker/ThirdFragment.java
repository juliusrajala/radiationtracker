package com.example.radiationtracker;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Size;

/**
 * Created by Julius on 23.9.2014.
 * Kolmas näkymä joka tulee piirtämään GL:n avulla texturesurfacen kamera-feedistä.
 * TODO: Katso mallia secondFragmentistä ja netistä
 * Näkymän OpenCV koodi pohjaa vahvasti Erik Hellmannin OpenCV esimerkki-koodiin
 * <erik.hellman@sonymobile.com>
 */
public class ThirdFragment extends Fragment implements OpenCVWorker.ResultCallback,
        SurfaceHolder.Callback, View.OnTouchListener, GestureDetector.OnDoubleTapListener{

    public static final int DRAW_RESULT_BITMAP = 10;
    private Handler mUiHandler;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Rect mSurfaceSize;
    private OpenCVWorker mWorker;
    private double mFpsResult;
    private Paint mFpsPaint;
    private GestureDetector mGestureDetector;

    public static ThirdFragment newInstance(){
        ThirdFragment fragmentThird = new ThirdFragment();
        Bundle args = new Bundle();
        fragmentThird.setArguments(args);
        return fragmentThird;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mGestureDetector = new GestureDetector(new MyOnGestureListener());
        mGestureDetector.setOnDoubleTapListener(this);
        mGestureDetector.setIsLongpressEnabled(false);

        mFpsPaint = new Paint();
        mFpsPaint.setColor(Color.GREEN);
        mFpsPaint.setDither(true);
        mFpsPaint.setFlags(Paint.SUBPIXEL_TEXT_FLAG);
        mFpsPaint.setTextSize(48);
        mFpsPaint.setTypeface(Typeface.SANS_SERIF);

        mSurfaceView = new SurfaceView(this.getActivity());
        mSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mSurfaceHolder = mSurfaceView.getHolder();

        mUiHandler = new Handler(Looper.getMainLooper(), new UiCallback());

    }

    @Override
    public void onResume(){
        super.onResume();

        mSurfaceHolder.addCallback(this);
        mSurfaceView.setOnTouchListener(this);
        setContentView(mSurfaceView);
    }

    @Override
    public void onPause(){
        super.onPause();
        mWorker.stopProcessing();
        mWorker.removeResultCallback(this);

        if(mSurfaceHolder != null){
            mSurfaceHolder.removeCallback(this);
        }
    }

    @Override
    public void onResultMatrixReady(Bitmap resultBitmap){
        mUiHandler.obtainMessage(DRAW_RESULT_BITMAP, resultBitmap).sendToTarget();
    }

    @Override
    public void onFpsUpdate(double fps){
        mFpsResult = fps;
    }

    private void initCameraView(){
        mWorker = new OpenCVWorker(OpenCVWorker.FIRST_CAMERA);
        mWorker.addResultCallback(this);
        new Thread(mWorker).start();
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        OpenCVLoader.intAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, new OpenCVLoaderCallback(this));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        mSurfaceSize = new Rect(0,0,width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){

    }
    @Override
    public boolean onTouch(View view, MotionEvent event){
        return mGestureDetector.onTouchEvent(event);
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event){
        pickColorFromTap(event);
        return true;
    }

    private void pickColorFromTap(MotionEvent event){
        Size previewSize= mWorker.getPreviewSize();
        double xFactor = previewSize.width / mSurfaceView.getWidth();
        double yFactor = previewSize.height / mSurfaceView.getHeight();
        mWorker.setSelectedPoint((int)(event.getX()*xFactor), (int) (event.getY()*yFactor));

    }

    @Override
    public boolean onDoubleTap(MotionEvent event){
        mWorker.clearSelectedColor();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event){
        return false;
    }

    private static final class OpenCVLoaderCallback extends BaseLoaderCallback {
        private Context mContext;

        public OpenCVLoaderCallback(Context context){
            super(context);
            mContext = context;
        }
        @Override
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    ((ThirdFragment) mContext).initCameraView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    }

    private class UiCallback implements Handler.Callback{
        @Override
        public boolean handleMessage(Message message){
            if(message.what == DRAW_RESULT_BITMAP){
                Bitmap resultBitmap = (Bitmap) message.obj;
                Canvas canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    canvas.drawBitmap(resultBitmap, null, mSurfaceSize, null);
                    canvas.drawText(String.format("FPS: %.2f",mFpsResult), 35,45, mFpsPaint);
                    String msg = "Single tap to select color. Double-tap to clear selection.";
                    float width = mFpsPaint.measureText(msg);
                    canvas.drawText(msg,mSurfaceView.getWidth()/2 - width/2,
                            mSurfaceView.getHeight()-30, mFpsPaint);
                }finally{
                    if(canvas != null){
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    mWorker.releaseResultBitmap(resultBitmap);
                }
            }
            return true;
        }
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent event){
            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        return view;
    }

}
