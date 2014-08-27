package voxar.cin.ufpe.br.whatsthesong.utils;

import android.media.MediaPlayer;
import android.widget.ImageView;

import voxar.cin.ufpe.br.whatsthesong.R;
import voxar.cin.ufpe.br.whatsthesong.activities.QuizScreenActivity;

/**
 * Created by Dicksson on 8/22/2014.
 */

public class ProgressBar implements Runnable {

    MediaPlayer mp;
    ImageView progress;
    QuizScreenActivity mActivity;
    public boolean running = true;

    public ProgressBar(QuizScreenActivity mActivity, MediaPlayer mp, int index) throws Exception {
        this.mActivity = mActivity;
        this.mp = mp;
        String instrument = null;

        switch (index) {
            case 1:
                instrument = "drum";
                break;
            case 2:
                instrument = "bass";
                break;
            case 3:
                instrument = "keyboard";
                break;
            case 4:
                instrument = "sax";
                break;
            case 5:
                instrument = "guitar";
                break;
            case 6:
                instrument = "voice";
                break;
        }

        progress = (ImageView) mActivity.findViewById(R.id.class.getField("loading_bar_" + instrument).getInt(0));
    }

    @Override
    public void run() {
        int currentPosition;

        while (running && mp != null) {
            try {
                currentPosition = mp.getCurrentPosition()/1000;
                final int finalCurrentPosition = currentPosition;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.updateLoadingBar(progress, finalCurrentPosition);
                    }
                });

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}