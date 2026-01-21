package org.senix.plugin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.senix.plugin.components.AutoRepairSettings;

import java.awt.*;

public class AutoRepairToggleCommand extends AbstractPlayerCommand {
    public AutoRepairToggleCommand() {
        super("autorepair", "Toggle auto-repair: Prevent tools from breaking by automatically using a repair kit from your inventory.");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {

        AutoRepairSettings settings = store.getComponent(ref, AutoRepairSettings.TYPE);

        boolean newState;
        if (settings == null) {
            settings = new AutoRepairSettings();
            settings.setEnabled(true);
            store.putComponent(ref, AutoRepairSettings.TYPE, settings);
            newState = true;
        } else {
            newState = !settings.isEnabled();
            settings.setEnabled(newState);
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        // mark the player's data as changed so it saves to disk
        player.markNeedsSave();

        Message message = Message.join(
                Message.raw("[Auto-Repair] Status: "),
                Message.raw(newState ? "ENABLED" : "DISABLED").color(newState ? Color.GREEN : Color.RED)
        );

        player.sendMessage(message);
    }
}
