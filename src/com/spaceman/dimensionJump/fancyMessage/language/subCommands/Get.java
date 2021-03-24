package com.spaceman.dimensionJump.fancyMessage.language.subCommands;

import com.spaceman.dimensionJump.commandHandler.SubCommand;
import com.spaceman.dimensionJump.fancyMessage.language.Language;
import org.bukkit.entity.Player;

import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.sendInfoTheme;

public class Get extends SubCommand {
    
    @Override
    public void run(String[] args, Player player) {
        // tport language get
        
        if (args.length == 2) {
            String lang = Language.getPlayerLangName(player.getUniqueId());
            
            if (lang.equals("custom")) {
                sendInfoTheme(player, "Your language is set to %s, this means that you have to use the TPort Language Resource Pack", lang);
            } else if (lang.equals("server")) {
                sendInfoTheme(player, "Your language is set to %s, this means that your language will be the same as the server", lang);
            } else {
                sendInfoTheme(player, "Your language is set to %s", lang);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/tport language get");
        }
    }
}
