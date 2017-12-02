package cn.chenxulu.lib.widget.recycler.utils;

/**
 *
 * @author xulu
 * @date 15/06/2017.
 */
public class MathUtil {

    public static int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static int findMin(int[] firstPositions) {
        int min = firstPositions[0];
        for (int value : firstPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }
}
