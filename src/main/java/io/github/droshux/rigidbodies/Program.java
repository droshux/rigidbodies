package io.github.droshux.rigidbodies;

import java.awt.*;

public class Program {

    public static void main(String[] args) {
        Canvas Canvas = new Canvas(640, 480, 10, 3);
//        Canvas.displayTime = true;
        RigidBody rb1 = new RigidBodyBuilder(Canvas)
                .setId("test1")
                .setMass(5)
                .setPosition(new Point(0, 0))
                .setColour(Color.BLUE)
                .setColliderFile("complex")
                .setGravity(true)
                .setElasticity(1)
                .setRigidity(0)
                .createRigidBody();
        rb1.Forces.add(new RigidBody.LocalForce(new Vector(10, 0),
                new Point(-1, -1),
                0.1));
        RigidBody floor = new RigidBodyBuilder(Canvas)
                .setId("floor")
                .setMass(1000)
                .setPosition(new Point(0, -5))
                .setColour(Color.DARK_GRAY)
                .setGravity(false)
                .setColliderFile("floor")
                .createRigidBody();
        //Canvas.start();
        System.out.println(floor.contains(Utils.VectorAdd(new Vector(floor.Collider[0].points[0]), new Vector(0.2, 0.2))));
        System.out.println(floor.contains(Utils.VectorAdd(new Vector(floor.Collider[0].points[0]), new Vector(-0.2, -0.2))));

        Triangle t1 = new Triangle(new Point(-1, -1), new Point(1, -1), new Point(0, 2));
        System.out.println(t1.contains(new Point(0, 0)));
        System.out.println(t1.contains(new Point(0, -2)));
    }
}