package com.zlcdgroup.camera.internal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zlcdgroup.camera.R;
import com.zlcdgroup.camera.util.CameraUtil;
import com.zlcdgroup.camera.util.Degrees;
import java.io.File;

import static android.app.Activity.RESULT_CANCELED;


/**
 * @author Aidan Follestad (afollestad)
 */
abstract class BaseCameraFragment extends Fragment implements CameraUriInterface, View.OnClickListener {

    protected ImageView  flash_model,volume_model;//闪光灯与拍照声音

    protected  ImageView  cancel_camera,tack_camera,config_camera;//拍照及确定取消

    protected  AutoFitTextureView   textureView;//预览界面

    protected RelativeLayout  camera_layout,camera_toploayou;


    protected   View  camera_moveleft,camera_movetop;

    protected TextView mRecordDuration;

    private boolean mIsRecording;
    protected String mOutputUri;
    protected BaseCaptureInterface mInterface;
    protected Handler mPositionHandler;
    protected MediaRecorder mMediaRecorder;

    protected static void LOG(Object context, String message) {
        Log.d(context instanceof Class<?> ? ((Class<?>) context).getSimpleName() :
                context.getClass().getSimpleName(), message);
    }

    private final Runnable mPositionUpdater = new Runnable() {
        @Override
        public void run() {
            if (mInterface == null || mRecordDuration == null) return;
            final long mRecordStart = mInterface.getRecordingStart();
            final long mRecordEnd = mInterface.getRecordingEnd();
            if (mRecordStart == -1 && mRecordEnd == -1) return;
            final long now = System.currentTimeMillis();
            if (mRecordEnd != -1) {
                if (now >= mRecordEnd) {
                    stopRecordingVideo(true);
                } else {
                    final long diff = mRecordEnd - now;
                    mRecordDuration.setText(String.format("-%s", CameraUtil.getDurationString(diff)));
                }
            } else {
                mRecordDuration.setText(CameraUtil.getDurationString(now - mRecordStart));
            }
            if (mPositionHandler != null)
                mPositionHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camera__fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        flash_model = (ImageView)view.findViewById(R.id.flash_model);
        volume_model = (ImageView)view.findViewById(R.id.flash_model);
        cancel_camera = (ImageView)view.findViewById(R.id.cancel_camera);
        tack_camera = (ImageView)view.findViewById(R.id.tack_camera);
        config_camera = (ImageView)view.findViewById(R.id.config_camera);
        textureView = (AutoFitTextureView)view.findViewById(R.id.preview_view);
        camera_layout = (RelativeLayout)view.findViewById(R.id.camera_layout);
        camera_toploayou = (RelativeLayout)view.findViewById(R.id.camera_toploayou);
        camera_moveleft = view.findViewById(R.id.camera_moveleft);
        camera_movetop  = view.findViewById(R.id.camera_movetop);

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mRecordDuration = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mInterface != null && mInterface.hasLengthLimit()) {
            if (mInterface.countdownImmediately() || mInterface.getRecordingStart() > -1) {
                if (mInterface.getRecordingStart() == -1)
                    mInterface.setRecordingStart(System.currentTimeMillis());
                startCounter();
            } else {
                mRecordDuration.setText(String.format("-%s", CameraUtil.getDurationString(mInterface.getLengthLimit())));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        mInterface = (BaseCaptureInterface) activity;
    }

    @NonNull
    protected final File getOutputMediaFile() {
        return CameraUtil.makeTempFile(getActivity(), getArguments().getString(CameraIntentKey.SAVE_DIR),getArguentsByKey(CameraIntentKey.SAVE_NAME), ".jpg");
    }

    public   String    getArguentsByKey(String  key){
       return getArguments().getString(key);
    }

    public abstract void openCamera();

    public abstract void closeCamera();

    public void cleanup() {
        closeCamera();
        releaseRecorder();
        stopCounter();
    }

    @Override
    public void onPause() {
        super.onPause();
        cleanup();
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mInterface = null;
    }

    public final void startCounter() {
        if (mPositionHandler == null)
            mPositionHandler = new Handler();
        else mPositionHandler.removeCallbacks(mPositionUpdater);
        mPositionHandler.post(mPositionUpdater);
    }

    @BaseCaptureActivity.CameraPosition
    public final int getCurrentCameraPosition() {
        if (mInterface == null) return BaseCaptureActivity.CAMERA_POSITION_UNKNOWN;
        return mInterface.getCurrentCameraPosition();
    }

    public final int getCurrentCameraId() {
        if (mInterface.getCurrentCameraPosition() == BaseCaptureActivity.CAMERA_POSITION_BACK)
            return (Integer) mInterface.getBackCamera();
        else return (Integer) mInterface.getFrontCamera();
    }

    public final void stopCounter() {
        if (mPositionHandler != null) {
            mPositionHandler.removeCallbacks(mPositionUpdater);
            mPositionHandler = null;
        }
    }

    public final void releaseRecorder() {
        if (mMediaRecorder != null) {
            if (mIsRecording) {
                try {
                    mMediaRecorder.stop();
                } catch (Throwable t) {
                    //noinspection ResultOfMethodCallIgnored
                    new File(mOutputUri).delete();
                    t.printStackTrace();
                }
                mIsRecording = false;
            }
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public boolean startRecordingVideo() {
        if (mInterface != null && mInterface.hasLengthLimit() && !mInterface.countdownImmediately()) {
            // Countdown wasn't started in onResume, start it now
            if (mInterface.getRecordingStart() == -1)
                mInterface.setRecordingStart(System.currentTimeMillis());
            startCounter();
        }

        final int orientation = Degrees.getActivityOrientation(getActivity());
        //noinspection ResourceType
        getActivity().setRequestedOrientation(orientation);
        mInterface.setDidRecord(true);
        return true;
    }

    public void stopRecordingVideo(boolean reachedZero) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("output_uri", mOutputUri);
    }

    @Override
    public final String getOutputUri() {
        return mOutputUri;
    }

    protected final void throwError(Exception e) {
        Activity act = getActivity();
        if (act != null) {
            act.setResult(RESULT_CANCELED, new Intent().putExtra(MaterialCamera.ERROR_EXTRA, e));
            act.finish();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.facing) {
            mInterface.toggleCameraPosition();
            mButtonFacing.setImageDrawable(VC.get(this, mInterface.getCurrentCameraPosition() == BaseCaptureActivity.CAMERA_POSITION_BACK ?
                    R.drawable.mcam_camera_front : R.drawable.mcam_camera_rear));
            closeCamera();
            openCamera();
        } else if (view.getId() == R.id.video) {
            if (mIsRecording) {
                stopRecordingVideo(false);
                mIsRecording = false;
            } else {
                if (getArguments().getBoolean(CameraIntentKey.SHOW_PORTRAIT_WARNING, true) &&
                        Degrees.isPortrait(getActivity())) {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.mcam_portrait)
                            .content(R.string.mcam_portrait_warning)
                            .positiveText(R.string.mcam_yes)
                            .negativeText(android.R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                    mIsRecording = startRecordingVideo();
                                }
                            })
                            .show();
                } else {
                    mIsRecording = startRecordingVideo();
                }
            }
        }
    }
}