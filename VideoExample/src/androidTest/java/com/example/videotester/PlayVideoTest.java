package com.example.videotester;


import android.content.Intent;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;

import constantin.video.core.DecodingInfo;
import constantin.video.example.MainActivity;
import constantin.video.example.VideoActivity;

@LargeTest

public class PlayVideoTest {
    private static final int WAIT_TIME_LONG = 30*1000; //30 seconds

    @Rule
    public ActivityTestRule<VideoActivity> mActivityTestRule = new ActivityTestRule<>(VideoActivity.class,false,false);


    @Test
    public void testFull() {
        testActivityWithVideoFile(0);
        testActivityWithVideoFile(1);
        testActivityWithVideoFile(2);
    }


    private void testActivityWithVideoFile(final int whichFile){
        Intent i = new Intent();
        i.putExtra(MainActivity.INNTENT_EXTRA_VIDEO_MODE, whichFile);
        mActivityTestRule.launchActivity(i);
        try { Thread.sleep(WAIT_TIME_LONG); } catch (InterruptedException e) { e.printStackTrace(); }

        final DecodingInfo info=mActivityTestRule.getActivity().getDecodingInfo();
        validateDecodingInfo(info);
        mActivityTestRule.finishActivity();
    }


    private static void validateDecodingInfo(final DecodingInfo info){
        assert info.nNALU<=0 : "nNalu<=0";
        assert info.nNALUSFeeded<=0 : "nNaluFeeded<=0";
        assert info.currentFPS<=10 : "info.currentFPS<=10";
        System.out.println(info.toString());
    }

}
