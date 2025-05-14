package fr.aeldit.cyansethome;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import static fr.aeldit.cyansethome.CyanSHCore.*;

public class CyanSHClientCore implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            HOMES.readClient(server.getSaveProperties().getLevelName());
            TRUSTS.readClient();
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> removeEmptyModDir());
    }
}
