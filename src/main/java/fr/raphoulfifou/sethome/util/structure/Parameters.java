package fr.raphoulfifou.sethome.util.structure;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Parameters {
    String name;
    RegistryKey<World> dimension;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;

    public Parameters(String name, RegistryKey<World> dimension, double x, double y, double z, float yaw, float pitch) {
        this.name = name;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
