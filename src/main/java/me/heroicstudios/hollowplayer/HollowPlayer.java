package me.heroicstudios.hollowplayer;

import me.heroicstudios.hollowplayer.listeners.GeoListener;
import me.heroicstudios.hollowplayer.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class HollowPlayer extends JavaPlugin {

    private NamespacedKey abilityKey;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        abilityKey = new NamespacedKey(this, "unlocked_abilities");
        GeoManager.init(this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GeoListener(this), this);

        HPCommand hpCommand = new HPCommand();
        getCommand("hollowplayer").setExecutor(hpCommand);
        getCommand("hollowplayer").setTabCompleter(hpCommand);
        
        RecipeManager.registerRecipes(this);

        // Permanent Shade Cloak task
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (isUnlocked(player, Ability.SHADE_CLOAK)) {
                    if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, false, false, false));
                    }
                }
            });
        }, 0L, 20L);
    }

    public NamespacedKey getAbilityKey() {
        return abilityKey;
    }

    public boolean isUnlocked(Player player, Ability ability) {
        String unlocked = player.getPersistentDataContainer().get(abilityKey, org.bukkit.persistence.PersistentDataType.STRING);
        return unlocked != null && unlocked.contains(ability.getId());
    }

    public void unlock(Player player, Ability ability) {
        String unlocked = player.getPersistentDataContainer().get(abilityKey, org.bukkit.persistence.PersistentDataType.STRING);
        if (unlocked == null) unlocked = "";
        if (!unlocked.contains(ability.getId())) {
            unlocked += "," + ability.getId();
            player.getPersistentDataContainer().set(abilityKey, org.bukkit.persistence.PersistentDataType.STRING, unlocked);
        }
    }

    public void removeAbility(Player player, Ability ability) {
        String unlocked = player.getPersistentDataContainer().get(abilityKey, org.bukkit.persistence.PersistentDataType.STRING);
        if (unlocked != null && unlocked.contains(ability.getId())) {
            unlocked = unlocked.replace("," + ability.getId(), "").replace(ability.getId(), "");
            player.getPersistentDataContainer().set(abilityKey, org.bukkit.persistence.PersistentDataType.STRING, unlocked);
        }
    }

    public void removeAllAbilities(Player player) {
        player.getPersistentDataContainer().remove(abilityKey);
    }

    private class HPCommand implements CommandExecutor, TabCompleter {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (args.length == 0) {
                sender.sendMessage("§6§lHollowPlayer Commands:");
                sender.sendMessage("§e/hp give <player> <ability> §7- Give an ability item");
                sender.sendMessage("§e/hp give <player> geo <amount> §7- Give physical Geo");
                sender.sendMessage("§e/hp remove <player> <ability|all> §7- Remove abilities");
                sender.sendMessage("§e/hp reload §7- Reload config and recipes");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("hollowplayer.admin")) {
                    sender.sendMessage("§cNo permission.");
                    return true;
                }
                reloadConfig();
                RecipeManager.registerRecipes(HollowPlayer.this);
                sender.sendMessage("§aHollowPlayer config and recipes reloaded!");
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("hollowplayer.admin")) {
                    sender.sendMessage("§cNo permission.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /hp remove <player> <ability|all>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }

                if (args[2].equalsIgnoreCase("all")) {
                    removeAllAbilities(target);
                    sender.sendMessage("§aRemoved all abilities from " + target.getName());
                    return true;
                }

                Ability ability = Ability.fromId(args[2]);
                if (ability == null) {
                    sender.sendMessage("§cAbility not found.");
                    return true;
                }
                removeAbility(target, ability);
                sender.sendMessage("§aRemoved " + ability.getName(HollowPlayer.this) + " from " + target.getName());
                return true;
            }

            if (args[0].equalsIgnoreCase("give")) {
                if (!sender.hasPermission("hollowplayer.admin")) {
                    sender.sendMessage("§cNo permission.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /hp give <player> <ability|geo> [amount]");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }

                if (args[2].equalsIgnoreCase("geo")) {
                    int amount = 1;
                    if (args.length >= 4) {
                        try {
                            amount = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("§cInvalid amount.");
                            return true;
                        }
                    }
                    target.getInventory().addItem(GeoManager.getGeoItem(amount));
                    sender.sendMessage("§aGave " + amount + " Geo to " + target.getName());
                    return true;
                }

                Ability ability = Ability.fromId(args[2]);
                if (ability == null) {
                    sender.sendMessage("§cAbility or 'geo' not found.");
                    return true;
                }
                target.getInventory().addItem(ability.getItem(HollowPlayer.this));
                sender.sendMessage("§aGave " + ability.getName(HollowPlayer.this) + " §ato " + target.getName());
                return true;
            }

            sender.sendMessage("§cUnknown command. Use /hp for help.");
            return true;
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            List<String> completions = new ArrayList<>();
            if (args.length == 1) {
                completions.add("give");
                completions.add("remove");
                completions.add("reload");
            } else if (args.length == 2 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("remove"))) {
                return null; // Bukkit handles player names automatically
            } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                completions.add("geo");
                for (Ability ability : Ability.values()) {
                    completions.add(ability.getId());
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
                completions.add("all");
                for (Ability ability : Ability.values()) {
                    completions.add(ability.getId());
                }
            } else if (args.length == 4 && args[0].equalsIgnoreCase("give") && args[2].equalsIgnoreCase("geo")) {
                completions.add("1");
                completions.add("10");
                completions.add("64");
            }
            
            String lastArg = args[args.length - 1].toLowerCase();
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(lastArg))
                    .collect(Collectors.toList());
        }
    }
}
