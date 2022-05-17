package io.github.droshux.rigidbodies;

import java.awt.*;

public class Program {

    public static void main(String[] args) {
        CanvasTemplate Canvas = new CanvasTemplate();
        new RigidBodyBuilder(Canvas)
                .setId("test1")
                .setMass(5)
                .setPosition(new Point(0, 0))
                .setColour(Color.BLUE)
                .setColliderFile("mesh2")
                .setGravity(true)
                .createRigidBody();

        new RigidBodyBuilder(Canvas)
                .setId("Ruler")
                .setGravity(false)
                .setColliderFile("mesh1")
                .setColour(Color.BLACK)
                .setPosition(new Point(0,0))
                .createRigidBody();
        Canvas.start();
    }
}