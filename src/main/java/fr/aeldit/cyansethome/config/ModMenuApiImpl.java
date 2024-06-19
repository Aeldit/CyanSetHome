package fr.aeldit.cyansethome.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fr.aeldit.cyanlib.lib.gui.CyanLibConfigScreen;

import static fr.aeldit.cyanlib.lib.CyanLib.CONFIG_CLASS_INSTANCES;
import static fr.aeldit.cyansethome.CyanSHCore.MODID;

public class ModMenuApiImpl implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> new CyanLibConfigScreen(null,
                CONFIG_CLASS_INSTANCES.get(MODID).getOptionsStorage(),
                parent,
                CONFIG_CLASS_INSTANCES.get(MODID).getOptionsStorage().getConfigClass()
        );
    }
}
