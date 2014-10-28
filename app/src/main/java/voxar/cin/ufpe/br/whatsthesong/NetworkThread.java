package voxar.cin.ufpe.br.whatsthesong;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
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
    ProgressDialog nDialog;

    public NetworkThread(QuizScreenActivity activity) {
        mActivity = activity;
        index = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        nDialog = new ProgressDialog(mActivity, R.style.Spinner);
        nDialog.setCancelable(false);
        nDialog.show();

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
            nDialog.dismiss();
        }

        final Song finalSong = aSong;

        TextView txtView;
        int id;
        final int size = finalSong.getUrls().size();

        try {
            ArrayList<String[]> options = finalSong.getOptions();
            String option[];
            for (int i = 1; i <= 4; i++) {
                option = options.get(i - 1);

                SpannableString span = new SpannableString(option[0]);
                span.setSpan(new RelativeSizeSpan(5f), 0, option[0].length(), 0);

                id = R.id.class.getField("button" + i).getInt(0);
                txtView = (TextView) mActivity.findViewById(id);
                txtView.setText(span + "\n" + option[1]);

//                id = R.id.class.getField("artist" + i).getInt(0);
//                txtView = (TextView) mActivity.findViewById(id);
//                txtView.setText(options.get(i - 1)[1]);
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
