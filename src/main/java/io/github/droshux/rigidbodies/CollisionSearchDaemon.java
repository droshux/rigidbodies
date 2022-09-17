package io.github.droshux.rigidbodies;

import java.util.List;

public class CollisionSearchDaemon {
	private final Point bottomLeft;
	private final Point topRight;
	private final Canvas canvasRef;
	private final RigidBody rb1;
	private final RigidBody rb2;
	public int generation;
	private final List<CollisionSearchDaemon> daemonBuffer;
	private final List<Point> outputPoints;

	public CollisionSearchDaemon(Point BL, Point TR, Canvas ref, RigidBody RB1, RigidBody RB2, int generation,
			List<CollisionSearchDaemon> daemonBufferRef, List<Point> outputPoints) {
		this.bottomLeft = BL;
		this.topRight = TR;
		this.canvasRef = ref;
		this.rb1 = RB1;
		this.rb2 = RB2;
		this.generation = generation;
		this.daemonBuffer = daemonBufferRef;
		this.outputPoints = outputPoints;
	}

	public void Search() {
		final double dX = (topRight.x - bottomLeft.y) / canvasRef.COLLISION_BREADTH;
		final double dY = (topRight.y - bottomLeft.y) / canvasRef.COLLISION_BREADTH;

		// System.out.println("Searching from " + bottomLeft + " to " + topRight);
		// Iterate through the given space
		for (double x = bottomLeft.x; x < topRight.x; x += dX) {
			for (double y = bottomLeft.y; y < topRight.y; y += dY) {

				// Check the point
				final Point checkPoint = new Point(x, y);
				// System.out.println("Checking: " + checkPoint);
				if (rb1.contains(checkPoint) && rb2.contains(checkPoint)) {
					// System.out.println("FOUND A HIT @ " + checkPoint);
					// If this is the final generation add the point to the list
					System.out.println("Generation: " + generation + " Depth: " + canvasRef.COLLISION_DEPTH);
					if (this.generation == canvasRef.COLLISION_DEPTH) {
						System.out.println("Before: " + outputPoints.size());
						outputPoints.add(checkPoint);
						System.out.println("Before: " + outputPoints.size());
					} else {
						System.out.println("Adding new daemon..." + (generation + 1));
						final int g = this.generation + 1;
						daemonBuffer.add(
								new CollisionSearchDaemon( // Otherwise create a new daemon!s
										checkPoint,
										new Point(checkPoint.x + dX, checkPoint.y + dY),
										canvasRef,
										rb1,
										rb2,
										g,
										daemonBuffer,
										outputPoints));
					}
				}
			}
		}

		daemonBuffer.remove(this);// KYS!
	}
}