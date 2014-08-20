package voxar.cin.ufpe.br.whatsthesong;

import java.util.ArrayList;

/**
 * Created by Dicksson on 8/6/2014.
 */
public class Song {

    private int answer;
    private int duration;
    private ArrayList<String> options;
    private ArrayList<String> urls;
    private ArrayList<Integer> indexes;

    public Song (){
        options = new ArrayList<String>();
        urls = new ArrayList<String>();
        indexes = new ArrayList<Integer>();
    }

    public int getAnswer() {
        return answer;
    }
    public void setAnswer(int answer) {
        this.answer = answer;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public ArrayList<String> getOptions() {
        return options;
    }
    public ArrayList<String> getUrls() {
        return urls;
    }
    public ArrayList<Integer> getIndexes() {
        return indexes;
    }

}
