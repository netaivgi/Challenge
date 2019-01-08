package app.moveotask.com.moveoproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import app.moveotask.com.moveoproject.accelerometerService.AccelerometerService;
import app.moveotask.com.moveoproject.models.AccelerometerResult;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public final static String BROADCAST_ACCELEROMETER_VALUES = "app.moveotask.com.moveoproject.ACCELEROMETER_VALUES";

    private BroadcastReceiver receiver;
    private Realm realm;
    private ConstraintLayout clRoot;
    private TextView tvResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        clRoot = findViewById(R.id.cl_root);
        tvResult = findViewById(R.id.tv_result);
        realm = Realm.getDefaultInstance();

        drawLastResult();

        setupReceiver();
        startAccelerometerService();
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACCELEROMETER_VALUES);
        this.registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(receiver);
    }

    private void drawLastResult() {
        Number lastObjectId = realm.where(AccelerometerResult.class).max("timestamp");
        if (lastObjectId != null) {
            Long lastId = lastObjectId.longValue();
            final RealmResults<AccelerometerResult> lastAccelerometerResults = realm.where(AccelerometerResult.class).equalTo("timestamp", lastId).findAll();
            if (lastAccelerometerResults.size() > 0) {
                AccelerometerResult lastAccelerometerResult = lastAccelerometerResults.get(0);
                updateUi(lastAccelerometerResult);
            }
        }
    }

    private void setupReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                long timestamp = intent.getLongExtra("timestamp", 0);
                float x = intent.getFloatExtra("x", 0);
                float y = intent.getFloatExtra("y", 0);
                float z = intent.getFloatExtra("z", 0);

                AccelerometerResult accelerometerResult = new AccelerometerResult(timestamp, x, y, z);

                updateUi(accelerometerResult);
            }

        };
    }

    private void updateUi(AccelerometerResult accelerometerResult) {
        double normalizedZ = accelerometerResult.getZ() / Math.sqrt(accelerometerResult.getX() * accelerometerResult.getX() + accelerometerResult.getY() * accelerometerResult.getY() + accelerometerResult.getZ() * accelerometerResult.getZ());
        double angle = Math.toDegrees(Math.acos(normalizedZ));

        String result = "Timestamp: " + accelerometerResult.getTimestamp() + "\n" + "Angle: " + angle + "\nx: " + accelerometerResult.getX() + " \ny: " + accelerometerResult.getY() + " \nz: " + accelerometerResult.getZ();
        tvResult.setText(result);

        if (angle < 50 && angle > 0) { //screen facing up
            clRoot.setBackgroundColor(Color.BLUE);
        } else if (angle > 150 && angle < 180) { //screen facing down
            clRoot.setBackgroundColor(Color.RED);
        } else { //screen facing the user
            clRoot.setBackgroundColor(Color.GREEN);
        }
    }

    public void startAccelerometerService() {
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopAccelerometerService(View v) {
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        stopService(serviceIntent);
    }

}
