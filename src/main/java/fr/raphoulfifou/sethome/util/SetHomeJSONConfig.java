package fr.raphoulfifou.sethome.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.raphoulfifou.sethome.util.structure.HomeParameters;
import fr.raphoulfifou.sethome.util.structure.Parameters;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SetHomeJSONConfig {
    private static final Path jsonPath = FabricLoader.getInstance().getConfigDir().resolve("sethome.json");
    private static final File jsonFile = FabricLoader.getInstance().getConfigDir().resolve("sethome.json").toFile();

    private static final Map<Object, Object> sethome = new HashMap<>();
    private static final Map<Object, Object> options = new HashMap<>();
    private static final Map<Object, Object> homesList = new HashMap<>();
    private static final List<Parameters> parametersList = new ArrayList<>();

    public static boolean allowHomes = true;
    public static boolean multiDimensionalHomes = true;
    public static int maxHomes = 15;

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
        SetHomeJSONConfig.allowHomes = allowHomes;
        try {
            writeChanges(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setAreHomesMultiDimensional(boolean multiDimensionalHomes) {
        SetHomeJSONConfig.multiDimensionalHomes = multiDimensionalHomes;
        try {
            writeChanges(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMaxHomes(int maxHomes) {
        SetHomeJSONConfig.maxHomes = maxHomes;
        try {
            writeChanges(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public static void main() {
        try (FileOutputStream fos = new FileOutputStream(String.valueOf(jsonPath));
             OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            Map<String, Object> options = new HashMap<>();
            options.put("areHomesAllowed", SetHomeJSONConfig.options.allowHomes);
            options.put("multiDimensionalHomes", SetHomeJSONConfig.options.multiDimensionalHomes);
            options.put("maxHomes", SetHomeJSONConfig.options.maxHomes);

            Parameters parameters = new Parameters("name", "dimension");
            List<Parameters> parametersList = new ArrayList<>();
            parametersList.add(parameters);

            HomeParameters parametersH = new HomeParameters(parametersList);
            List<HomeParameters> homesList = new ArrayList<>();
            homesList.add(parametersH);

            Map<Object, Object> sethome = new HashMap<>();
            sethome.put("options", options);
            sethome.put("homes", homesList);

            GSON.toJson(sethome, isr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */

    /**
     * Called by the 'sethome' command
     * Create a new parameter with the input values and put it into a List (parametersList)
     * Create a new homeParameter with the parametersList value
     * and add to the homesList Map the uuid of the player executing the command
     *      -> if the player already have one or more homes, it will add the home to the homesList
     *      -> if the player don't already have a home, it will create a new Map with its UUID and add to it the home
     * Write the homesList into the "sethome.json" file, located in the config folder of the game (client or server)
     */
    public void createHome(UUID uuid, String name, RegistryKey<World> dimension, double x, double y, double z, float yaw, float pitch) {
        try {

            Parameters parameters = new Parameters(name, dimension, x, y, z, yaw, pitch);
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

    public static SetHomeJSONConfig load() {
        SetHomeJSONConfig config;

        if (Files.exists(jsonPath)) {
            try (FileReader reader = new FileReader(jsonFile)) {
                config = GSON.fromJson(reader, SetHomeJSONConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not read config", e);
            }
        } else {
            config = new SetHomeJSONConfig();
            try {
                config.writeDefaultConfig();
            } catch (IOException e) {
                throw new RuntimeException("Could not update config file", e);
            }
        }
        return config;
    }

    /**
     * Called by the createHome function located in this class, when creating a home
     *
     *      -> If the file doesn't exists to the given path, a new one is created
     *      -> If the the given path is not a directory, an IOException is thrown
     *
     *      > Try to add the given map to the "sethomes" Map, and write it into the "sethome.json" file
     *      > Throws an IOExecption
     */
    public void writeHome(Map<Object, Object> map) throws IOException {
        Path dir = jsonPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        try (FileWriter writer = new FileWriter(jsonFile, false)) {
            sethome.put("homes", map);

            GSON.toJson(sethome, writer);

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
     *      > Throws an IOExecption
     */
    public void writeChanges(Map<Object, Object> map) throws IOException {
        Path dir = jsonPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        try (FileWriter writer = new FileWriter(jsonFile, false)) {
            options.put("areHomesAllowed", SetHomeJSONConfig.allowHomes);
            options.put("multiDimensionalHomes", SetHomeJSONConfig.multiDimensionalHomes);
            options.put("maxHomes", SetHomeJSONConfig.maxHomes);

            sethome.put("options", map);

            GSON.toJson(sethome, writer);

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
     *      > Try to replace the current value of the option given value (map),
     *          and write it into the "sethome.json" file
     *      > Throws an IOExecption
     */
    public void writeDefaultConfig() throws IOException {
        Path dir = jsonPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        try (FileOutputStream fos = new FileOutputStream(String.valueOf(jsonPath));
             OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            options.put("areHomesAllowed", true);
            options.put("multiDimensionalHomes", true);
            options.put("maxHomes", 15);

            sethome.put("options", options);
            sethome.put("homes", homesList);

            GSON.toJson(sethome, isr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
