package com.spaceman.dimensionJump.cooldown;

import com.spaceman.dimensionJump.commandHandler.ArgumentType;
import com.spaceman.dimensionJump.commandHandler.EmptyCommand;
import com.spaceman.dimensionJump.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.spaceman.dimensionJump.fancyMessage.Message.translateKeyPrefix;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.*;

public class CooldownSubCommand extends SubCommand {
    
    /*
     * This pre-made sub-command uses the following utils:
     * - Pre-made Main template
     * - CommandHandler
     * - FancyMessage
     * */
    
    private final String commandName;
    static String permissionPrefix;
    
    private final EmptyCommand emptyCooldownValue;
    
    public CooldownSubCommand(String commandName, String permissionPrefix) {
        this.commandName = commandName;
        CooldownSubCommand.permissionPrefix = permissionPrefix;
        
        emptyCooldownValue = new EmptyCommand();
        emptyCooldownValue.setCommandName("value", ArgumentType.OPTIONAL);
        emptyCooldownValue.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".cooldown.cooldownCommand.value.commandDescription"));
        emptyCooldownValue.setPermissions(permissionPrefix + ".cooldown.set", permissionPrefix + ".admin.cooldown");
        
        EmptyCommand emptyCooldown = new EmptyCommand();
        emptyCooldown.setCommandName("cooldown", ArgumentType.REQUIRED);
        emptyCooldown.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".cooldown.cooldownCommand.commandDescription"));
        emptyCooldown.setTabRunnable((args, player) -> {
            if (!emptyCooldownValue.hasPermissionToRun(player, false)) return new ArrayList<>();
            
            ArrayList<String> originalList = new ArrayList<>();
            Arrays.stream(CooldownManager.values()).map(CooldownManager::name).forEach(originalList::add);
            if (originalList.contains(args[1])) {
                ArrayList<String> list = new ArrayList<>(originalList);
                list.add("permission");
                list.remove(args[1]);
                return list;
            }
            
            return new ArrayList<>();
        });
        emptyCooldown.addAction(emptyCooldownValue);
        addAction(emptyCooldown);
    }
    
    @Override
    public String getName(String arg) {
        return "cooldown";
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        Arrays.stream(CooldownManager.values()).map(CooldownManager::name).forEach(list::add);
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        //command cooldown <cooldown> [value]
        
        if (args.length == 2) {
            if (CooldownManager.contains(args[1])) {
                String cooldown = CooldownManager.valueOf(args[1]).value();
                sendErrorTranslation(player, translateKeyPrefix + ".cooldown.cooldownCommand.get.value", args[1], cooldown);
            } else {
                sendErrorTranslation(player, translateKeyPrefix + ".cooldown.cooldownCommand.get.error", args[1]);
            }
            
        } else if (args.length == 3) {
            if (!emptyCooldownValue.hasPermissionToRun(player, true)) {
                return;
            }
            if (CooldownManager.contains(args[1])) {
                try {
                    Long.parseLong(args[2]);
                } catch (NumberFormatException nfe) {
                    if (!args[2].equalsIgnoreCase("permission")) {
                        if (!CooldownManager.contains(args[2])) {
                            sendErrorTranslation(player, translateKeyPrefix + ".cooldown.cooldownCommand.set.invalidEntry", args[2]);
                            return;
                        } else if (args[1].equalsIgnoreCase(args[2])) {
                            sendErrorTranslation(player, translateKeyPrefix + ".cooldown.cooldownCommand.set.setToSelf");
                            return;
                        }
                    } else {
                        sendInfoTranslation(player, translateKeyPrefix + ".cooldown.cooldownCommand.set.permissionInfo",
                                /*todo set command*/commandName + ".cooldown.<cooldown>.<X>");
                    }
                }
                
                CooldownManager.valueOf(args[1]).edit(args[2]);
                sendSuccessTheme(player, translateKeyPrefix + ".cooldown.cooldownCommand.set.succeeded", args[1], args[2]);
            } else {
                sendErrorTranslation(player, translateKeyPrefix + ".cooldown.cooldownCommand.set.error", args[1]);
            }
        } else {
            sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage",
                    /*todo set command*/"/" + commandName + " <cooldown> [value]");
        }
        
    }
}
