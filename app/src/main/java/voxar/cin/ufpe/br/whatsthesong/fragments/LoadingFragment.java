package voxar.cin.ufpe.br.whatsthesong.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import voxar.cin.ufpe.br.whatsthesong.R;

/**
 * Created by Dicksson on 7/31/2014.
 */
public class LoadingFragment extends DialogFragment {

    public final static String TAG = "TestDialog";

    public static DialogFragment newInstance() {
        LoadingFragment myFragment = new LoadingFragment();
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }
}
