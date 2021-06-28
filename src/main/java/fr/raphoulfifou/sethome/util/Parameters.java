package fr.raphoulfifou.sethome.util;

public class Parameters {
    String name;
    String dimension;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;

    public Parameters(String name, String dimension, double x, double y, double z, float yaw, float pitch) {
        this.name = name;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
