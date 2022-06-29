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
    public Vector getUnitVector() {return Utils.VectorScalarMultiply(this, 1/this.getMagnitude());}

    /*
    What I tried figuring out on my own:
    public Vector ComponentAlongVector(Vector v) {
        Vector self = new Vector(this.x, this.y); //Copy this to new vector
        Vector iNEW = v.getUnitVector(); Vector jNEW = new Vector(iNEW.matrixTransform(Utils.rotate90)); //Set the horizontal and vertical components of the new space to be equal and perpendicular to the unit vector of the input vector respectively
        Utils.Matrix M = new Utils.Matrix(iNEW.x, jNEW.x, iNEW.y, jNEW.y); //Set up the matrix to transform the space
        self.matrixTransformSelf(M); //Change of basis?
        self = new Vector(self.x, 0); //Take only the x component (the part in the direction of the input vector)
        self.matrixTransformSelf(M.Inverse()); //Return to normal space
        return self;
    }
     */

    //Using https://math.stackexchange.com/questions/286391/find-the-component-of-veca-along-vecb
    public Vector ComponentAlongVector(Vector v) {return Utils.VectorScalarMultiply(v.getUnitVector(), Utils.DotProduct(this, v)/v.getMagnitude());}
}