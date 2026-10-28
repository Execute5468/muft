package net.adequoir.muft.client.input;

import org.joml.Vector2i;

public class Scroller {
    private double cumulHorizontal;
    private double cumulVertical;

    public Vector2i update(double horizontal, double vertical) {
        if (isOpposite(cumulHorizontal, horizontal)) this.cumulHorizontal = 0.0;
        if (isOpposite(cumulVertical, vertical)) this.cumulVertical = 0.0;

        int i = (int) (this.cumulHorizontal += horizontal);
        int j = (int) (this.cumulVertical += vertical);
        if (i == 0 && j == 0) return new Vector2i(0, 0);

        cumulHorizontal -= i;
        cumulVertical -= j;
        return new Vector2i(i, j);
    }

    public static int scrollCycling(double amount, int selectedIndex, int total) {
        int i = (int) Math.signum(amount);
        selectedIndex -= i;
        return selectedIndex < 0 ? total - 1 : selectedIndex % total;
    }

    private static boolean isOpposite(double cumulScalar, double scalar) {
        return cumulScalar != 0.0 && Math.signum(cumulScalar) != Math.signum(scalar);
    }
}
