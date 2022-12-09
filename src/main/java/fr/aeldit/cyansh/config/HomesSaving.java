package fr.aeldit.cyansh.config;

import net.fabricmc.loader.api.FabricLoader;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class HomesSaving
{
    private static final String COMMENT = "This file stores the homes of all players";
    static Path propertiesPath = FabricLoader.getInstance().getConfigDir().resolve("homes.properties");

    public static void create_default() throws IOException
    {
        Properties properties = new Properties();
        // NB: This uses ISO-8859-1 with unicode escapes as the encoding
        try (OutputStream os = Files.newOutputStream(propertiesPath))
        {
            properties.store(os, COMMENT);
        }
    }

    public static void load()
    {
        if (!Files.exists(propertiesPath))
        {
            try
            {
                create_default();
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        Properties properties = new Properties();
        // NB: This uses ISO-8859-1 with unicode escapes as the encoding
        try (InputStream is = Files.newInputStream(propertiesPath))
        {
            properties.load(is);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        try (OutputStream os = Files.newOutputStream(propertiesPath))
        {
            properties.store(os, "");
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
