package io.github.droshux.rigidbodies;

import java.awt.*;

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

                Canvas.start();

                /*
                 * // Generate random clustered data
                 * final int numClusters = Utils.RandInt(1, 5);
                 * System.out.println("Number of clusters: " + numClusters);
                 * Point[] dataArr = new Point[] {
                 * new Point(0, 0),
                 * new Point(2, 1),
                 * new Point(-1, 0),
                 * new Point(0, -1),
                 * 
                 * new Point(7, 4),
                 * new Point(6, 5),
                 * new Point(7, 6)
                 * };
                 * List<Point> data = new ArrayList<>(Arrays.asList(dataArr));
                 * 
                 * System.out.println("Number of points: " + data.size());
                 * 
                 * // Find clusters
                 * final int K = Utils.optimalK(data, 6);
                 * System.out.println("K: " + K);
                 * Utils.Cluster[] clusters = new Utils.Cluster[K];
                 * clusters = Utils.Kmeans(data, K);
                 * for (Cluster C : clusters) {
                 * System.out.println(C);
                 * }
                 * System.out.println("Done!");
                 */
        }
}