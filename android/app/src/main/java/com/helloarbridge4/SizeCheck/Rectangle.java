package com.helloarbridge4.SizeCheck;

import com.helloarbridge4.Point3F.Point3F;

import java.util.ArrayList;

public class Rectangle extends Polygon {
    public double width;
    public double height;

    public Rectangle(float x, float y, float width, float height)
    {
        this.width = width;
        this.height = height;
        this.points = new ArrayList<Point3F>(4);
        this.points.add(new Point3F(x, y,0f));
        this.points.add(new Point3F(x + width, y, 0f));
        this.points.add(new Point3F(x + width, y + height, 0f));
        this.points.add(new Point3F(x, y + height, 0f));
        calcCenter();
    }

    public Rectangle(float width, float height)
    {
        this.width = width;
        this.height = height;
        this.points = new ArrayList<Point3F>(4);
        this.points.add(new Point3F(0, 0, 0f));
        this.points.add(new Point3F(width, 0, 0f));
        this.points.add(new Point3F(width, height, 0f));
        this.points.add(new Point3F(0, height, 0f));
        calcCenter();
    }

    public double area()
    {
        return width * height;
    }
}
