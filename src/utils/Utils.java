package utils;

public class Utils {
    public static int compareDouble(double f1, double f2) {
        if (Math.abs(f1 - f2) < 0.00001) {
            return 0;
        } else if (f1 < f2) {
            return -1;
        } else {
            return +1;
        }
    }
}
