package io.github.droshux.rigidbodies;

import java.awt.*;
//import java.util.ArrayList;
//import java.util.Arrays;

public class Program {

        public static void main(String[] args) {

                Canvas Canvas = new Canvas(640, 480, 5, 1); // 15 is enough data points!
                // Canvas.displayTime = true;

                RigidBody rb1 = new RigidBodyBuilder(Canvas)
                                .setId("test1")
                                .setMass(5)
                                .setPosition(new Point(0, -3))
                                .setColour(Color.BLUE)
                                .setColliderFile("complex")
                                .setGravity(true)
                                .setElasticity(1)
                                .setRigidity(0)
                                .createRigidBody();
                rb1.Rotate(Math.PI / 2);
                new RigidBodyBuilder(Canvas)
                                .setId("floor")
                                .setMass(1000)
                                .setPosition(new Point(0, -5))
                                .setColour(Color.DARK_GRAY)
                                .setGravity(false)
                                .setColliderFile("floor")
                                .createRigidBody();

                Canvas.start();

                for (double y = rb1.BoundingBox[0].y; y <= rb1.BoundingBox[1].y; y += (rb1.BoundingBox[1].y-rb1.BoundingBox[0].y)/100) {
                        for (double x = rb1.BoundingBox[0].x; x <= rb1.BoundingBox[1].x; x += (rb1.BoundingBox[1].x-rb1.BoundingBox[0].x)/100) {
                                if (rb1.contains(new Point(x, y))) {
                                        System.out.print('1');
                                } else System.out.print('0');
                        }
                        System.out.print('\n');
                }
        }
}