package com.spaceman.dimensionJump.commandHandler;

import com.spaceman.dimensionJump.commandHandler.customRunnables.RunRunnable;
import com.spaceman.dimensionJump.fancyMessage.Message;
import com.spaceman.dimensionJump.fancyMessage.TextComponent;
import com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.dimensionJump.fancyMessage.events.ClickEvent;
import com.spaceman.dimensionJump.fancyMessage.events.HoverEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spaceman.dimensionJump.fancyMessage.Message.translateKeyPrefix;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.ColorType.*;
import static com.spaceman.dimensionJump.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.dimensionJump.fancyMessage.TextComponent.textComponent;

public class HelpCommand extends SubCommand {
    
    /*
    * This help sub command is created with ColorTheme and ColorFormatter,
    * to use ColorTheme import: 'import static com.spaceman.colorFormatter.ColorTheme.ColorType.*;'
    * to use ColorFormatter import: 'import static com.spaceman.colorFormatter.ColorFormatter.*;'
    *
    * as default its set to ColorTheme
    *
    * Example on how to use:
    *
    * in your CommandTemplate you can override the registerActions method
    * put there:
    *
    * HelpCommand helpCommand = new HelpCommand(this);
    * addAction(helpCommand);
    *
    * This is a simple help command. It automatic collects all the commands that are in the CommandTemplate.
    * You have to be sure that ALL your sub commands have the commandName and commandDescription set.
    *
    * If you want more help pages (for extra explanation):
    * helpCommand.addExtraHelp("ExtraHelpName", new Message(textComponent("your extra help info to be send on execution"))); //or
    * helpCommand.addExtraHelp("ExtraHelpName", (args, player) -> {//your code to be executed})); //or
    * EmptyCommand extraHelpCommand = new EmptyCommand(){
    *    @Override
    *    public String getName(String arg) {
    *        return "ExtraHelpName";
    *    }
    *};
    * extraHelpCommand.setRunnable((args, player) -> {//your code to be executed});
    * helpCommand.addExtraHelp("ExtraHelpName", extraHelpCommand);
    *
    * */
    
    private int listSize = 10;
    private final CommandTemplate template;
    private final Message commandMessage;
    private final List<String> extraHelp = new ArrayList<>();
    
    public HelpCommand(CommandTemplate template) {
        this(template, formatInfoTranslation(translateKeyPrefix + ".command.help.defaultDescription"));
        template.getPlugin().getLogger().info("No help command description given, using default one for " + template.getName());
    }
    
