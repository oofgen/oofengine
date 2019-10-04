package oof.oofengine.data;

public class Vector3f {
    private float x;
    private float y;
    private float z;

    public Vector3f(String fileLine) {
        String[] lineSplit = fileLine.split(" ");
        // Ignore the first element, this will contain letters
        this.x = Float.parseFloat(lineSplit[1]);
        this.y = Float.parseFloat(lineSplit[2]);
        this.z = Float.parseFloat(lineSplit[3]);
    }

    public String toString() {
        return String.format("%f %f %f", this.x, this.y, this.z);
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
}
