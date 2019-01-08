package app.moveotask.com.moveoproject.accelerometerService;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.HashSet;
import app.moveotask.com.moveoproject.App;

/**
 * Created by netaivgi on 7.1.2019.
 */

public class MovementDetector implements SensorEventListener {

    private SensorManager sensorMan;
    private Sensor accelerometer;

    private MovementDetector() {
    }

    private static MovementDetector mInstance;

    public static MovementDetector getInstance() {
        if (mInstance == null) {
            mInstance = new MovementDetector();
            mInstance.init();
        }
        return mInstance;
    }

    private HashSet<Listener> mListeners = new HashSet<Listener>();

    private void init() {
        sensorMan = (SensorManager) App.getInstance().getSystemService(AccelerometerService.SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start() {
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorMan.unregisterListener(this);
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            for (Listener listener : mListeners) {
                listener.onMotionDetected(event);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface Listener {
        void onMotionDetected(SensorEvent event);
    }
}
