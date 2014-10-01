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
    String instrument[] = {"", "drum", "bass", "keyboard", "sax", "guitar", "voice"};
    QuizScreenActivity mActivity;
    public boolean running = true;
    static int pos;
    int size, index, instrumentIndex;

    public ProgressBar(QuizScreenActivity mActivity, MediaPlayer mp, int index, int instrumentIndex, int size) throws Exception {
        this.mActivity = mActivity;
        this.mp = mp;
        this.instrumentIndex = instrumentIndex;
        this.size = size;
        this.index = index;
    }

    @Override
    public void run() {
        pos = ((mActivity.findViewById(R.id.loadingBarFrame).getLayoutParams().width)/size) * index;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.updateLoadingBar(instrument[instrumentIndex - 1], pos, mActivity.findViewById(R.id.loadingBarFrame).getLayoutParams().height);
            }
        });

        while (running && mp != null) {
            try {
                pos += (1000 * mActivity.findViewById(R.id.loadingBarFrame).getLayoutParams().width)/(size * mp.getDuration());
                final int finalCurrentPosition = pos;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.updateLoadingBar(instrument[instrumentIndex], finalCurrentPosition, mActivity.findViewById(R.id.loadingBarFrame).getLayoutParams().height);
                    }
                });

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (instrumentIndex == 6) {
            pos = ((mActivity.findViewById(R.id.loadingBarFrame).getLayoutParams().width) / size) * (index + 1);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.updateLoadingBar(instrument[instrumentIndex], pos, mActivity.findViewById(R.id.loadingBarFrame).getLayoutParams().height);
                }
            });
        }

    }
}