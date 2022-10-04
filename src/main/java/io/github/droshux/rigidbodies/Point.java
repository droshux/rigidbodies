package io.github.droshux.rigidbodies;

import java.util.Objects;

public class Point {
    public double x;
    public double y;

    public Point(double X, double Y) {
        this.x = X;
        this.y = Y;
    }

    // Gets the distance between two points
    public double DistanceTo(Point other) {
        double deltaX = Math.abs(this.x - other.x);
        double deltaY = Math.abs(this.y - other.y);
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    // To string override :)

    @Override
    public String toString() {
        return "(" + Utils.Round(x, 3) + ", " + Utils.Round(y, 3) + ")";
    }

    public void matrixTransformSelf(double ix, double jx,
            double iy, double jy) {
        double xOUT = (ix * x) + (jx * y);
        double yOUT = (iy * x) + (jy * y);

        x = xOUT;
        y = yOUT;
    }

    @SuppressWarnings("unused")
    public void matrixTransformSelf(Utils.Matrix M) {
        double xOUT = (M.i.x * x) + (M.j.x * y);
        double yOUT = (M.i.y * x) + (M.j.y * y);

        x = xOUT;
        y = yOUT;
    }

    @SuppressWarnings("unused")
    public Point matrixTransform(double ix, double jx,
            double iy, double jy) {
        double xOUT = (ix * x) + (jx * y);
        double yOUT = (iy * x) + (jy * y);

        return new Point(xOUT, yOUT);
    }

    @SuppressWarnings("unused")
    public Point matrixTransform(Utils.Matrix M) {
        double xOUT = (M.i.x * x) + (M.j.x * y);
        double yOUT = (M.i.y * x) + (M.j.y * y);

        return new Point(xOUT, yOUT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Point point))
            return false;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}