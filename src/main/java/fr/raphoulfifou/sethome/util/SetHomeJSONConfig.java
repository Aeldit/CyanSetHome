package fr.raphoulfifou.sethome.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.raphoulfifou.sethome.HomeClientCore;
import fr.raphoulfifou.sethome.HomeServerCore;
import fr.raphoulfifou.sethome.util.structure.HomeParameters;
import fr.raphoulfifou.sethome.util.structure.Parameters;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SetHomeJSONConfig {
    public static final Path jsonPath = FabricLoader.getInstance().getConfigDir().resolve("sethome.json");
    public static final File jsonFile = FabricLoader.getInstance().getConfigDir().resolve("sethome.json").toFile();

    public static Map<Object, Object> sethome = new HashMap<>();
    public static Map<Object, Object> options = new HashMap<>();
    public static Map<Object, Object> homes = new HashMap<>();
    public static List<Parameters> parametersList = new ArrayList<>();

    public boolean allowHomes = true;
    public int maxHomes = 15;

    // Get a variable's value
    public boolean areHomesAllowed() {
        return allowHomes;
    }
    public int getMaxHomes() {
        Object value = sethome.get(options.get(maxHomes));
        return (int) value;
    }

    public int getHomesNumber(UUID uuid) {
        if (homes.containsKey(uuid)) {
            homes.get(uuid);
        }
        return 0;
    }
    public String[] getHomes() {
        return new String[0];
    }

    // Set the value of the option to the given value
    public void setAreHomesAllowed(boolean allowHomes) {
        this.allowHomes = allowHomes;
        try {
            writeChanges(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
        try {
            writeChanges(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by the 'sethome' command
     * Create a new parameter with the input values and put it into a List (parametersList)
     * Create a new homeParameter with the parametersList value
     * and add to the homesMap Map the uuid of the player executing the command
     *
     *      -> if the player already have one or more homes, it will add the home to the homesMap
     *      -> if the player don't already have a home, it will create a new Map with its UUID and add to it the home
     *
     * Write the homesMap into the "sethome.json" file, located in the config folder of the game (client or server)
     */
    public void createHome(UUID uuid, String name, String dimension, double x, double y, double z, float yaw, float pitch) {
        try {
            Parameters parameters = new Parameters(name, dimension, x, y, z, yaw, pitch);
            parametersList.add(parameters);

            HomeParameters homeParameters = new HomeParameters(parametersList);

            homes.put(uuid, homeParameters);

            writeHome(homes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    /**
     * <p>Called by the {@link HomeServerCore#onInitializeServer() onInitializeServer} and
     * {@link HomeClientCore#onInitializeClient() onInitializeClient} functions located in
     * {@link fr.raphoulfifou.sethome.HomeServerCore HomeServerCore} and
     * {@link fr.raphoulfifou.sethome.HomeClientCore HomeClientCore}</p>
     *
     * <ul>If the file exists to the given path (jsonPath)
     *         <li>-> Try to read the file as a json file</li>
     *         <li>-> Throws a {@code RuntimeException}</li>
     * </ul>
     * <ul>Else
     *     <li>-> Try to write the default config</li>
     *     <li>-> Throws a {@code RuntimeException}</li>
     * </ul>
     */
    public void load() {
        if (Files.exists(jsonPath)) {
            try (FileReader fr = new FileReader(jsonFile)) {
                SetHomeJSONConfig.sethome = GSON.fromJson(fr, Map.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not read config", e);
            }
        } else {
            try {
                this.writeDefaultConfig();
            } catch (IOException e) {
                throw new RuntimeException("Could not write default config file", e);
            }
        }
    }

    /**
     * <p>Called by the createHome function located in this class, when creating a home.</p>
     *
     * <p>Calls the {@link SetHomeJSONConfig#load() load} method.</p>
     *
     * <ul>Try with a FileWriter (fw) of the jsonFile to:
     *     <li>> add the given map to the "sethome" Map under the name "homes",
     *          and write it into the "sethome.json" file</li>
     * </ul>
     * <ul>Catch an {@code IOException}</ul>
     *
     * @throws IOException if the config could not be writen
     */
    public void writeHome(Map<Object, Object> map) throws IOException {
        load();

        try (FileWriter fw = new FileWriter(jsonFile, false)) {
            sethome.put("homes", map);

            GSON.toJson(sethome, fw);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Called by the {@link SetHomeJSONConfig#createHome(UUID, String, String, double, double, double, float, float)
     * createHome} method, when creating a home.</p>
     *
     * <p>Calls the {@link SetHomeJSONConfig#load() load} method.</p>
     *
     * <ul>Try with a FileWriter (fw) of the jsonFile to:
     *     <li>> put in the options Map the values corresponding to their key, and put the given map to the "sethome"
     *     Map under the name options. Then write it to the "sethome.json" file.</li>
     * </ul>
     * <ul>Catch an {@code IOException}</ul>
     *
     * @throws IOException if the config could not be writen
     */
    public void writeChanges(Map<Object, Object> map) throws IOException {
        load();

        try (FileWriter fw = new FileWriter(jsonFile, false)) {

            options.put("areHomesAllowed", this.allowHomes);
            options.put("maxHomes", this.maxHomes);

            sethome.put("options", map);

            GSON.toJson(sethome, fw);

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
     *      > Try to write the default optionsMap in the "optionsMap" Map,
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
            options.put("maxHomes", 15);

            sethome.put("options", options);
            sethome.put("homes", homes);

            GSON.toJson(sethome, fw);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
