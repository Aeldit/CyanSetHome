package fr.aeldit.cyansh;

import eu.midnightdust.lib.config.MidnightConfig;
import fr.aeldit.cyansh.commands.ConfigCommands;
import fr.aeldit.cyansh.commands.HomeCommands;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CyanSHClientCore implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger(CyanSHServerCore.MODID);
    public static final String MODNAME = CyanSHServerCore.MODNAME;

    @Override
    // Initialize the differents instances (here commands) when lauched on client (used when in singleplayer)
    public void onInitializeClient() {
        MidnightConfig.init(CyanSHServerCore.MODID, CyanSHMidnightConfig.class);
        CyanSHClientCore.LOGGER.info("{} Successfully initialized config", MODNAME);

        // Register all the commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            ConfigCommands.register(dispatcher);
            HomeCommands.register(dispatcher);
        });

        CyanSHClientCore.LOGGER.info("{} Successfully initialized commands", MODNAME);
        CyanSHClientCore.LOGGER.info("{} Successfully completed initialization", MODNAME);
    }

}