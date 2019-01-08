package app.moveotask.com.moveoproject.accelerometerService;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import java.util.Timer;
import java.util.TimerTask;
import app.moveotask.com.moveoproject.App;
import app.moveotask.com.moveoproject.MainActivity;
import app.moveotask.com.moveoproject.models.AccelerometerResult;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by netaivgi on 7.1.2019.
 */

public class AccelerometerService extends IntentService {

    private final int ACCELEROMETER_SAMPLE_PERIOD = 300;
    private final int ACCELEROMETER_SERVICE_NOTIFICATION_ID = 50;
    private  final int DATABASE_MAX_RECORDS = 500;

    private float x = 0;
    private float y = 0;
    private float z = 0;

    private Timer timer = new Timer();

    public AccelerometerService() {
        super("AccelerometerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotificaton("");
        startAccelerometerSensor();

        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        stopAccelerometerSensor();
        super.onDestroy();
    }

    private void showNotificaton(String text){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notificationBuilder = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Accelerometer Service is running")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build();

        startForeground(ACCELEROMETER_SERVICE_NOTIFICATION_ID, notificationBuilder);
    }

    private void startAccelerometerSensor() {
        MovementDetector movementDetector = MovementDetector.getInstance();
        movementDetector.addListener(new MovementDetector.Listener() {
            @Override
            public void onMotionDetected(SensorEvent event) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
            }
        });

        movementDetector.start();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AccelerometerResult lastValues = new AccelerometerResult(System.currentTimeMillis(), x, y, z);
                saveAccelerometerResults(lastValues);
                sendBroadcast(lastValues);

                cleanDatabase();

                String result = "x: "+x+" y: "+y+" z: "+z;
                showNotificaton(result);
            }
        }, 0, ACCELEROMETER_SAMPLE_PERIOD);
    }

    private void stopAccelerometerSensor() {
        MovementDetector.getInstance().stop();
        timer.cancel();
    }

    private void saveAccelerometerResults(AccelerometerResult accelerometerResult) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(accelerometerResult);
        realm.commitTransaction();
    }

    private void sendBroadcast(AccelerometerResult accelerometerResult) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.BROADCAST_ACCELEROMETER_VALUES);
        broadcastIntent.putExtra("x", accelerometerResult.getX());
        broadcastIntent.putExtra("y", accelerometerResult.getY());
        broadcastIntent.putExtra("z", accelerometerResult.getZ());
        broadcastIntent.putExtra("timestamp", accelerometerResult.getTimestamp());
        getApplicationContext().sendBroadcast(broadcastIntent);
    }

    public void cleanDatabase() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<AccelerometerResult> accelerometerResults = realm.where(AccelerometerResult.class).findAll();
        int size = accelerometerResults.size();
        if (size == DATABASE_MAX_RECORDS) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Long firstObjectId = realm.where(AccelerometerResult.class).min("timestamp").longValue();
                    final RealmResults<AccelerometerResult> oldestAccelerometerResults = realm.where(AccelerometerResult.class).equalTo("timestamp", firstObjectId).findAll();
                    oldestAccelerometerResults.deleteAllFromRealm();
                }
            });
        }
    }
}

