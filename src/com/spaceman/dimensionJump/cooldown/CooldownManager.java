package com.spaceman.dimensionJump.cooldown;

import com.spaceman.Main;
import com.spaceman.dimensionJump.fileHander.Files;
import com.spaceman.dimensionJump.fileHander.GettingFiles;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import static com.spaceman.dimensionJump.fancyMessage.Message.translateKeyPrefix;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.ColorType.varError2Color;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.ColorType.varErrorColor;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.formatTranslation;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;

public enum CooldownManager {
    
    /*
    * This Cooldown Util uses the following utils:
    * - Pre-made Main template
    * - Main
    * - FileHandler
    * - FancyMessage
    *
    * in your onEnable:
    * CooldownManager.setDefaultValues();
    * CooldownManager.loopCooldown = false; //to reset the loop catcher (for if there are any chances made)
    * */
    
    COOLDOWN1("3000"),
    COOLDOWN2("COOLDOWN1");//todo edit
    
    public static boolean loopCooldown = false;
    private static final HashMap<UUID, HashMap<CooldownManager, Long>> cooldownTime = new HashMap<>();
    
    private final String defaultValue;
    
    CooldownManager(String defValue) {
        this.defaultValue = defValue;
    }
    
    public static void setDefaultValues() {
        Files config = GettingFiles.getFile("config"); //todo set config
        for (CooldownManager cooldown : CooldownManager.values()) {
            if (!config.getConfig().contains("cooldown." + cooldown.name())) {
                cooldown.edit(cooldown.defaultValue);
            }
        }
    }
    
    public static boolean contains(String name) {
        for (CooldownManager cooldownManager : CooldownManager.values()) {
            if (cooldownManager.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public String value() {
        Files config = GettingFiles.getFile("config"); //todo set config
        return config.getConfig().getString("cooldown." + this.name());
    }
    
    public void edit(String value) {
        Files config = GettingFiles.getFile("config"); //todo set config
        config.getConfig().set("cooldown." + this.name(), value);
        config.saveConfig();
    }
    
    public void edit(long value) {
        Files config = GettingFiles.getFile("config"); //todo set config
        config.getConfig().set("cooldown." + this.name(), String.valueOf(value));
        config.saveConfig();
    }
    
    public void update(Player player) {
        if (contains(this.value())) {
            CooldownManager.valueOf(this.value()).update(player);
            return;
        }
        HashMap<CooldownManager, Long> timeMap = cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>());
        timeMap.put(this, System.currentTimeMillis());
        cooldownTime.put(player.getUniqueId(), timeMap);
    }
    
    private long getTime(Player player, CooldownManager start) {
        if (this.name().equals((start == null ? "" : start.name())) || loopCooldown) {
            Main.getInstance().getLogger().log(Level.WARNING, "There is a loop in the cooldown configuration...");
            loopCooldown = true;
            return 0;
        }
        Files config = GettingFiles.getFile("config"); //todo set config
        if (!config.getConfig().contains("cooldown." + this.name())) {
            return 0;
        } else {
            String cooldownValue = this.value();
            if (cooldownValue.equals("permission")) {
                for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                    if (permissionInfo.getPermission().toLowerCase().startsWith((CooldownSubCommand.permissionPrefix + ".cooldown" + this.name() + ".").toLowerCase())) {
                        try {
                            long value = Long.parseLong(permissionInfo.getPermission().replace(CooldownSubCommand.permissionPrefix + ".cooldown" + this.name() + ".", ""));
                            return (cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(this, 0L) + value) - System.currentTimeMillis();
                        } catch (NumberFormatException nfe) {
                            //todo support other Cooldowns in Permissions
                            Main.getInstance().getLogger().log(Level.WARNING, "Permission " + CooldownSubCommand.permissionPrefix + ".cooldown" + this.name() + ".X is not a valid Long value");
                            return 0;
                        }
                    }
                }
                return 0;
            }
            for (CooldownManager cooldownManager : CooldownManager.values()) {
                if (cooldownValue.equalsIgnoreCase(cooldownManager.name())) {
                    if (start == null) {
                        start = this;
                    }
                    return cooldownManager.getTime(player, start);
                }
            }
            try {
                long value = Long.parseLong(this.value());
                return (cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(this, 0L) + value) - System.currentTimeMillis();
            } catch (NumberFormatException nfe) {
                Main.getInstance().getLogger().log(Level.WARNING, "There is an error in the cooldown configuration...");
                return 0;
            }
        }
    }
    
    public long getTime(Player player) {
        return getTime(player, null);
    }
    
    public boolean hasCooled(Player player) {
        return hasCooled(player, true);
    }
    
    public boolean hasCooled(Player player, boolean sendMessage) {
        long cooldownInSeconds = this.getTime(player) / 1000;
        if (cooldownInSeconds > 0) {
            if (sendMessage) {
//                sendErrorTheme(player, "You must wait another %s to use this again",
//                        (cooldownInSeconds) + " second" + ((cooldownInSeconds) == 1 ? "" : "s"));
                
                sendErrorTranslation(player, translateKeyPrefix + ".cooldown.cooldownManager.delayTime", cooldownInSeconds,
                        (cooldownInSeconds == 1 ? formatTranslation(varErrorColor, varError2Color, translateKeyPrefix + ".command.second") :
                                formatTranslation(varErrorColor, varError2Color, translateKeyPrefix + ".command.seconds")));
            }
            return false;
        }
        return true;
    }
}
