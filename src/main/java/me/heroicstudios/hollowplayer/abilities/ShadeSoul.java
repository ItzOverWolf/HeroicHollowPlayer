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

public class ShadeSoul {

    public static void use(Player player) {
        ConfigurationSection config = player.getServer().getPluginManager().getPlugin("HollowPlayer").getConfig();
        int cost = config.getInt("abilities.shade_soul.cost", 4);
        if (player.getFoodLevel() < cost) {
            player.sendMessage(config.getString("messages.not-enough-soul", "§cNot enough soul!"));
            return;
        }

        player.setFoodLevel(player.getFoodLevel() - cost);
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection();

        try {
            player.getWorld().playSound(loc, Sound.valueOf("ENTITY_WARDEN_SONIC_BOOM"), 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            player.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
        }

        // Beam effect
        for (int i = 1; i <= 8; i++) {
            Location beamLoc = loc.clone().add(direction.clone().multiply(i));
            try {
                player.getWorld().spawnParticle(Particle.valueOf("SONIC_BOOM"), beamLoc, 1);
            } catch (IllegalArgumentException e) {
                player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, beamLoc, 1);
            }
            
            // Small explosion effect at each step
            player.getWorld().spawnParticle(Particle.SQUID_INK, beamLoc, 5, 0.2, 0.2, 0.2, 0.05);

            // Damage entities in the beam
            double damage = config.getDouble("abilities.shade_soul.damage", 16.0);
            Collection<Entity> entities = player.getWorld().getNearbyEntities(beamLoc, 1.5, 1.5, 1.5);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity && entity != player) {
                    ((LivingEntity) entity).damage(damage, player);
                    entity.setVelocity(direction.clone().multiply(0.5).add(new Vector(0, 0.2, 0)));
                }
            }
        }
    }
}
