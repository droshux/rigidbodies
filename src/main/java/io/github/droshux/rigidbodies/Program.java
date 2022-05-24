package io.github.droshux.rigidbodies;

import java.awt.*;

public class Program {

    public static void main(String[] args) {
        Canvas Canvas = new Canvas();
        new RigidBodyBuilder(Canvas)
                .setId("test1")
                .setMass(5)
                .setPosition(new Point(0, 0))
                .setColour(Color.BLUE)
                .setColliderFile("mesh2")
                .setGravity(true)
                .setElasticity(1)
                .setRigidity(0)
                .createRigidBody();

        new RigidBodyBuilder(Canvas)
                .setId("floor")
                .setMass(1000)
                .setPosition(new Point(0, -3))
                .setColour(Color.DARK_GRAY)
                .setGravity(false)
                .setColliderFile("floor")
                .createRigidBody();
        Canvas.start();
    }
}