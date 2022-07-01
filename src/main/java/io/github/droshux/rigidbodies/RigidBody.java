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
    public final double MomentOfInertia; //kgm^2
    public final boolean UseGravity;
    public Color Colour;
    public double elasticity;
    public double rigidity;

    public Canvas canvas;

    public List<Vector> Forces = new ArrayList<>();
    public Vector Velocity = new Vector(0,0);
    public double AngularVelocity = 0;
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
        MomentOfInertia = calculateI();
        canvas.Objects.add(this);
    }

    private double calculateI() {
        List<Point> polygon = new ArrayList<>();
        for (Triangle t : Collider) polygon.addAll(Arrays.asList(t.points)); //Make a list of all points in the mesh
        polygon = polygon.stream().distinct().collect(Collectors.toList()); //Remove duplicates
        double Jx = 0;
        for (int i = 0; i <= polygon.size()-1; i++) {
            int iPlus = i + 1;
            if (iPlus >= polygon.size()) iPlus = 0; //Loop round
            Point Pi = polygon.get(i); Point PiPlus = polygon.get(iPlus); //Get two points
            Jx += ((Pi.x * PiPlus.y)-(PiPlus.x * Pi.y)) * (Math.pow(Pi.x, 2) + (Pi.x * PiPlus.x) + Math.pow(PiPlus.x, 2));
        }
        Jx /= 12;

        double Jy = 0;
        for (int i = 0; i <= polygon.size()-1; i++) {
            int iPlus = i + 1;
            if (iPlus >= polygon.size()) iPlus = 0; //Loop round
            Point Pi = polygon.get(i); Point PiPlus = polygon.get(iPlus); //Get two points
            Jy += ((Pi.x * PiPlus.y)-(PiPlus.x * Pi.y)) * (Math.pow(Pi.y, 2) + (Pi.y * PiPlus.y) + Math.pow(PiPlus.y, 2));
        }
        Jy /= 12;

        return Math.abs(Jx + Jy);
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
                p.matrixTransformSelf(
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
        if (!Forces.contains(Utils.g) && UseGravity) Forces.add(Weight);
        Vector resultantForce = new Vector(0,0);
        for (Vector force : Forces) resultantForce = Utils.VectorAdd(resultantForce, force);
        Velocity = Utils.VectorAdd(Velocity, Utils.VectorScalarMultiply(resultantForce, delta/Mass));
        Position = Utils.VectorAdd(new Vector(Position), Utils.VectorScalarMultiply(Velocity, delta));
    }

    public void AddForceAtPosition(Vector force, Point localPosition, double delta) {
        Forces.add(force); //Add the force normally
        double Torque = localPosition.x * force.y - localPosition.y * force.x; //2D torque calculation
        AngularVelocity += Torque * delta / MomentOfInertia;
    }
}