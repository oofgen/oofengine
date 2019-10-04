package oof.oofengine.data;

public class Vector2f {
    private float x;
    private float y;

    public Vector2f(String fileLine) {
        String[] lineSplit = fileLine.split(" ");
        // Ignore the first element, this will contain letters
        this.x = Float.parseFloat(lineSplit[1]);
        this.y = Float.parseFloat(lineSplit[2]);
    }

    public String toString() {
        return String.format("%f %f", this.x, this.y);
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
}
