package io.github.droshux.rigidbodies;

import javax.swing.*;
import java.awt.*;

public class Program {

    public static CanvasTemplate Canvas;

    public static void main(String[] args) {
        Canvas = new CanvasTemplate();
        RigidBody r1 = new RigidBody("test1", 5, new Point(0,0), Color.BLUE, "mesh2.txt");
        SwingUtilities.invokeLater(() -> {
            /*for (int i = 0; i < 5000; i++) {
                Canvas.repaint();
                Canvas.Update(i);
            }*/
            r1.Rotate(Math.toRadians(45));
            Canvas.repaint();
        });
    }
}