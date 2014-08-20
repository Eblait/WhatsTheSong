package voxar.cin.ufpe.br.whatsthesong.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import voxar.cin.ufpe.br.whatsthesong.R;

/**
 * Created by Dicksson on 7/31/2014.
 */
public class TitleScreenActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title_screen);
    }

    public void play(View v) {
        Intent intent = new Intent(this, QuizScreenActivity.class);
        startActivity(intent);
    }
}
