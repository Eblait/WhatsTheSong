package voxar.cin.ufpe.br.whatsthesong;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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

import voxar.cin.ufpe.br.whatsthesong.fragments.LoadingFragment;

/**
 * Created by Dicksson on 8/1/2014.
 */

public class NetworkThread extends AsyncTask<File, Integer, Song> {

    public int index;
    FragmentActivity mActivity;
    public MediaPlayer player;
    public Song aSong;
    FrameLayout frame;

    public NetworkThread(FragmentActivity activity) {
        mActivity = activity;
        index = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        frame = (FrameLayout) mActivity.findViewById(R.id.frame);
        Log.d("CHILD COUNT", "" + frame.getChildCount());

        for (int i = 1; i <= 6; i++) {
            frame.getChildAt(i).clearAnimation();
        }
        frame.clearAnimation();
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

        final Song finalSong = aSong;

        TextView txtView;
        int id;
        final int size = finalSong.getUrls().size();

        try {
            ArrayList<String> options = finalSong.getOptions();

            for (int i = 1; i <= 4; i++) {
                id = R.id.class.getField("music" + i).getInt(0);
                txtView = (TextView) mActivity.findViewById(id);
                txtView.setText(options.get(i - 1));
            }

            options.clear();

            player = MediaPlayer.create(mActivity, Uri.parse(mActivity.getFilesDir() + "/track" + index + ".mp3"));
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mActivity.deleteFile("track" + index + ".mp3");
                    index++;
                    int id;

                    try {
                        if (index < size) {
                            id = R.id.class.getField("imageView" + finalSong.getIndexes().get(index)).getInt(0);
                            ImageView instrument = (ImageView) mActivity.findViewById(id);
                            AnimationSet animationSet = new AnimationSet(true);

                            RotateAnimation r = new RotateAnimation(0.0f, -360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            r.setDuration(1000);
                            animationSet.addAnimation(r);

                            TranslateAnimation a = new TranslateAnimation(
                                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, (- (620 - (40 * (6 - size))) + index * 40),
                                    Animation.ABSOLUTE,0, Animation.ABSOLUTE,0);
                            a.setDuration(1000);
                            animationSet.addAnimation(a);

                            animationSet.setFillAfter(true);
                            instrument.startAnimation(animationSet);

                            player.reset();
                            player.setDataSource(mActivity, Uri.parse(mActivity.getFilesDir() + "/track" + index + ".mp3"));
                            player.prepare();
                            player.start();
                            player.setOnCompletionListener(this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ImageView instrument = (ImageView) mActivity.findViewById(R.id.class.getField("imageView" + finalSong.getIndexes().get(index)).getInt(0));
            AnimationSet animationSet = new AnimationSet(true);

            RotateAnimation r = new RotateAnimation(0.0f, -360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            r.setDuration(1000);
            animationSet.addAnimation(r);

            TranslateAnimation a = new TranslateAnimation(
                    Animation.ABSOLUTE,0, Animation.ABSOLUTE, (- (620 - (40 * (6 - size))) + index * 40),
                    Animation.ABSOLUTE,0, Animation.ABSOLUTE,0);
            a.setDuration(1000);
            animationSet.addAnimation(a);

            animationSet.setFillAfter(true);
            instrument.startAnimation(animationSet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        player.reset();
    }
}
