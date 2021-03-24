package com.spaceman.dimensionJump.fancyMessage.language.subCommands;

import com.spaceman.dimensionJump.commandHandler.ArgumentType;
import com.spaceman.dimensionJump.commandHandler.EmptyCommand;
import com.spaceman.dimensionJump.commandHandler.SubCommand;
import com.spaceman.dimensionJump.fancyMessage.language.Language;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.sendSuccessTheme;
import static com.spaceman.dimensionJump.fancyMessage.language.Language.getAvailableLang;

public class Set extends SubCommand {
    
    public Set() {
        EmptyCommand custom = new EmptyCommand();
        custom.setCommandName("custom", ArgumentType.FIXED);
        
        EmptyCommand server = new EmptyCommand();
        server.setCommandName("server", ArgumentType.FIXED);
        
        EmptyCommand serverLanguage = new EmptyCommand();
        serverLanguage.setCommandName("server language", ArgumentType.REQUIRED);
        
        addAction(custom);
        addAction(server);
        addAction(serverLanguage);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        Collection<String> list = new ArrayList<>(getAvailableLang());
        list.addAll(Arrays.asList("custom", "server"));
        return list;
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language set custom
        // tport language set server
        // tport language set <server language>
        
        if (args.length == 3) {
            if (args[2].equalsIgnoreCase("custom")) {
                Language.setPlayerLang(player.getUniqueId(), "custom");
                sendSuccessTheme(player, "Successfully set your TPort language to %s, make sure that you are using a TPort Language Resource Pack", "custom");
            } else if (args[2].equalsIgnoreCase("server")) {
                Language.setPlayerLang(player.getUniqueId(), "server");
                sendSuccessTheme(player, "Successfully set your TPort language to %s", "server");
            } else {
                if (Language.setPlayerLang(player.getUniqueId(), args[2])) {
                    sendSuccessTheme(player, "Successfully set your TPort language to %s", args[2].toLowerCase());
                } else {
                    sendErrorTheme(player, "TPort server language %s does not exist", args[2].toLowerCase());
                }
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport language set <custom|server|<server language>>");
        }
    }
}
