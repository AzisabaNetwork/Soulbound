package net.azisaba.soulbound.listener;

import de.epiceric.shopchest.event.ShopBuySellEvent;
import net.azisaba.soulbound.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShopChestListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onShopBuySell(ShopBuySellEvent e) {
        if (ItemUtil.getSoulbound(e.getShop().getItem().getItemStack()) != null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "このアイテムは取引できません。");
        }
    }
}
