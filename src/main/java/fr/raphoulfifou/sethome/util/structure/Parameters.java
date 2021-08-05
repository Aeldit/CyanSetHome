package fr.raphoulfifou.sethome.util.structure;

public class Parameters {
    public String name;
    public String dimension;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;


    /**
     * Used when a home is created to store its values
     * In the json file, it is inside the 'HomeParameters'
     * (Elements insinde '' are reffering to the class name)
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
