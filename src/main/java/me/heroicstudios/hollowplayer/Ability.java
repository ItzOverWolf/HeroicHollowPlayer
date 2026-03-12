package me.heroicstudios.hollowplayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public enum Ability {
    ABYSS_SHRIEK("abyss_shriek"),
    SHADE_SOUL("shade_soul"),
    DESCENDING_DARK("descending_dark"),
    MONARCH_WINGS("monarch_wings"),
    SHADE_CLOAK("shade_cloak");

    private final String id;

    Ability(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public String getName(HollowPlayer plugin) {
        return plugin.getConfig().getString("abilities." + id + ".name", id);
    }

    public String getDescription(HollowPlayer plugin) {
        return plugin.getConfig().getString("abilities." + id + ".description", "");
    }

    public Material getMaterial(HollowPlayer plugin) {
        String matName = plugin.getConfig().getString("abilities." + id + ".material");
        Material mat = matName != null ? Material.matchMaterial(matName) : null;
        return mat != null ? mat : Material.PAPER;
    }

    public ItemStack getItem(HollowPlayer plugin) {
        ItemStack item = new ItemStack(getMaterial(plugin));
        ItemMeta meta = item.getItemMeta();
        
        String name = getName(plugin);
        meta.displayName(LegacyComponentSerializer.legacySection().deserialize("§6Ability: §b" + name));
        
        List<Component> lore = new ArrayList<>();
        lore.add(LegacyComponentSerializer.legacySection().deserialize("§7" + getDescription(plugin)));
        lore.add(Component.text(""));
        lore.add(LegacyComponentSerializer.legacySection().deserialize("§eRight-Click to unlock!"));
        meta.lore(lore);
        
        meta.getPersistentDataContainer().set(plugin.getAbilityKey(), PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }

    public static Ability fromId(String id) {
        for (Ability a : values()) {
            if (a.id.equalsIgnoreCase(id)) return a;
        }
        return null;
    }
}
