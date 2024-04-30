package language.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Circle {
    public final int radius;
    public final int diameter;
    public final Point center;

    public Circle(int radius, @NotNull Point center) {
        this.radius = radius;
        this.diameter = radius * 2;
        this.center = center;
    }

    public Circle resize(int radiusToAdd) {
        return new Circle(radius + radiusToAdd, center);
    }

    public Point generateEdgePoint() {
        Random random = new Random();

        double theta = 2 * Math.PI * random.nextDouble();

        int x = (int) (this.radius * Math.cos(theta)) + center.x;
        int y = (int) (this.radius * Math.sin(theta)) + center.y;

        return new Point(x, y);
    }

    public List<Point> generateRangePoints(int r1, int r2, int nPoints) {
        if (r1 > this.radius || r2 > this.radius)
            throw new RuntimeException("interval radius is greater than radius");

        if (r1 == r2)
            throw new RuntimeException("interval radius are equals");

        int minRadius = Math.min(r1, r2);

        Random random = new Random();
        List<Point> points = new ArrayList<>();

        double divTheta = 2 * Math.PI / nPoints;
        double theta = 2 * Math.PI * random.nextDouble();

        for (int t = 0; t < nPoints; t++) {
            double randomRadius = Math.abs(r1 - r2) * random.nextDouble();
            double radius = randomRadius + minRadius;

            double cosX = Math.cos(theta + divTheta * t);
            double sinY = Math.sin(theta + divTheta * t);

            int x = (int) (radius * cosX) + center.x;
            int y = (int) (radius * sinY) + center.y;

            points.add(new Point(x, y));
        }

        return points;
    }
}
