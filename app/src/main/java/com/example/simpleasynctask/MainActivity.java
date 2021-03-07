package com.example.simpleasynctask;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView, addAnswerTV, addCountTV;
    private ProgressBar mProgress;
    private Button addButton;
    String mWait;
    int addCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView1);
        mProgress = findViewById(R.id.progressBar);
        addAnswerTV = findViewById(R.id.answer);
        addCountTV = findViewById(R.id.addCount);
        addButton = findViewById(R.id.addButton);
    }

    public void addNumbers (View view) {
        Random n = new Random();
        Random o = new Random();
        int x = n.nextInt(100) + o.nextInt(100);
        String a = String.valueOf(x);
        addAnswerTV.setText(a);
        addCount++;
    }

    public void startTask(View view) {
        addCount = 0;
        Random r = new Random();
        int n = r.nextInt(11);
        int s = n * 2000;
        mWait = String.valueOf(s);

        Resources res = getResources();
        String tText = String.format(res.getString(R.string.napping), mWait);
        mTextView.setText(tText);
        SimpleAsyncTask task = new SimpleAsyncTask(this);
        task.execute(mWait);
    }

    private static class SimpleAsyncTask extends AsyncTask<String, Integer, String> {
        private WeakReference<MainActivity> activityWeakReference;

        SimpleAsyncTask(MainActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(String... params) {
            int s = Integer.parseInt(params[0]);
            try {
                //Thread.sleep(s);
                for (int i = 0; i <= 10; i++) {
                    Thread.sleep(s/100);
                    publishProgress(i * 10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Awake at last after sleeping for " + s + " milliseconds!";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.mProgress.setVisibility(View.VISIBLE);
            activity.addButton.setVisibility(View.VISIBLE);
            activity.addAnswerTV.setVisibility(View.VISIBLE);
            activity.addCount = 0;
            activity.addCountTV.setText("");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            MainActivity activity =  activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.mProgress.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.mTextView.setText(result);
            int i = activity.addCount;
            String s = String.format(Locale.US, "Add executed %d times", i);
            activity.addCountTV.setText(s);
            activity.mProgress.setVisibility(View.INVISIBLE);
            activity.addAnswerTV.setVisibility(View.INVISIBLE);
            activity.addButton.setVisibility(View.INVISIBLE);
        }
    }
}