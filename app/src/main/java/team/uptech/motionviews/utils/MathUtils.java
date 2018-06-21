package team.uptech.motionviews.utils;

import android.graphics.PointF;
import android.support.annotation.NonNull;

import java.util.Vector;

public class MathUtils {

    /**
     * For more info:
     * <a href="http://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle">StackOverflow: How to check point is in rectangle</a>
     *
     * @param pt point to check
     * @param v1 vertex 1 of the triangle
     * @param v2 vertex 2 of the triangle
     * @param v3 vertex 3 of the triangle
     * @return true if point (x, y) is inside the triangle
     */
    public static boolean pointInTriangle(@NonNull PointF pt, @NonNull PointF v1,
                                          @NonNull PointF v2, @NonNull PointF v3) {

        boolean b1 = crossProduct(pt, v1, v2) < 0.0f;
        boolean b2 = crossProduct(pt, v2, v3) < 0.0f;
        boolean b3 = crossProduct(pt, v3, v1) < 0.0f;

        return (b1 == b2) && (b2 == b3);
    }

    /**
     * calculates cross product of vectors AB and AC
     *
     * @param a beginning of 2 vectors
     * @param b end of vector 1
     * @param c enf of vector 2
     * @return cross product AB * AC
     */
    private static float crossProduct(@NonNull PointF a, @NonNull PointF b, @NonNull PointF c) {
        return crossProduct(a.x, a.y, b.x, b.y, c.x, c.y);
    }

    /**
     * calculates cross product of vectors AB and AC
     *
     * @param ax X coordinate of point A
     * @param ay Y coordinate of point A
     * @param bx X coordinate of point B
     * @param by Y coordinate of point B
     * @param cx X coordinate of point C
     * @param cy Y coordinate of point C
     * @return cross product AB * AC
     */
    private static float crossProduct(float ax, float ay, float bx, float by, float cx, float cy) {
        return (ax - cx) * (by - cy) - (bx - cx) * (ay - cy);
    }

    /**
     * Create 2d vector from 2 points
     */
    public static PointF getVector(PointF tail, PointF top) {
        return new PointF(top.x - tail.x, top.y - tail.y);
    }

    /**
     * Pythagorean theorem to calculate vector length
     */
    public static float vectorLength (PointF tail, PointF top) {
        return vectorLength(getVector(tail, top));
    }
    public static float vectorLength (PointF vector) {
        return (float)Math.hypot(vector.x,vector.y);
    }

    /**
     * Normalize vestor to get vetror direction
     */
    public static PointF getNormalizedVector (PointF tail, PointF top) {
        return getNormalizedVector(getVector(tail, top));
    }
    public static PointF getNormalizedVector (PointF vector) {
        float length = vectorLength(vector);
        return new PointF(vector.x / length, vector.y / length);
    }

    /**
     *
     * @param tail
     * @param top
     * @return vector direction (= normalized vector)
     */
    public static PointF vectorDirection (PointF tail, PointF top) {
        return getNormalizedVector(tail, top);
    }

    /**
     *
     * @param tail
     * @param top
     * @return Rotated direction of vector
     */
    public static PointF rotatedVectorDirection (PointF tail, PointF top, int angle) {
        PointF direction = vectorDirection(tail,top);
        float currentAngle = (float)(Math.toDegrees(Math.atan2(direction.y, direction.x)));
        direction.x = (float)Math.sin(Math.toRadians(angle - currentAngle));
        direction.y = (float)Math.cos(Math.toRadians(angle - currentAngle));

        return getNormalizedVector(direction);
    }
}