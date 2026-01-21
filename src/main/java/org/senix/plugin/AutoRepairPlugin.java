package org.senix.plugin;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.senix.plugin.commands.AutoRepairToggleCommand;
import org.senix.plugin.commands.NearlyBreakToolCommand;
import org.senix.plugin.components.AutoRepairSettings;
import org.senix.plugin.events.AutoRepairJoinHandler;
import org.senix.plugin.systems.BlockBreakEventSystem;

public class AutoRepairPlugin extends JavaPlugin {

    public AutoRepairPlugin(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        super.setup();

        AutoRepairSettings.TYPE = getEntityStoreRegistry().registerComponent(
                AutoRepairSettings.class,
                "senix:autorepair_settings",
                AutoRepairSettings.CODEC
        );

        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, AutoRepairJoinHandler::onPlayerReady);

        this.getEntityStoreRegistry().registerSystem(new BlockBreakEventSystem());

        this.getCommandRegistry().registerCommand(new AutoRepairToggleCommand());

        // Only used for testing purposes
//        this.getCommandRegistry().registerCommand(new NearlyBreakToolCommand());
    }
}
