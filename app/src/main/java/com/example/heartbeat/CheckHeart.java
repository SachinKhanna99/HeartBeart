package com.example.heartbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class CheckHeart extends AppCompatActivity {

    public static final String TAG = "CameraResult--->";
    private TextureView textureView;
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private static final int REQUEST_CAMERA_PERMISSION = 1;


    public Handler mBackgroundHandler;
    public HandlerThread mBackgroundThread;

    public static int hrtratebpm;
    private int mCurrentRollingAverage;
    private int mLastRollingAverage;
    private int mLastLastRollingAverage;
    private long[] mTimeArray;
    private int numCaptures = 0;
    private int mNumBeats = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_heart);

        textureView = findViewById(R.id.texture);
        mTimeArray = new long[15];

    }
        TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Bitmap bitmap = textureView.getBitmap();
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                int[] pixel = new int[height * width];

                bitmap.getPixels(pixel, 0, width, width / 2, height / 2, width / 20, height / 20);
                int sum = 0;
                for (int i = 0; i < height * width; i++) {
                    int red = (pixel[i] >> 16) & 0xFF;
                    sum = sum + red;
                }

                if (numCaptures == 20) {

                    mCurrentRollingAverage = 20;
                } else if (numCaptures > 20 && numCaptures < 49) {
                    mCurrentRollingAverage = (mCurrentRollingAverage * (numCaptures - 20) + sum) / (numCaptures - 19);
                } else if (numCaptures >= 49) {
                    mCurrentRollingAverage = (mCurrentRollingAverage * 29 + sum) / 30;
                    if (mLastRollingAverage > mCurrentRollingAverage && mLastRollingAverage > mLastLastRollingAverage && mNumBeats < 15) {
                        mTimeArray[mNumBeats] = System.currentTimeMillis();

                        mNumBeats++;
                        if (mNumBeats == 15) {
                            calcBPM();
                        }
                    }
                }
                numCaptures++;
                // Save previous two values
                mLastLastRollingAverage = mLastRollingAverage;
                mLastRollingAverage = mCurrentRollingAverage;
            }
        };


        private void calcBPM () {
            int med;
            long[] timedist = new long[14];
            for (int i = 0; i < 14; i++) {
                timedist[i] = mTimeArray[i + 1] - mTimeArray[i];
            }
            Arrays.sort(timedist);
            med = (int) timedist[timedist.length / 2];
            hrtratebpm = 60000 / med;

            Toast.makeText(this, "Heartbeat is : " + hrtratebpm, Toast.LENGTH_SHORT).show();
            // add to database
            String re= String.valueOf(hrtratebpm);
            Saveinfo(re);
        }

    private void Saveinfo(String result) {
      Intent i=new Intent(CheckHeart.this,Result.class);
      i.putExtra("result",result);
      startActivity(i);
      finish();
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                Log.e(TAG, "onOpened: ");
                cameraDevice = camera;
                OpenPreview();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                cameraDevice.close();
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                if (cameraDevice != null) {
                    cameraDevice.close();
                }
                cameraDevice = null;

                Toast.makeText(CheckHeart.this, "Error : Unable to open camera", Toast.LENGTH_SHORT).show();
            }
        };

        private void OpenPreview () {

            try {
                SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                assert surfaceTexture != null;
                surfaceTexture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
                Surface surface = new Surface(surfaceTexture);
                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureRequestBuilder.addTarget(surface);
                cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        if (null == cameraDevice) {
                            return;
                        }
                        cameraCaptureSessions = session;
                        updatePreview();
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                        Toast.makeText(CheckHeart.this, "Configuration change", Toast.LENGTH_SHORT).show();
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        protected void StartBackgroundActivity ()
        {
            mBackgroundThread = new HandlerThread("Camera Background");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }

        protected void stopBackgroundThread () {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private void updatePreview () {
            if (null == cameraDevice) {
                Log.e(TAG, "updatePreview error, return");
            }
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
            try {
                cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        private void openCamera () {
            CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            Log.e(TAG, "is camera open");
            try {
                cameraId = manager.getCameraIdList()[0];
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CheckHeart.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    return;
                }
                manager.openCamera(cameraId, stateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "openCamera X");
        }
        private void closeCamera () {
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            if (requestCode == REQUEST_CAMERA_PERMISSION) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // close the app
                    Toast.makeText(CheckHeart.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

        @Override
        protected void onStart () {
            super.onStart();
        }

        @Override
        protected void onStop () {
            super.onStop();
        }

        @Override
        protected void onPause () {
            closeCamera();
            stopBackgroundThread();
            super.onPause();
        }

        @Override
        protected void onResume () {
            super.onResume();
            Log.e(TAG, "onResume");
            StartBackgroundActivity();
            if (textureView.isAvailable()) {
                openCamera();
            } else {
                textureView.setSurfaceTextureListener(textureListener);
            }
        }
    }

