package voxar.cin.ufpe.br.whatsthesong.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import voxar.cin.ufpe.br.whatsthesong.NetworkThread;
import voxar.cin.ufpe.br.whatsthesong.R;
import voxar.cin.ufpe.br.whatsthesong.utils.OnSwipeTouchListener;
import voxar.cin.ufpe.br.whatsthesong.utils.Typefaces;

/**
 * Created by Dicksson on 8/1/2014.
 */

public class QuizScreenActivity extends FragmentActivity {

    NetworkThread nt;
    public static int SCORE = 0;
    public int width;
    public int height;
    final int DEVICE_SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_screen);

        findViewById(R.id.horizontalScrollView).setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if (nt.index + 1 != nt.aSong.getUrls().size()) {
                    nt.player.seekTo(nt.player.getDuration());
                    findViewById(R.id.horizontalScrollView).setOnTouchListener(this);
                }
            }
        });

        Typeface tf = Typefaces.get(getApplicationContext(), "RobotoCondensed-Bold.ttf");
        TextView tv;
        Button bt;
        try {
            int id;
            for (int i = 1; i <= 2; i++) {
                id = R.id.class.getField("score" + i).getInt(0);
                tv = (TextView) findViewById(id);
                tv.setTypeface(tf);
            }

            tf = Typefaces.get(getApplicationContext(), "RobotoCondensed-Light.ttf");

            for (int i = 1; i <= 4; i++) {
                id = R.id.class.getField("button" + i).getInt(0);
                bt = (Button) findViewById(id);
                bt.setTypeface(tf);

//                id = R.id.class.getField("artist" + i).getInt(0);
//                bt = (Button) findViewById(id);
//                bt.setTypeface(tf);
            }

            final ImageView view = (ImageView) findViewById(R.id.loadingBar);

            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    width = view.getWidth();
                    height = view.getHeight();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nt == null) {
            nt = new NetworkThread(this);
            nt.execute(this.getFilesDir());
        } else if (nt.isCancelled()) {
            clear();
            nt = new NetworkThread(this);
            nt.execute(this.getFilesDir());
        } else if(nt.player.getCurrentPosition() < nt.player.getDuration()) {
            nt.player.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("EXIT", "from onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (nt.player.isPlaying()) {
            nt.player.pause();
        }
    }

    @Override
    public void onBackPressed() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizScreenActivity.this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                clear();
                finish();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // 3. Show the dialog
        builder.create().show();
    }

    public void onOptionClicked(View v) {
        nt.thread.running = false;

        int id = v.getId(), answer = 0;
        boolean result = false;

        switch (id) {
            case R.id.button1:
                answer = 1;
                break;

            case R.id.button2:
                answer = 2;
                break;

            case R.id.button3:
                answer = 3;
                break;

            case R.id.button4:
                answer = 4;
                break;
        }

        if (answer == nt.aSong.getAnswer()) {
            double b1 = nt.aSong.getDuration();
            double b2 = nt.aSong.getIndexes().size();
            double b3 = nt.index;
            double b4 = nt.player.getCurrentPosition() / 1000;

            double roundScore = ((1 - ((b3 * b1) + b4) / (b1 * b2)) * 100);

            if (roundScore > 0) SCORE += roundScore;

            TextView tv = (TextView) findViewById(R.id.score2);
            tv.setText(String.valueOf(SCORE));

            result = true;
        }

        nt.player.release();
        nt.cancel(true);

        Intent intent = new Intent(this, ResultScreenActivity.class);
        intent.putExtra("RESULT", result);
        startActivity(intent);

    }

    public void animateInstrument(int instrumentIndex, int size, int index) {
        ImageView instrument;
        try {
            instrument = (ImageView) findViewById(R.id.class.getField("imageView" + instrumentIndex).getInt(0));
            AnimationSet animationSet = new AnimationSet(true);

            RotateAnimation r = new RotateAnimation(0.0f, -360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            r.setDuration(1000);
            animationSet.addAnimation(r);

            TranslateAnimation a = new TranslateAnimation(
                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, (- (DEVICE_SCREEN_WIDTH - (40 * (6 - size))) + index * 40),
                    Animation.ABSOLUTE,0, Animation.ABSOLUTE,0);
            a.setDuration(1000);
            animationSet.addAnimation(a);

            animationSet.setFillAfter(true);
            instrument.startAnimation(animationSet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateLoadingBar(String instrument, int width, int height) {
        if (!instrument.isEmpty()) {
            try {
                ImageView bar = (ImageView) findViewById(R.id.class.getField("loading_bar_" + instrument).getInt(0));
                bar.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clear() {

        //Delete tracks of previous round
        File dir = getFilesDir();
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                Log.d("DELETED", "" + new File(dir, children[i]).delete());
            }
        }

        FrameLayout frame;
        String instruments[] = {"drum", "bass", "keyboard", "sax", "guitar", "voice"};

        //Reset everything to the starting state
        frame = (FrameLayout) findViewById(R.id.instrument_frame);
        for (int i = 1; i <= 6; i++) {
            frame.getChildAt(i).clearAnimation();
        }

        frame = (FrameLayout) findViewById(R.id.loadingBarFrame);
        for (int i = 0; i <= 5; i++) {
            updateLoadingBar(instruments[i], 0, frame.getLayoutParams().height);
        }

        int id;
        Button button;
        try {
            for (int i = 1; i <= 4; i++) {
                id = R.id.class.getField("button" + i).getInt(0);
                button = (Button) findViewById(id);
                button.setText("");

//                id = R.id.class.getField("artist" + i).getInt(0);
//                txtView = (TextView) findViewById(id);
//                txtView.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}