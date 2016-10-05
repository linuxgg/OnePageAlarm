package linuxgg.com.timealarm2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
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
    private int timeLeft = 0;
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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Toast.makeText(this, "MusicService onCreate()", Toast.LENGTH_SHORT).show();

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
            mp = MediaPlayer.create(this, R.raw.bongo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MediaPlayer mp = new MediaPlayer();

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.cancel();
        mp.release();
        Toast.makeText(this, "MusicService onDestroy()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
