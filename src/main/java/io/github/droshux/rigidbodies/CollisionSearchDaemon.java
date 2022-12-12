package io.github.droshux.rigidbodies;

import java.util.ArrayList;
import java.util.List;

public class CollisionSearchDaemon {
	private final Point bottomLeft;
	private final Point topRight;
	private final Canvas canvasRef;
	private final RigidBody rb1;
	private final RigidBody rb2;
	public int generation;

	public CollisionSearchDaemon(Point BL, Point TR, Canvas ref, RigidBody RB1, RigidBody RB2, int generation) {
		this.bottomLeft = BL;
		this.topRight = TR;
		this.canvasRef = ref;
		this.rb1 = RB1;
		this.rb2 = RB2;
		this.generation = generation;
	}

	public CollisionSearchDaemon.DaemonResults Search() {
		List<CollisionSearchDaemon> daemonBuffer = new ArrayList<>();
		List<Point> outputPoints = new ArrayList<>();

		final double dX = (topRight.x - bottomLeft.y) / canvasRef.COLLISION_BREADTH;
		final double dY = (topRight.y - bottomLeft.y) / canvasRef.COLLISION_BREADTH;

		// Iterate through the given space
		for (double x = bottomLeft.x; x < topRight.x; x += dX) {
			for (double y = bottomLeft.y; y < topRight.y; y += dY) {

				// Check the point
				final Point checkPoint = new Point(x, y);
				if (rb1.contains(checkPoint)
						&& rb2.contains(checkPoint)) {
					// If this is the final generation add the point to the list
					if (this.generation == canvasRef.COLLISION_DEPTH) {
						outputPoints.add(checkPoint);
					} else {
						final int g = this.generation + 1;
						daemonBuffer.add(
								new CollisionSearchDaemon( // Otherwise create a new daemon!s
										checkPoint,
										new Point(checkPoint.x + dX, checkPoint.y + dY),
										canvasRef,
										rb1,
										rb2,
										g));
					}
				}
			}
		}

		return new DaemonResults(daemonBuffer, outputPoints);
	}

	public static record DaemonResults(List<CollisionSearchDaemon> daemonBuffer, List<Point> outputPoints) {
	}

	public static List<Point> SpaceSearch(Point BL, Point TR, int generation, Canvas canvas, RigidBody RB1,
			RigidBody RB2) {
		List<Point> outPoints = new ArrayList<>();
		final double dy = (TR.y - BL.y) / canvas.COLLISION_BREADTH;
		final double dx = (TR.x - BL.x) / canvas.COLLISION_BREADTH;

		for (double y = BL.y; y <= TR.y; y += dy) {
			for (double x = BL.x; x <= TR.x; x += dx) {
				Point check = new Point(x, y);
				if (RB1.contains(check) && RB2.contains(check)) {
					if (generation < canvas.COLLISION_DEPTH) {
						outPoints.addAll(SpaceSearch(
								new Point(check.x - dx, check.y - dy),
								new Point(check.x + dx, check.y + dy),
								generation + 1, canvas, RB1, RB2));
					} else {
						outPoints.add(check);
					}
				}
			}
		}

		return outPoints;
	}
}
