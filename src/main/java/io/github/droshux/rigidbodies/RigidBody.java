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
    public Color Colour;

    public CanvasTemplate canvas;

    public RigidBody(String id, float mass, Point position, Color col, String colliderFile, CanvasTemplate canvasTemplate) {
        this.id = id; this.Mass = mass; this.Position = position; this.Colour = col; this.canvas = canvasTemplate;
        this.Collider = Utils.getMeshFromFile(colliderFile);

        Point COM = CenterOfMass();
        //Translate all coordinates so that 0,0 is COM
        for (Triangle t : Collider) for (Point p : t.points) {
            p.x -= COM.x;
            p.y -= COM.y;
        }

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
}