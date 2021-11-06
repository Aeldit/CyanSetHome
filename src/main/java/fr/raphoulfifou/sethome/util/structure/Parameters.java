package fr.raphoulfifou.sethome.util.structure;

/**
 * @since 0.0.1
 * @see HomeParameter
 * @author Raphoulfifou
 */
public class Parameters {
    public String name;
    public String dimension;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;


    /**
     * <p>Used when a home is created to store its values</p>
     * <p>In the json file, it is inside the {@link HomeParameter HomeParameters}</p>
     */
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
