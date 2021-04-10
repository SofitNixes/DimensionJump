package com.spaceman.dimensionJump.playerUUID;

import com.spaceman.dimensionJump.Main;
import com.spaceman.dimensionJump.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerUUID {
    
    /*
     * This PlayerUUID Util uses the following utils:
     * - ColorFormatter or ColorTheme
     * */
    
    public static String getPlayerName(String uuid) {
        return getPlayerName(UUID.fromString(uuid));
    }
    
    public static String getPlayerName(UUID uuid) {
        if (uuid == null) return null;
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            return !offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline() ? null : offlinePlayer.getName();
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
    
    // returns null if not found
    public static UUID getPlayerUUID(String playerName) {
        return Main.getOrDefault(getProfile(playerName), new Pair<String, UUID>(null, null)).getRight();
    }
    
    // returns null values if not found
    public static Pair<String, UUID> getProfile(String playerName) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if ((op.hasPlayedBefore() || op.isOnline()) && op.getName() != null && op.getName().equalsIgnoreCase(playerName)) {
                return new Pair<>(op.getName(), op.getUniqueId());
            }
        }
        return new Pair<>(null, null);
    }
}
