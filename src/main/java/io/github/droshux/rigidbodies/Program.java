package io.github.droshux.rigidbodies;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Program {

        public static void main(String[] args) {
                /*
                 * Canvas Canvas = new Canvas(640, 480, 10, 4);
                 * // Canvas.displayTime = true;
                 * 
                 * RigidBody rb1 = new RigidBodyBuilder(Canvas)
                 * .setId("test1")
                 * .setMass(5)
                 * .setPosition(new Point(0, 0))
                 * .setColour(Color.BLUE)
                 * .setColliderFile("complex")
                 * .setGravity(true)
                 * .setElasticity(1)
                 * .setRigidity(0)
                 * .createRigidBody();
                 * rb1.Forces.add(new RigidBody.LocalForce(new Vector(10, 0),
                 * new Point(-1, -1),
                 * 0.1));
                 * new RigidBodyBuilder(Canvas)
                 * .setId("floor")
                 * .setMass(1000)
                 * .setPosition(new Point(0, -5))
                 * .setColour(Color.DARK_GRAY)
                 * .setGravity(false)
                 * .setColliderFile("floor")
                 * .createRigidBody();
                 * 
                 * new RigidBodyBuilder(Canvas)
                 * .setId("testBox")
                 * .setPosition(new Point(0, -3))
                 * .setColliderFile("mesh1")
                 * .setMass(0)
                 * .setGravity(false)
                 * .setColour(Color.RED)
                 * .createRigidBody();
                 * 
                 * Canvas.start();
                 */

                Point p1 = new Point(Utils.RandInt(0, 10), Utils.RandInt(0, 10));
                Point p2 = new Point(Utils.RandInt(0, 10), Utils.RandInt(0, 10));
                Point p3 = new Point(Utils.RandInt(0, 10), Utils.RandInt(0, 10));

                List<Point> data = new ArrayList<>();
                data.add(p1);
                data.add(p2);
                data.add(p3);

                for (int i = 0; i < 5; i++) {
                        data.add(Utils.VectorAdd(new Vector(p1), new Vector(Utils.RandInt(-1, 1), Utils.RandInt(-1, 1)))
                                        .getEndPoint());
                        data.add(Utils.VectorAdd(new Vector(p2), new Vector(Utils.RandInt(-1, 1), Utils.RandInt(-1, 1)))
                                        .getEndPoint());
                        data.add(Utils.VectorAdd(new Vector(p3), new Vector(Utils.RandInt(-1, 1), Utils.RandInt(-1, 1)))
                                        .getEndPoint());
                }
                data = Utils.removeDuplicates(data);
                for (Point p : data) {
                        System.out.println(p);
                }
                System.out.println("CLUSTERING.......");
                Utils.Cluster[] cl = Utils.Kmeans(data, 3);
                for (Utils.Cluster C : cl) {
                        System.out.println(C);
                }
        }
}