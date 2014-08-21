package voxar.cin.ufpe.br.whatsthesong.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import voxar.cin.ufpe.br.whatsthesong.R;

public class FragmentTestActivity extends Activity {

    /**
     * Used for "what" parameter to handler messages
     */
    final static int MSG_WHAT = ('F' << 16) + ('T' << 8) + 'A';
    final static int MSG_SHOW_DIALOG = 1;

    int value = 1;

    final static class State extends Fragment {

        static final String TAG = "State";
        /**
         * Handler for this activity
         */
        public ConcreteTestHandler handler = new ConcreteTestHandler();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onResume() {
            super.onResume();

            handler.setActivity(getActivity());
            handler.resume();
        }

        @Override
        public void onPause() {
            super.onPause();

            handler.pause();
        }

        public void onDestroy() {
            super.onDestroy();
            handler.setActivity(null);
        }
    }

    /**
     * 2 second delay
     */
    final static int DELAY = 2000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_search_view);

        if (savedInstanceState == null) {
            final Fragment state = new State();
            final FragmentManager fm = getFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            ft.add(state, State.TAG);
            ft.commit();
        }

        final Button button = (Button) findViewById(R.id.horizontalScrollView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FragmentManager fm = getFragmentManager();
                State fragment = (State) fm.findFragmentByTag(State.TAG);
                if (fragment != null) {
                    // Send a message with a delay onto the message looper
                    fragment.handler.sendMessageDelayed(
                            fragment.handler.obtainMessage(MSG_WHAT, MSG_SHOW_DIALOG, value++),
                            DELAY);
                }
            }
        });
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    /**
     * Simple test dialog fragment
     */
    public static class TestDialog extends DialogFragment {

        int value;

        /**
         * Fragment Tag
         */
        final static String TAG = "TestDialog";

        public TestDialog() {
        }

        public TestDialog(int value) {
            this.value = value;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View inflatedView = inflater.inflate(R.layout.abc_action_bar_decor, container, false);
            TextView text = (TextView) inflatedView.findViewById(R.id.music1);
            text.setText(getString(R.string.score, value));
            return inflatedView;
        }
    }

    /**
     * Message Handler class that supports buffering up of messages when the
     * activity is paused i.e. in the background.
     */
    static class ConcreteTestHandler extends PauseHandler {

        /**
         * Activity instance
         */
        protected Activity activity;

        /**
         * Set the activity associated with the handler
         *
         * @param activity
         *            the activity to set
         */
        final void setActivity(Activity activity) {
            this.activity = activity;
        }

        @Override
        final protected boolean storeMessage(Message message) {
            // All messages are stored by default
            return true;
        };

        @Override
        final protected void processMessage(Message msg) {

            final Activity activity = this.activity;
            if (activity != null) {
                switch (msg.what) {

                    case MSG_WHAT:
                        switch (msg.arg1) {
                            case MSG_SHOW_DIALOG:
                                final FragmentManager fm = activity.getFragmentManager();
                                final TestDialog dialog = new TestDialog(msg.arg2);

                                // We are on the UI thread so display the dialog
                                // fragment
                                dialog.show(fm, TestDialog.TAG);
                                break;
                        }
                        break;
                }
            }
        }
    }
}
