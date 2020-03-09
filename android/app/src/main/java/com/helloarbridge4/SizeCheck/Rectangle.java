package com.helloarbridge4.SizeCheck;

import com.helloarbridge4.Point3F.Point3F;

import java.util.ArrayList;

public class Rectangle extends Polygon {
    public double width;
    public double height;

    public Rectangle(float x, float y, float width, float height) {
        this.width = width;
        this.height = height;
        this.points = new ArrayList<Point3F>(4);
        this.points.add(new Point3F(x, 0f, y));
        this.points.add(new Point3F(x + width, 0f, y));
        this.points.add(new Point3F(x + width, 0f, y + height));
        this.points.add(new Point3F(x, 0f, y + height));
        calcCenter();
    }

    public double area()
    {
        return width * height;
    }
}
