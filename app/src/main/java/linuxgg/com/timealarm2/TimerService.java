package linuxgg.com.timealarm2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tom on 2016/10/5.<p>
 * Version 1.0 <p>
 * Desc :    <p>
 */
public class TimerService extends Service {
    public static final String TAG = TimerService.class.getSimpleName();
    public static final String TIMERSERVICE_TIMELEFT = "TIMERSERVICE_TIMELEFT";
    public static final String TIMERSERVICE_RECORD_PATH = "TIMERSERVICE_RECORD_PATH";
    private int timeLeft = 0;
    private String filePath;
    Timer t;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mp.start();
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeLeft = intent.getIntExtra(TAG, 0);
        filePath = intent.getStringExtra(TIMERSERVICE_RECORD_PATH);


        t = new Timer("timer");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    Intent i = new Intent(TAG);
                    i.putExtra(TIMERSERVICE_TIMELEFT, timeLeft);
                    sendBroadcast(i);
                    Log.d(TAG, timeLeft + "");
                } else {
                    handler.sendEmptyMessage(0);
                }
            }
        }, 0, 1000);
        try {
            if (TextUtils.isEmpty(filePath)) {
                mp = MediaPlayer.create(this, R.raw.bongo);
            } else {
                mp = new MediaPlayer();
                mp.setDataSource(filePath);
                mp.prepare();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    PowerManager.WakeLock wakeLock = null;

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TimerService.class.getName());
        wakeLock.acquire();

    }

    MediaPlayer mp = new MediaPlayer();

    @Override
    public void onDestroy() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        super.onDestroy();
        t.cancel();
        mp.release();
        Toast.makeText(this, R.string.alarm_cancel, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
