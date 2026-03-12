package me.heroicstudios.hollowplayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GeoManager {

    private static NamespacedKey geoKey;

    public static void init(HollowPlayer plugin) {
        geoKey = new NamespacedKey(plugin, "is_geo");
    }

    public static ItemStack getGeoItem(int amount) {
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS, amount);
        ItemMeta meta = item.getItemMeta();
        
        meta.displayName(Component.text("Geo", NamedTextColor.AQUA).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("A small, glimmering crystal used as", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("currency in Hallownest.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        // Enchanted glow without lore
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Tag the item so we know it's a Geo item
        meta.getPersistentDataContainer().set(geoKey, PersistentDataType.BYTE, (byte) 1);
        
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isGeo(ItemStack item) {
        if (item == null || item.getType() != Material.PRISMARINE_CRYSTALS || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(geoKey, PersistentDataType.BYTE);
    }
}
