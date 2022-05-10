package io.github.droshux.rigidbodies;

public class Triangle {
    public Point[] points = new Point[3];
    public Triangle(Point p1, Point p2, Point p3) {
        points[0] = p1; points[1] = p2; points[2] = p3;
    }
    public Triangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        points[0] = new Point(x1, y1);
        points[1] = new Point(x2, y2);
        points[2] = new Point(x3, y3);
    }

    //Get the area of an arbitrary 3 points or the triangle itself
    public double area(Point p1, Point p2, Point p3) {
        //Get Side lengths
        double a = p2.DistanceTo(p3);
        double b = p1.DistanceTo(p3);
        double c = p1.DistanceTo(p2);

        //Get an angle using the cosine rule and use the area sine rule to find the area
        double Angle = Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2))/(2 * a * b));
        return 0.5f * a * b * Math.sin(Angle);
    }
    public double area() {
        //Get Side lengths
        double a = points[1].DistanceTo(points[2]);
        double b = points[0].DistanceTo(points[2]);
        double c = points[0].DistanceTo(points[1]);

        //Get an angle using the cosine rule and use the area sine rule to find the area
        double Angle = Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2))/(2 * a * b));
        return 0.5f * a * b * Math.sin(Angle);
    }

    //Check if a point is withing the triangle
    public boolean contains(Point p) {
        //Get total area
        double TotalArea = area(points[0], points[1], points[2]);

        //Divide the triangle into 3 smaller triangles and sum up their area
        double AreaSum = 0f;
        AreaSum = AreaSum + area(p, points[0], points[1]);
        AreaSum = AreaSum + area(p, points[0], points[2]);
        AreaSum = AreaSum + area(p, points[1], points[2]);

        //If the three triangles add up to the total area the point is inside
        return (AreaSum <= TotalArea);
    }

    public Point getCenterOfMass() {
        //Get the mean of the x and y coordinates
        double meanX = (points[0].x + points[1].x + points[2].x) /3;
        double meanY = (points[0].y + points[1].y + points[2].y) /3;
        //And return the point that gives :)
        return new Point(meanX, meanY);
    }
}
