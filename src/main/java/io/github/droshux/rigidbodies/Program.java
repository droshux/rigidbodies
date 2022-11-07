package io.github.droshux.rigidbodies;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Program {

        public static void main(String[] args) {

                Canvas Canvas = new Canvas(640, 480, 5, 3); // 15 is enough data points!
                // Canvas.displayTime = true;

                RigidBody rb1 = new RigidBodyBuilder(Canvas)
                                .setId("test1")
                                .setMass(5)
                                .setPosition(new Point(0, 0))
                                .setColour(Color.BLUE)
                                .setColliderFile("mesh2")
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

                // Canvas.start();

                System.out.println(Utils.optimalK(new ArrayList<>(Arrays.asList(new Point[] {
                                new Point(-0.8, -0.45),
                                new Point(-0.80001, -0.450001),
                                new Point(-0.80002, -0.450001),
                                new Point(0.2, -0.45),
                                new Point(0.20001, -0.450001),
                                new Point(0.20002, -0.450002)
                })), 6));

        }
}