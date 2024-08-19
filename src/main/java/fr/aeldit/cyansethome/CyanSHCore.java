package fr.aeldit.cyansethome;

import fr.aeldit.cyanlib.lib.CyanLib;
import fr.aeldit.cyanlib.lib.CyanLibLanguageUtils;
import fr.aeldit.cyansethome.config.CyanLibConfigImpl;
import fr.aeldit.cyansethome.homes.Homes;
import fr.aeldit.cyansethome.homes.Trusts;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static fr.aeldit.cyansethome.homes.Homes.HOMES_PATH;

public class CyanSHCore
{
    public static final String MODID = "cyansethome";
    public static final Logger CYANSH_LOGGER = LoggerFactory.getLogger(MODID);
    public static Path MOD_PATH = FabricLoader.getInstance().getConfigDir().resolve(MODID);

    public static final Homes HomesObj = new Homes();
    public static final Trusts TrustsObj = new Trusts();

    public static CyanLib CYANSH_LIB_UTILS = new CyanLib(MODID, new CyanLibConfigImpl());
    public static CyanLibLanguageUtils CYANSH_LANG_UTILS = CYANSH_LIB_UTILS.getLanguageUtils();

    public static void checkOrCreateHomesDir()
    {
        if (!Files.exists(MOD_PATH))
        {
            try
            {
                Files.createDirectory(MOD_PATH);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        if (!Files.exists(HOMES_PATH))
        {
            try
            {
                Files.createDirectory(HOMES_PATH);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void removeEmptyModDir()
    {
        if (Files.exists(HOMES_PATH))
        {
            File[] listOfFiles = new File(HOMES_PATH.toUri()).listFiles();

            if (listOfFiles != null && listOfFiles.length == 0)
            {
                try
                {
                    Files.delete(HOMES_PATH);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        if (Files.exists(MOD_PATH))
        {
            File[] listOfFiles = new File(MOD_PATH.toUri()).listFiles();

            if (listOfFiles != null && listOfFiles.length == 0)
            {
                try
                {
                    Files.delete(MOD_PATH);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
