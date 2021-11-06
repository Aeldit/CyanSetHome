package fr.raphoulfifou.sethome.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.raphoulfifou.sethome.HomeClientCore;
import fr.raphoulfifou.sethome.HomeServerCore;
import fr.raphoulfifou.sethome.util.structure.HomeParameter;
import fr.raphoulfifou.sethome.util.structure.Parameters;
import net.fabricmc.loader.api.FabricLoader;

/**
 * @since 0.0.1
 * @see Parameters
 * @see HomeParameter
 * @author Raphoulfifou
 */
public class SetHomeJSONConfig {

    // Constants
    public static final Path jsonPath = FabricLoader.getInstance().getConfigDir().resolve("sethome.json");
    public static final File jsonFile = FabricLoader.getInstance().getConfigDir().resolve("sethome.json").toFile();

    // Variables
    public Map<Object, Object> sethome = new HashMap<>();
    public Map<Object, Object> options = new HashMap<>();
    public Map<Object, Object> homes = new HashMap<>();
    public List<Parameters> parametersList = new ArrayList<>();

    public boolean allowHomes = true;
    public int maxHomes = 15;

    public File file;

    // Get a variable's value
    public boolean areHomesAllowed() {
        return allowHomes;
    }
    public int getMaxHomes() {
        Object value = sethome.get(options.get(maxHomes));
        return (int) value;
    }
    public int getHomesNumber(UUID uuid) {
        return homes.size();
    }
    public String[] getHomes() {
        return new String[0];
    }

    // Set the value of the option to the given value
    public void setAreHomesAllowed(boolean allowHomes) {
        this.allowHomes = allowHomes;
        writeChanges();
    }
    public void setMaxHomes(int maxHomes) {
        this.maxHomes = maxHomes;
        writeChanges();
    }

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    /**
     * <p>Called by the {@link HomeServerCore#onInitializeServer() onInitializeServer} and
     * {@link HomeClientCore#onInitializeClient() onInitializeClient} functions respectively located in
     * {@link fr.raphoulfifou.sethome.HomeServerCore HomeServerCore} and
     * {@link fr.raphoulfifou.sethome.HomeClientCore HomeClientCore}</p>
     *
     * <ul>If the file exists to the given path (jsonPath):
     *         <li>> Try to read the file as a json file.</li>
     *         <li>> Throws a {@code RuntimeException} if the cofig could not be read.</li>
     * </ul>
     * <ul>Else
     *     <li>> Try to write the default config by calling the {@link SetHomeJSONConfig#writeDefaultConfig()
     *     writeDefaultConfig} method.</li>
     *     <li>> Throws a {@code RuntimeException} if the default config could not be written.</li>
     * </ul>
     */
    public SetHomeJSONConfig load(File file)
    {
        SetHomeJSONConfig config;

        if (file.exists()) {
            try (FileReader fr = new FileReader(file)) {
                config = GSON.fromJson(fr, SetHomeJSONConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not read config", e);
            }
        } else {
            config = new SetHomeJSONConfig();
        }

        config.file = file;
        config.writeChanges();

        return config;
    }


    /**
     * <p>Called by the {@link fr.raphoulfifou.sethome.commands.SetHomeCommand#setHome sethome} method.</p>
     *
     * <ul>Try to:
     *      <li>Create a new parameter with the input values and put it into a List (parametersList).</li>
     *      <li>Create a new homeParameter with the parametersList value.</li>
     *      <li>Put to the homeParameter in the the Map named with the uuid of the player inside the homes Map.</li>
     * </ul>
     * <p>Calls the {@link SetHomeJSONConfig#writeHome(Map) writeHome} method with 'homes' as parameter.</p>
     * <ul>Catche an {@code IOException} if the config could not be written.</ul>
     */
    public void createHome(UUID uuid, String name, String dimension, double x, double y, double z, float yaw, float pitch) {
        //load();
        try (FileWriter fw = new FileWriter(jsonFile, false))
        {
            Parameters parameters = new Parameters(name, dimension, x, y, z, yaw, pitch);
            parametersList.add(parameters);

            HomeParameter homeParameter = new HomeParameter(parametersList);

            homes.put(uuid, homeParameter);

            sethome.put("homes", homes);
            GSON.toJson(sethome, fw);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Called by the {@link SetHomeJSONConfig#createHome(UUID, String, String, double, double, double, float, float)
     *      createHome} method creating a home.</p>
     *
     * <p>Calls the {@link SetHomeJSONConfig#load() load} method.</p>
     *
     * <ul>Try with a FileWriter (fw) of the jsonFile to:
     *     <li>> add the given map to the "sethome" Map under the name "homes",
     *          and write it into the "sethome.json" file.</li>
     * </ul>
     * @throws IOException if the config could not be written.
     */
    public void writeHome(Map<Object, Object> map) throws IOException {
        //load();

        try (FileWriter fw = new FileWriter(jsonFile, false))
        {
            sethome.put("homes", map);

            GSON.toJson(sethome, fw);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Called by the {@link SetHomeJSONConfig#createHome(UUID, String, String, double, double, double, float, float)
     * createHome} method, when modifying the values inside the file.</p>
     *
     * <p>Calls the {@link SetHomeJSONConfig#load() load} method.</p>
     *
     * <ul>Try with a FileWriter (fw) of the jsonFile to:
     *     <li>> put in the options Map the values corresponding to their key, and put the given map to the "sethome"
     *     Map under the name options. Then write it to the "sethome.json" file.</li>
     * </ul>
     * @throws IOException if the config could not be written.
     */
    public void writeChanges() {
        File dir = this.file.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Could not create parent directories");
            }
        } else if (!dir.isDirectory()) {
            throw new RuntimeException("The parent file is not a directory");
        }

        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save configuration file", e);
        }
    }

    /**
     * <p>Called by the {@link SetHomeJSONConfig#load() load} method.</p>
     *
     * <ul>If the file doesn't exists to the given path (dir):
     *      <li>-> a directory is created to the given path (dir).</li>
     * </ul>
     * <ul>Else if the given path is not a directory:
     *      <li>-> An {@code IOException} is thrown.</li>
     * </ul>
     *
     * <ul>Try with a FileWriter (fw) of the jsonFile to:
     *      <li>> put the default values to their respective map, and then write them to the jsonFile.</li>
     * </ul>
     * @throws IOException if the config could not be written.
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

    public void save() {
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(GSON.toJson(options));
		} catch (IOException e) {
			HomeClientCore.LOGGER.error("Error saving config", e);
		}
	}

}
