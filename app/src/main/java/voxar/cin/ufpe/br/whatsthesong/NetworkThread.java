package voxar.cin.ufpe.br.whatsthesong;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import voxar.cin.ufpe.br.whatsthesong.activities.QuizScreenActivity;
import voxar.cin.ufpe.br.whatsthesong.fragments.LoadingFragment;
import voxar.cin.ufpe.br.whatsthesong.utils.ProgressBar;

/**
 * Created by Dicksson on 8/1/2014.
 */

public class NetworkThread extends AsyncTask<File, Integer, Song> {

    QuizScreenActivity mActivity;
    public int index;
    public MediaPlayer player;
    public Song aSong;
    public ProgressBar thread;

    public NetworkThread(QuizScreenActivity activity) {
        mActivity = activity;
        index = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        DialogFragment dialogFragment = LoadingFragment.newInstance();
        dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialogFragment.setCancelable(false);
        ft.add(dialogFragment, LoadingFragment.TAG);
        ft.commitAllowingStateLoss();

    }

    @Override
    protected Song doInBackground(File... directory) {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            SAXHandler handler = new SAXHandler();
            saxParser.parse(new InputSource(new URL("http://www.cin.ufpe.br/~jmxnt/voxarmusic").openStream()), handler);

            aSong = handler.trackList;
            ArrayList<String> urls = aSong.getUrls();

            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet;
            HttpResponse response;
            HttpEntity entity;

            for (int i = 0; i < urls.size(); i++) {
                httpGet = new HttpGet(urls.get(i));
                response = client.execute(httpGet);
                entity = response.getEntity();
                byte content[] = EntityUtils.toByteArray(entity);

                FileOutputStream outputStream = new FileOutputStream(new File(directory[0], "track" + i + ".mp3"));
                outputStream.write(content);
                outputStream.close();

                Log.d("joma", "track" + i + " saved.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return aSong;
    }

    @Override
    protected void onPostExecute(Song aSong) {
        super.onPostExecute(aSong);

        if ((mActivity != null) && (!mActivity.isFinishing())) {
            FragmentManager fm = mActivity.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            LoadingFragment dialog = (LoadingFragment) fm.findFragmentByTag(LoadingFragment.TAG);
            ft.remove(dialog);
            ft.commitAllowingStateLoss();
        }

        final Song finalSong = aSong;

        TextView txtView;
        int id;
        final int size = finalSong.getUrls().size();

        try {
            ArrayList<String[]> options = finalSong.getOptions();

            for (int i = 1; i <= 4; i++) {

                id = R.id.class.getField("music" + i).getInt(0);
                txtView = (TextView) mActivity.findViewById(id);
                txtView.setText(options.get(i - 1)[0]);

                id = R.id.class.getField("artist" + i).getInt(0);
                txtView = (TextView) mActivity.findViewById(id);
                txtView.setText(options.get(i - 1)[1]);
            }

            options.clear();

            player = MediaPlayer.create(mActivity, Uri.parse(mActivity.getFilesDir() + "/track" + index + ".mp3"));
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    thread.running = false;
                    index++;

                    try {
                        if (index < size) {
                            mActivity.animateInstrument(finalSong.getIndexes().get(index), size, index);

                            player.reset();
                            player.setDataSource(mActivity, Uri.parse(mActivity.getFilesDir() + "/track" + index + ".mp3"));
                            player.prepare();
                            player.start();
                            player.setOnCompletionListener(this);

                            thread = new ProgressBar(mActivity, player, index, finalSong.getIndexes().get(index), size);
                            thread.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            mActivity.animateInstrument(finalSong.getIndexes().get(index), size, index);
            thread = new ProgressBar(mActivity, player, index, finalSong.getIndexes().get(index), size);
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
