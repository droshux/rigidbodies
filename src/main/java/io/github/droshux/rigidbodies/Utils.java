package io.github.droshux.rigidbodies;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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
        Cluster[] output = new Cluster[K];
        Cluster[] previous = new Cluster[K];
        do {
            // Create the clusters based on random points
            List<Point> startPoints = new ArrayList<>();
            for (int i = 0; i < K; i++) {
                if (output[i] == null) {
                    Point InitialPoint = points.get(RandInt(0, points.size() - 1));
                    while (startPoints.contains(InitialPoint)) {
                        InitialPoint = points.get(RandInt(0, points.size() - 1));
                    }
                    startPoints.add(InitialPoint);
                    output[i] = new Cluster(InitialPoint);
                } else {
                    output[i] = new Cluster(previous[i].Centroid());
                }
            }

            // Assign all points to their nearest cluster
            for (Point p : points) {
                if (!startPoints.contains(p)) {
                    double minDistance = Double.MAX_VALUE;
                    Cluster bestCluster = output[0];
                    for (Cluster C : output) {
                        if (p.DistanceTo(C.Centroid()) < minDistance) {
                            minDistance = p.DistanceTo(C.Centroid());
                            bestCluster = C;
                        }
                    }
                    bestCluster.addPoint(p);
                }
            }

            // If nothing has changed: exit the loop
            List<Cluster> prev = new ArrayList<>(Arrays.asList(previous));
            List<Cluster> outP = new ArrayList<>(Arrays.asList(output));
            if (!(prev.containsAll(outP) && outP.containsAll(prev)))
                break;
        } while (true);

        for (int i = 0; i < output.length; i++) {
            output[i].correctErrors(1.5);
        }

        return output;
    }

    // Code stolen from:
    // https://www.geeksforgeeks.org/java-program-to-calculate-standard-deviation/
    private double standardDeviation(double[] arr) {
        double sum = 0;
        double mean = 0;
        double standardDeviation = 0;
        double sq = 0;
        double res = 0;

        int n = arr.length;

        System.out.println("Elements are:");
        for (int i = 0; i < n; i++) {
            System.out.println(arr[i]);
        }

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
    private double MahanalobisDistance(Point p, Cluster C) {
        double[] distances = new double[C.clusterPoints.size()];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = C.clusterPoints.get(i).DistanceTo(C.Centroid());
        }
        final double sd = standardDeviation(distances);
        return p.DistanceTo(C.Centroid()) / sd;
    }

    // https://en.wikipedia.org/wiki/Determining_the_number_of_clusters_in_a_data_set
    public int optimalK(List<Point> points) {
        /*
         * JumpMethod(X):
         * Let Y = (p/2)
         * Init a list D, of size n+1
         * Let D[0] = 0
         * For k = 1 ... n:
         * Cluster X with k clusters (e.g., with k-means)
         * Let d = Distortion of the resulting clustering
         * D[k] = d^(-Y)
         * Define J(i) = D[i] - D[i-1]
         * Return the k between 1 and n that maximizes J(k)
         */
        return 3; // TODO implement this method
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
        private boolean needsUpdate = true;

        public Cluster(Point initPoint) {
            clusterPoints = new ArrayList<>();
            clusterPoints.add(initPoint);
        }

        public void addPoint(Point p) {
            clusterPoints.add(p);
            needsUpdate = true;
        }

        public void removePoint(Point p) {
            clusterPoints.remove(p);
            needsUpdate = true;
        }

        /**
         * @param leniency Any points more than leniency * mean distance from the
         *                 centroid are removed
         */
        public void correctErrors(double leniency) {
            double[] distances = new double[clusterPoints.size()];
            double mean = 0;
            for (int i = 0; i < distances.length; i++) {
                distances[i] = clusterPoints.get(i).DistanceTo(centroid);
                mean += distances[i];
            }
            mean /= distances.length;
            /*
             * double standardDeviation = 0;
             * for (int i = 0; i < distances.length; i++) {
             * standardDeviation += (distances[i] - mean) * (distances[i] - mean);
             * }
             * standardDeviation = Math.sqrt(standardDeviation / distances.length);
             */
            List<Point> newClusterPoints = new ArrayList<>();
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] <= leniency * mean) {
                    newClusterPoints.add(clusterPoints.get(i));
                } else
                    System.out.println("Error corrected!");
            }
            clusterPoints = new ArrayList<>();
            clusterPoints.addAll(newClusterPoints);
            needsUpdate = true;
        }

        public Point Centroid() {
            if (needsUpdate) {
                double x = 0;
                double y = 0;
                for (Point p : clusterPoints) {
                    x += p.x;
                    y += p.y;
                }
                x /= clusterPoints.size();
                y /= clusterPoints.size();
                centroid = new Point(x, y);
                needsUpdate = false;
                return new Point(x, y);
            } else {
                return centroid;
            }
        }

        public boolean equals(Cluster other) {
            if (other == null) {
                return false;
            }

            if (clusterPoints.size() != other.clusterPoints.size())
                return false;

            if (Centroid() != other.Centroid())
                return false;

            for (Point p : clusterPoints)
                if (!other.clusterPoints.contains(p))
                    return false;

            return true;
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