package fr.raphoulfifou.cyansh;

import eu.midnightdust.lib.config.MidnightConfig;
import fr.raphoulfifou.cyansh.commands.SetCommands;
import fr.raphoulfifou.cyansh.commands.SetHomeCommand;
import fr.raphoulfifou.cyansh.config.SethomeMidnightConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Raphoulfifou
 * @see HomeClientCore
 * @since 0.0.1
 */
@Environment(EnvType.SERVER)
public class HomeServerCore implements DedicatedServerModInitializer
{

    public static final String MODID = "cyansh";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final String MODNAME = "[SetHome]";

    @Override
    // Initialize the differents instances (here commands) when lauched on server
    public void onInitializeServer()
    {
        MidnightConfig.init(MODID, SethomeMidnightConfig.class);
        HomeServerCore.LOGGER.info("{} Successfully initialized config", MODNAME);

        // Register all the commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            SetHomeCommand.register(dispatcher);
            SetCommands.register(dispatcher);
        });
        HomeServerCore.LOGGER.info("{} Successfully initialized commands", MODNAME);
        HomeServerCore.LOGGER.info("{} Successfully completed initialization", MODNAME);
    }

}