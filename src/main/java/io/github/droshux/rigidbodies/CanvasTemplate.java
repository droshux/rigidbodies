package io.github.droshux.rigidbodies;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CanvasTemplate extends JFrame {

    public static final int CANVAS_WIDTH = 640;
    public static final int CANVAS_HEIGHT = 480;
    public static final int FPS = 60;

    private static int n = 0;

    public List<RigidBody> Objects = new ArrayList<>();

    public Point CameraPos = new Point(0, -0.5f);
    public double pixelsPerMeter = 50f;

    private CanvasDrawer canvas;

    public CanvasTemplate() {
        canvas = new CanvasDrawer();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        Container cp = getContentPane();
        cp.add(canvas);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setTitle("TEST");
        setVisible(true);
    }

    private class CanvasDrawer extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            //super.paintComponent(g);
            setBackground(Color.WHITE);

            System.out.println("Rendering");
            for (RigidBody r : Objects) {
                DrawRigidbody(r, g);
            }
        }

        public int @NotNull [] WorldToScreenPos(Point p) {

            double deltaX = p.x - CameraPos.x;
            double deltaY = p.y - CameraPos.y;
            int[] out = new int[2];
            out[0] = (int) Math.round(deltaX * pixelsPerMeter + (CANVAS_WIDTH/2));
            out[1] = (int) Math.round(deltaY * pixelsPerMeter + (CANVAS_HEIGHT / 2));
            return out;
        }

        public void DrawRigidbody(RigidBody rb, Graphics g) {
            g.setColor(rb.Colour); //Set colour
            System.out.println(n); n++;
            //Repeat for every triangle in the mesh
            for (Triangle t : rb.Collider) {

                //Create lists of x and y coords
                List<Integer> xPoints = new ArrayList<>(); List<Integer> yPoints = new ArrayList<>();
                for (Point p : t.points) {
                    Point vertexPos = new Point(rb.Position.x + p.x, rb.Position.y + p.y);
                    xPoints.add(WorldToScreenPos(vertexPos)[0]);
                    yPoints.add(WorldToScreenPos(vertexPos)[1]);
                }

                //Copy lists to arrays to convert Integer to int (Don't ask)
                int[] x = new int[xPoints.size()];
                for (int i = 0; i < x.length; i++) x[i] = xPoints.get(i);
                int[] y = new int[yPoints.size()];
                for (int i = 0; i < y.length; i++) y[i] = yPoints.get(i);

                //Draw the triangle
                Polygon triangle = new Polygon(x, y, 3);
                g.fillPolygon(triangle);
            }
        }
    }

    @Contract(pure = true)
    private @Nullable RigidBody getRigidbodyByID(String id) {
        for (RigidBody rb : Objects) {
            if (rb.id.equals(id)) return rb; }
        return null;
    }

    public void Update(int frameNumber) {
        long FrameStart = System.nanoTime();

        //CODE THAT RUNS EVERY FRAME
        /*try {
            java.util.Objects.requireNonNull(getRigidbodyByID("test1")).RotateDegrees(1);
        } catch (NullPointerException e) {System.out.println("test1 does not exist");}*/

        //canvas.repaint();
        //System.out.println("Canvas has been painted");

        //Ensure running at the right FPS
        double FrameTime = System.nanoTime() - FrameStart;
        System.out.println("Frame " + frameNumber + " took " + FrameTime + " ns");
        double IdealFrameTime = (1f / FPS) * 1000000000f; //Ideal frame time in nanoseconds
        if (FrameTime < IdealFrameTime) {
            try {
                System.out.println("Waiting: " + (IdealFrameTime - FrameTime) / 1000000 + "ms");
                Thread.sleep((long)(IdealFrameTime - FrameTime) / 1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}