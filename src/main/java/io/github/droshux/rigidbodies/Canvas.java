package io.github.droshux.rigidbodies;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//Code lazily copy-pasted from https://gist.github.com/iiAtlas/4122531
public class Canvas extends java.awt.Canvas implements Runnable {

    final private JFrame frame;

    @SuppressWarnings("FieldCanBeLocal")
    private final int CANVAS_WIDTH, CANVAS_HEIGHT, FPS = 60;

    public final int COLLISION_BREADTH;
    public final int COLLISION_DEPTH;

    private boolean running = false;

    public List<RigidBody> Objects = new ArrayList<>();
    public Point CameraPos = new Point(0, 0);
    public final int pixelsPerMeter = 25;
    public double timePassed = 0;
    public boolean displayTime = false;

    public Canvas(int W, int H, int B, int D) {

        CANVAS_WIDTH = W;
        CANVAS_HEIGHT = H;
        COLLISION_BREADTH = B;
        COLLISION_DEPTH = D;

        frame = new JFrame("FPS: ~ TPS: ~");
        frame.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBackground(Color.white);

        frame.add(this);
        frame.setVisible(true);
    }

    public void start() {
        if (!running) {
            running = true;
            new Thread(this).start();
        }
    }

    @SuppressWarnings("unused")
    private void stop() {
        running = false;
    }

    private void preDraw() { // Method which prepares the screen for drawing
        BufferStrategy bs = getBufferStrategy(); // Gets the buffer strategy our canvas is currently using
        if (bs == null) { // True if our canvas has no buffer strategy (should only happen once when we
                          // first start the game)
            createBufferStrategy(2); // Create a buffer strategy using two buffers (double buffer the canvas)
            return; // Break out of the preDraw method instead of continuing on, this way we have to
                    // check again if bs == null instead of just assuming createBufferStrategy(2)
                    // worked
        }

        Graphics g = bs.getDrawGraphics(); // Get the graphics from our buffer strategy (which is connected to our
                                           // canvas)
        g.setColor(getBackground());
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT); // Fill the screen with the canvas' background color
        g.setColor(getForeground());

        draw(g); // Call our draw method, passing in the graphics object which we just got from
                 // our buffer strategy

