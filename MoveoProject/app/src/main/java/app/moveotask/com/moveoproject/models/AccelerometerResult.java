package app.moveotask.com.moveoproject.models;

import io.realm.RealmObject;


    /**
     * Created by netaivgi on 7.1.2019.
     */

    public  class AccelerometerResult extends RealmObject {

    private long timestamp;
    private float x;
    private float y;
    private float z;

    public AccelerometerResult(){

    }

    public AccelerometerResult(long timestamp, float x, float y, float z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "AccelerometerResult{" +
                "timestamp=" + timestamp +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}