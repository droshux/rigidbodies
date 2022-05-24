package io.github.droshux.rigidbodies;

public class MovingPoint extends Point{
	Vector Velocity = new Vector(0,0);
	public MovingPoint(double X, double Y) {super(X, Y);}
	public MovingPoint(double X, double Y, Vector V) {super(X, Y);this.Velocity = V;}
}