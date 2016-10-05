package linuxgg.com.timealarm2;

import android.app.Service;
import android.content.Intent;
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
    private int timeLeft = 6;
    Timer t;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            onDestroy();
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.cancel();
        Toast.makeText(this, "MusicService onDestroy()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
