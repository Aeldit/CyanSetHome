package fr.raphoulfifou.sethome.util.structure;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;

public class Parameters {
    String name;
    RegistryKey<World> dimension;
    List<CoordinatesParam> coordinatesParams;


    /**
     * Used when a home is created to store its values
     * In the json file, it is inside the 'HomeParameters'
     * (Elements insinde '' are reffering to the class name)
     */
    public Parameters(String name, RegistryKey<World> dimension, List<CoordinatesParam> coordinatesParams) {
        this.name = name;
        this.dimension = dimension;
        this.coordinatesParams = coordinatesParams;
    }
}
