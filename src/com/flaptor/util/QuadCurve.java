package com.flaptor.util;

import java.util.Random;

/**
 * This class implements a quadratic curve that can be used to interpolate values.
 */
public class QuadCurve {

    // The coeficients of the quadratic curve
    private double a, b, c;

    /**
     * Create a curve with all coefficientes = 0
     */
    public QuadCurve () {
        a = 0;
        b = 0;
        c = 0;
    }

    /**
     * Creates a curve that passes through the three given points (x1,y1), (x2,y2), (x3,y3).
     */
    public QuadCurve (double x1, double y1, double x2, double y2, double x3, double y3) {
        setCurve (x1, y1, x2, y2, x3, y3);
    }

    /**
     * Calculates the coefficients for a curve that passes through the three given points (x1,y1), (x2,y2), (x3,y3).
     */
    public void setCurve (double x1, double y1, double x2, double y2, double x3, double y3) throws ArithmeticException {
        a = ((y3 - y1) * (x1 - x2) + (y1 - y2) * (x1 - x3)) / ((x1 - x2) * (x3*x3 - x1*x1) - (x2*x2 - x1*x1) * (x1 - x3));
        b = a * (x2*x2 - x1*x1) / (x1 - x2) + (y1 - y2) / (x1 - x2);
        c = y1 - a*x1*x1 - b*x1;
    }

    /**
     * Uses the curve to interpolate the given x value.
     */
    public double getY (double x) {
        return a*x*x+b*x+c;
    }


    public static void main (String[] args) {
        Random rnd = new Random(System.currentTimeMillis());
        int[] px = new int[3];
        double[] py = new double[3];
        for (int i=0; i<3; i++) {
            px[i]=rnd.nextInt(30);
            py[i]=80*rnd.nextDouble();
        }
        QuadCurve curve = new QuadCurve(px[0],py[0],px[1],py[1],px[2],py[2]);
        for (int x=0; x<30; x++) {
            double y = curve.getY(x);
            if (y < 0) {
                System.out.print(">");
            } else if (y > 80) {
                System.out.print("<");
            } else if (x==px[0] || x==px[1] || x==px[2]) {
                System.out.print("**********************************************************************************".substring((int)y));
            } else {
                System.out.print("----------------------------------------------------------------------------------".substring((int)y));
            }
            System.out.println();
        }
    }

}

