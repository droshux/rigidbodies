package io.github.droshux.rigidbodies;

import java.util.List;

public class CollisionSearchDaemon {
	private final Point bottomLeft;
	private final Point topRight;
	private final Canvas canvasRef;
	private final RigidBody rb1;
	private final RigidBody rb2;
	private final int generation;
	private final List<CollisionSearchDaemon> daemonBuffer;
	private final List<Point> outputPoints;

	 public CollisionSearchDaemon(Point BL, Point TR, Canvas ref, RigidBody RB1, RigidBody RB2, int generation, List<CollisionSearchDaemon> daemonBufferRef, List<Point> outputPoints) {
		 this.bottomLeft = BL; this.topRight = TR; this.canvasRef = ref; this.rb1 = RB1; this.rb2 = RB2; this.generation = generation; this.daemonBuffer = daemonBufferRef; this.outputPoints = outputPoints;
	}

	public void Search() {
		 final double dX = (topRight.x - bottomLeft.y) / canvasRef.COLLISION_BREADTH;
		 final double dY = (topRight.y - bottomLeft.y) / canvasRef.COLLISION_BREADTH;

		 System.out.println("Searching from "+ bottomLeft + " to " + topRight);
		 //Iterate through the given space
		 for (double x = bottomLeft.x; x < topRight.x; x += dX) {
			 for (double y = bottomLeft.y; y < topRight.y; y += dY) {

				 //Check the point
				 final Point checkPoint = new Point(x, y);
				 System.out.println("Checking: " + checkPoint);
				 if (rb1.contains(checkPoint) && rb2.contains(checkPoint)) {
					 System.out.println("FOUND A HIT @ " + checkPoint);
					 //If this is the final generation add the point to the list
					 if (generation == canvasRef.COLLISION_DEPTH) outputPoints.add(checkPoint);
					 else daemonBuffer.add(new CollisionSearchDaemon(checkPoint, new Point(checkPoint.x + dX, checkPoint.y + dY), canvasRef, rb1, rb2, generation + 1, daemonBuffer, outputPoints)); //Otherwise create a new daemon
				 }
			 }
		 }

		 daemonBuffer.remove(this);//KYS!
	}
}