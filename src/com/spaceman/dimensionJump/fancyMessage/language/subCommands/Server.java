package com.spaceman.dimensionJump.fancyMessage.language.subCommands;

import com.spaceman.dimensionJump.commandHandler.ArgumentType;
import com.spaceman.dimensionJump.commandHandler.EmptyCommand;
import com.spaceman.dimensionJump.commandHandler.SubCommand;
import com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.dimensionJump.fancyMessage.language.Language;
import org.bukkit.entity.Player;

import java.util.Collection;

import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.*;
import static com.spaceman.dimensionJump.fancyMessage.language.Language.getAvailableLang;

public class Server extends SubCommand {
    
    public Server() {
        EmptyCommand language = new EmptyCommand();
        language.setCommandName("language", ArgumentType.OPTIONAL);
        addAction(language);
    }
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        return getAvailableLang();
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language server [language]
        
        if (args.length == 2) {
            ColorTheme.sendInfoTheme(player, "Server language is set to %s", Language.getServerLangName());
        } else if (args.length == 3) {
            if (Language.setServerLang(args[2])) {
                sendSuccessTheme(player, "Successfully set the server language to %s", Language.getServerLangName());
            } else {
                sendErrorTheme(player, "Language file %s does not exist, therefore it could not be set", args[2]);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport language server [language]");
        }
    }
}