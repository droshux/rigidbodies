package io.github.droshux.rigidbodies;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {

    public static final String pathToMeshes = "src/main/resources/meshes/";
    public static final Vector g = new Vector(0, -9.81);
    public static final Matrix rotate90 = new Matrix(0, -1, 1, 0);

    @Contract("_, _ -> new")
    public static @NotNull Vector VectorAdd(@NotNull Vector v1, @NotNull Vector v2) {
        return new Vector(v1.x + v2.x, v1.y + v2.y);
    }
    @Contract("_, _ -> new")
    public static @NotNull Vector VectorScalarMultiply(@NotNull Vector v1, double Scalar) {
        return new Vector(v1.x * Scalar, v1.y * Scalar);
    }

    //From: https://www.mathsisfun.com/algebra/vectors-dot-product.html
    public static double DotProduct(Vector v1, Vector v2) {return v1.x * v2.x + v1.y * v2.y;}

    @Contract("_, _ -> new")
    public static @NotNull Matrix ScalarMultiply(@NotNull Matrix M, double scalar) {return new Matrix(scalar * M.i.x, scalar * M.j.x,
                                                                                scalar * M.i.y, scalar * M.j.y);}

    public static double Round(double input, int sf) {
        BigDecimal bigDecimal = new BigDecimal(input);
        bigDecimal = bigDecimal.round(new MathContext(sf));
        return bigDecimal.floatValue();
    }

    public static double getGradient(@NotNull Point p1, @NotNull Point p2) {return Math.abs(p1.y - p2.y) / Math.abs(p1.x - p2.x);}
    @SuppressWarnings("unused")
    public static double get_Y_intercept(@NotNull Point p1, Point p2) {return p1.y - getGradient(p1, p2) * p1.x;}

    public static Triangle @NotNull [] getMeshFromFile(String filePath) {
        try {
            Scanner fileReader = new Scanner(new File(pathToMeshes + filePath + ".txt")); //Create file reader
            List<Triangle> out = new ArrayList<>(); //Create a new list of triangles
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine(); //Get the line
                String[] points = data.split(":"); //Get the array of points in the line
                Triangle t = new Triangle(0, 0, 0, 0, 0, 0); //Make a blank new triangle
                int counter = 0;
                for (String p : points) { //Iterate through points in the line
                    p = p.replace("(", ""); p = p.replace(")", ""); //Clean up the point
                    t.points[counter++] = new Point(Float.parseFloat(p.split(",")[0]), Float.parseFloat(p.split(",")[1])); //Store the point in the triangle
                }
                out.add(t); //Add the triangle to the mesh
            }
            fileReader.close(); //Close the reader
            return out.toArray(new Triangle[0]); //And return the mesh
        } catch (FileNotFoundException e) {
            System.out.println("File: " + filePath + " cannot be located :(");
            return new Triangle[]{
                    new Triangle(0, 0, 0, 1, 1, 1),
                    new Triangle(0, 0, 1, 0, 1, 1)
            }; //Return a 1x1 square by default
        } catch (NumberFormatException e) {
            System.out.println("Invalid floating point number entered :(");
            return new Triangle[]{
                    new Triangle(0, 0, 0, 1, 1, 1),
                    new Triangle(0, 0, 1, 0, 1, 1)
            }; //Return a 1x1 square by default
        }
    }

    public static class Matrix {
        public Vector i = new Vector(1, 0); public Vector j = new Vector(0, 1);
        public Matrix(double ix, double jx,
                      double iy, double jy) {i = new Vector(ix, iy); j = new Vector(jx, jy);}
        @SuppressWarnings("unused")
        public Matrix() {}

        //https://www.mathsisfun.com/algebra/matrix-inverse.html
        @SuppressWarnings("unused")
        public Matrix Inverse() {
            double determinant = (i.x * j.y) - (j.x * i.y);
            return ScalarMultiply(new Matrix(j.y, -j.x,
                                            -i.y, i.x), 1/determinant);
        }

        @Override
        public String toString() {
            return "[" +
                    i.x + ", " + j.x + "\n" +
                    i.y + ", " + j.y + ']';
        }
    }
}