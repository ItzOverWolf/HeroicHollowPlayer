package me.heroicstudios.hollowplayer.abilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MonarchWings {

    public static void use(Player player) {
        ConfigurationSection config = player.getServer().getPluginManager().getPlugin("HollowPlayer").getConfig();
        int cost = config.getInt("abilities.monarch_wings.cost", 1);
        if (player.getFoodLevel() < cost) {
            player.sendMessage(config.getString("messages.not-enough-soul", "§cNot enough soul!"));
            return;
        }

        player.setFoodLevel(player.getFoodLevel() - cost);
        player.setFlying(false);
        player.setAllowFlight(false);
        
        // Previous fun momentum jump
        Vector v = player.getLocation().getDirection().setY(0.6).multiply(1.2);
        player.setVelocity(v);
        
        Location loc = player.getLocation();
        player.getWorld().playSound(loc, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.2f);
        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 20, 0.2, 0.1, 0.2, 0.05);
        player.getWorld().spawnParticle(Particle.CLOUD, loc, 10, 0.5, 0.1, 0.5, 0.05);
    }
}
