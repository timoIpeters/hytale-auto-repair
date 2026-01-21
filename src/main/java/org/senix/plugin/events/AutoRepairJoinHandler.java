package org.senix.plugin.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.senix.plugin.components.AutoRepairSettings;

import java.awt.*;

public class AutoRepairJoinHandler {
    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        Ref<EntityStore> playerRef =  player.getReference();
        if (playerRef == null) return;

        AutoRepairSettings settings = playerRef.getStore().getComponent(playerRef, AutoRepairSettings.TYPE);
        boolean isEnabled = settings != null && settings.isEnabled();


        Message message = Message.join(
                Message.raw("[Auto-Repair] Status: "),
                Message.raw(isEnabled ? "ENABLED" : "DISABLED").color(isEnabled ? Color.GREEN : Color.RED)
        );
        player.sendMessage(message);
    }
}
