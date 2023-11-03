package net.azisaba.soulbound;

import net.azisaba.loreeditor.api.event.EventBus;
import net.azisaba.loreeditor.api.event.ItemEvent;
import net.azisaba.loreeditor.libs.net.kyori.adventure.text.Component;
import net.azisaba.loreeditor.libs.net.kyori.adventure.text.format.NamedTextColor;
import net.azisaba.loreeditor.libs.net.kyori.adventure.text.format.TextDecoration;
import net.azisaba.soulbound.commands.SoulboundCommand;
import net.azisaba.soulbound.listener.MythicListener;
import net.azisaba.soulbound.listener.SoulboundListener;
import net.azisaba.soulbound.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Soulbound extends JavaPlugin {
    private final Set<String> soulboundMythicItems = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        soulboundMythicItems.addAll(getConfig().getStringList("soulbound-mythic-items"));
        EventBus.INSTANCE.register(this, ItemEvent.class, 128, e -> {
            UUID soulbound = ItemUtil.getSoulbound(e.getBukkitItem());
            if (soulbound != null) {
                if (e.getPlayer().hasPermission("soulbound.see-details")) {
                    e.addLore(Component.text("* Soulbound (取引不可) <" + soulbound + "> *", NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false));
                } else {
                    e.addLore(Component.text("* Soulbound (取引不可) *", NamedTextColor.DARK_GRAY)
                            .decoration(TextDecoration.ITALIC, false));
                }
            }
        });
        Objects.requireNonNull(getCommand("soulbound")).setExecutor(new SoulboundCommand(this));
        Bukkit.getPluginManager().registerEvents(new SoulboundListener(getConfig().getBoolean("keep-soulbound-on-death", false)), this);
        Bukkit.getPluginManager().registerEvents(new MythicListener(this), this);
    }

    @Override
    public void onDisable() {
        getConfig().set("soulbound-mythic-items", new ArrayList<>(soulboundMythicItems));
        saveConfig();
    }

    public @NotNull Set<@NotNull String> getSoulboundMythicItems() {
        return soulboundMythicItems;
    }
}
