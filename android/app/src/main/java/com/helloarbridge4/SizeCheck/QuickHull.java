package com.helloarbridge4.SizeCheck;

import com.helloarbridge4.Point3F.Point3F;
import java.util.ArrayList;

public class QuickHull {
    public static ArrayList<Point3F> getConvexHull(ArrayList<Point3F> points) {
        ArrayList<Point3F> convexHull = new ArrayList<Point3F>();
        if (points.size() < 3) {
            return (ArrayList) points.clone();
        }

        int minPoint = -1, maxPoint = -1;
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).x < minX) {
                minX = points.get(i).x;
                minPoint = i;
            }
            if (points.get(i).x > maxX) {
                maxX = points.get(i).x;
                maxPoint = i;
            }
        }
        final Point3F A = points.get(minPoint);
        final Point3F B = points.get(maxPoint);
        convexHull.add(A);
        convexHull.add(B);
        points.remove(A);
        points.remove(B);

        ArrayList<Point3F> leftSet = new ArrayList<Point3F>();
        ArrayList<Point3F> rightSet = new ArrayList<Point3F>();

        for (int i = 0; i < points.size(); i++) {
            Point3F p = points.get(i);
            if (pointLocation(A, B, p) == -1){
                leftSet.add(p);
            }
            else if (pointLocation(A, B, p) == 1)
                rightSet.add(p);
        }

        hullSet(A, B, rightSet, convexHull);
        hullSet(B, A, leftSet, convexHull);

        return convexHull;
    }



    public static void hullSet(Point3F A, Point3F B, ArrayList<Point3F> set, ArrayList<Point3F> hull) {
        int insertPosition = hull.indexOf(B);
        if (set.size() == 0) {
            return;
        }
        if (set.size() == 1) {
            Point3F p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        float dist = Float.MIN_VALUE;
        int furthestPoint = -1;
        for (int i = 0; i < set.size(); i++) {
            Point3F p = set.get(i);
            float distance = distance(A, B, p);
            if (distance > dist) {
                dist = distance;
                furthestPoint = i;
            }
        }

        Point3F P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);

        ArrayList<Point3F> leftSetAP = new ArrayList<Point3F>();
        for (int i = 0; i < set.size(); i++) {
            Point3F M = set.get(i);
            if (pointLocation(A, P, M) == 1) {
                leftSetAP.add(M);
            }
        }

        ArrayList<Point3F> leftSetPB = new ArrayList<Point3F>();
        for (int i = 0; i < set.size(); i++) {
            Point3F M = set.get(i);
            if (pointLocation(P, B, M) == 1)  {
                leftSetPB.add(M);
            }
        }
        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);
    }

    public static float distance(Point3F A, Point3F B, Point3F C) {
        float ABx = B.x - A.x;
        float ABy = B.y - A.y;
        float num = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        if (num < 0) {
            num = -num;
        }
        return num;
    }

    public static int pointLocation(Point3F A, Point3F B, Point3F P) {
        float cp1 = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        if (cp1 > 0)
            return 1;
        else if (cp1 == 0)
            return 0;
        else
            return -1;
    }
}
