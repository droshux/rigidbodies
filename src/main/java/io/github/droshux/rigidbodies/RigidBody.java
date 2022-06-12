package io.github.droshux.rigidbodies;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RigidBody {
    public String id;
    public  Triangle[] Collider;
    public Point Position;
    public double Mass; //In kilograms
    public final boolean UseGravity;
    public Color Colour;
    public double elasticity;
    public double rigidity;

    public Canvas canvas;

    public List<Vector> Forces = new ArrayList<>();
    public Vector Velocity = new Vector(0,0);
    public Vector Weight;

    public RigidBody(String id, float mass, Point position, Color col, String colliderFile, Canvas canvasTemplate, boolean useGravity, double elasticity, double rigidity) {
        this.id = id; this.Mass = mass; this.Position = position; this.Colour = col; this.canvas = canvasTemplate; this.UseGravity=useGravity; this.elasticity = elasticity; this.rigidity = rigidity;
        this.Collider = Utils.getMeshFromFile(colliderFile);

        Point COM = CenterOfMass();
        //Translate all coordinates so that 0,0 is COM
        for (Triangle t : Collider) for (Point p : t.points) {
            p.x -= COM.x;
            p.y -= COM.y;
        }
        if (UseGravity) Weight = Utils.VectorScalarMultiply(Utils.g, Mass);
        canvas.Objects.add(this);
    }

    public Point CenterOfMass() {
        List<Point> MeshPoints = new ArrayList<>();
        for (Triangle t : Collider) MeshPoints.addAll(Arrays.asList(t.points)); //Make a list of all points in the mesh
        MeshPoints = MeshPoints.stream().distinct().collect(Collectors.toList()); //Remove duplicates
        float totalX = 0; float totalY = 0; //Calculate mean...
        for (Point point : MeshPoints) {
            totalX += point.x; totalY += point.y;
        }
        totalX /= MeshPoints.size(); totalY /= MeshPoints.size();
        return new Point(totalX, totalY); //Return :)
    }

    public void Rotate(double theta) {
        for (Triangle t : Collider)
            for (Point p : t.points) {
                p.matrixTransform(
                        Math.cos(theta), -Math.sin(theta),
                        Math.sin(theta), Math.cos(theta));
            }
    }

    @SuppressWarnings("unused")
    private Point LocalToWorldSpace(Point p) {return new Point(this.Position.x + p.x, this.Position.y + p.y);}
    @SuppressWarnings("unused")
    private Point WorldToLocalSpace(Point p) {return new Point(p.x-this.Position.x, p.y-this.Position.y);}

    @SuppressWarnings("unused")
    public void RotateAboutPoint(Point point, double theta) {
        for (Triangle t : Collider) for (Point p : t.points) {
            p.x -= point.x;
            p.y -= point.y;
        }
        Rotate(theta);
        for (Triangle t : Collider) for (Point p : t.points) {
            p.x += point.x;
            p.y += point.y;
        }
    }

    public void Update(double delta) {
        if (!Forces.contains(Weight) && UseGravity) Forces.add(Weight);

        //Predict Acceleration
        Vector preAcceleration = new Vector(0,0);
        for (Vector force : Forces) preAcceleration = Utils.VectorAdd(preAcceleration, Utils.VectorScalarMultiply(force, this.Mass)); //Calculate predicted acceleration

        //Predict Velocity
        Vector preVelocity = Utils.VectorAdd(this.Velocity, preAcceleration);

        Forces = new ArrayList<>(); //Wipe forces
    }

    //Use Ray-marching to find the furthest valid position for the rigidbody
    private Point predictPosition(Vector velocity, double delta) {
        Point output = Position;



        return output;
    }

    private List<Collision> getCollisions(Vector vel, double delta) {
        List<Collision> collisions = new ArrayList<>();
        for (Triangle t : Collider) for (Point p : t.points) {
            double r = vel.getMagnitude() * delta; //Radius of circle that contains every location this point could move to.
            double xPos = LocalToWorldSpace(p).x; double yPos = LocalToWorldSpace(p).y;
            int[][] permutations = {
                    {0, 1},
                    {0, 2},
                    {1, 2}
            };
            for (RigidBody rb : canvas.Objects) for (Triangle otherT : rb.Collider) for (int[] line : permutations) {
                if (rb != this) {
                    Point p1 = rb.LocalToWorldSpace(otherT.points[line[0]]); Point p2 = rb.LocalToWorldSpace(otherT.points[line[1]]);
                    double gradient = Utils.getGradient(p1, p2); double intercept = Utils.get_Y_intercept(p1, p2);
                    //Now we have all the pieces to find the a, b and c for the discriminant
                    double a = Math.pow(gradient, 2) + 1;
                    double b = (2 * gradient * intercept) - (2 * gradient * yPos) - (2* xPos);
                    double c = Math.pow(xPos, 2) + Math.pow(yPos, 2) + Math.pow(intercept, 2) - (2 * yPos * intercept) - Math.pow(r, 2);
                    //Time to calculate discriminant
                    double discriminant = Math.pow(b, 2) - (4 * a * c);
                    if (discriminant >= 0) collisions.add(new Collision(p, new Point[] {p1, p2}, rb)); //If the discriminant is less than 0 the line intersects the circle
                }
            }
        }
        return collisions;
    }

    private static class Collision {
        public Point point; public Point[] line; public RigidBody rigidBody;
        public Collision(Point p, Point[] l, RigidBody rb) {point = p; line = l; rigidBody = rb;}
    }
}