package net.azisaba.soulbound.listener;

import net.azisaba.loreeditor.api.item.CraftItemStack;
import net.azisaba.loreeditor.api.item.tag.CompoundTag;
import net.azisaba.soulbound.Soulbound;
import net.azisaba.soulbound.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MythicListener implements Listener {
    private final Soulbound plugin;

    public MythicListener(Soulbound plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!e.getPlayer().isOnline()) return;
            Inventory inventory = e.getPlayer().getInventory();
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) continue;
                if (plugin.getSoulboundMythicItems().contains(ItemUtil.getMythicType(item))) {
                    net.azisaba.loreeditor.api.item.ItemStack nms = CraftItemStack.STATIC.asNMSCopy(item);
                    if (nms != null) {
                        CompoundTag tag = nms.getOrCreateTag();
                        tag.setString("soulbound", e.getPlayer().getUniqueId().toString());
                        nms.setTag(tag);
                        inventory.setItem(i, CraftItemStack.STATIC.asCraftMirror(nms));
                    }
                }
            }
        }, 20 * 5);
    }
}
