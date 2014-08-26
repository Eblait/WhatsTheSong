package voxar.cin.ufpe.br.whatsthesong.utils;

import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import voxar.cin.ufpe.br.whatsthesong.R;

/**
 * Created by Dicksson on 8/22/2014.
 */

public class ProgressBar implements Runnable {

    MediaPlayer mp;
    ImageView progress;
    FragmentActivity mActivity;

    public ProgressBar(FragmentActivity mActivity, MediaPlayer mp, String instrument) throws Exception {
        this.mActivity = mActivity;
        this.mp = mp;
        progress = (ImageView) mActivity.findViewById(R.id.class.getField("loading_bar_" + instrument).getInt(0));
    }

    @Override
    public void run() {
        // mp is your MediaPlayer
        // progress is your ProgressBar

        final int[] currentPosition = {0};
        int total = mp.getDuration();

        final Handler handler = new Handler();
        while (mp != null && currentPosition[0] < total) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentPosition[0] = mp.getCurrentPosition();
                    progress.setScaleX(currentPosition[0] * 5);
                }
            });
        }
    }
}