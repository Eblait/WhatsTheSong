package voxar.cin.ufpe.br.whatsthesong.utils;

import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

import voxar.cin.ufpe.br.whatsthesong.R;
import voxar.cin.ufpe.br.whatsthesong.activities.QuizScreenActivity;

/**
 * Created by Dicksson on 8/22/2014.
 */

public class ProgressBar extends Thread {

    MediaPlayer mp;
    String instrument;
    QuizScreenActivity mActivity;
    public boolean running = true;
    int pos, size;

    public ProgressBar(QuizScreenActivity mActivity, MediaPlayer mp, int index, int size) throws Exception {
        this.mActivity = mActivity;
        this.mp = mp;

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

        this.size = size;
        pos = ((mActivity.findViewById(R.id.loadingBarFrame).getLayoutParams().width)/size * mp.getDuration()/1000) * (index - 1);
    }

    @Override
    public void run() {
        int currentPosition;

        while (running && mp != null) {
            try {
                currentPosition = pos + (mp.getCurrentPosition()/1000);
                final int finalCurrentPosition = currentPosition;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.updateLoadingBar(instrument, finalCurrentPosition, mActivity.findViewById(R.id.loadingBarFrame).getLayoutParams().height);
                    }
                });

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}