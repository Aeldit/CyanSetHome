package fr.raphoulfifou.sethome.util.structure;

public class CoordinatesParam {
    double x;
    double y;
    double z;
    float yaw;
    float pitch;

    /**
     * Used when a home is created to store its coordinates
     * In the json file, it is inside the 'Parameters'
     * (Elements insinde '' are reffering to the class name)
     */
    public CoordinatesParam(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
