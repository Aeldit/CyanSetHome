package fr.aeldit.cyansh;

import eu.midnightdust.lib.config.MidnightConfig;
import fr.aeldit.cyansh.commands.HomeCommand;
import fr.aeldit.cyansh.commands.SetCommands;
import fr.aeldit.cyansh.commands.SetHomeCommand;
import fr.aeldit.cyansh.config.HomesSaving;
import fr.aeldit.cyansh.config.SethomeMidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author Raphoulfifou
 * @see HomeServerCore
 * @since 0.0.1
 */
@Environment(EnvType.CLIENT)
public class HomeClientCore implements ClientModInitializer
{

    public static final Logger LOGGER = LogManager.getLogger(HomeServerCore.MODID);
    public static final String MODNAME = HomeServerCore.MODNAME;

    @Override
    // Initialize the differents instances (here commands) when lauched on client (used when in singleplayer)
    public void onInitializeClient()
    {
        MidnightConfig.init(HomeServerCore.MODID, SethomeMidnightConfig.class);
        HomeClientCore.LOGGER.info("{} Successfully initialized config", MODNAME);

        // Register all the commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            SetHomeCommand.register(dispatcher);
            HomeCommand.register(dispatcher);
        });
        try
        {
            HomesSaving.create_default();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        HomeClientCore.LOGGER.info("{} Successfully initialized commands", MODNAME);
        HomeClientCore.LOGGER.info("{} Successfully completed initialization", MODNAME);
    }

}