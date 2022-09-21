package io.github.droshux.rigidbodies;

import java.awt.*;

public class Program {

        public static void main(String[] args) {
                Canvas Canvas = new Canvas(640, 480, 10, 4);
                // Canvas.displayTime = true;

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
                new RigidBodyBuilder(Canvas)
                                .setId("floor")
                                .setMass(1000)
                                .setPosition(new Point(0, -5))
                                .setColour(Color.DARK_GRAY)
                                .setGravity(false)
                                .setColliderFile("floor")
                                .createRigidBody();

                new RigidBodyBuilder(Canvas)
                                .setId("testBox")
                                .setPosition(new Point(0, -3))
                                .setColliderFile("mesh1")
                                .setMass(0)
                                .setGravity(false)
                                .setColour(Color.RED)
                                .createRigidBody();

                Canvas.start();
        }
}