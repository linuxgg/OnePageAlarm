package linuxgg.com.timealarm2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import linuxgg.com.timealarm2.views.RoundProgressBar;

public class MainActivity extends AppCompatActivity {

    private final int MAX = 60;

    private final int PROGRESS_TAG = 0;
    private final int SECOND_PROGRESS_TAG = 1;
    private ProgressBar timer_progress;
    private RoundProgressBar roundProgress;
    private Button settings, pause;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS_TAG:
                    int currentProgress = (roundProgress.getProgress() + 5);
                    if(currentProgress == MAX){
                        roundProgress.setProgress(  MAX);
                        roundProgress.setText(  MAX + "");
                    }else{

                        roundProgress.setProgress(currentProgress % MAX);
                        roundProgress.setText(currentProgress % MAX + "");
                    }
                    progressGrow();
                    break;
                case SECOND_PROGRESS_TAG:
                    break;
                default:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        timer_progress = (ProgressBar) findViewById(R.id.timer_progress);
        roundProgress = (RoundProgressBar) findViewById(R.id.roundProgress);
        settings = (Button) findViewById(R.id.set);
        pause = (Button) findViewById(R.id.pause);

        roundProgress.setProgress(0);
        roundProgress.setMax(MAX);
        roundProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressGrow();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(PROGRESS_TAG);
            }
        });

    }

    private void progressGrow() {
        handler.sendEmptyMessageDelayed(PROGRESS_TAG, 1000);
    }
}
