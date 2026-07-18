package de.derniklaas.mccislandtooltips.client;

import java.util.Locale;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.multiplayer.ServerData;

public class MCCIslandTooltipsClient implements ClientModInitializer {

    private static boolean onMCCIsland = false;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((_, _, client) -> {
            ServerData server = client.getCurrentServer();
            onMCCIsland = server != null && isMCCIslandAddress(server.ip);
        });
        ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> onMCCIsland = false);
    }

    public static boolean isOnMCCIsland() {
        return onMCCIsland;
    }

    private static boolean isMCCIslandAddress(String address) {
        String host = address.toLowerCase(Locale.ROOT);
        int portSeparator = host.lastIndexOf(':');
        if (portSeparator != -1) {
            host = host.substring(0, portSeparator);
        }
        return host.equals("mccisland.net") || host.endsWith(".mccisland.net");
    }
}
