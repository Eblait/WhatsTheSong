package voxar.cin.ufpe.br.whatsthesong.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import voxar.cin.ufpe.br.whatsthesong.R;
import voxar.cin.ufpe.br.whatsthesong.utils.Typefaces;

public class ResultScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);

        Typeface tf = Typefaces.get(getApplicationContext(), "RobotoCondensed-Bold.ttf");
        TextView tv = (TextView) findViewById((R.id.text_result));
        boolean result = this.getIntent().getBooleanExtra("RESULT", false);

        if (result) {
            tv.setText("CONGRATS!");
            tv.setTextColor(Color.parseColor("#D2CF42"));
        } else {
            tv.setText("WRONG!");
            tv.setTextColor(Color.parseColor("#DE455F"));
        }

        tv.setTypeface(tf);

    }

    public void onClickReturn(View v) {
        finish();
    }

}
