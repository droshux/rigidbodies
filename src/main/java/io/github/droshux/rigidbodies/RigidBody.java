package io.github.droshux.rigidbodies;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RigidBody {
    public String id;
    public Triangle[] Collider;
    public Point[] BoundingBox = new Point[2]; // Axis aligned bounding box in WORLD SPACE. [0] is bottom left and [1]
                                               // is top right
    public Point Position;
    public double Mass; // In kilograms
    public final double MomentOfInertia; // kgm^2
    public final boolean UseGravity;
    public Color Colour;
    public double elasticity;
    public double rigidity;

    public Canvas canvas;

    public List<LocalForce> Forces = new ArrayList<>();
    public Vector Velocity = new Vector();
    public Vector Acceleration = new Vector();
    public double AngularVelocity = 0;
    public LocalForce Weight;

    public RigidBody(String id, float mass, Point position, Color col, String colliderFile, Canvas canvasTemplate,
            boolean useGravity, double elasticity, double rigidity) {
        this.id = id;
        this.Mass = mass;
        this.Position = position;
        this.Colour = col;
        this.canvas = canvasTemplate;
        this.UseGravity = useGravity;
        this.elasticity = elasticity;
        this.rigidity = rigidity;
        this.Collider = Utils.getMeshFromFile(colliderFile);

        Point COM = CenterOfMass();
        // Translate all coordinates so that 0,0 is COM
        for (Triangle t : Collider)
            for (Point p : t.points) {
                p.x -= COM.x;
                p.y -= COM.y;
            }
        if (UseGravity)
            Weight = new LocalForce(Utils.VectorScalarMultiply(Utils.g, Mass), new Point(0, 0), 1);
        MomentOfInertia = calculateI();
        UpdateBoundingBox();
        canvas.Objects.add(this);
    }

    private double calculateI() {
        List<Point> polygon = new ArrayList<>();
        for (Triangle t : Collider)
            polygon.addAll(Arrays.asList(t.points)); // Make a list of all points in the mesh
        polygon = Utils.removeDuplicates(polygon); // Remove duplicates
        double Jx = 0;
        for (int i = 0; i <= polygon.size() - 1; i++) {
            int iPlus = i + 1;
            if (iPlus >= polygon.size())
                iPlus = 0; // Loop round
            Point Pi = polygon.get(i);
            Point PiPlus = polygon.get(iPlus); // Get two points
            Jx += ((Pi.x * PiPlus.y) - (PiPlus.x * Pi.y))
                    * (Math.pow(Pi.x, 2) + (Pi.x * PiPlus.x) + Math.pow(PiPlus.x, 2));
        }
        Jx /= 12;

        double Jy = 0;
        for (int i = 0; i <= polygon.size() - 1; i++) {
            int iPlus = i + 1;
            if (iPlus >= polygon.size())
                iPlus = 0; // Loop round
            Point Pi = polygon.get(i);
            Point PiPlus = polygon.get(iPlus); // Get two points
            Jy += ((Pi.x * PiPlus.y) - (PiPlus.x * Pi.y))
                    * (Math.pow(Pi.y, 2) + (Pi.y * PiPlus.y) + Math.pow(PiPlus.y, 2));
        }
        Jy /= 12;

        return Math.abs(Jx + Jy);
    }

    public Point CenterOfMass() {
        List<Point> MeshPoints = new ArrayList<>();
        for (Triangle t : Collider)
            MeshPoints.addAll(Arrays.asList(t.points)); // Make a list of all points in the mesh
        MeshPoints = Utils.removeDuplicates(MeshPoints); // Remove duplicates
        float totalX = 0;
        float totalY = 0; // Calculate mean...
        for (Point point : MeshPoints) {
            totalX += point.x;
            totalY += point.y;
        }
        totalX /= MeshPoints.size();
        totalY /= MeshPoints.size();
        return new Point(totalX, totalY); // Return :)
    }

    public void Rotate(double theta) {
        for (Triangle t : Collider)
            for (Point p : t.points) {
                p.matrixTransformSelf(
                        Math.cos(theta), -Math.sin(theta),
                        Math.sin(theta), Math.cos(theta));
            }
    }

    public Point LocalToWorldSpace(Point p) {
        return new Point(this.Position.x + p.x, this.Position.y + p.y);
    }

    public Point WorldToLocalSpace(Point p) {
        return new Point(p.x - this.Position.x, p.y - this.Position.y);
    }

    public void RotateAboutPoint(Point point, double theta) {
        for (Triangle t : Collider)
            for (Point p : t.points) {
                p.x -= point.x;
                p.y -= point.y;
            }
        Rotate(theta);
        for (Triangle t : Collider)
            for (Point p : t.points) {
                p.x += point.x;
                p.y += point.y;
            }
    }

    public void Update(double delta) {
        if (!Forces.contains(Weight) && UseGravity)
            Forces.add(Weight);
        Acceleration = new Vector();
        Iterator<LocalForce> i = Forces.iterator();
        while (i.hasNext()) {
            LocalForce F = i.next();
            AddForceAtPosition(F, delta);
            F.duration -= delta;
            if (F.duration <= 0)
                i.remove();
        }
        Velocity = Utils.VectorAdd(Velocity, Acceleration);
        Position = Utils.VectorAdd(new Vector(Position), Utils.VectorScalarMultiply(Velocity, delta));
        Rotate(AngularVelocity * delta);
        UpdateBoundingBox();
    }

    public void AddForceAtPosition(Vector force, Point localPosition, double delta) {
        Acceleration = Utils.VectorAdd(Acceleration, Utils.VectorScalarMultiply(force, delta / Mass));
        double Torque = localPosition.x * force.y - localPosition.y * force.x; // 2D torque calculation
        AngularVelocity += Torque * delta / MomentOfInertia;
    }

    public void AddForceAtPosition(LocalForce F, double delta) {
        Acceleration = Utils.VectorAdd(Acceleration, Utils.VectorScalarMultiply(F.forceVector, delta / Mass));
        double Torque = F.localPosition.x * F.forceVector.y - F.localPosition.y * F.forceVector.x; // 2D torque
                                                                                                   // calculation
        AngularVelocity += Torque * delta / MomentOfInertia;
    }

    private void UpdateBoundingBox() {
        Point InitialPoint = Collider[0].points[0]; // First Point to be tested
        double minX = InitialPoint.x, maxX = InitialPoint.x, minY = InitialPoint.y, maxY = InitialPoint.y; // Initialise
                                                                                                           // min and
                                                                                                           // max

        for (Triangle t : Collider)
            for (Point p : t.points) {
                minX = Math.min(p.x, minX);
                maxX = Math.max(p.x, maxX);
                minY = Math.min(p.y, minY);
                maxY = Math.max(p.y, maxY);
            }
        BoundingBox[0] = LocalToWorldSpace(new Point(minX, minY));
        BoundingBox[1] = LocalToWorldSpace(new Point(maxX, maxY));
    }

    public boolean contains(Point p) {
        boolean output = false;

        for (Triangle t : this.Collider) {
            final Point worldPoint = WorldToLocalSpace(p);
            output = t.contains(worldPoint);
            if (output)
                break;
        }

        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RigidBody rigidBody))
            return false;
        return Double.compare(rigidBody.Mass, Mass) == 0
                && Double.compare(rigidBody.MomentOfInertia, MomentOfInertia) == 0 && UseGravity == rigidBody.UseGravity
                && Double.compare(rigidBody.elasticity, elasticity) == 0
                && Double.compare(rigidBody.rigidity, rigidity) == 0 && id.equals(rigidBody.id)
                && Colour.equals(rigidBody.Colour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Mass, MomentOfInertia, UseGravity, Colour, elasticity, rigidity);
    }

    @Override
    public String toString() {
        String out = id + ": \n    Position: " + Position.toString();
        List<Point> Points = new ArrayList<>();
        for (Triangle t : Collider)
            for (Point p : t.points)
                Points.add(p);

        Points = Utils.removeDuplicates(Points);
        for (Point p : Points)
            out += ("\n        " + LocalToWorldSpace(p).toString());
        return out;
    }

    public static class LocalForce {
        public Vector forceVector;
        public Point localPosition;

        public double duration;

        public LocalForce(Vector forceVector, Point localPosition, double duration) {
            this.forceVector = forceVector;
            this.localPosition = localPosition;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return "LocalForce{" +
                    "forceVector=" + forceVector +
                    ", localPosition=" + localPosition +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            LocalForce that = (LocalForce) o;
            return forceVector.equals(that.forceVector) && localPosition.equals(that.localPosition);
        }

        @Override
        public int hashCode() {
            return Objects.hash(forceVector, localPosition);
        }
    }
}