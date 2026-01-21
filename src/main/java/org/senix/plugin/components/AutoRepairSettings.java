package org.senix.plugin.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Component;

public class AutoRepairSettings implements Component<EntityStore> {

    public static ComponentType<EntityStore, AutoRepairSettings> TYPE;

    public static final BuilderCodec<AutoRepairSettings> CODEC = BuilderCodec.builder(
                    AutoRepairSettings.class,
                    AutoRepairSettings::new
            )
            .addField(new KeyedCodec<>("Enabled", Codec.BOOLEAN),
                    (data, value) -> data.enabled = value,
                    data -> data.enabled)
            .build();

    private boolean enabled = false;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public Component<EntityStore> clone() {
        AutoRepairSettings copy = new AutoRepairSettings();
        copy.enabled = this.enabled;
        return copy;
    }
}
