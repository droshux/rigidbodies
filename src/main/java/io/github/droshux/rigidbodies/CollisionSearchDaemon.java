package io.github.droshux.rigidbodies;

import java.util.ArrayList;
import java.util.List;

public class CollisionSearchDaemon {
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
