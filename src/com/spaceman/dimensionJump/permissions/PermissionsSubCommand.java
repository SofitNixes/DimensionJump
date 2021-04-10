package com.spaceman.dimensionJump.permissions;

import com.spaceman.dimensionJump.commandHandler.ArgumentType;
import com.spaceman.dimensionJump.commandHandler.EmptyCommand;
import com.spaceman.dimensionJump.commandHandler.SubCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.spaceman.dimensionJump.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.dimensionJump.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.dimensionJump.fancyMessage.Message.translateKeyPrefix;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.dimensionJump.permissions.PermissionHandler.isPermissionEnabled;

public class PermissionsSubCommand extends SubCommand {
    
    private final String commandName;
    
    public PermissionsSubCommand(String commandName, String permissionPrefix) {
        this.commandName = commandName;
        
        EmptyCommand emptyEnableState = new EmptyCommand();
        emptyEnableState.setCommandName("state", ArgumentType.OPTIONAL);
        emptyEnableState.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".command.permissions.enable.state.commandDescription"));
        emptyEnableState.setPermissions(permissionPrefix + ".permissions.enable", permissionPrefix + ".admin.permissions");
        EmptyCommand emptyEnable = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyEnable.setCommandName("enable", ArgumentType.FIXED);
        emptyEnable.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".command.permissions.enable.commandDescription"));
        emptyEnable.setTabRunnable(((args, player) -> Arrays.asList("true", "false")));
        emptyEnable.setRunnable(((args, player) -> {
            if (args.length == 2) {
                sendInfoTranslation(player, translateKeyPrefix + ".command.permissions.enable.succeeded",
                        (isPermissionEnabled() ? formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color,translateKeyPrefix + ".command.permissions.enabled") :
                                formatTranslation(ColorType.varInfoColor, ColorType.varInfo2Color,translateKeyPrefix + ".command.permissions.disabled")));
            } else if (args.length == 3) {
                if (emptyEnableState.hasPermissionToRun(player, true)) {
                    if (args[2].equalsIgnoreCase("true")) {
                        if (PermissionHandler.enablePermissions(true)) {
                            sendSuccessTranslation(player, translateKeyPrefix + ".command.permissions.enable.state.succeeded",
                                    formatTranslation(ColorType.varSuccessColor, ColorType.varSuccess2Color,translateKeyPrefix + ".command.permissions.enabled"));
                        } else {
                            sendErrorTranslation(player, translateKeyPrefix + ".command.permissions.enable.state.alreadyInState",
                                    formatTranslation(ColorType.varErrorColor, ColorType.varError2Color,translateKeyPrefix + ".command.permissions.enabled"));
                        }
                    } else if (args[2].equalsIgnoreCase("false")) {
                        if (PermissionHandler.enablePermissions(false)) {
                            sendSuccessTranslation(player, translateKeyPrefix + ".command.permissions.enable.state.succeeded",
                                    formatTranslation(ColorType.varSuccessColor, ColorType.varSuccess2Color,translateKeyPrefix + ".command.permissions.disabled"));
                        } else {
                            sendErrorTranslation(player, translateKeyPrefix + ".command.permissions.enable.state.alreadyInState",
                                    formatTranslation(ColorType.varErrorColor, ColorType.varError2Color,translateKeyPrefix + ".command.permissions.disabled"));
                        }
                    } else {
                        sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage", "/" + commandName + " permissions enable [true|false]");
                    }
                }
            } else {
                sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage", "/" + commandName + " permissions enable [state]");
            }
        }));
        emptyEnable.addAction(emptyEnableState);
        
        addAction(emptyEnable);
    }
    
    @Override
    public void run(String[] args, Player player) {
        //command permissions enable [state]
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage", "/" + commandName + " permissions " + convertToArgs(getActions(), false));
        
    }
}