        g.dispose(); // Dispose of our graphics object because it is no longer needed, and
                     // unnecessarily taking up memory
        bs.show(); // Show the buffer strategy, flip it if necessary (make back buffer the visible
                   // buffer and vice versa)
    }

    private void draw(Graphics g) {
        for (RigidBody rb : Objects) {
            DrawRigidbody(rb, g);
        }
    }

    private void tick(double delta) {
        timePassed += delta / 100;
        // Utils.clearScreen();
        if (displayTime)
            System.out.println(Utils.Round(timePassed, 3));
        for (RigidBody rb : Objects) {
            rb.Update(delta / 100);
        }

        // Collision Check:
        // Broad phase
        List<RigidBody[]> xIntersects = getBroadPhaseCollisionPairs(false);
        List<RigidBody[]> yIntersects = getBroadPhaseCollisionPairs(true);
        List<RigidBody[]> CollisionPairs = new ArrayList<>();
        for (RigidBody[] rbAx : xIntersects) {
            for (RigidBody[] rbAy : yIntersects) {
                if (Arrays.stream(rbAx).toList().contains(rbAy[0]) || Arrays.stream(rbAx).toList().contains(rbAy[1])) {
                    CollisionPairs.add(rbAx);
                }
            }
        }

        if (CollisionPairs.size() > 0) {
            for (RigidBody[] rbA : CollisionPairs) {
                List<Point> cPoints = narrowPhase(rbA[0], rbA[1]);
                if (cPoints.size() > 0) {
                    System.out.println("RB1 INVOLVED: " + rbA[0].toString());
                    System.out.println("RB2 INVOLVED: " + rbA[1].toString());
                    System.out.println("Running K means on " + cPoints.size() + " points:");
                    for (Point p : cPoints)
                        System.out.println("    " + p.toString());
                    final int K = Utils.optimalK(cPoints, cPoints.size());
                    System.out.println("??????????????????? K: " + K + " ???????????????????");
                    Utils.Cluster[] clusters = Utils.Kmeans(cPoints, K);
                    rbA[0].Velocity = new Vector();
                    rbA[0].Position = new Point(100, 100);
                    for (Utils.Cluster C : clusters)
                        System.out.println("Size: " + C.clusterPoints.size() + "\nCentroid: " + C.Centroid() + "\n");
                }

            }
        }
    }

    private List<Point> narrowPhase(RigidBody rb1, RigidBody rb2) {
        final Point BL = new Point(Math.min(rb1.BoundingBox[0].x, rb2.BoundingBox[0].x),
                Math.min(rb1.BoundingBox[0].y, rb2.BoundingBox[0].y));
        final Point TR = new Point(Math.max(rb1.BoundingBox[1].x, rb2.BoundingBox[1].x),
                Math.max(rb1.BoundingBox[1].y, rb2.BoundingBox[1].y));

        return CollisionSearchDaemon.SpaceSearch(BL, TR, 0, this, rb1, rb2);
        /*
         * List<CollisionSearchDaemon> daemonList = new ArrayList<>(); // DEMON CORE!!
         * List<CollisionSearchDaemon> daemonBuffer = new ArrayList<>();
         * List<Point> collisionPoints = new ArrayList<>();
         * 
         * daemonList.add(new CollisionSearchDaemon(BL, TR, this, rb1, rb2, 0));
         * while (daemonList.size() > 0) {
         * daemonBuffer = new ArrayList<>();
         * // System.out.println("DAEMON LIST LENGTH: " + daemonList.size());
         * for (int i = 0; i < daemonList.size(); i++) {
         * CollisionSearchDaemon daemon = daemonList.get(i); // Get a daemon from the
         * list
         * final CollisionSearchDaemon.DaemonResults sResults = daemon.Search(); //
         * Search space for all points
         * daemonBuffer.addAll(sResults.daemonBuffer()); // take the new generation out
         * of the record
         * collisionPoints.addAll(sResults.outputPoints()); // take the collision point
         * out the record
         * }
         * 
         * // Overwrite the daemons with the buffer
         * daemonList = new ArrayList<>(daemonBuffer);
         * }
         */

    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {

        long lastTime = System.currentTimeMillis(); // Time since we last looped (tick + draw), initialized here to the
                                                    // current time
        long secondTime = lastTime + 1000; // Target time one second ahead of when we last updated fps/tps
        double msPerTick = 1000d / FPS; // Milliseconds expected in a single tick

        int frames = 0, ticks = 0; // Used for counting frames and ticks while in between seconds, later used to
                                   // set fps and tps
        double delta = 0; // Represents the time passed since last tick

        boolean needsRender = false; // True when the screen is dirty (when we have ticked)

        while (running) {
            long currentTime = System.currentTimeMillis(); // The time when we began our game loop
            long timeDifference = currentTime - lastTime; // The difference in time since the last game loop (expressed
                                                          // as a negative value
            lastTime = currentTime; // Reassign lastTime since we have used it, the last time is now the current
                                    // time

            delta += timeDifference / msPerTick; // A representation of the time passed, converted into a more
                                                 // manageable format

            while (delta >= 1) { // Tick as many times as delta is greater then one
                ticks++; // Increase temporary ticks variable, later used to set tps
                tick(delta); // Call the tick method, pass it delta, so it can alter any movement accordingly
                delta--; // Decrease delta because we have used it

                needsRender = true; // Informs us that the screen is dirty, must be rendered
            }

            if (needsRender) {
                frames++; // Increase temporary frames variable, later used to set fps
                preDraw(); // Draw the screen (or at least call the method which draws the screen)
            }

            if (System.currentTimeMillis() >= secondTime) { // True when the current time is equal to (or greater than)
                                                            // one second since we last updated fps/tps
                int fps = frames; // set fps to the frames variable which has been increasing over the last second
                int tps = ticks; // set tps to the frames variable which has been increasing over the last second
                frames = 0; // reset frame count
                ticks = 0; // reset tick count

                frame.setTitle("FPS: " + fps + " TPS: " + tps);

                secondTime = System.currentTimeMillis() + 1000; // Set the time which we must again update fps/tps (one
                                                                // second from the current time)
            }

            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            } // Attempt to sleep the thread for 10ms, not necessary to run it nonstop even
              // though our game will behave okay
            needsRender = false;
        }
    }

    public int[] WorldToScreenPos(Point p) {

        double deltaX = p.x - CameraPos.x;
        double deltaY = CameraPos.y - p.y;
        int[] out = new int[2];
        out[0] = (int) Math.round(deltaX * pixelsPerMeter + (CANVAS_WIDTH / 2f));
        out[1] = (int) Math.round(deltaY * pixelsPerMeter + (CANVAS_HEIGHT / 2f));
        return out;
    }

    public void DrawRigidbody(RigidBody rb, Graphics g) {
        g.setColor(rb.Colour); // Set colour
        // Repeat for every triangle in the mesh
        for (Triangle t : rb.Collider) {

            // Create lists of x and y coordinates
            List<Integer> xPoints = new ArrayList<>();
            List<Integer> yPoints = new ArrayList<>();
            for (Point p : t.points) {
                Point vertexPos = new Point(rb.Position.x + p.x, rb.Position.y + p.y);
                xPoints.add(WorldToScreenPos(vertexPos)[0]);
                yPoints.add(WorldToScreenPos(vertexPos)[1]);
            }

            // Copy lists to array to convert Integer to int (Don't ask)
            int[] x = new int[xPoints.size()];
            for (int i = 0; i < x.length; i++)
                x[i] = xPoints.get(i);
            int[] y = new int[yPoints.size()];
            for (int i = 0; i < y.length; i++)
                y[i] = yPoints.get(i);

            // Draw the triangle
            Polygon triangle = new Polygon(x, y, 3);
            g.fillPolygon(triangle);
        }
    }

    private List<RigidBody[/* Length 2 */]> getBroadPhaseCollisionPairs(boolean useYaxis) {
        List<RigidBody[]> output = new ArrayList<>();
        List<sortAndSweepValue> values = new ArrayList<>();
        for (RigidBody rb : Objects) {
            if (useYaxis) {
                values.add(new sortAndSweepValue(rb.BoundingBox[0].y, true, rb));
                values.add(new sortAndSweepValue(rb.BoundingBox[1].y, false, rb));
            } else {
                values.add(new sortAndSweepValue(rb.BoundingBox[0].x, true, rb));
                values.add(new sortAndSweepValue(rb.BoundingBox[1].x, false, rb));
            }
        } // Project the two corners of the bounding box onto the x-axis and mark them as
          // beginning or end
        Collections.sort(values); // Then sort these values
        List<sortAndSweepValue> activeIntervals = new ArrayList<>();
        for (sortAndSweepValue v : values) { // Sweep through them
            // However if it is an end then find and remove the corresponding end
            if (v.isBeginning) {
                activeIntervals.add(v);
                // If the value is a beginning of an interval add it to the list of active
                // intervals
                for (sortAndSweepValue other : activeIntervals)
                    if (!other.equals(v))
                        output.add(new RigidBody[] { v.parent, other.parent }); // and add pairs with all active
                                                                                // intervals to output
            } else
                activeIntervals.removeIf(b -> b.isBeginning && b.parent.equals(v.parent));
            // if it is an end remove the corresponding beginning from active intervals
        }
        return output;
    }

    private record sortAndSweepValue(double value, boolean isBeginning, RigidBody parent)
            implements Comparable<sortAndSweepValue> {
        @Override
        public int compareTo(sortAndSweepValue other) {
            return Double.compare(value, other.value);
        }
    }
}