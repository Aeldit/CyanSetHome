package fr.raphoulfifou.sethome.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.raphoulfifou.sethome.util.structure.CoordinatesParam;
import fr.raphoulfifou.sethome.util.structure.HomeParameters;
import fr.raphoulfifou.sethome.util.structure.Parameters;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SetHomeJSONConfig {
    private static final Path jsonPath = FabricLoader.getInstance().getConfigDir().resolve("sethome.json");
    private static final File jsonFile = FabricLoader.getInstance().getConfigDir().resolve("sethome.json").toFile();

    private static Map<Object, Object> sethome = new HashMap<>();
    private static Map<Object, Object> options = new HashMap<>();
    private static Map<Object, Object> homesList = new HashMap<>();
    private static final List<Parameters> parametersList = new ArrayList<>();
    private static final List<CoordinatesParam> coordinatesList = new ArrayList<>();

    public boolean allowHomes = true;
    public boolean multiDimensionalHomes = true;
    public int maxHomes = 15;

    // Get a variable's value
    public boolean areHomesAllowed() {
        return allowHomes;
    }
    public boolean areHomesMultiDimensional() {
        return multiDimensionalHomes;
    }
    public int getMaxHomes() {
        return maxHomes;
    }

    // Set the value of the option to the given value
    public void setAreHomesAllowed(boolean allowHomes) {
        this.allowHomes = allowHomes;
        try {
            writeChanges(options, null, allowHomes, "areHomesAllowed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setAreHomesMultiDimensional(boolean multiDimensionalHomes) {
        this.multiDimensionalHomes = multiDimensionalHomes;
        try {
            writeChanges(options, null, multiDimensionalHomes, "multiDimensionalHomes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
        try {
            writeChanges(options, maxHomes, null, "maxHomes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the 'sethome' command
     * Create a new parameter with the input values and put it into a List (parametersList)
     * Create a new homeParameter with the parametersList value
     * and add to the homesList Map the uuid of the player executing the command
     *
     *      -> if the player already have one or more homes, it will add the home to the homesList
     *      -> if the player don't already have a home, it will create a new Map with its UUID and add to it the home
     *
     * Write the homesList into the "sethome.json" file, located in the config folder of the game (client or server)
     */
    public void createHome(UUID uuid, String name, RegistryKey<World> dimension, double x, double y, double z, float yaw, float pitch) {
        try {
            CoordinatesParam coordinatesParam = new CoordinatesParam(x, y, z, yaw, pitch);
            coordinatesList.add(coordinatesParam);

            Parameters parameters = new Parameters(name, dimension, coordinatesList);
            parametersList.add(parameters);

            HomeParameters homeParameters = new HomeParameters(parametersList);
            //newHome.add(homeParameters);

            homesList.put(uuid, homeParameters);

            writeHome(homesList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getHomes() {
        return new String[0];
    }
    /*public RegistryKey<World> getHomeDimension(String homeName) {
        this.homesList.get(homeName);
        return parametersList.;
    }
     */

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    /**
     * Called by the 'onInitializeClient' and 'onInitializeServer' function located in HomeClientCore and HomeServerCore
     *
     *      -> If the file exists to the given path (jsonPath)
     *          > Try to read the file as a json file
     *          > Throws a RuntimeException
     *      -> Else
     *          > Try to write the default config
     *          > Throws a RuntimeException
     */
    public void load() {

        if (Files.exists(jsonPath)) {
            try (FileReader reader = new FileReader(jsonFile)) {
                SetHomeJSONConfig.sethome = GSON.fromJson(reader, Map.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not read config", e);
            }
        } else {
            try {
                this.writeDefaultConfig();
            } catch (IOException e) {
                throw new RuntimeException("Could not update config file", e);
            }
        }
    }

    /**
     * Called by the createHome function located in this class, when creating a home
     *
     *      -> If the file doesn't exists to the given path, a new one is created
     *      -> If the the given path is not a directory, an IOException is thrown
     *
     *      > Try to add the given map to the "sethomes" Map, and write it into the "sethome.json" file
     *
     * @throws IOException if the config could not be write
     */
    public void writeHome(Map<Object, Object> map) throws IOException {
        Path dir = jsonPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        try (FileWriter fw = new FileWriter(jsonFile, false)) {
            sethome.put("homes", map);

            GSON.toJson(sethome, fw);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the 'set' functions located in this class, when applying changes to the "sethome.json" file
     *
     *      -> If the file doesn't exists to the given path, a new one is created
     *      -> If the the given path is not a directory, an IOException is thrown
     *
     *      > Try to replace the current value of the option given value (map),
     *          and write it into the "sethome.json" file
     *
     * @throws IOException if the config could not be write
     */
    public void writeChanges(Map<Object, Object> map, Integer intValue, Boolean boolValue, String option) throws IOException {
        Path dir = jsonPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        try (FileWriter fw = new FileWriter(jsonFile, false)) {
            if (intValue == null) {
                options.put("areHomesAllowed", boolValue);
                options.put("multiDimensionalHomes", boolValue);
                options.put("maxHomes", this.maxHomes);
            }
            else if (boolValue == null) {
                options.put("areHomesAllowed", this.allowHomes);
                options.put("multiDimensionalHomes", this.multiDimensionalHomes);
                options.put("maxHomes", intValue);
            }
            else {
                return;
            }

            SetHomeJSONConfig.sethome.put("options", map);

            GSON.toJson(SetHomeJSONConfig.sethome, fw);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the load function located in this class, when loading the config file ("sethome.json")
     *
     *      -> If the file doesn't exists to the given path, a new one is created
     *      -> If the the given path is not a directory, an IOException is thrown
     *
     *      > Try to write the default options in the "options" Map,
     *          and write an empty "homes" Map into the "sethome.json" file
     *
     * @throws IOException if the config could not be write
     */
    public void writeDefaultConfig() throws IOException {
        Path dir = jsonPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        try (FileWriter fw = new FileWriter(jsonFile)) {

            options.put("areHomesAllowed", true);
            options.put("multiDimensionalHomes", true);
            options.put("maxHomes", 15);

            sethome.put("options", options);
            sethome.put("homes", homesList);

            GSON.toJson(sethome, fw);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
