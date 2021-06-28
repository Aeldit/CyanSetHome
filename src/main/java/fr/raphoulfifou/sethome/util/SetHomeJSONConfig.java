package fr.raphoulfifou.sethome.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SetHomeJSONConfig {
    public final Options options = new Options();
    private static Path jsonPath = FabricLoader.getInstance().getConfigDir().resolve("sethome.json");

    public static class Options {
        public boolean allowHomes = true;
        public int maxHomes = 15;
        public boolean multiDimensionalHomes = true;

        // Get a variable's value
        public boolean areHomesAllowed() {
            return this.allowHomes;
        }
        public int getMaxHomes() {
            return this.maxHomes;
        }
        public boolean areHomesMultiDimensional() {
            return this.multiDimensionalHomes;
        }

        // Set a variable's value
        public void setAreHomesAllowed(boolean allowHomes) {
            this.allowHomes = allowHomes;
        }
        public void setMaxHomes(int maxHomes) {
            this.maxHomes = maxHomes;
        }
        public void setAreHomesMultiDimensional(boolean multiDimensionalHomes) {
            this.multiDimensionalHomes = multiDimensionalHomes;
        }
    }

    public static void main() {
        try (FileOutputStream fos = new FileOutputStream(String.valueOf(jsonPath));
             OutputStreamWriter isr = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {

            Parameters parameters = new Parameters("name", "dimension", 0, 0, 0, 0, 0);

            /*
            if(homesMap.contains(uuid)) {
                return;
            } else {
                homesMap.add(uuid);
            }
             */

            List<Parameters> parametersList = new ArrayList<>();
            parametersList.add(parameters);

            HomeParameters parametersH = new HomeParameters(parametersList);

            List<HomeParameters> homesList = new ArrayList<>();
            homesList.add(parametersH);

            GSON.toJson(homesList, isr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public void createHome(UUID uuid, String name, ServerWorld dimension, double x, double y, double z, float yaw, float pitch) {
        //String fileName = "config/sethome.json";

        try (FileOutputStream fos = new FileOutputStream(String.valueOf(jsonPath));
             OutputStreamWriter isr = new OutputStreamWriter(fos,
                     StandardCharsets.UTF_8)) {

            Gson gson = new Gson();

            Parameters nameP = new Parameters("name", name);
            Parameters dimensionP = new Parameters("dimension", dimension.toString());
            Parameters xP = new Parameters("x", Objects.toString(x));
            Parameters yP = new Parameters("y", Objects.toString(y));
            Parameters zP = new Parameters("z", Objects.toString(z));
            Parameters yawP = new Parameters("yaw", Objects.toString(yaw));
            Parameters pitchP = new Parameters("pitch", Objects.toString(pitch));

            if(homesMap.contains(uuid)) {
                return;
            } else {
                homesMap.add(uuid);
            }

            List<Parameters> parameters = new ArrayList<>();
            parameters.add(nameP);
            parameters.add(dimensionP);
            parameters.add(xP);
            parameters.add(yP);
            parameters.add(zP);
            parameters.add(yawP);
            parameters.add(pitchP);

            HomeParameters parametersH = new HomeParameters(parameters);

            List<HomeParameters> homesList = new ArrayList<>();
            homesList.add(parametersH);

            gson.toJson(homesList, isr);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */

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

        Files.writeString(jsonPath, GSON.toJson(this));
    }

}
