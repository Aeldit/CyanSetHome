package fr.aeldit.cyansh;

import eu.midnightdust.lib.config.MidnightConfig;
import fr.aeldit.cyansh.commands.ConfigCommands;
import fr.aeldit.cyansh.commands.HomeCommands;
import fr.aeldit.cyansh.commands.PermissionCommands;
import fr.aeldit.cyansh.config.CyanSHMidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        CyanSHClientCore.LOGGER.info("{} Successfully initialized config", MODNAME);

        // Register all the commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            ConfigCommands.register(dispatcher);
            HomeCommands.register(dispatcher);
            PermissionCommands.register(dispatcher);
        });

        // TODO -> make work
        /*ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            File[] files = locationsPath.toFile().listFiles();

            assert files != null;
            for (File file : files) {
                Properties properties = new Properties();
                try {
                    properties.load(new FileInputStream(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (properties.isEmpty()) {
                    CyanSHClientCore.LOGGER.info("{} {}", MODNAME, properties);
                    try {
                        Files.delete(file.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });*/

        CyanSHClientCore.LOGGER.info("{} Successfully initialized commands", MODNAME);
        CyanSHClientCore.LOGGER.info("{} Successfully completed initialization", MODNAME);
    }
}
