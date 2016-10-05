package linuxgg.com.timealarm2.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import linuxgg.com.timealarm2.MainActivity;
import linuxgg.com.timealarm2.TimerService;

/**
 * Created by tom on 2016/10/5.<p>
 * Version 1.0 <p>
 * Desc :    <p>
 */
public class TimerBroadcastReceiver extends BroadcastReceiver {
    Handler handler;

    public TimerBroadcastReceiver(Handler handler) {
        this.handler = handler;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TimerService.TAG)) {
            Message msg = new Message();
            msg.what = MainActivity.PROGRESS_TAG;
            Bundle bd = new Bundle();
            bd.putInt(TimerService.TIMERSERVICE_TIMELEFT, intent.getIntExtra(TimerService.TIMERSERVICE_TIMELEFT, 0));
            msg.setData(bd);
            handler.sendMessage(msg);
        }
    }
}
