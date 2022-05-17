package io.github.droshux.rigidbodies;

public class Vector extends Point {
    public Vector(double X, double Y) {
        super(X, Y);
    }
    public Vector(Point p) { super(p.x, p.y);}

    //Gets direction of vector where right is 0 radians and count anti-clockwise
    public double getDirection() {
        return Math.atan(y/x);
    }
    //Gets magnitude of vector using pythagoras's theorem
    public double getMagnitude() {
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }
    public void setDirectionAndMagnitude(double Direction, double Magnitude) {
        x = Magnitude * Math.cos(Direction); y = Magnitude * Math.sin(Direction);
    }
    public Point getEndPoint() {
        return new Point(this.x, this.y);
    }
}