package voxar.cin.ufpe.br.whatsthesong.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import voxar.cin.ufpe.br.whatsthesong.NetworkThread;
import voxar.cin.ufpe.br.whatsthesong.R;
import voxar.cin.ufpe.br.whatsthesong.utils.OnSwipeTouchListener;
import voxar.cin.ufpe.br.whatsthesong.utils.Typefaces;

/**
 * Created by Dicksson on 8/1/2014.
 */
public class QuizScreenActivity extends FragmentActivity {

    NetworkThread nt;

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
        try {
            int id;
            for (int i = 1; i <= 2; i++) {
                id = R.id.class.getField("score" + i).getInt(0);
                tv = (TextView) findViewById(id);
                tv.setTypeface(tf);
            }

            tf = Typefaces.get(getApplicationContext(), "RobotoCondensed-Light.ttf");

            for (int i = 1; i <= 4; i++) {
                id = R.id.class.getField("music" + i).getInt(0);
                tv = (TextView) findViewById(id);
                tv.setTypeface(tf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nt == null || nt.isCancelled()) {
            nt = new NetworkThread(this);
            nt.execute(this.getFilesDir());
        } else if (nt.player.isPlaying()){
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
        Log.d("EXIT", "from onStop");
    }

    public void onOptionClicked(View v) {
        nt.cancel(true);
        Log.d("THREAD STILL LIVES", "" + nt.aSong.getDuration());

        int id = v.getId(), answer = 0;
        boolean result = false;

        switch (id) {
            case R.id.option1:
                answer = 1;
                break;

            case R.id.option2:
                answer = 2;
                break;

            case R.id.option3:
                answer = 3;
                break;

            case R.id.option4:
                answer = 4;
                break;
        }

        if (answer == nt.aSong.getAnswer()) {
            TextView tv = (TextView) findViewById(R.id.score2);
            String txtScore = tv.getText().toString();
            int intScore = Integer.parseInt(txtScore);

            intScore += 1000;

            tv.setText(String.valueOf(intScore));

            result = true;
        }

        Intent intent = new Intent(this, ResultScreenActivity.class);
        intent.putExtra("RESULT", result);
        startActivity(intent);
    }

}
