package com.spaceman.dimensionJump.fancyMessage.language.subCommands;

import com.spaceman.dimensionJump.commandHandler.SubCommand;
import com.spaceman.dimensionJump.fancyMessage.TextType;
import com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme;
import com.spaceman.dimensionJump.fancyMessage.language.Language;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.spaceman.dimensionJump.fancyMessage.Message.command;
import static com.spaceman.dimensionJump.fancyMessage.Message.translateKeyPrefix;
import static com.spaceman.dimensionJump.fancyMessage.MessageUtils.getOrDefaultCaseInsensitive;
import static com.spaceman.dimensionJump.fancyMessage.TextComponent.textComponent;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Test extends SubCommand {
    
    @Override
    public Collection<String> tabList(Player player, String[] args) {
        JSONObject translation = Language.getServerLang();
        return (Collection<String>) translation.keySet().stream().collect(Collectors.toList());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // command language test <id>
        
        if (args.length == 3) {
            JSONObject translation = Language.getPlayerLang(player.getUniqueId());
            String raw = args[2];
            if (translation == null) {
                ColorTheme.formatInfoTranslation(translateKeyPrefix + ".language.test", raw, textComponent(raw).setType(TextType.TRANSLATE)).sendMessage(player);
            } else if (getOrDefaultCaseInsensitive(translation, raw, null) != null) {
                String translated = (String) translation.get(raw);
                ColorTheme.sendInfoTranslation(player, translateKeyPrefix + ".language.test", raw, translated);
            } else {
                sendErrorTheme(player, "%s is not used as translation id", raw);
            }
        } else {
            sendErrorTheme(player, "Usage: %s", "/" + command + " language test <id>");
        }
    }
}
