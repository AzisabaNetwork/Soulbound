package net.azisaba.soulbound.listener;

import de.epiceric.shopchest.event.ShopBuySellEvent;
import net.azisaba.soulbound.Soulbound;
import net.azisaba.soulbound.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SoulboundListener implements Listener {
    private final Soulbound plugin;

    public SoulboundListener(@NotNull Soulbound plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDropITem(PlayerDropItemEvent e) {
        if (plugin.allowDrop) return;
        if (e.getPlayer().getUniqueId().equals(ItemUtil.getSoulbound(e.getItemDrop().getItemStack()))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "このアイテムはドロップできません。");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent e) {
        if (e.getPlayer().hasPermission("soulbound.bypass")) {
            return;
        }
        UUID uuid = ItemUtil.getSoulbound(e.getItem().getItemStack());
        if (uuid != null && !e.getPlayer().getUniqueId().equals(uuid)) {
            e.setCancelled(true);
            e.setFlyAtPlayer(false);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityPickupItem(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) return;
        if (ItemUtil.getSoulbound(e.getItem().getItemStack()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemFrameBroke(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof ItemFrame)) return;
        if (e.getDamager().hasPermission("soulbound.bypass")) return;
        ItemStack item = ((ItemFrame) e.getEntity()).getItem();
        UUID uuid = ItemUtil.getSoulbound(item);
        if (uuid != null && !e.getDamager().getUniqueId().equals(uuid)) {
            e.getDamager().sendMessage(ChatColor.RED + "この額縁は破壊できません。");
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onItemFrameBroke(HangingBreakEvent e) {
        if (!(e.getEntity() instanceof ItemFrame)) return;
        ItemStack item = ((ItemFrame) e.getEntity()).getItem();
        UUID uuid = ItemUtil.getSoulbound(item);
        if (uuid != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryPickupItemLowPriority(InventoryPickupItemEvent e) {
        if (ItemUtil.getSoulbound(e.getItem().getItemStack()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryPickupItemHighestPriority(InventoryPickupItemEvent e) {
        if (ItemUtil.getSoulbound(e.getItem().getItemStack()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDispense(BlockDispenseArmorEvent e) {
        if (e.getTargetEntity().hasPermission("soulbound.bypass")) return;
        UUID uuid = ItemUtil.getSoulbound(e.getItem());
        if (uuid != null && !e.getTargetEntity().getUniqueId().equals(uuid)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDispense(BlockDispenseEvent e) {
        if (ItemUtil.getSoulbound(e.getItem()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        if (e.getPlayer().hasPermission("soulbound.bypass")) return;
        UUID uuid = ItemUtil.getSoulbound(e.getArmorStandItem());
        if (uuid != null && !e.getPlayer().getUniqueId().equals(uuid)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "このアイテムは操作できません。");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (player.hasPermission("soulbound.bypass")) return;
        UUID uuid = ItemUtil.getSoulbound(e.getCurrentItem());
        if (uuid != null && !player.getUniqueId().equals(uuid)) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "このアイテムはクリックできません。");
            return;
        }
        if (e.getInventory().getHolder() instanceof ShulkerBox || e.getInventory().getClass().getName().endsWith(".inventory.CraftInventoryCustom")) {
            if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER && ItemUtil.getSoulbound(e.getCurrentItem()) != null) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + "このアイテムは取引できません。");
            }
            int button = e.getHotbarButton();
            if (button != -1) {
                if (ItemUtil.getSoulbound(player.getInventory().getItem(button)) != null) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "このアイテムは取引できません。");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (plugin.keepSoulboundOnDeath) {
            e.getDrops().removeIf(stack -> {
                boolean keep = ItemUtil.getSoulbound(stack) != null;
                if (keep) e.getItemsToKeep().add(stack);
                return keep;
            });
        }
    }
}
