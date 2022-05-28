package io.github.droshux.rigidbodies;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RigidBody {
    public String id;
    public  Triangle[] Collider;
    public Point Position;
    public double Mass; //In kilograms
    public final boolean UseGravity;
    public Color Colour;
    public double elasticity;
    public double rigidity;

    public Canvas canvas;

    public List<Vector> Forces = new ArrayList<>();
    public Vector Weight;

    public RigidBody(String id, float mass, Point position, Color col, String colliderFile, Canvas canvasTemplate, boolean useGravity, double elasticity, double rigidity) {
        this.id = id; this.Mass = mass; this.Position = position; this.Colour = col; this.canvas = canvasTemplate; this.UseGravity=useGravity; this.elasticity = elasticity; this.rigidity = rigidity;
        this.Collider = Utils.getMeshFromFile(colliderFile);

        Point COM = CenterOfMass();
        //Translate all coordinates so that 0,0 is COM oh and also go to world space
        for (Triangle t : Collider) for (Point p : t.points) {
            p.x -= COM.x;
            p.y -= COM.y;
            Point q = LocalToWorldSpace(p);
            p.x = q.x; p.y = q.y;
        }
        if (UseGravity) Weight = Utils.VectorScalarMultiply(Utils.g, Mass);
        canvas.Objects.add(this);
    }

    public Point CenterOfMass() {
        List<Point> MeshPoints = new ArrayList<>();
        for (Triangle t : Collider) MeshPoints.addAll(Arrays.asList(t.points)); //Make a list of all points in the mesh
        MeshPoints = MeshPoints.stream().distinct().collect(Collectors.toList()); //Remove duplicates
        float totalX = 0; float totalY = 0; //Calculate mean...
        for (Point point : MeshPoints) {
            totalX += point.x; totalY += point.y;
        }
        totalX /= MeshPoints.size(); totalY /= MeshPoints.size();
        return new Point(totalX, totalY); //Return :)
    }

    public void Rotate(double theta) {
        for (Triangle t : Collider)
            for (Point p : t.points) {
                p.matrixTransform(
                        Math.cos(theta), -Math.sin(theta),
                        Math.sin(theta), Math.cos(theta));
            }
    }

    //@SuppressWarnings("unused")
    private Point LocalToWorldSpace(Point p) {return new Point(this.Position.x + p.x, this.Position.y + p.y);}
    private MovingPoint LocalToWorldSpace(MovingPoint p) {return new MovingPoint(this.Position.x + p.x, this.Position.y + p.y,new Vector(LocalToWorldSpace(p.Velocity.getEndPoint())));}
    @SuppressWarnings("unused")
    private Point WorldToLocalSpace(Point p) {return new Point(p.x-this.Position.x, p.y-this.Position.y);}

    @SuppressWarnings("unused")
    public void RotateAboutPoint(Point point, double theta) {
        for (Triangle t : Collider) for (Point p : t.points) {
            p.x -= point.x;
            p.y -= point.y;
        }
        Rotate(theta);
        for (Triangle t : Collider) for (Point p : t.points) {
            p.x += point.x;
            p.y += point.y;
        }
    }

    public void Update(double delta) {
        if (!Forces.contains(Weight) && UseGravity) Forces.add(Weight);
        Vector acceleration = new Vector(0,0);
        for (Vector F : Forces) {
            acceleration = Utils.VectorAdd(acceleration, Utils.VectorScalarMultiply(F, 1/Mass)); //F=ma therefore a=F/m
        }
        for (Triangle t: Collider) for (MovingPoint p : t.points) {
            p.Velocity = Utils.VectorAdd(p.Velocity, acceleration);
        }
        List<Collision> collisions = getCollisions(delta);
        for (Collision c : collisions) ReflectPoint(c, delta);
        for (Triangle t : Collider) for (MovingPoint p : t.points) {
            p.x += p.Velocity.x * delta; p.y += p.Velocity.y * delta;
        }
    }

    private List<Collision> getCollisions(double delta) {
        List<Collision> collisions = new ArrayList<>();
        for (Triangle t : Collider) for (MovingPoint p : t.points) {
            double r = p.Velocity.getMagnitude() * delta; //Radius of circle that contains every location this point could move to.
            double xPos = LocalToWorldSpace(p).x; double yPos = LocalToWorldSpace(p).y;
            int[][] permutations = {
                    {0, 1},
                    {0, 2},
                    {1, 2}
            };
            for (RigidBody rb : canvas.Objects) {
                List<Collision> localCollisions = new ArrayList<>();
                for (Triangle otherT : rb.Collider) for (int[] line : permutations) if (!Objects.equals(rb.id, id)) {
                    Point p1 = rb.LocalToWorldSpace(otherT.points[line[0]]);
                    Point p2 = rb.LocalToWorldSpace(otherT.points[line[1]]);
                    double gradient = Utils.getGradient(p1, p2);
                    double intercept = Utils.get_Y_intercept(p1, p2);
                    //Now we have all the pieces to find the a, b and c for the discriminant
                    double a = Math.pow(gradient, 2) + 1;
                    double b = (2 * gradient * intercept) - (2 * gradient * yPos) - (2 * xPos);
                    double c = Math.pow(xPos, 2) + Math.pow(yPos, 2) + Math.pow(intercept, 2) - (2 * yPos * intercept) - Math.pow(r, 2);
                    //Time to calculate discriminant
                    double discriminant = Math.pow(b, 2) - (4 * a * c);
                    if (discriminant >= 0) {
                        Point[] ln = new Point[]{p1, p2};
                        double distance, root1, root2;
                        root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
                        root2 = (-b - Math.sqrt(discriminant)) / (2 * a); //Use the quadratic formula to find the two x coordinates of intersection
                        distance = Math.min(p.DistanceTo(new Point(root1, Utils.linearFunction(root1, ln))), p.DistanceTo(new Point(root2, Utils.linearFunction(root2, ln)))); //Sets the distance to the distance of the nearest intersection
                        localCollisions.add(new Collision(p, ln, distance)); //If the discriminant is less than 0 the line intersects the circle
                    }
                }
                Collections.sort(localCollisions);
                if (localCollisions.size() != 0) collisions.add(localCollisions.get(0));
            }
        }
        return collisions;
    }

    //https://stackoverflow.com/questions/573084/how-to-calculate-bounce-angle?noredirect=1&lq=1
    private void ReflectPoint(Collision collision, double delta) {
        MovingPoint p = collision.point;
        double dx = collision.line[1].x - collision.line[0].x;
        double dy = collision.line[1].y - collision.line[0].y;
        Vector normal;
        Point prevLocation = new Point(p.x - (p.Velocity.x * delta), p.y - (p.Velocity.y * delta));
        if (prevLocation.y > Utils.linearFunction(prevLocation, collision.line)) normal = new Vector(-dy, dx);
        else normal = new Vector(dy, -dx);
        Vector u = Utils.VectorScalarMultiply(normal, (Utils.Dot(p.Velocity, normal) / Utils.Dot(normal, normal)));
        Vector w = Utils.VectorSubtract(p.Velocity, u);

        //TODO if I add a friction system make sure to multiply w by the coefficient of friction
        Vector reflectVelocity = Utils.VectorSubtract(w, Utils.VectorScalarMultiply(u, elasticity));
        //collision.point.Velocity = reflectVelocity;
        for (Triangle t : Collider) for (MovingPoint point : t.points) {
            if (!point.equals(p)) {
                point.Velocity = Utils.VectorAdd(point.Velocity, Utils.VectorScalarMultiply(reflectVelocity, rigidity));
            } else point.Velocity = reflectVelocity;
        }
    }

    private static class Collision implements Comparable<Collision> {
        public MovingPoint point; public Point[] line; public double distance;
        public Collision(MovingPoint p, Point[] l, double d) {point = p; line = l; distance = d;}
        @Override
        public int compareTo(Collision col) {
            if (distance != col.distance) {
                boolean b = (distance < col.distance);
                return b ? 1 : -1;
            } else return 0;
        }
    }
}