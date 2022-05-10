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
    public float Mass; //In kilograms
    public Color Colour;
    //private float Density; //In kilograms per meter squared

    public RigidBody(String id, float mass, Point position, Color col, Triangle[] collider) {
        this.id = id; this.Mass = mass; this.Position = position; this.Collider = collider; this.Colour = col;
        Point COM = CenterOfMass();
        //Translate all coordinates so that 0,0 is COM
        for (Triangle t : Collider) for (Point p : t.points) {
                p.x -= COM.x;
                p.y -= COM.y;
        }

        Program.Canvas.Objects.add(this);
    }
    public RigidBody(String id, float mass, Point position, Color col, String colliderFile) {
        this.id = id; this.Mass = mass; this.Position = position; this.Colour = col;
        this.Collider = Utils.getMeshFromFile(colliderFile);

        Point COM = CenterOfMass();
        //Translate all coordinates so that 0,0 is COM
        for (Triangle t : Collider) for (Point p : t.points) {
            p.x -= COM.x;
            p.y -= COM.y;
        }

        Program.Canvas.Objects.add(this);
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

    //Rotate rigid-body by some angle in degrees
    public void RotateDegrees(double theta) {
        int angle = 0;
        final double radian = (double)Math.toRadians(1);
        while (angle < theta) {
            for (Triangle t : Collider)
                for (Point p : t.points) {
                    p.matrixTransform(
                            Math.cos(radian), -Math.sin(radian),
                            Math.sin(radian), Math.cos(radian));
                }
            angle++;
        }
    }
}
