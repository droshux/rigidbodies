package io.github.droshux.rigidbodies;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import io.github.droshux.rigidbodies.Utils.Cluster;

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

                // Generate random clustered data
                final int numClusters = Utils.RandInt(1, 5);
                List<Point> data = new ArrayList<>();
                for (int i = 0; i < numClusters; i++) {
                        final Point seedPoint = new Point(0, 3 * numClusters);
                        data.add(seedPoint);
                        final int extraPoints = Utils.RandInt(0, 5);
                        for (int j = 0; j < extraPoints; j++) {
                                int xOffset = 0;
                                int yOffset = 0;
                                while (xOffset == 0 && yOffset == 0) {
                                        xOffset = Utils.RandInt(-1, 1);
                                        yOffset = Utils.RandInt(-1, 1);
                                }
                                System.out.println("Offsets found");
                                data.add(Utils.VectorAdd(new Vector(seedPoint), new Vector(xOffset, yOffset))
                                                .getEndPoint());
                        }
                        System.out.println("Cluster built");
                }
                data = Utils.removeDuplicates(data);
                System.out.println("Clusters finished");

                // Find clusters
                final int K = Utils.optimalK(data, 6);
                Utils.Cluster[] clusters = new Utils.Cluster[K];
                clusters = Utils.Kmeans(data, K);
                for (Cluster C : clusters) {
                        System.out.println(C.Centroid());
                        System.out.println(C.Distortion());
                        System.out.println(C.clusterPoints.size());
                        System.out.println();
                }
                System.out.println("Done!");
        }
}