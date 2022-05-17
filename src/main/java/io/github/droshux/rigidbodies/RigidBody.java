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

    public CanvasTemplate canvas;

    public List<Vector> Forces = new ArrayList<>();
    public Vector Velocity = new Vector(0,0);
    public Vector Weight;

    public RigidBody(String id, float mass, Point position, Color col, String colliderFile, CanvasTemplate canvasTemplate, boolean useGravity) {
        this.id = id; this.Mass = mass; this.Position = position; this.Colour = col; this.canvas = canvasTemplate; this.UseGravity=useGravity;
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
        Vector acceleration = new Vector(0,0);
        for (Vector F : Forces) {
            acceleration = Utils.VectorAdd(acceleration, Utils.VectorScalarMultiply(F, 1/Mass)); //F=ma therefore a=F/m
        }
        Velocity = Utils.VectorAdd(Velocity, Utils.VectorScalarMultiply(acceleration, delta));
        this.Position = new Point(this.Position.x + (Velocity.x * delta), this.Position.y + (Velocity.y * delta));
    }
}