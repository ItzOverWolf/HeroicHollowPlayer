package me.heroicstudios.hollowplayer.listeners;

import me.heroicstudios.hollowplayer.Ability;
import me.heroicstudios.hollowplayer.HollowPlayer;
import me.heroicstudios.hollowplayer.RecipeManager;
import me.heroicstudios.hollowplayer.abilities.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PlayerListener implements Listener {

    private final HollowPlayer plugin;
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();

    public PlayerListener(HollowPlayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RecipeManager.discoverRecipes(player);
        
        // Initial application of Shade Cloak if unlocked
        if (plugin.isUnlocked(player, Ability.SHADE_CLOAK)) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 40, 1, false, false, false));
        }
    }

    private boolean isWorldDisabled(Player player) {
        String worldName = player.getWorld().getName();
        List<String> whitelist = plugin.getConfig().getStringList("whitelisted-worlds");
        List<String> blacklist = plugin.getConfig().getStringList("blacklisted-worlds");

        if (!whitelist.isEmpty() && !whitelist.contains(worldName)) return true;
        return blacklist.contains(worldName);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isWorldDisabled(player)) return;

        ItemStack item = event.getItem();
        if (item != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(plugin.getAbilityKey(), PersistentDataType.STRING)) {
                String id = item.getItemMeta().getPersistentDataContainer().get(plugin.getAbilityKey(), PersistentDataType.STRING);
                Ability ability = Ability.fromId(id);
                if (ability != null) {
                    event.setCancelled(true);
                    if (plugin.isUnlocked(player, ability)) {
                        player.sendMessage(plugin.getConfig().getString("messages.already-unlocked", "§cAlready unlocked!"));
                        return;
                    }
                    unlockAbility(player, ability);
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }

        // Mace Pogo reset
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            Material mace = Material.matchMaterial("MACE");
            if (mace != null && mainHand.getType() == mace) {
                if (plugin.isUnlocked(player, Ability.MONARCH_WINGS)) {
                    player.setAllowFlight(true);
                }
            }
        }
    }

    private Color parseColor(String name) {
        if (name == null) return Color.WHITE;
        switch (name.toUpperCase()) {
            case "AQUA": return Color.AQUA;
            case "BLACK": return Color.BLACK;
            case "BLUE": return Color.BLUE;
            case "FUCHSIA": return Color.FUCHSIA;
            case "GRAY": return Color.GRAY;
            case "GREEN": return Color.GREEN;
            case "LIME": return Color.LIME;
            case "MAROON": return Color.MAROON;
            case "NAVY": return Color.NAVY;
            case "OLIVE": return Color.OLIVE;
            case "ORANGE": return Color.ORANGE;
            case "PURPLE": return Color.PURPLE;
            case "RED": return Color.RED;
            case "SILVER": return Color.SILVER;
            case "TEAL": return Color.TEAL;
            case "YELLOW": return Color.YELLOW;
            default: return Color.WHITE;
        }
    }

    private void unlockAbility(Player player, Ability ability) {
        plugin.unlock(player, ability);

        // Firework
        ConfigurationSection fwSection = plugin.getConfig().getConfigurationSection("unlock-firework");
        if (fwSection != null && fwSection.getBoolean("enabled", true)) {
            Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
            FireworkMeta meta = fw.getFireworkMeta();
            try {
                Color color = parseColor(fwSection.getString("color", "AQUA"));
                Color fade = parseColor(fwSection.getString("fade", "WHITE"));
                FireworkEffect.Type type = FireworkEffect.Type.valueOf(fwSection.getString("type", "BALL_LARGE"));
                meta.addEffect(FireworkEffect.builder().withColor(color).withFade(fade).with(type).build());
                meta.setPower(fwSection.getInt("power", 1));
                fw.setFireworkMeta(meta);
            } catch (Exception ignored) {}
        }

        String abilityName = ability.getName(plugin);
        String desc = ability.getDescription(plugin);

        // Title
        String mainTitle = plugin.getConfig().getString("titles.unlock-main", "§6§lABILITY UNLOCKED");
        String subTitle = plugin.getConfig().getString("titles.unlock-sub", "§b{ability}").replace("{ability}", abilityName);
        player.showTitle(Title.title(legacy.deserialize(mainTitle), legacy.deserialize(subTitle)));

        // Chat
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        player.sendMessage(legacy.deserialize(prefix + plugin.getConfig().getString("messages.unlock-chat", "").replace("{ability}", abilityName)));
        player.sendMessage(legacy.deserialize(plugin.getConfig().getString("messages.unlock-description", "").replace("{description}", desc)));

        // Announcement
        if (plugin.getConfig().getBoolean("announcements.enabled") && plugin.getConfig().getBoolean("announcements.abilities." + ability.getId())) {
            String ann = plugin.getConfig().getString("messages.announcement", "").replace("{player}", player.getName()).replace("{ability}", abilityName);
            Bukkit.broadcast(legacy.deserialize(ann));
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (isWorldDisabled(player)) return;

        float pitch = player.getLocation().getPitch();

        if (pitch < -70 && plugin.isUnlocked(player, Ability.ABYSS_SHRIEK)) {
            AbyssShriek.use(player);
        } else if (pitch > 70 && plugin.isUnlocked(player, Ability.DESCENDING_DARK)) {
            DescendingDark.use(player, plugin);
        } else if (plugin.isUnlocked(player, Ability.SHADE_SOUL)) {
            ShadeSoul.use(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (isWorldDisabled(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (DescendingDark.isDescending(player)) {
                event.setCancelled(true);
                DescendingDark.land(player, plugin);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isWorldDisabled(player)) return;
        
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            if (player.isOnGround() && plugin.isUnlocked(player, Ability.MONARCH_WINGS)) {
                player.setAllowFlight(true);
            }
        }

        if (DescendingDark.isDescending(player)) {
            if (player.isOnGround() || player.getLocation().getBlock().getType() == Material.WATER) {
                DescendingDark.land(player, plugin);
            }
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (isWorldDisabled(player)) return;

        if (plugin.isUnlocked(player, Ability.MONARCH_WINGS)) {
            event.setCancelled(true);
            MonarchWings.use(player);
        }
    }
}
