package com.helloarbridge4.SizeCheck;

import android.util.Log;

import com.helloarbridge4.Point3F.Point3F;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public final class TwoDimensionalOrientedBoundingBox {

    protected enum Corner { UPPER_RIGHT, UPPER_LEFT, LOWER_LEFT, LOWER_RIGHT }

    public static Point3F[] getOBB(ArrayList<Point3F> pointList) {
        Polygon polygon =  new Polygon(pointList);
        return getMinimumBoundingRectangle(polygon);
    }

    public static Point3F[] getMinimumBoundingRectangle(Polygon polygon) {
        Rectangle[] rects = new Rectangle[polygon.edgeCount()];

        for(int i=0;i<polygon.edgeCount();i++)
        {
            Point3F edge = polygon.getEdge(i);
            //Rotate the polygon so that the current edge is parallel to a major axis
            //The y-Axis in this use case
            double theta = Math.acos(edge.normalise2D().y);
            polygon.rotate(theta);
            //Calculate a bounding box
            rects[i] = boundingBox(polygon);
            polygon.rotate(-theta);
            rects[i].rotate(-theta, polygon.getCenter());
        }

        double minArea = Double.MAX_VALUE;
        Rectangle box = rects[0];

        //Find the bounding box with the smallest area, this is the minimum bounding box
        for(int i=0;i<rects.length;i++)
        {
            double area = rects[i].area();
            if(area < minArea)
            {
                minArea = area;
                box = rects[i];
            }
        }
        Point3F[]  boxPoints  = new Point3F[4];

        for (int i = 0;  i < 4;  i++) {
            boxPoints[i] =  box.getPoint(i);
        }
        return boxPoints;

    }

    public static Rectangle boundingBox(Polygon polygon)
    {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for(int i=0;i<polygon.pointCount();i++)
        {
            Point3F p = polygon.getPoint(i);
            if(minX > p.x)
                minX = p.x;
            if(maxX < p.x)
                maxX = p.x;
            if(minY > p.y)
                minY = p.y;
            if(maxY < p.y)
                maxY = p.y;
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public static Point3F[] getMinimumBoundingRectangle(ArrayList<Point3F> points) throws IllegalArgumentException {
        ArrayList<Point3F[]> rectangles = getAllBoundingRectangles(points);

        Point3F[] minimum = null;
        double area = Long.MAX_VALUE;

        for (Point3F[] rectangle : rectangles) {

            double tempArea = getArea(rectangle);

            if (minimum == null || tempArea < area) {
                minimum = rectangle;
                area = tempArea;
            }
        }

        return minimum;
    }

    public static ArrayList<Point3F[]> getAllBoundingRectangles(ArrayList<Point3F> points) throws IllegalArgumentException {

        ArrayList<Point3F[]> rectangles = new ArrayList<>();

        ArrayList<Point3F> convexHull = QuickHull.getConvexHull(points);

        Caliper I = new Caliper(convexHull, getIndex(convexHull, Corner.UPPER_RIGHT), 90);
        Caliper J = new Caliper(convexHull, getIndex(convexHull, Corner.UPPER_LEFT), 180);
        Caliper K = new Caliper(convexHull, getIndex(convexHull, Corner.LOWER_LEFT), 270);
        Caliper L = new Caliper(convexHull, getIndex(convexHull, Corner.LOWER_RIGHT), 0);

        while(L.currentAngle < 90.0) {

            rectangles.add(new Point3F[]{
                    L.getIntersection(I),
                    I.getIntersection(J),
                    J.getIntersection(K),
                    K.getIntersection(L)
            });

            float smallestTheta = (float) getSmallestTheta(I, J, K, L);

            I.rotateBy(smallestTheta);
            J.rotateBy(smallestTheta);
            K.rotateBy(smallestTheta);
            L.rotateBy(smallestTheta);
        }

        return rectangles;
    }



    private static double getSmallestTheta(Caliper I, Caliper J, Caliper K, Caliper L) {

        double thetaI = I.getDeltaAngleNextPoint();
        double thetaJ = J.getDeltaAngleNextPoint();
        double thetaK = K.getDeltaAngleNextPoint();
        double thetaL = L.getDeltaAngleNextPoint();

        if(thetaI <= thetaJ && thetaI <= thetaK && thetaI <= thetaL) {
            return thetaI;
        }
        else if(thetaJ <= thetaK && thetaJ <= thetaL) {
            return thetaJ;
        }
        else if(thetaK <= thetaL) {
            return thetaK;
        }
        else {
            return thetaL;
        }
    }

    protected static int getIndex(ArrayList<Point3F> convexHull, Corner corner) {

        int index = 0;
        Point3F point = convexHull.get(index);
//        for(int i = 1; i < convexHull.size() -1 ; i++) {
        for(int i = 1; i < convexHull.size(); i++) {

            Point3F temp = convexHull.get(i);
            boolean change = false;

            switch(corner) {
                case UPPER_RIGHT:
                    change = (temp.x > point.x || (temp.x == point.x && temp.y > point.y));
                    break;
                case UPPER_LEFT:
                    change = (temp.y > point.y || (temp.y == point.y && temp.x < point.x));
                    break;
                case LOWER_LEFT:
                    change = (temp.x < point.x || (temp.x == point.x && temp.y < point.y));
                    break;
                case LOWER_RIGHT:
                    change = (temp.y < point.y || (temp.y == point.y && temp.x > point.x));
                    break;
            }

            if(change) {
                index = i;
                point = temp;
            }
        }

        return index;
    }

    public static double getArea(Point3F[] rectangle) {

        double deltaXAB = rectangle[0].x - rectangle[1].x;
        double deltaYAB = rectangle[0].y - rectangle[1].y;

        double deltaXBC = rectangle[1].x - rectangle[2].x;
        double deltaYBC = rectangle[1].y - rectangle[2].y;

        double lengthAB = Math.sqrt((deltaXAB * deltaXAB) + (deltaYAB * deltaYAB));
        double lengthBC = Math.sqrt((deltaXBC * deltaXBC) + (deltaYBC * deltaYBC));

        return lengthAB * lengthBC;
    }




    protected static class Caliper {
        final static double SIGMA = 0.00000000001;

        final List<Point3F> convexHull;
        int pointIndex;
        float currentAngle;

        Caliper(List<Point3F> convexHull, int pointIndex, float currentAngle) {
            this.convexHull = convexHull;
            this.pointIndex = pointIndex;
            this.currentAngle = currentAngle;
        }

        float getAngleNextPoint() {

            Point3F p1 = convexHull.get(pointIndex);
            Point3F p2 = convexHull.get((pointIndex + 1) % convexHull.size());

            float deltaX = p2.x - p1.x;
            float deltaY = p2.y - p1.y;

            float angle = (float) (Math.atan2(deltaY, deltaX) * 180 / Math.PI);

            return angle < 0 ? 360 + angle : angle;
        }

         float getConstant() {

            Point3F p = convexHull.get(pointIndex);

            return p.y - (getSlope() * p.x);
        }

        float getDeltaAngleNextPoint() {

            float angle = getAngleNextPoint();

            angle = angle < 0 ? 360 + angle - currentAngle : angle - currentAngle;

            return angle < 0 ? 360 : angle;
        }

        Point3F getIntersection(Caliper that) {

            // the x-intercept of 'this' and 'that': x = ((c2 - c1) / (m1 - m2))
            float x;
            // the y-intercept of 'this' and 'that', given 'x': (m*x) + c
            float y;

            if(this.isVertical()) {
                x = convexHull.get(pointIndex).x;
            }
            else if(this.isHorizontal()) {
                x = that.convexHull.get(that.pointIndex).x;
            }
            else {
                x = (that.getConstant() -  this.getConstant()) / (this.getSlope() - that.getSlope());
            }

            if(this.isVertical()) {
                y = that.getConstant();
            }
            else if(this.isHorizontal()) {
                y = this.getConstant();
            }
            else {
                y = (this.getSlope() * x) + this.getConstant();
            }

            return new Point3F(x, y,0);
        }

        float getSlope() {
            return (float) Math.tan(Math.toRadians(currentAngle));
        }

        boolean isHorizontal() {
            return (Math.abs(currentAngle) < SIGMA) || (Math.abs(currentAngle - 180.0) < SIGMA);
        }

        boolean isVertical() {
            return (Math.abs(currentAngle - 90.0) < SIGMA) || (Math.abs(currentAngle - 270.0) < SIGMA);
        }

        void rotateBy(float angle) {

            if(this.getDeltaAngleNextPoint() == angle) {
                pointIndex++;
            }

            this.currentAngle = (this.currentAngle + angle) % 360;
        }
    }

     public static class Scan {
        private static final String TAG = "GrahamScan";
        protected static  enum Turn {CW, CCW, COLLINEAR}

        public static ArrayList<Point3F> getConvexHull(ArrayList<Point3F> points) throws IllegalArgumentException {
            ArrayList<Point3F> sorted = new ArrayList<>(getSortedPointSet(points));
            if(sorted.size() < 3) {
                throw new IllegalArgumentException("can only create a convex hull of 3 or more unique points");
            }

            if(allCollinear(sorted)) {
                throw new IllegalArgumentException("cannot create a convex hull from collinear points");
            }

            Stack<Point3F> stack = new Stack<Point3F>();
            stack.push(sorted.get(0));
            stack.push(sorted.get(1));

            for (int i = 2; i < sorted.size(); i++) {

                try {
                    Point3F head = sorted.get(i);
                    Point3F middle = stack.pop();

                    Point3F tail = stack.peek();

                    Scan.Turn turn = getTurn(tail, middle, head);

                    switch(turn) {
                        case CCW:
                            stack.push(middle);
                            stack.push(head);
                            break;
                        case CW:
                            i--;
                            break;
                        case COLLINEAR:
                            stack.push(head);
                            break;
                    }
                } catch (EmptyStackException e) {
                    //Log.d(TAG,e.getMessage());
                }



            }

            stack.push(sorted.get(0));

            return new ArrayList<Point3F>(stack);
        }


        protected static Set<Point3F> getSortedPointSet(ArrayList<Point3F> points) {

            final Point3F lowest = getLowestPoint(points);

            TreeSet<Point3F> set = new TreeSet<Point3F>(new Comparator<Point3F>() {
                @Override
                public int compare(Point3F a, Point3F b) {

                    if (a.equals(b)) {
                        return 0;
                    }

                    double thetaA = Math.atan2((long) a.y - lowest.y, (long) a.x - lowest.x);
                    double thetaB = Math.atan2((long) b.y - lowest.y, (long) b.x - lowest.x);

                    if (thetaA < thetaB) {
                        return -1;
                    } else if (thetaA > thetaB) {
                        return 1;
                    } else {
                        double distanceA = Math.sqrt((((long) lowest.x - a.x) * ((long) lowest.x - a.x)) +
                                (((long) lowest.y - a.y) * ((long) lowest.y - a.y)));
                        double distanceB = Math.sqrt((((long) lowest.x - b.x) * ((long) lowest.x - b.x)) +
                                (((long) lowest.y - b.y) * ((long) lowest.y - b.y)));

                        if (distanceA < distanceB) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                }
            });
            set.addAll(points);
            return set;
        }

        protected static boolean allCollinear(List<Point3F> points) {
            if (points.size() < 2) return true;

            Point3F a = points.get(0);
            Point3F b = points.get(1);

            for (int i = 2; i < points.size(); i++) {
                Point3F c = points.get(i);

                if (getTurn(a,b,c) != Scan.Turn.COLLINEAR) {
                    return false;
                }
            }
            return true;
        }

        protected static Point3F getLowestPoint(ArrayList<Point3F> points) {

            Point3F lowest = points.get(0);

            for(int i = 1; i < points.size(); i++) {

                Point3F temp = points.get(i);

                if(temp.y < lowest.y || (temp.y == lowest.y && temp.x < lowest.x)) {
                    lowest = temp;
                }
            }

            return lowest;
        }

        public static Scan.Turn getTurn(Point3F a, Point3F b, Point3F c) {
            float crossProduct = ((b.x - a.x)*(c.y - a.y)) - ((b.y-a.y)*(c.x-a.x));

            if (crossProduct > 0) {
                return Scan.Turn.CCW;
            } else if (crossProduct < 0) {
                return Scan.Turn.CW;
            } else {
                return Scan.Turn.COLLINEAR;
            }
        }
    }
}


