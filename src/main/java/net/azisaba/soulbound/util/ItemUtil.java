package net.azisaba.soulbound.util;

import net.azisaba.loreeditor.api.item.CraftItemStack;
import net.azisaba.loreeditor.api.item.tag.CompoundTag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class ItemUtil {
    private static final Pattern SOULBOUND_REGEX = Pattern.compile(".*\\[Soulbound: (.+)].*");

    @Contract("null, _ -> null")
    public static @Nullable String getStringTag(@Nullable ItemStack stack, @NotNull String key) {
        if (stack == null || stack.getType().isAir()) return null;
        net.azisaba.loreeditor.api.item.ItemStack nms = CraftItemStack.STATIC.asNMSCopy(stack);
        if (nms == null) return null;
        CompoundTag tag = nms.getTag();
        if (tag == null) return null;
        return tag.getString(key);
    }

    @Contract("null -> null")
    public static @Nullable String getMythicType(@Nullable ItemStack stack) {
        String type = getStringTag(stack, "MYTHIC_TYPE");
        if (type == null || type.isEmpty()) return null;
        return type;
    }

    @Contract("null -> null")
    public static @Nullable UUID getSoulbound(@Nullable ItemStack stack) {
        if (stack == null) return null;
        String uuid = getStringTag(stack, "soulbound");
        if (uuid != null && uuid.length() == 36) {
            return UUID.fromString(uuid);
        }
        List<String> lore = stack.getLore();
        if (lore != null) {
            for (String line : lore) {
                if (!line.contains("[Soulbound: ")) continue;
                if (line.matches(SOULBOUND_REGEX.pattern())) {
                    try {
                        return UUID.fromString(line.replaceAll(SOULBOUND_REGEX.pattern(), "$1"));
                    } catch (Exception ignored) {}
                }
            }
        }
        return null;
    }
}
