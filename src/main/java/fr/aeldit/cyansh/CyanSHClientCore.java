package fr.aeldit.cyansh;

import eu.midnightdust.lib.config.MidnightConfig;
import fr.aeldit.cyansh.commands.ConfigCommands;
import fr.aeldit.cyansh.commands.HomeCommands;
import fr.aeldit.cyansh.commands.PermissionCommands;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import static fr.aeldit.cyansh.util.Utils.*;

public class CyanSHClientCore implements ClientModInitializer
{
    public static final String MODID = "cyansh";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final String MODNAME = "[CyanSetHome]";

    @Override
    // Initialize the differents instances (here commands) when lauched on client (used when in singleplayer)
    public void onInitializeClient()
    {
        MidnightConfig.init(CyanSHServerCore.MODID, CyanSHMidnightConfig.class);
        LOGGER.info("{} Successfully initialized config", MODNAME);

        // Register all the commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            ConfigCommands.register(dispatcher);
            HomeCommands.register(dispatcher);
            PermissionCommands.register(dispatcher);
        });
        LOGGER.info("{} Successfully initialized commands", MODNAME);

        // Check if the players names matches the UUID in the trust file, and renames them if needed
        // Same but with the homes file name (UUID_playerName)
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            String playerKey = player.getUuidAsString() + "_" + player.getName().getString();

            File currentHomesDir = new File(homesPath.toUri());
            Path currentHomesPath = Path.of(homesPath + "\\" + playerKey + ".properties");
            checkOrCreateHomesDir();
            File[] listOfFiles = currentHomesDir.listFiles();

            if (listOfFiles != null)
            {
                for (File file : listOfFiles)
                {
                    if (file.isFile())
                    {
                        if (Objects.equals(file.getName().split("_")[0], player.getUuidAsString()) && !Objects.equals(file.getName().split("_")[1], player.getName().getString()))
                        {
                            try
                            {
                                Files.move(file.toPath(), currentHomesPath.resolveSibling(playerKey + ".properties"));
                                LOGGER.info("{} Rename the file '{}' to '{}' because the player changed its pseudo", MODNAME, file.getName(), playerKey + ".properties");
                            } catch (IOException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }

            checkOrCreateTrustFile();
            try
            {
                Properties properties = new Properties();
                properties.load(new FileInputStream(trustPath.toFile()));
                String prevName;
                if (properties.stringPropertyNames().size() != 0)
                {
                    if (!properties.containsKey(playerKey))
                    {
                        for (String key : properties.stringPropertyNames())
                        {
                            if (Objects.equals(key.split("_")[0], player.getUuidAsString()))
                            {
                                prevName = key.split("_")[1];
                                if (!Objects.equals(key.split("_")[1], player.getName().getString()))
                                {
                                    properties.put(playerKey, properties.get(key));
                                    properties.remove(key);
                                    properties.store(new FileOutputStream(trustPath.toFile()), null);
                                    LOGGER.info("{} Updated {}'s pseudo in the trust file, because the player changed its pseudo (previously {})", MODNAME, player.getName().getString(), prevName);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        });

        LOGGER.info("{} Successfully completed initialization", MODNAME);
    }
}
