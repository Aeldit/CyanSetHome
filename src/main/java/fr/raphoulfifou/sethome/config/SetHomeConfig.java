package fr.raphoulfifou.sethome.config;

import fr.raphoulfifou.sethome.HomeServerCore;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class SetHomeConfig {
    private static final Logger LOGGER = LogManager.getLogger(HomeServerCore.MODID);

    private static final String COMMENT = "This file stores the homes and the settings for the SetHome mod";

    private static Path path;

    public SetHomeConfig() {
        path = FabricLoader.getInstance().getConfigDir().resolve("sethome.properties");
    }

}
