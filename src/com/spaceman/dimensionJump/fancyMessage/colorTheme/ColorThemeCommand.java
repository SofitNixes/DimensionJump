package com.spaceman.dimensionJump.fancyMessage.colorTheme;

import com.spaceman.dimensionJump.commandHandler.ArgumentType;
import com.spaceman.dimensionJump.commandHandler.EmptyCommand;
import com.spaceman.dimensionJump.commandHandler.SubCommand;
import com.spaceman.dimensionJump.fancyMessage.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spaceman.dimensionJump.commandHandler.CommandTemplate.convertToArgs;
import static com.spaceman.dimensionJump.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.dimensionJump.fancyMessage.Message.translateKeyPrefix;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.*;

public class ColorThemeCommand extends SubCommand {
    
    private final String commandName;
    
    public ColorThemeCommand(String commandName) {
        this.commandName = commandName;
        
        EmptyCommand empty = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return "";
            }
        };
        empty.setCommandName("");
        empty.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".colorTheme.commandDescription"));
        
        EmptyCommand emptySetTypeChat = new EmptyCommand();
        emptySetTypeChat.setCommandName("chat color", ArgumentType.REQUIRED);
        emptySetTypeChat.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".colorTheme.set.type.chat.commandDescription"));
        EmptyCommand emptySetTypeHex = new EmptyCommand();
        emptySetTypeHex.setCommandName("hex color", ArgumentType.REQUIRED);
        emptySetTypeHex.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".colorTheme.set.type.hex.commandDescription"));
        EmptyCommand emptySetType = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                if (Arrays.stream(ColorType.values()).anyMatch(type -> type.name().equalsIgnoreCase(argument)))
                    return argument;
                return "";
            }
        };
        emptySetType.setCommandName("type", ArgumentType.REQUIRED);
        emptySetType.setTabRunnable(((args, player) -> {
            if (args[3].startsWith("#")) {
                return args[3].length() < 8 ? Collections.singletonList(args[3] + "#ffffff".substring(args[3].length(), 7)) : Collections.singletonList(args[3].substring(0, 7));
            } else {
                List<String> list = Arrays.stream(ChatColor.values()).map(Enum::name).collect(Collectors.toList());
                list.add("#ffffff");
                list.add("#000000");
                list.add("#");
                return list;
            }
        }));
        emptySetType.setRunnable(((args, player) -> {
            if (args.length == 4) {
                if (ColorType.getTypes().contains(args[2])) {
                    if (Arrays.stream(ChatColor.values()).map(ChatColor::name).anyMatch(c -> c.equalsIgnoreCase(args[3]))) { //command colorTheme set <type> <chat color>
                        ColorType.valueOf(args[2]).setColor(player, new MultiColor(ChatColor.valueOf(args[3].toUpperCase())));
                        
                        Message message = formatTranslation(ColorType.valueOf(args[2]), ColorType.varInfo2Color, translateKeyPrefix + ".colorTheme.this");
                        message.getText().forEach(t -> t.setInsertion(ColorType.valueOf(args[2]).getColor(player).getColorAsValue()));
                        sendSuccessTranslation(player, translateKeyPrefix + ".colorTheme.set.type.chat.succeeded", ColorType.valueOf(args[2]).name(), message);
                    } else if (args[3].matches("#[0-9a-fA-F]{6}")) {//command colorTheme set <type> <hex color>
                        ColorType.valueOf(args[2]).setColor(player, new MultiColor(args[3]));
                        
                        Message message = formatTranslation(ColorType.valueOf(args[2]), ColorType.varInfo2Color, translateKeyPrefix + ".colorTheme.this");
                        message.getText().forEach(t -> t.setInsertion(ColorType.valueOf(args[2]).getColor(player).getColorAsValue()));
                        sendSuccessTranslation(player, translateKeyPrefix + ".colorTheme.set.type.hex.succeeded", ColorType.valueOf(args[2]).name(), message);
                    } else {
                        sendErrorTranslation(player, translateKeyPrefix + ".colorTheme.set.type.colorNotFound", args[3]);
                    }
                } else {
                    sendErrorTranslation(player, translateKeyPrefix + ".colorTheme.set.type.colorTypeNotFound", args[2]);
                }
            } else {
                sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage", "/" + commandName + " colorTheme set <type> <chat color|hex color>");
            }
        }));
        emptySetType.addAction(emptySetTypeChat);
        emptySetType.addAction(emptySetTypeHex);
        EmptyCommand emptySetTheme = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                if (ColorTheme.getDefaultThemes().contains(argument)) return argument;
                return "";
            }
        };
        emptySetTheme.setCommandName("theme", ArgumentType.REQUIRED);
        emptySetTheme.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".colorTheme.set.theme.commandDescription"));
        emptySetTheme.setRunnable(((args, player) -> {
            if (args.length == 3) {
                ColorTheme.setDefaultTheme(player, args[2]);
                sendSuccessTranslation(player, translateKeyPrefix + ".colorTheme.set.theme.succeeded", args[2]);
            } else {
                sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage", "/" + commandName + " colorTheme set <theme>");
            }
        }));
        EmptyCommand emptySet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptySet.setCommandName("set", ArgumentType.FIXED);
        emptySet.setTabRunnable((args, player) -> Stream.concat(ColorTheme.getDefaultThemes().stream(), ColorType.getTypes().stream()).collect(Collectors.toList()));
        emptySet.setRunnable(((args, player) -> {
            if (args.length > 2 && runCommands(emptySet.getActions(), args[2], args, player)) {
                return;
            }
            sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage", "/" + commandName + " colorTheme set " + convertToArgs(emptySet.getActions(), false));
        }));
        emptySet.addAction(emptySetTheme);
        emptySet.addAction(emptySetType);
        
        EmptyCommand emptyGetType = new EmptyCommand();
        emptyGetType.setCommandName("type", ArgumentType.REQUIRED);
        emptyGetType.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".colorTheme.get.commandDescription"));
        EmptyCommand emptyGet = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return getCommandName();
            }
        };
        emptyGet.setCommandName("get", ArgumentType.FIXED);
        emptyGet.setTabRunnable((args, player) -> Arrays.stream(ColorTheme.ColorType.values()).map(Enum::name).collect(Collectors.toList()));
        emptyGet.setRunnable(((args, player) -> {
            if (args.length == 3) {
                if (ColorType.getTypes().contains(args[2])) {
                    Message message = formatTranslation(ColorType.valueOf(args[2]), ColorType.varInfo2Color, translateKeyPrefix + ".colorTheme.this");
                    message.getText().forEach(t -> t.setInsertion(ColorType.valueOf(args[2]).getColor(player).getColorAsValue()));
                    sendInfoTranslation(player, translateKeyPrefix + ".colorTheme.get.succeeded", ColorType.valueOf(args[2]).name(), message);
                } else {
                    sendErrorTheme(player, translateKeyPrefix + ".colorTheme.get.colorNotFound", args[2]);
                }
            } else {
                sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage", "/" + commandName + " colorTheme get <type>");
            }
        }));
        emptyGet.addAction(emptyGetType);
        
        addAction(empty);
        addAction(emptySet);
        addAction(emptyGet);
    }
    
    @Override
    public String getName(String arg) {
        return "colorTheme";
    }
    
    @Override
    public void run(String[] args, Player player) {
        //command colorTheme
        //command colorTheme set <theme>
        //command colorTheme set <type> <chat color>
        //command colorTheme set <type> <hex color>
        //command colorTheme get <type>
        
        if (args.length == 1) {
            sendInfoTranslation(player, translateKeyPrefix + ".colorTheme.succeeded", "info");
            sendSuccessTranslation(player, translateKeyPrefix + ".colorTheme.succeeded", "success");
            sendErrorTranslation(player, translateKeyPrefix + ".colorTheme.succeeded", "error");
        } else {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
            sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage", "/" + commandName + " colorTheme " + convertToArgs(getActions(), true));
        }
    }
}
