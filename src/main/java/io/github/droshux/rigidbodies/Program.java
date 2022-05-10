package io.github.droshux.rigidbodies;

import javax.swing.*;
import java.awt.*;

public class Program {

    public static CanvasTemplate Canvas;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           Canvas = new CanvasTemplate();
            RigidBody r1 = new RigidBody("test1", 5, new Point(0,0), Color.BLUE, "mesh2.txt");
            Thread renderer = new Thread() {
                public void run() {
                    System.out.println("RENDER LOOP BEGAN");
                    while (true) {//for (int i = 0; i < 5000; i++) {
                        //System.out.println("PAINTING");
                        Canvas.repaint();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            renderer.start();
            for (int i = 0; i < 5000; i++) {
                Canvas.Update(i);
            }
        });
    }
}
