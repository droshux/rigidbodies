package io.github.droshux.rigidbodies;

public class Vector extends Point {
    public Vector(double X, double Y) {
        super(X, Y);
    }

    //Gets direction of vector where right is 0 radians and count anti-clockwise
    public float getDirection() {
        return (float)Math.atan(y/x);
    }
    //Gets magnitude of vector using pythagoras's theorem
    public float getMagnitude() {
        return (float)Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }
    public void setDirectionAndMagnitude(float Direction, float Magnitude) {
        x = Magnitude * (float)Math.cos(Direction); y = Magnitude * (float)Math.sin(Direction);
    }
}
