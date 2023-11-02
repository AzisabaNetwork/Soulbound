package net.azisaba.soulbound.commands;

import net.azisaba.loreeditor.api.item.CraftItemStack;
import net.azisaba.loreeditor.api.item.tag.CompoundTag;
import net.azisaba.soulbound.Soulbound;
import net.azisaba.soulbound.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SoulboundCommand implements TabExecutor {
    private final Soulbound plugin;

    public SoulboundCommand(Soulbound plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/soulbound (reload|add|remove|list)");
            return true;
        }
        switch (args[0]) {
            case "reload":
                plugin.reloadConfig();
                break;
            case "add": {
                Player player = (Player) sender;
                String mythicType = args.length > 1 ? args[1] : ItemUtil.getMythicType(player.getInventory().getItemInMainHand());
                if (mythicType != null) {
                    if (plugin.getSoulboundMythicItems().add(mythicType)) {
                        sender.sendMessage(ChatColor.GREEN + "Added " + mythicType);
                    } else {
                        sender.sendMessage(ChatColor.RED + mythicType + " is already in the list");
                    }
                }
                break;
            }
            case "remove": {
                Player player = (Player) sender;
                String mythicType = args.length > 1 ? args[1] : ItemUtil.getMythicType(player.getInventory().getItemInMainHand());
                if (mythicType != null) {
                    if (plugin.getSoulboundMythicItems().remove(mythicType)) {
                        sender.sendMessage(ChatColor.GREEN + "Removed " + mythicType);
                    } else {
                        sender.sendMessage(ChatColor.RED + mythicType + " is not in list");
                    }
                }
                break;
            }
            case "list":
                sender.sendMessage(String.join(", ", plugin.getSoulboundMythicItems()));
                break;
            case "apply": {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "/soulbound apply <uuid or name>");
                    return true;
                }
                Player player = (Player) sender;
                ItemStack stack = player.getInventory().getItemInMainHand();
                if (stack.getType() == Material.AIR) {
                    player.sendMessage(ChatColor.RED + "あいてむもって");
                    return true;
                }
                //noinspection deprecation
                UUID uuid = args[1].length() == 36 ? UUID.fromString(args[1]) : Bukkit.getOfflinePlayer(args[1]).getUniqueId();
                net.azisaba.loreeditor.api.item.ItemStack nms = CraftItemStack.STATIC.asNMSCopy(stack);
                if (nms == null) throw new RuntimeException("item is null?");
                CompoundTag tag = nms.getOrCreateTag();
                tag.setString("soulbound", uuid.toString());
                nms.setTag(tag);
                player.getInventory().setItemInMainHand(CraftItemStack.STATIC.asCraftMirror(nms));
                break;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "add", "remove", "list", "apply").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
