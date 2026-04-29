package game.util.geometry;

// larp
public class Lerp {

    public static float lerp(float point1, float point2, float fraction) {
        return (1 - fraction) * point1 + fraction * point2;
    }

    public static Vec2f lerp(Vec2f point1, Vec2f point2, float fraction) {
        return new Vec2f(
            lerp(point1.x(), point2.x(), fraction),
            lerp(point1.y(), point2.y(), fraction)
        );
    }

    public static Vec3f lerp(Vec3f point1, Vec3f point2, float fraction) {
        return new Vec3f(
            lerp(point1.x(), point2.x(), fraction),
            lerp(point1.y(), point2.y(), fraction),
            lerp(point1.z(), point2.z(), fraction)
        );
    }
}
