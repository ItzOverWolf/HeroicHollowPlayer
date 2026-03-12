# HollowPlayer - Hollow Knight for Minecraft

A highly configurable PaperMC plugin (**1.18 - 1.21.1+**) that brings the magical abilities and mechanics of Hollow Knight into Minecraft.

## ✨ Features

### 🔥 Abilities
*   **Abyss Shriek**: `Shift + Look Up`. Unleashes souls upwards to damage and knock back enemies.
*   **Shade Soul**: `Shift + Look Forward`. Fires a powerful shadow projectile.
*   **Descending Dark**: `Shift + Look Down`. Dives rapidly, granting fall damage immunity and creating a massive explosion on landing.
*   **Monarch Wings**: `Double Jump`. Perform a second jump mid-air with fun momentum-based physics.
*   **Shade Cloak**: `Passive`. Grants a permanent Speed II effect.

### 💰 Geo System
*   **Physical Currency**: Geo is a physical item (**Prismarine Crystal**) with a custom name, enchanted glow, and lore.
*   **Earn Geo**: Mobs drop Geo directly on death.
*   **Mob Overrides**: Specific mobs (Zombies, Endermen, Withers, Dragons, etc.) drop configurable amounts of Geo.
*   **Crafting**: Use Geo items as ingredients in custom crafting recipes to create Ability Unlock items.

### 🛠️ Crafting & Unlocking
*   **Dynamic Recipes**: All ability items are craftable. You can change the shape and every ingredient in the `config.yml`.
*   **Permanent Unlocks**: Right-click a crafted Ability Item to unlock it forever.
*   **Unlock Effects**: Features customizable gradient titles, fireworks, chat messages, and server-wide announcements.

### 🌍 World Management & Compatibility
*   **Cross-Version Support**: Works flawlessly from Minecraft 1.18 up to the latest 1.21.x versions.
*   **World Control**: Whitelist or Blacklist specific worlds where abilities and recipes should be disabled.

## 📜 Commands
The plugin features full **Tab Completion** for all commands.
*   `/hp` - View the help menu.
*   `/hp give <player> <ability>` - Give a specific ability unlock item.
*   `/hp give <player> geo <amount>` - Give a specific amount of physical Geo items.
*   `/hp reload` - Reload the configuration and re-register all crafting recipes instantly.

## ⚙️ Configuration (`config.yml`)
Almost everything is adjustable:
*   **Abilities**: Change names, descriptions, materials, hunger costs, and damage values.
*   **Messages**: Customize every chat message, prefix, and announcement.
*   **Titles**: Edit the main and sub-titles shown when unlocking an ability.
*   **Geo**: Adjust drop chances and amounts for any mob type.
*   **Fireworks**: Change the color, fade, type, and power of the unlock firework.
*   **Recipes**: Full control over the 3x3 crafting grid for every ability.

## 🔑 Permissions
*   `hollowplayer.use` (Default: true) - Access to use unlocked abilities and basic commands.
*   `hollowplayer.admin` (Default: op) - Access to `/hp give` and `/hp reload`.
