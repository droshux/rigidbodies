package io.github.droshux.rigidbodies;

import java.awt.*;

public class Program {

    public static void main(String[] args) {

                System.setProperty("tstPink", "0XFF80C8");
                Canvas Canvas = new Canvas(640, 480, 16, 1, io.github.droshux.rigidbodies.Canvas.CollisionMode.Full); // 15 is enough data points!
                Canvas.pixelsPerMeter = 15;

               RigidBody testRb = new RigidBodyBuilder(Canvas)
                       .setId("Test Object")
                       .setColliderFile("testmesh")
                       .setColour(Color.getColor("tstPink"))
                       .setMass(1)
                       .setGravity(true)
                       .setPosition(new Point(0,0))
                       .createRigidBody();

               RigidBody spear = new RigidBodyBuilder(Canvas)
                       .setId("Platform")
                       .setColliderFile("floor")
                       .setColour(Color.RED)
                       .setPosition(new Point(0,-10))
                       .setMass(1)
                       .setGravity(false)
                       .createRigidBody();
               System.out.println("BEGIN SIMULATION!");
               Canvas.start();
        }
}