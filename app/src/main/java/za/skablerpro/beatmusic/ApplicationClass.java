package za.skablerpro.beatmusic;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    public static final String CHANNEL_ID1 = "Channel1";
    public static final String CHANNEL_ID2 = "Channel2";
    public static final String ACTION_PREVIOUS = "ActionPrevious";
    public static final String ACTION_NEXT = "ActionNext";
    public static final String ACTION_PLAY = "ActionPlay";
    public static final String ACTION_STOP = "ActionStop";
    public static final String ACTION_CLOSE = "ActionClose";
    public static final String ACTION_SEEK_TO = "ActionSeekTo";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID1,
                    "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc...");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID2,
                    "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 2 Desc...");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
