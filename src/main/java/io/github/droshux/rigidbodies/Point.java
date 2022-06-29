package io.github.droshux.rigidbodies;

public class Point {
    public double x;
    public double y;

    public Point(double X, double Y) {
        this.x = X; this.y = Y;
    }

    //Gets the distance between two points
    public float DistanceTo(Point other) {
        double deltaX = Math.abs(this.x-other.x);
        double deltaY = Math.abs(this.y-other.y);
        return (float)Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    //To string override :)

    @Override
    public String toString() {
        return "(" + Utils.Round(x, 3) + ", " + Utils.Round(y, 3) + ")";
    }

    public void matrixTransformSelf(double ix, double jx,
                                double iy, double jy) {
        double xOUT = (ix * x) + (jx * y);
        double yOUT = (iy * x) + (jy  * y);

        x = xOUT; y=yOUT;
    }
    public void matrixTransformSelf(Utils.Matrix M) {
        double xOUT = (M.i.x * x) + (M.j.x * y);
        double yOUT = (M.i.y * x) + (M.j.y * y);

        x=xOUT;y=yOUT;
    }
    public Point matrixTransform(double ix, double jx,
                                double iy, double jy) {
        double xOUT = (ix * x) + (jx * y);
        double yOUT = (iy * x) + (jy  * y);

        return new Point(xOUT, yOUT);
    }
    public Point matrixTransform(Utils.Matrix M) {
        double xOUT = (M.i.x * x) + (M.j.x * y);
        double yOUT = (M.i.y * x) + (M.j.y * y);

        return new Point(xOUT, yOUT);
    }
}