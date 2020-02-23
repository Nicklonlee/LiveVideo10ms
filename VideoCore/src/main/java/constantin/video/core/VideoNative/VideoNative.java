package constantin.video.core.VideoNative;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Surface;

import java.io.File;
import constantin.video.core.R;
import static android.content.Context.MODE_PRIVATE;

public class VideoNative {
    static {
        System.loadLibrary("VideoNative");
    }
    public static native long initialize(Context context, String groundRecordingDirectory);
    public static native void finalize(long nativeVideoPlayer);
    //Consumers are currently
    //1) The LowLag decoder (if Surface!=null)
    //2) The GroundRecorderRAW (if enableGroundRecording=true)
    public static native void nativeAddConsumers(long nativeInstance, Surface surface);
    public static native void nativeRemoveConsumers(long videoPlayerN);

    public static native void nativePassNALUData(long nativeInstance,byte[] b,int offset,int size);
    public static native void nativeStartReceiver(long nativeInstance, AssetManager assetManager);
    public static native void nativeStopReceiver(long nativeInstance);

    /**
     * Debugging/ Testing only
     */
    public static native String getVideoInfoString(long nativeInstance);
    public static native boolean anyVideoDataReceived(long nativeInstance);
    public static native boolean anyVideoBytesParsedSinceLastCall(long nativeInstance);
    public static native boolean receivingVideoButCannotParse(long nativeInstance);

    //call this via java to run the callback(s)
    //TODO: Use message queue from cpp for performance
    public static native <T extends INativeVideoParamsChanged> void nativeCallBack(T t, long nativeInstance);

    public static final int VS_SOURCE_UDP=0;
    public static final int VS_SOURCE_FILE=1;
    public static final int VS_SOURCE_ASSETS =2;
    public static final int VS_SOURCE_FFMPEG_URL=3;
    public static final int VS_SOURCE_EXTERNAL=4;
    public enum VS_SOURCE{UDP,FILE,ASSETS,FFMPEG,EXTERNAL}


    public static boolean PLAYBACK_FLE_EXISTS(final Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("pref_video", MODE_PRIVATE);
        final String filename=sharedPreferences.getString(context.getString(R.string.VS_PLAYBACK_FILENAME),"");
        File tempFile = new File(filename);
        return tempFile.exists();
    }

    public static VS_SOURCE getVS_SOURCE(final Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("pref_video", MODE_PRIVATE);
        final int val=sharedPreferences.getInt(context.getString(R.string.VS_SOURCE),0);
        VS_SOURCE ret=VS_SOURCE.values()[val];
        sharedPreferences.getInt(context.getString(R.string.VS_SOURCE),0);
        return ret;
    }

    @SuppressLint("ApplySharedPref")
    public static void setVS_SOURCE(final Context context, final VS_SOURCE val){
        SharedPreferences sharedPreferences=context.getSharedPreferences("pref_video", MODE_PRIVATE);
        sharedPreferences.edit().putInt(context.getString(R.string.VS_SOURCE),val.ordinal()).commit();
    }

    @SuppressLint("ApplySharedPref")
    public static void setVS_ASSETS_FILENAME_TEST_ONLY(final Context context, final String filename){
        SharedPreferences sharedPreferences=context.getSharedPreferences("pref_video", MODE_PRIVATE);
        sharedPreferences.edit().putString(context.getString(R.string.VS_ASSETS_FILENAME_TEST_ONLY),filename).commit();
    }
    @SuppressLint("ApplySharedPref")
    public static void setVS_FILE_ONLY_LIMIT_FPS(final Context context, final int limitFPS){
        SharedPreferences sharedPreferences=context.getSharedPreferences("pref_video", MODE_PRIVATE);
        sharedPreferences.edit().putInt(context.getString(R.string.VS_FILE_ONLY_LIMIT_FPS),limitFPS).commit();
    }

    private static String getDirectory(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/FPV_VR/";
    }

    //0=normal
    //1=stereo
    //2=equirectangular 360 sphere
    public static int videoMode(final Context c){
        final SharedPreferences pref_video=c.getSharedPreferences("pref_video",MODE_PRIVATE);
        return pref_video.getInt(c.getString(R.string.VS_VIDEO_VIEW_TYPE),0);
    }

    @SuppressLint("ApplySharedPref")
    public static void initializePreferences(final Context context,final boolean readAgain){
        PreferenceManager.setDefaultValues(context,"pref_video",MODE_PRIVATE,R.xml.pref_video,readAgain);
        final SharedPreferences pref_video=context.getSharedPreferences("pref_video", MODE_PRIVATE);
        final String filename=pref_video.getString(context.getString(R.string.VS_PLAYBACK_FILENAME),context.getString(R.string.VS_PLAYBACK_FILENAME_DEFAULT_VALUE));
        if(filename.equals(context.getString(R.string.VS_PLAYBACK_FILENAME_DEFAULT_VALUE))){
            pref_video.edit().putString(context.getString(R.string.VS_PLAYBACK_FILENAME),
                    getDirectory()+"Video/"+"filename.h264").commit();
        }
    }
}
