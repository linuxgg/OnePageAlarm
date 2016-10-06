package linuxgg.com.timealarm2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import linuxgg.com.timealarm2.utils.FileUtils;
import linuxgg.com.timealarm2.views.RoundProgressBar;
import linuxgg.com.timealarm2.views.TimerBroadcastReceiver;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_TIME = 60;
    private int countTime;

    public static final int PROGRESS_TAG = 0;
    private final int SECOND_PROGRESS_TAG = 1;
    private AlertDialog settingDialog;
    private ProgressBar timer_progress;
    private RoundProgressBar roundProgress;
    private TimerBroadcastReceiver timerBroadcastReceiver;
    private Button settings, clear;
    private final static String RECORD_PATH = "/records";
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
                .setTitle(R.string.warning)
                .setMessage(R.string.time_out)
                .setCancelable(false)
                .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
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
        delRecords();
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
                        .setTitle(R.string.set_time)
                        .setView(dialogView)
                        .setCancelable(false)
                        .show();
                final NumberPicker dialog_h_picker = (NumberPicker) dialogView.findViewById(R.id.dialog_h_picker);
                dialog_h_picker.setMaxValue(100);
                dialog_h_picker.setMinValue(0);
                final NumberPicker dialog_m_picker = (NumberPicker) dialogView.findViewById(R.id.dialog_m_picker);
                dialog_m_picker.setMaxValue(300);
                dialog_m_picker.setMinValue(0);
                final Button cancel = (Button) dialogView.findViewById(R.id.dialog_cancel);
                final Button done = (Button) dialogView.findViewById(R.id.dialog_done);
                final Button replay = (Button) dialogView.findViewById(R.id.dialog_replay);
                final ImageButton recordStartButton = (ImageButton) dialogView.findViewById(R.id.button_record2);
                final ImageButton recordStopButton = (ImageButton) dialogView.findViewById(R.id.button_record3);


                final File folder = new File(getCacheDir().getAbsolutePath() + RECORD_PATH);
                if (!folder.exists()) {
                    folder.mkdirs();
                }


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (settingDialog != null && settingDialog.isShowing()) {
                            settingDialog.dismiss();
                            settingDialog = null;
                        }
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mediaRecorder.release();
                        resetCountDone(false);
                        countTime = dialog_m_picker.getValue() * DEFAULT_TIME;


                        if (settingDialog != null && settingDialog.isShowing()) {
                            settingDialog.dismiss();
                            settingDialog = null;
                        }

                        if (dialog_m_picker.getValue() != 0) {
                            roundProgress.setMax(countTime);
                            Intent i = new Intent(MainActivity.this
                                    , TimerService.class);
                            i.putExtra(TimerService.TAG, countTime);
                            i.putExtra(TimerService.TIMERSERVICE_RECORD_PATH, mFileName);
                            startService(i);
                        }


                    }
                });

                replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("", "play button clicked " + mFileName);
                        if (TextUtils.isEmpty(mFileName)) {
                            Toast.makeText(MainActivity.this, R.string.no_record, Toast.LENGTH_SHORT).show();
                        } else {
                            final MediaPlayer mp = new MediaPlayer();
                            try {

                                recordStartButton.setEnabled(false);
                                recordStopButton.setEnabled(false);
                                cancel.setEnabled(false);
                                replay.setEnabled(false);
                                done.setEnabled(false);


                                mp.setDataSource(mFileName);
                                mp.prepare();
                                mp.start();
                                replay.setText(R.string.playing);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        mp.stop();
                                        mp.release();
                                        replay.setText(R.string.replay);

                                        recordStartButton.setEnabled(true);
                                        recordStopButton.setEnabled(true);
                                        cancel.setEnabled(true);
                                        replay.setEnabled(true);
                                        done.setEnabled(true);
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });


                recordStartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("", "start record");
                        try {

                            recordStartButton.setVisibility(View.GONE);
                            recordStartButton.setEnabled(false);
                            recordStopButton.setVisibility(View.VISIBLE);
                            recordStopButton.setEnabled(true);
                            cancel.setEnabled(false);
                            replay.setEnabled(false);
                            done.setEnabled(false);


                            mediaRecorder = new MediaRecorder();
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                            mFileName = folder.getAbsolutePath() + "/" + System.currentTimeMillis() + ".3gp";
                            mediaRecorder.setOutputFile(mFileName);
                            mediaRecorder.prepare();
                            mediaRecorder.start();

                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                });

                recordStopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("", "stop record");
                        try {
                            mediaRecorder.setOnErrorListener(null);
                            mediaRecorder.stop();

                            recordStartButton.setVisibility(View.VISIBLE);
                            recordStartButton.setEnabled(true);
                            recordStopButton.setVisibility(View.GONE);
                            recordStopButton.setEnabled(false);
                            cancel.setEnabled(true);
                            replay.setEnabled(true);
                            done.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.clear_warning)
                        .setCancelable(false)
                        .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resetCountDone(true);
                            }
                        })
                        .setNegativeButton(R.string.string_cancel, null)
                        .show();


            }
        });


    }

    private void delRecords() {
        FileUtils.deleteFile(getCacheDir().getAbsolutePath() + RECORD_PATH);
    }

    private String mFileName;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;

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
