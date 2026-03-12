package me.heroicstudios.hollowplayer.listeners;

import me.heroicstudios.hollowplayer.GeoManager;
import me.heroicstudios.hollowplayer.HollowPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class GeoListener implements Listener {

    private final HollowPlayer plugin;
    private final Random random = new Random();

    public GeoListener(HollowPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!plugin.getConfig().getBoolean("geo.enabled")) return;
        
        Location loc = event.getEntity().getLocation();
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        // Player death drop (not handling physical Geo in inventory for now, just bonus drop)
        if (event.getEntity() instanceof Player victim) {
            // Drop some bonus geo at player death location
            event.getDrops().add(GeoManager.getGeoItem(random.nextInt(10) + 5));
            return;
        }

        // Mob drops
        double chance = plugin.getConfig().getDouble("geo.drop-chance", 0.5);
        if (random.nextDouble() > chance) return;

        EntityType type = event.getEntityType();
        int min = plugin.getConfig().getInt("geo.min-drop", 1);
        int max = plugin.getConfig().getInt("geo.max-drop", 5);

        ConfigurationSection overrides = plugin.getConfig().getConfigurationSection("geo.mob-overrides");
        if (overrides != null && overrides.contains(type.name())) {
            min = overrides.getInt(type.name() + ".min");
            max = overrides.getInt(type.name() + ".max");
        }

        int amount = random.nextInt(Math.max(1, max - min + 1)) + min;
        event.getDrops().add(GeoManager.getGeoItem(amount));
        killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.5f);
    }
}