    public HelpCommand(CommandTemplate template, Message commandMessage) {
        this.template = template;
        this.commandMessage = commandMessage;
        
        EmptyCommand commandList = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                try {
                    Integer.parseInt(argument);
                    return argument;
                } catch (NumberFormatException nfe) {
                    return "";
                }
            }
        };
        commandList.setCommandName("page", ArgumentType.REQUIRED);
        commandList.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".command.help.page.commandDescription"));
        commandList.setRunnable((args, player) -> {
            
            int startIndex;
            try {
                startIndex = (Integer.parseInt(args[1]) - 1) * listSize;
            } catch (NumberFormatException nfe) {
                sendErrorTranslation(player, translateKeyPrefix + ".command.help.page.notANumber", args[1]);
                return;
            }
            
            HashMap<String, SubCommand> commandMap = template.collectActions();
            List<String> commandArrayList = new ArrayList<>(commandMap.keySet());
            
            if (startIndex > commandMap.size()) {
                startIndex = (commandMap.size() / listSize) * listSize;
            }
            if (startIndex < 0) {
                startIndex = 0;
            }
            
            ColorTheme theme = ColorTheme.getTheme(player);
            
            int page = startIndex / listSize + 1;
            
            Message buttons = new Message();
            if (commandArrayList.size() > listSize) {
                buttons.addText(textComponent(" (", theme.getInfoColor()));
                if (page != 1) {
                    HoverEvent backwardHover = new HoverEvent(textComponent("/" + template.getName() + " help " + (page - 1), theme.getInfoColor()));
                    ClickEvent backwardClick = ClickEvent.runCommand("/" + template.getName() + " help " + (page - 1));
                    buttons.addText(textComponent("<-", theme.getVarInfoColor(), backwardHover, backwardClick));
                }
                if (page < commandArrayList.size() / listSize) {
                    if (page != 1) buttons.addText(" ");
                    
                    HoverEvent forwardHover = new HoverEvent(textComponent("/" + template.getName() + " help " + (page + 1), theme.getInfoColor()));
                    ClickEvent forwardClick = ClickEvent.runCommand("/" + template.getName() + " help " + (page + 1));
                    buttons.addText(textComponent("->", theme.getVarInfoColor(), forwardHover, forwardClick));
                }
                buttons.addText(textComponent(")", theme.getInfoColor()));
            }
            
            Message commands = new Message();
            boolean color = true;
            for (int i = startIndex; i < startIndex + listSize && i < commandArrayList.size(); i++) {
                String command = commandArrayList.get(i);
                SubCommand subCommand = commandMap.get(command);
                
                TextComponent commandComponent = commandToComponent(command, subCommand, player, color);
                commands.addText(commandComponent);
                
                commands.addNewLine();
                color = !color;
            }
            commands.removeLast();
            
            sendInfoTranslation(player, translateKeyPrefix + ".command.help.page.succeeded", "/" + template.getName(), page, buttons, commands);
        });
        
        EmptyCommand commandHelp = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return template.getName();
            }
        };
        commandHelp.setCommandName(template.getName() + " command", ArgumentType.REQUIRED);
        commandHelp.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".command.help.command.commandDescription"));
        commandHelp.setRunnable((args, player) -> {
            HashMap<String, SubCommand> commandMap = template.collectActions();
            String command = "/" + StringUtils.join(args, " ", 1, args.length);
            
            Message commands = new Message();
            boolean color = true;
            for (String tmpCommand : commandMap.keySet()) {
                if (tmpCommand.toLowerCase().startsWith(command.toLowerCase()) || command.equalsIgnoreCase(tmpCommand)) {
                    SubCommand subCommand = commandMap.get(tmpCommand);
                    TextComponent commandComponent = commandToComponent(tmpCommand, subCommand, player, color);
                    commands.addText(commandComponent);
                    commands.addNewLine();
                    color = !color;
                }
            }
            commands.removeLast();
            
            sendInfoTranslation(player, translateKeyPrefix + ".command.help.command.succeeded", command, commands);
        });
        commandHelp.setTabRunnable((args, player) -> {
            String s = StringUtils.join(args, " ", 1, args.length - 1) + " ";
            return template.collectActions().keySet().stream()
                    .map(c -> c.substring(1).toLowerCase())
                    .filter(c -> c.startsWith(s.toLowerCase()))
                    .map(c -> c.replaceFirst("(?i)" + s, ""))
                    .collect(Collectors.toList());
        });
        commandHelp.setLooped(true);
        
        if (commandMessage != null) {
            addAction(new EmptyCommand(){
                @Override
                public String getCommandName() {
                    return "";
                }
                
                @Override
                public Message getCommandDescription() {
                    return commandMessage;
                }
                
                @Override
                public String getName(String argument) {
                    return "";
                }
            });
        }
        addAction(commandList);
        addAction(commandHelp);
    }
    
    private TextComponent commandToComponent(String command, SubCommand subCommand, Player player, boolean color) {
        TextComponent textComponent = new TextComponent(command);
        textComponent.setColor(color ? varInfoColor : varInfo2Color);
        
        Message hover = new Message();
        hover.addMessage(subCommand.getCommandDescription());
        if (!subCommand.getPermissions().isEmpty()) {
            hover.addNewLine();
            hover.addNewLine();
            hover.addMessage(subCommand.permissionsHover());
            hover.addNewLine();
            if (subCommand.getPermissions().stream().anyMatch(p -> p.contains("<") || p.contains("["))) {
                hover.addMessage(formatErrorTranslation(translateKeyPrefix + ".command.help.page.cantMeasurePerm"));
            } else {
                hover.addMessage(formatInfoTranslation(translateKeyPrefix + ".command.help.page.perm",
                        (subCommand.hasPermissionToRun(player, false) ?
                                formatTranslation(goodColor, goodColor, translateKeyPrefix + ".command.help.page.do") :
                                formatTranslation(badColor, badColor, translateKeyPrefix + ".command.help.page.dont")
                        )));
            }
        }
        textComponent.addTextEvent(new HoverEvent(hover)).setInsertion(command);
        return textComponent;
    }
    
    public void addExtraHelp(String helpName, Message message) {
        addExtraHelp(helpName, (args, player) -> message.sendMessage(player));
    }
    
    public void addExtraHelp(String helpName, RunRunnable command) {
        EmptyCommand helpCommand = new EmptyCommand() {
            @Override
            public String getName(String argument) {
                return helpName;
            }
        };
        helpCommand.setRunnable(command);
        helpCommand.setCommandDescription(formatInfoTranslation(translateKeyPrefix + ".command.help.extraHelp"));
        addExtraHelp(helpName, helpCommand);
    }
    
    public void addExtraHelp(String helpName, EmptyCommand helpCommand) {
        extraHelp.add(helpName);
        helpCommand.setCommandName(helpName, ArgumentType.FIXED);
        addAction(helpCommand);
    }
    
    public boolean removeExtraHelp(String dataName) {
        extraHelp.remove(dataName);
        return removeAction(dataName) != null;
    }
    
    @Override
    public String getName(String arg) {
        return "help";
    }
    
    public int getListSize() {
        return listSize;
    }
    
    public void setListSize(int listSize) {
        this.listSize = Math.max(1, listSize);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add(template.getName());
        list.addAll(extraHelp);
        int commandSize = template.collectActions().size();
        IntStream.range(0, commandSize / listSize).mapToObj(i -> String.valueOf(i + 1)).forEach(list::add);
        if (commandSize % listSize != 0) {
            list.add(String.valueOf(commandSize / listSize + 1));
        }
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        if (args.length <= 1) {
            if (commandMessage != null) {
                commandMessage.sendMessage(player);
                return;
            }
        } else {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
        }
        sendErrorTranslation(player, translateKeyPrefix + ".command.wrongUsage",
                "/" + template.getName() + " help " + (commandMessage == null ? "<" : "[") + "page|" + template.getName() + " command..." +
                        extraHelp.stream().collect(Collectors.joining("|", (extraHelp.size() == 0 ? "" : "|"), "")) + (commandMessage == null ? ">" : "]"));
    }
}
