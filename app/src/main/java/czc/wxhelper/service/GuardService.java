package czc.wxhelper.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.blankj.utilcode.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import czc.wxhelper.R;
import czc.wxhelper.activity.MainActivity;
import czc.wxhelper.constant.Const;
import czc.wxhelper.event.EasyEvent;
import czc.wxhelper.event.EventType;
import czc.wxhelper.manager.FloatWindowManager;
import czc.wxhelper.util.PreferenceHelper;
import czc.wxhelper.util.MyAppUtil;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 守护服务
 */
public class GuardService extends Service {

    private Subscription mSubscribe;
    private static final int SERVICE_ID = 10010;

    private static final String CHANNEL_ID_SPORT = "121312";

    public GuardService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void jumpToWxAfterServiceConnect(EasyEvent event) {
        startActivity(new Intent(this, MainActivity.class));
        if (event.type == EventType.TYPE_SERVICE_HAS_CONNECTED) {
            mSubscribe = Observable.interval(1, TimeUnit.SECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long time) {
                            if (time > 3) {
                                mSubscribe.unsubscribe();
                                PreferenceHelper.putBoolean(Const.PREF_KEY_STOP_AUTO_ADD_PEOPLE_FLAG, false);
                                MyAppUtil.jumpToWx(GuardService.this);
                            } else {
                                ToastUtils.showShort("倒计时" + (3 - time) + "s,即将跳转到微信");
                            }
                        }
                    });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(Const.EXTRA_OPEN_FLOWWINDOW, false)) {
                if (!FloatWindowManager.isWindowShowing()) {
                    try {
                        FloatWindowManager.createSmallWindow(getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

//        if (Build.VERSION.SDK_INT < 18) {
//            startForeground(SERVICE_ID, new Notification());
//            startService(new Intent(this, GuardInnerService.class));
//        } else {
//            startForeground(SERVICE_ID, new Notification());
//        }
        showNotification(SERVICE_ID);
        return START_STICKY;
    }

    private void showNotification(int serviceId) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID_SPORT);
        builder.setSmallIcon(R.drawable.icon_auto)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_alan))
                .setOngoing(true)
                .setUsesChronometer(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("微信好友助手服务")
                .setContentText("made by alan")
                .setTicker("微信助手")
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(false)
                .setLocalOnly(true)
                .setChannelId(CHANNEL_ID_SPORT)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(Notification.VISIBILITY_PUBLIC);


        Intent intent = new Intent();
        //带返回栈的 pendingIntent
            intent.setClass(getApplicationContext(), MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);



        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SPORT, "微信好友助手", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("微信好友助手服务");
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }



        Notification notification = builder.build();
        builder.setPublicVersion(notification);
        startForeground(SERVICE_ID, notification);
        notificationManager.notify(SERVICE_ID, notification);
    }

    public static class GuardInnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(SERVICE_ID, new Notification());
            rx.Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            stopForeground(true);
                            stopSelf();
                        }
                    });
            return START_STICKY;
        }
    }
}
