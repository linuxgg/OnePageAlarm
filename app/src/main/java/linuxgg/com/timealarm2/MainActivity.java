package linuxgg.com.timealarm2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import linuxgg.com.timealarm2.views.RoundProgressBar;
import linuxgg.com.timealarm2.views.TimerBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    private int countTime;

    public static final int PROGRESS_TAG = 0;
    private final int SECOND_PROGRESS_TAG = 1;
    private AlertDialog settingDialog;
    private ProgressBar timer_progress;
    private RoundProgressBar roundProgress;
    private TimerBroadcastReceiver timerBroadcastReceiver;
    private Button settings, clear;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case PROGRESS_TAG:
                    if (countTime == 0) return;
                    int textTime = msg.getData().getInt(TimerService.TIMERSERVICE_TIMELEFT);
                    int currentProgress = (countTime - textTime);

                    if (textTime == 0) {
                        resetCountDone(false);
                        showTimeoutDialog();
                    } else {

                        roundProgress.setProgress(currentProgress % countTime);
                        if (textTime > 60) {
                            roundProgress.setText((textTime / 60) + ":" + (textTime % 60));
                        } else {
                            roundProgress.setText("" + (textTime % 60));
                        }
                    }
                    break;
                case SECOND_PROGRESS_TAG:
                    break;
                default:

                    break;
            }
        }
    };

    private void showTimeoutDialog() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Warning!!!")
                .setMessage("Time out!!!")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetCountDone(true);
                    }
                })
                .show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        timerBroadcastReceiver = new TimerBroadcastReceiver(handler);

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TimerService.TAG);
        registerReceiver(timerBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void initView() {

        timer_progress = (ProgressBar) findViewById(R.id.timer_progress);
        roundProgress = (RoundProgressBar) findViewById(R.id.roundProgress);
        resetCountDone(true);
        settings = (Button) findViewById(R.id.set);
        clear = (Button) findViewById(R.id.clear);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
                settingDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Setting time")
                        .setView(dialogView)
                        .setCancelable(false)
                        .show();
                final NumberPicker dialog_h_picker = (NumberPicker) dialogView.findViewById(R.id.dialog_h_picker);
                dialog_h_picker.setMaxValue(100);
                dialog_h_picker.setMinValue(0);
                final NumberPicker dialog_m_picker = (NumberPicker) dialogView.findViewById(R.id.dialog_m_picker);
                dialog_m_picker.setMaxValue(300);
                dialog_m_picker.setMinValue(0);
                Button cancel = (Button) dialogView.findViewById(R.id.dialog_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (settingDialog != null && settingDialog.isShowing()) {
                            settingDialog.dismiss();
                            settingDialog = null;
                        }
                    }
                });
                Button done = (Button) dialogView.findViewById(R.id.dialog_done);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetCountDone(false);
                        countTime = dialog_m_picker.getValue() * 10;


                        if (settingDialog != null && settingDialog.isShowing()) {
                            settingDialog.dismiss();
                            settingDialog = null;
                        }

                        if (dialog_m_picker.getValue() != 0) {
                            roundProgress.setMax(countTime);
                            Intent i = new Intent(MainActivity.this
                                    , TimerService.class);
                            i.putExtra(TimerService.TAG, countTime);
                            startService(i);
                        }


                    }
                });

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetCountDone(true);

            }
        });

    }

    private void resetCountDone(boolean needStopService) {
        if (needStopService) {
            Intent i = new Intent(MainActivity.this, TimerService.class);
            stopService(i);
        }
        handler.removeMessages(PROGRESS_TAG);
        roundProgress.setProgress(0);
        roundProgress.setText("" + 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerBroadcastReceiver != null) {
            unregisterReceiver(timerBroadcastReceiver);
        }
    }
}
