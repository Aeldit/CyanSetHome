package fr.raphoulfifou.sethome.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.raphoulfifou.sethome.util.structure.HomeParameters;
import fr.raphoulfifou.sethome.util.structure.Parameters;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SetHomeJSONConfig {
    private static Path jsonPath = FabricLoader.getInstance().getConfigDir().resolve("sethome.json");

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

    // Set a variable's value
    public void setAreHomesAllowed(boolean allowHomes) {
        SetHomeJSONConfig.allowHomes = allowHomes;
        try {
            writeChanges();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setAreHomesMultiDimensional(boolean multiDimensionalHomes) {
        SetHomeJSONConfig.multiDimensionalHomes = multiDimensionalHomes;
        try {
            writeChanges();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMaxHomes(int maxHomes) {
        SetHomeJSONConfig.maxHomes = maxHomes;
        try {
            writeChanges();
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

    public void createHome(UUID uuid, String name, String dimension, double x, double y, double z, float yaw, float pitch) {
        try (FileOutputStream fos = new FileOutputStream(String.valueOf(jsonPath));
             OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            Parameters parameters = new Parameters(name, dimension, x, y, z, yaw, pitch);
            List<Parameters> parametersList = new ArrayList<>();
            parametersList.add(parameters);

            HomeParameters parametersH = new HomeParameters(parametersList);
            List<HomeParameters> homesList = new ArrayList<>();
            homesList.add(parametersH);

            Map<Object, Object> sethome = new HashMap<>();
            sethome.put("homes", homesList);

            GSON.toJson(sethome, isr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] getHomes() {
        return new String[0];
    }
    /*
    public static void getDimension(String homeName) {
        return;
    }
    */

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    public static void load(Path path) {
        SetHomeJSONConfig config;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, SetHomeJSONConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not parse config", e);
            }
        } else {
            config = new SetHomeJSONConfig();
        }

        jsonPath = path;

        try {
            config.writeChanges();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update config file", e);
        }
    }

    public void writeChanges() throws IOException {
        Path dir = jsonPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        try (FileOutputStream fos = new FileOutputStream(String.valueOf(jsonPath));
             OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            Map<String, Object> options = new HashMap<>();
            options.put("areHomesAllowed", SetHomeJSONConfig.allowHomes);
            options.put("multiDimensionalHomes", SetHomeJSONConfig.multiDimensionalHomes);
            options.put("maxHomes", SetHomeJSONConfig.maxHomes);

            List<HomeParameters> homesList = new ArrayList<>();

            Map<Object, Object> sethome = new HashMap<>();
            sethome.put("options", options);
            sethome.put("homes", homesList);

            GSON.toJson(sethome, isr);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Files.writeString(jsonPath, GSON.toJson(this));
    }

}
