package voxar.cin.ufpe.br.whatsthesong.utils;

import android.media.MediaPlayer;
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

    public ProgressBar (FragmentActivity mActivity, MediaPlayer mp, String instrument) throws Exception {
        this.mp = mp;
        progress = (ImageView) mActivity.findViewById(R.id.class.getField("loading_bar_" + instrument).getInt(0));
    }

    @Override
    public void run() {
        // mp is your MediaPlayer
        // progress is your ProgressBar

        int currentPosition = 0;
        int total = mp.getDuration();
        while (mp != null && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mp.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            progress.setScaleX(currentPosition * 5);
        }
    }
}
