package me.heroicstudios.hollowplayer.abilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class AbyssShriek {

    public static void use(Player player) {
        ConfigurationSection config = player.getServer().getPluginManager().getPlugin("HollowPlayer").getConfig();
        int cost = config.getInt("abilities.abyss_shriek.cost", 6);
        if (player.getFoodLevel() < cost) {
            player.sendMessage(config.getString("messages.not-enough-soul", "§cNot enough soul!"));
            return;
        }

        player.setFoodLevel(player.getFoodLevel() - cost);
        Location loc = player.getLocation();
        try {
            Sound roar = Sound.valueOf("ENTITY_WARDEN_ROAR");
            Sound boom = Sound.valueOf("ENTITY_WARDEN_SONIC_BOOM");
            player.getWorld().playSound(loc, roar, 1.0f, 1.0f);
            player.getWorld().playSound(loc, boom, 1.0f, 0.5f);
        } catch (IllegalArgumentException e) {
            player.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
        }

        // Visual effect: Multiple sonic booms going up
        for (int i = 0; i < 5; i++) {
            Location effectLoc = loc.clone().add(0, i * 1.5, 0);
            try {
                Particle particle = Particle.valueOf("SONIC_BOOM");
                player.getWorld().spawnParticle(particle, effectLoc, 1);
            } catch (IllegalArgumentException e) {
                player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, effectLoc, 1);
            }
        }

        // Damage entities above
        double damage = player.getServer().getPluginManager().getPlugin("HollowPlayer").getConfig().getDouble("damage.abyss_shriek", 24.0);
        Collection<Entity> entities = player.getWorld().getNearbyEntities(loc.clone().add(0, 4, 0), 4, 6, 4);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && entity != player) {
                ((LivingEntity) entity).damage(damage, player);
                entity.setVelocity(new Vector(0, 1.0, 0));
            }
        }
    }
}
