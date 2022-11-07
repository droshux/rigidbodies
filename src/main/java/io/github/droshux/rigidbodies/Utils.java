package io.github.droshux.rigidbodies;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Utils {

    public static final String pathToMeshes = "src/main/resources/meshes/";
    public static final String pathToConfigs = "src/main/resources/configs/";
    public static final Vector g = new Vector(0, -9.81);
    public static final Matrix rotate90 = new Matrix(0, -1, 1, 0);

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static int RandInt(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector VectorAdd(@NotNull Vector v1, @NotNull Vector v2) {
        return new Vector(v1.x + v2.x, v1.y + v2.y);
    }

    @Contract("_, _ -> new")
    public static @NotNull Vector VectorScalarMultiply(@NotNull Vector v1, double Scalar) {
        return new Vector(v1.x * Scalar, v1.y * Scalar);
    }

    // From: https://www.mathsisfun.com/algebra/vectors-dot-product.html
    public static double DotProduct(Vector v1, Vector v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    @Contract("_, _ -> new")
    public static @NotNull Matrix ScalarMultiply(@NotNull Matrix M, double scalar) {
        return new Matrix(scalar * M.i.x, scalar * M.j.x,
                scalar * M.i.y, scalar * M.j.y);
    }

    public static double Round(double input, int sf) {
        BigDecimal bigDecimal = new BigDecimal(input);
        bigDecimal = bigDecimal.round(new MathContext(sf));
        return bigDecimal.floatValue();
    }

    public static <T> List<T> removeDuplicates(List<T> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }

    public static double getGradient(@NotNull Point p1, @NotNull Point p2) {
        return Math.abs(p1.y - p2.y) / Math.abs(p1.x - p2.x);
    }

    public static double get_Y_intercept(@NotNull Point p1, Point p2) {
        return p1.y - getGradient(p1, p2) * p1.x;
    }

    public static double linearFunction(double xInput, Point[] twoPoints) {
        return getGradient(twoPoints[0], twoPoints[1]) * xInput + get_Y_intercept(twoPoints[0], twoPoints[1]);
    }

    public static double[] AXplusBYplusC(Point[] twoPoints) {
        double[] output = new double[3];
        output[0] = -getGradient(twoPoints[0], twoPoints[1]); // A
        output[1] = 1; // B
        output[2] = getGradient(twoPoints[0], twoPoints[1]) * twoPoints[0].x - twoPoints[0].y;
        return output;
    }

    public static double distanceToLine(Point inPoint, double[] ABC) {
        return Math.abs(ABC[0] * inPoint.x + ABC[1] * inPoint.y + ABC[2])
                / Math.sqrt(Math.pow(ABC[0], 2) + Math.pow(ABC[1], 2)); // From:
                                                                        // https://www.cuemath.com/geometry/distance-of-a-point-from-a-line/
    }

    public static Cluster[] Kmeans(List<Point> points, int K) {
        // If K is 1 just put every point into one cluster
        if (K == 1) {
            return new Cluster[] { new Cluster(points) };
        }

        Cluster[] output = new Cluster[K];
        Cluster[] previous = new Cluster[K];
        boolean changed = true;
        int iterationCount = 0;

        do {
            List<Point> startPoints = new ArrayList<>();

            // Create the clusters based on random points
            for (int i = 0; i < K; i++) {
                // If this is the first iteration:
                if (output[i] == null) {
                    // Select K unique points from points...
                    Point InitialPoint;
                    do {
                        InitialPoint = points.get(Utils.RandInt(0, points.size() - 1));
                    } while (startPoints.contains(InitialPoint));
                    startPoints.add(InitialPoint);
                    // ...and use them as the seeds for the first generation
                    output[i] = new Cluster(InitialPoint);
                } else
                // On all other iterations:
                {
                    // Use the previous centroids as seeds for the next generation
                    final Point nextSeed = previous[i].Centroid();
                    output[i] = new Cluster(nextSeed);
                    startPoints.add(nextSeed);
                }
            }

            // Assign all points to their nearest cluster
            for (Point p : points) {
                if (!startPoints.contains(p)) { // If they aren't already a seed point
                    double minDistance = Double.MAX_VALUE;
                    Cluster bestCluster = null;
                    for (Cluster C : output) {
                        if (p.DistanceTo(C.Centroid()) < minDistance) {
                            minDistance = p.DistanceTo(C.Centroid()); // The best cluster is the cluster with the
                                                                      // closest euclidian distance to the centroid
                            bestCluster = C;
                        }
                    }
                    // Add the point to the best possible cluster
                    bestCluster.addPoint(p);
                }
            }

            if (iterationCount > 0) { // If this isn't the first iteration (hopefully) remove the original seed point
                // from each cluster
                for (int i = 0; i < K; i++) {
                    if (output[i].clusterPoints.size() > 1) // avoid empty clusters
                        output[i].removePoint(startPoints.get(i));
                }

                // Convert to lists
                final List<Cluster> prev = new ArrayList<>(Arrays.asList(previous));
                final List<Cluster> outP = new ArrayList<>(Arrays.asList(output));
                // changed = !(prev.containsAll(outP) && outP.containsAll(prev));

                // Custom check for changes (runs in O(n^2) though)
                changed = false;
                for (Cluster C_out : outP) {
                    boolean matchFound = false;
                    // Check if any of the previous clusters are the same as the current one
                    for (Cluster C_prev : prev) {
                        if (C_out.equals(C_prev)) { // Cluter.equals() checks cluterpoints match
                            matchFound = true;
                            break;
                        } else
                            matchFound = false;

                    }
                    // If a match isn't found the clustering has changed and we can stop checking
                    if (!matchFound) {
                        changed = true;
                        break;
                    }
                }
            }

            // If this has tried more than twice the number of points times break
            if (iterationCount > 2 * points.size())
                break;

            // If something has changed or this is the first iteration continue the loop
            previous = output.clone();
            iterationCount++;
        } while (changed);

        return output;
    }

    // Code stolen from:
    // https://www.geeksforgeeks.org/java-program-to-calculate-standard-deviation/
    private static double standardDeviation(double[] arr) {
        double sum = 0;
        double mean = 0;
        double standardDeviation = 0;
        double sq = 0;
        double res = 0;

        int n = arr.length;

        for (int i = 0; i < n; i++) {
            sum = sum + arr[i];
        }

        mean = sum / (n);

        for (int i = 0; i < n; i++) {

            standardDeviation = standardDeviation + Math.pow((arr[i] - mean), 2);

        }

        sq = standardDeviation / n;
        res = Math.sqrt(sq);
        return res;
    }

    // From: https://en.wikipedia.org/wiki/Mahalanobis_distance
    private static double MahanalobisDistance(Point p, Cluster C) {
        double[] distances = new double[C.clusterPoints.size()];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = C.clusterPoints.get(i).DistanceTo(C.Centroid());
        }
        final double sd = standardDeviation(distances);
        return p.DistanceTo(C.Centroid()) / sd;
    }

    // https://en.wikipedia.org/wiki/Determining_the_number_of_clusters_in_a_data_set
    public static int optimalK(List<Point> points, int MaxK) {
        /*
         * Let Y = (p/2)
         * Init a list D, of size n+1
         * Let D[0] = 0
         * For k = 1 ... n:
         * --> Cluster X with k clusters (e.g., with k-means)
         * --> Let d = Distortion of the resulting clustering
         * --> D[k] = d^(-Y)
         * Define J(i) = D[i] - D[i-1]
         * Return the k between 1 and n that maximizes J(k)
         */
        double[] D = new double[MaxK + 1];
        D[0] = 0;
        for (int k = 1; k <= MaxK; k++) {
            Cluster[] clusters = Kmeans(points, k);
            double d = 0;
            for (Cluster C : clusters) {
                d += C.Distortion();
            }
            d /= clusters.length;
            D[k] = Math.pow(d, -1); // 1 over d
            // D[k] = d;
        }

        double[] J = new double[D.length];
        for (int i = 1; i < D.length; i++) {
            J[i] = D[i] - D[i - 1];
        }
        int bestK = 1;
        double maxJ = J[1];
        for (int kTest = 1; kTest < J.length; kTest++) {
            if (J[kTest] > maxJ) {
                maxJ = J[kTest];
                bestK = kTest;
            }
        }

        /*
         * int bestK = 1;
         * double minD = Double.MAX_VALUE;
         * for (int i = 1; i < D.length; i++) {
         * if (D[i] < minD) {
         * minD = D[i];
         * bestK = i;
         * }
         * }
         */
        return bestK;
    }

    public static Triangle @NotNull [] getMeshFromFile(String filePath) {
        try {
            Scanner fileReader = new Scanner(new File(pathToMeshes + filePath + ".txt")); // Create file reader
            List<Triangle> out = new ArrayList<>(); // Create a new list of triangles
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine(); // Get the line
                String[] points = data.split(":"); // Get the array of points in the line
                Triangle t = new Triangle(0, 0, 0, 0, 0, 0); // Make a blank new triangle
                int counter = 0;
                for (String p : points) { // Iterate through points in the line
                    p = p.replace("(", "");
                    p = p.replace(")", ""); // Clean up the point
                    t.points[counter++] = new Point(Float.parseFloat(p.split(",")[0]),
                            Float.parseFloat(p.split(",")[1])); // Store the point in the triangle
                }
                out.add(t); // Add the triangle to the mesh
            }
            fileReader.close(); // Close the reader
            return out.toArray(new Triangle[0]); // And return the mesh
        } catch (FileNotFoundException e) {
            System.out.println("File: " + filePath + " cannot be located :(");
            return new Triangle[] {
                    new Triangle(0, 0, 0, 1, 1, 1),
                    new Triangle(0, 0, 1, 0, 1, 1)
            }; // Return a 1x1 square by default
        } catch (NumberFormatException e) {
            System.out.println("Invalid floating point number entered :(");
            return new Triangle[] {
                    new Triangle(0, 0, 0, 1, 1, 1),
                    new Triangle(0, 0, 1, 0, 1, 1)
            }; // Return a 1x1 square by default
        }
    }

    public static class Cluster {
        public List<Point> clusterPoints;
        private Point centroid;
        private boolean centroidNeedsUpdate = true;
        private double distortion;
        private boolean distortionNeedsUpdate = true;

        public Cluster(Point initPoint) {
            clusterPoints = new ArrayList<>();
            clusterPoints.add(initPoint);
        }

        public Cluster(List<Point> initPoints) {
            clusterPoints = new ArrayList<>(initPoints);
        }

        public void addPoint(Point p) {
            clusterPoints.add(p);
            centroidNeedsUpdate = true;
        }

        public void removePoint(Point p) {
            clusterPoints.remove(p);
            centroidNeedsUpdate = true;
        }

        public double Distortion() {
            if (distortionNeedsUpdate) {
                double sum = 0;
                for (Point p : clusterPoints) {
                    sum += MahanalobisDistance(p, this);
                    // sum += p.DistanceTo(this.Centroid());
                }
                sum /= clusterPoints.size();
                distortion = sum;
                distortionNeedsUpdate = false;
            }
            return distortion;
        }

        /**
         * @param leniency Any points more than leniency * mean distance from the
         *                 centroid are removed
         */
        public void correctErrors(double leniency) {
            double[] distances = new double[clusterPoints.size()];
            double mean = 0;
            for (int i = 0; i < distances.length; i++) {
                distances[i] = clusterPoints.get(i).DistanceTo(Centroid());
                mean += distances[i];
            }
            mean /= distances.length;
            List<Point> newClusterPoints = new ArrayList<>();
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] <= leniency * mean) {
                    newClusterPoints.add(clusterPoints.get(i));
                }
            }
            clusterPoints = new ArrayList<>();
            clusterPoints.addAll(newClusterPoints);
            centroidNeedsUpdate = true;
        }

        public Point Centroid() {
            if (centroidNeedsUpdate) {
                double x = 0;
                double y = 0;
                for (Point p : clusterPoints) {
                    x += p.x;
                    y += p.y;
                }
                x /= clusterPoints.size();
                y /= clusterPoints.size();
                centroid = new Point(x, y);
                centroidNeedsUpdate = false;
                return new Point(x, y);
            } else {
                return centroid;
            }
        }

        public boolean equals(Cluster other) {
            if (other == null) {
                return false;
            }
            return Centroid().equals(other.Centroid());
            /*
             * 
             * if (clusterPoints.size() != other.clusterPoints.size())
             * return false;
             * 
             * if (!clusterPoints.containsAll(other.clusterPoints))
             * return false;
             */
        }

        public String toString() {
            if (clusterPoints.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("Centroid: " + Centroid().toString() + "\n");
                sb.append("Cluster Points:\n");
                for (Point p : clusterPoints) {
                    sb.append("  ");
                    sb.append(p);
                    sb.append("\n");
                }
                return sb.toString();
            } else {
                return "EMPTY CLUSTER";
            }
        }
    }

    public static class Matrix {
        public Vector i = new Vector(1, 0);
        public Vector j = new Vector(0, 1);

        public Matrix(double ix, double jx,
                double iy, double jy) {
            i = new Vector(ix, iy);
            j = new Vector(jx, jy);
        }

        public Matrix() {
        }

        // https://www.mathsisfun.com/algebra/matrix-inverse.html
        public Matrix Inverse() {
            double determinant = (i.x * j.y) - (j.x * i.y);
            return ScalarMultiply(new Matrix(j.y, -j.x,
                    -i.y, i.x), 1 / determinant);
        }

        @Override
        public String toString() {
            return "[" +
                    i.x + ", " + j.x + "\n" +
                    i.y + ", " + j.y + ']';
        }
    }
}