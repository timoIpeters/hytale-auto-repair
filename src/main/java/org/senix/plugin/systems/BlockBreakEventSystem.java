package org.senix.plugin.systems;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemQuality;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.senix.plugin.components.AutoRepairSettings;

import java.awt.*;

public class BlockBreakEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    public BlockBreakEventSystem() {
        super(BreakBlockEvent.class);
    }

    @Override
    public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl BreakBlockEvent breakBlockEvent) {
        PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        if (playerRef == null) return;

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null) return;

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        Inventory inv = player.getInventory();
        ItemStack tool = inv.getItemInHand();

        // Check if tool is about to break
        if (tool != null && tool.getDurability() <= 1f && !tool.isBroken()) {
            AutoRepairSettings settings = store.getComponent(ref, AutoRepairSettings.TYPE);

            if (settings != null && settings.isEnabled() && tryConsumeRepairKit(player, "Tool_Repair_Kit_Iron")) {

                ItemStack repaired = tool.withRestoredDurability(tool.getMaxDurability());

                ItemContainer targetContainer;
                short slotIndex;

                if (inv.getActiveToolsSlot() != -1 && tool.equals(inv.getToolsItem())) {
                    targetContainer = inv.getTools();
                    slotIndex = inv.getActiveToolsSlot();
                } else {
                    targetContainer = inv.getHotbar();
                    slotIndex = inv.getActiveHotbarSlot();
                }

                if (targetContainer != null && slotIndex != -1) {
                    targetContainer.setItemStackForSlot(slotIndex, repaired);

                    // send the update inventory data as packet
                    playerRef.getPacketHandler().writeNoCache(inv.toPacket());


                    IndexedLookupTableAssetMap<String, ItemQuality> qualityMap = ItemQuality.getAssetMap();
                    ItemQuality quality = qualityMap.getAsset(tool.getItem().getQualityIndex());

                    com.hypixel.hytale.protocol.Color hytaleColor = quality != null ? quality.getTextColor() : new com.hypixel.hytale.protocol.Color();
                    Color rarityColor = (quality != null) ? new Color(hytaleColor.red & 0xFF, hytaleColor.green & 0xFF, hytaleColor.blue & 0xFF) : Color.WHITE;

                    Message message = Message.join(
                            Message.raw("Auto-repaired your "),
                            Message.translation(tool.getItem().getTranslationKey()).color(rarityColor)
                    );

                    playerRef.sendMessage(message);
                }
            }
        }
    }



    private static boolean tryConsumeRepairKit(Player player, String targetId) {
        Inventory inv = player.getInventory();
        // Search the actual containers
        ItemContainer[] searchOrder = { inv.getHotbar(), inv.getStorage() };

        for (ItemContainer container : searchOrder) {
            for (short i = 0; i < container.getCapacity(); i++) {
                ItemStack stack = container.getItemStack(i);

                // Match the exact string from your logs
                if (stack != null && stack.getItemId().equalsIgnoreCase(targetId)) {
                    container.removeItemStackFromSlot(i, 1);
                    inv.markChanged();
                    return true;
                }
            }
        }
        return false;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}

