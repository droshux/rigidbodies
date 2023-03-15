package io.github.droshux.rigidbodies;

import java.awt.*;
import java.util.Arrays;

public class Program {

    public static void main(String[] args) {

        Canvas canvas = new Canvas(640, 480, 16, 1, Canvas.CollisionMode.Full);

        new RigidBodyBuilder(canvas)
                .setId("OBJECT")
                .setColour(Color.RED)
                .setGravity(true)
                .setPosition(new Point(0, 10))
                .setMass(1)
                .setColliderFile("mesh2")
                .createRigidBody()
                .Rotate(Math.PI / 2);
        new RigidBodyBuilder(canvas)
                .setId("OBJECT2")
                .setColliderFile("mesh1")
                .setPosition(new Point(0, -1))
                .setColour(Color.GRAY)
                .createRigidBody();


        canvas.start();
    }
}