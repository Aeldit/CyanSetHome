package fr.aeldit.cyansethome;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static fr.aeldit.cyansethome.CyanSHCore.*;

public class CyanSHServerCore implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        HOMES.readServer();
        TRUSTS.readServer();

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> removeEmptyModDir());
    }
}
