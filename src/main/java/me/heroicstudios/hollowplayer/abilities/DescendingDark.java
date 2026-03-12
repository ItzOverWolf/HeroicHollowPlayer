package me.heroicstudios.hollowplayer.abilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Collection;

public class DescendingDark {

    private static final String METADATA_KEY = "hollowplayer_descending";

    public static void use(Player player, Plugin plugin) {
        ConfigurationSection config = plugin.getConfig();
        int cost = config.getInt("abilities.descending_dark.cost", 8);
        if (player.getFoodLevel() < cost) {
            player.sendMessage(config.getString("messages.not-enough-soul", "§cNot enough soul!"));
            return;
        }

        player.setFoodLevel(player.getFoodLevel() - cost);
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));
        
        // Force player down
        player.setVelocity(new Vector(0, -3.0, 0));
        
        try {
            player.getWorld().playSound(player.getLocation(), Sound.valueOf("ENTITY_WARDEN_DIG"), 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
        }
        player.getWorld().spawnParticle(Particle.SQUID_INK, player.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
    }

    public static void land(Player player, Plugin plugin) {
        if (!player.hasMetadata(METADATA_KEY)) return;
        player.removeMetadata(METADATA_KEY, plugin);

        Location loc = player.getLocation();
        player.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
        try {
            player.getWorld().playSound(loc, Sound.valueOf("ENTITY_WARDEN_SONIC_BOOM"), 1.0f, 0.8f);
        } catch (IllegalArgumentException e) {
            player.getWorld().playSound(loc, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 0.8f);
        }
        
        // Visuals
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
        try {
            player.getWorld().spawnParticle(Particle.valueOf("SONIC_BOOM"), loc, 3, 1.0, 0.1, 1.0);
        } catch (IllegalArgumentException e) {
            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 3, 1.0, 0.1, 1.0);
        }
        player.getWorld().spawnParticle(Particle.SQUID_INK, loc, 50, 2.0, 0.5, 2.0, 0.1);

        // Damage nearby
        double damage = plugin.getConfig().getDouble("abilities.descending_dark.damage", 30.0);
        Collection<Entity> entities = player.getWorld().getNearbyEntities(loc, 5, 3, 5);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && entity != player) {
                ((LivingEntity) entity).damage(damage, player);
                entity.setVelocity(entity.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.5).setY(0.5));
            }
        }
    }

    public static boolean isDescending(Player player) {
        return player.hasMetadata(METADATA_KEY);
    }
}
