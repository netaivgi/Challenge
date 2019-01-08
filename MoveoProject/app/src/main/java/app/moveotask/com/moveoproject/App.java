package app.moveotask.com.moveoproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import io.realm.Realm;

/**
 * Created by netaivgi on 7.1.2019.
 */

public class App extends Application {

    public static final String CHANNEL_ID = "ServiceChannel";
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Realm.init(this);
        createNotificationChannel();
    }

    public static App getInstance(){
        return instance;
    }

    private void createNotificationChannel() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}