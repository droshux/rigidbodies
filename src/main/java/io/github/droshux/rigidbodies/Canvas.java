package io.github.droshux.rigidbodies;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;

//Code lazily copy-pasted from https://gist.github.com/iiAtlas/4122531
public class Canvas extends java.awt.Canvas implements Runnable{

    final private JFrame frame;

    @SuppressWarnings("FieldCanBeLocal")
    private final int CANVAS_WIDTH = 640, CANVAS_HEIGHT = 480, FPS = 60;

    private boolean running = false;

    public List<RigidBody> Objects = new ArrayList<>();
    public Point CameraPos = new Point(0,0);
    public final int pixelsPerMeter = 50;

    public Canvas() {
        frame = new JFrame("FPS: ~ TPS: ~");
        frame.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBackground(Color.white);

        frame.add(this);
        frame.setVisible(true);
    }

    public void start() {
        if(!running) {
            running = true;
            new Thread(this).start();
        }
    }

    @SuppressWarnings("unused")
    private void stop() {
        running = false;
    }

    private void preDraw() { //Method which prepares the screen for drawing
        BufferStrategy bs = getBufferStrategy(); //Gets the buffer strategy our canvas is currently using
        if(bs == null) { //True if our canvas has no buffer strategy (should only happen once when we first start the game)
            createBufferStrategy(2); //Create a buffer strategy using two buffers (double buffer the canvas)
            return; //Break out of the preDraw method instead of continuing on, this way we have to check again if bs == null instead of just assuming createBufferStrategy(2) worked
        }

        Graphics g = bs.getDrawGraphics(); //Get the graphics from our buffer strategy (which is connected to our canvas)
        g.setColor(getBackground());
        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT); //Fill the screen with the canvas' background color
        g.setColor(getForeground());

        draw(g); //Call our draw method, passing in the graphics object which we just got from our buffer strategy

        g.dispose(); //Dispose of our graphics object because it is no longer needed, and unnecessarily taking up memory
        bs.show(); //Show the buffer strategy, flip it if necessary (make back buffer the visible buffer and vice versa)
    }

    private void draw(Graphics g) {
        for (RigidBody rb : Objects) {
            DrawRigidbody(rb, g);
        }
    }

    private void tick(@SuppressWarnings("unused") double delta) {
        for (RigidBody rb : Objects) {
            rb.Update(delta/1000);
        }
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {

        long lastTime = System.currentTimeMillis(); //Time since we last looped (tick + draw), initialized here to the current time
        long secondTime = lastTime + 1000; //Target time one second ahead of when we last updated fps/tps
        double msPerTick = 1000d / FPS; //Milliseconds expected in a single tick

        int frames = 0, ticks = 0; //Used for counting frames and ticks while in between seconds, later used to set fps and tps
        double delta = 0; //Represents the time passed since last tick

        boolean needsRender = false; //True when the screen is dirty (when we have ticked)

        while(running) {
            long currentTime = System.currentTimeMillis(); //The time when we began our game loop
            long timeDifference = currentTime - lastTime; //The difference in time since the last game loop (expressed as a negative value
            lastTime = currentTime; //Reassign lastTime since we have used it, the last time is now the current time

            delta += timeDifference / msPerTick; //A representation of the time passed, converted into a more manageable format

            while(delta >= 1) { //Tick as many times as delta is greater then one
                ticks++; //Increase temporary ticks variable, later used to set tps
                tick(delta); //Call the tick method, pass it delta, so it can alter any movement accordingly
                delta--; //Decrease delta because we have used it

                needsRender = true; //Informs us that the screen is dirty, must be rendered
            }

            if(needsRender) {
                frames++; //Increase temporary frames variable, later used to set fps
                preDraw(); //Draw the screen (or at least call the method which draws the screen)
            }

            if(System.currentTimeMillis() >= secondTime) { //True when the current time is equal to (or greater than) one second since we last updated fps/tps
                int fps = frames; //set fps to the frames variable which has been increasing over the last second
                int tps = ticks; //set tps to the frames variable which has been increasing over the last second
                frames = 0; //reset frame count
                ticks = 0; //reset tick count

                frame.setTitle("FPS: " + fps + " TPS: " + tps);

                secondTime = System.currentTimeMillis() + 1000; //Set the time which we must again update fps/tps (one second from the current time)
            }

            try { Thread.sleep(10); } catch(Exception e) { e.printStackTrace(); } //Attempt to sleep the thread for 10ms, not necessary to run it nonstop even though our game will behave okay
            needsRender = false;
        }
    }

    public int[] WorldToScreenPos(Point p) {

        double deltaX = p.x - CameraPos.x;
        double deltaY = CameraPos.y - p.y;
        int[] out = new int[2];
        out[0] = (int) Math.round(deltaX * pixelsPerMeter + (CANVAS_WIDTH/2f));
        out[1] = (int) Math.round(deltaY * pixelsPerMeter + (CANVAS_HEIGHT / 2f));
        return out;
    }

    public void DrawRigidbody(RigidBody rb, Graphics g) {
        g.setColor(rb.Colour); //Set colour
        //Repeat for every triangle in the mesh
        for (Triangle t : rb.Collider) {

            //Create lists of x and y coordinates
            List<Integer> xPoints = new ArrayList<>(); List<Integer> yPoints = new ArrayList<>();
            for (Point p : t.points) {
                Point vertexPos = new Point(rb.Position.x + p.x, rb.Position.y + p.y);
                xPoints.add(WorldToScreenPos(vertexPos)[0]);
                yPoints.add(WorldToScreenPos(vertexPos)[1]);
            }

            //Copy lists to array to convert Integer to int (Don't ask)
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