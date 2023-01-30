package fr.aeldit.cyansh;

import eu.midnightdust.lib.config.MidnightConfig;
import fr.aeldit.cyansh.commands.ConfigCommands;
import fr.aeldit.cyansh.commands.HomeCommands;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import fr.aeldit.cyansh.util.Constants;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CyanSHServerCore implements DedicatedServerModInitializer
{
    public static final String MODID = "cyansh";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final String MODNAME = "[CyanSetHome]";

    @Override
    // Initialize the differents instances (here commands) when lauched on server
    public void onInitializeServer()
    {
        MidnightConfig.init(MODID, CyanSHMidnightConfig.class);
        CyanSHServerCore.LOGGER.info("{} Successfully initialized config", MODNAME);

        Constants.generateAllMaps();

        // Register all the commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            ConfigCommands.register(dispatcher);
            HomeCommands.register(dispatcher);
        });
        CyanSHServerCore.LOGGER.info("{} Successfully initialized commands", MODNAME);
        CyanSHServerCore.LOGGER.info("{} Successfully completed initialization", MODNAME);
    }
}
