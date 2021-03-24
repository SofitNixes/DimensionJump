package com.spaceman.dimensionJump.fancyMessage.language;

import com.spaceman.dimensionJump.Main;
import com.spaceman.dimensionJump.commandHandler.CommandTemplate;
import com.spaceman.dimensionJump.commandHandler.SubCommand;
import com.spaceman.dimensionJump.fancyMessage.Message;
import com.spaceman.dimensionJump.fancyMessage.TextType;
import com.spaceman.dimensionJump.fancyMessage.language.subCommands.*;
import com.spaceman.dimensionJump.fileHander.Files;
import com.spaceman.dimensionJump.fileHander.GettingFiles;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import static com.spaceman.dimensionJump.commandHandler.CommandTemplate.runCommands;
import static com.spaceman.dimensionJump.fancyMessage.TextComponent.textComponent;
import static com.spaceman.dimensionJump.fancyMessage.colorTheme.ColorTheme.sendErrorTheme;

public class Language extends SubCommand { //todo translate
    
    protected static String configFileName = "TPortConfig";
    protected static HashMap<String, JSONObject> languages = new HashMap<>();
    
    public static boolean setServerLang(String lang) {
        if (languages.containsKey(lang)) {
            Files config = GettingFiles.getFile(configFileName);
            config.getConfig().set("language.server", lang);
            config.saveConfig();
            return true;
        }
        return false;
    }
    
    public static String getServerLangName() {
        String name = GettingFiles.getFile(configFileName).getConfig().getString("language.server", "en_us.json");
        if (!languages.containsKey(name)) {
            name = "en_us.json";
        }
        return name;
    }
    
    public static JSONObject getServerLang() {
        return languages.get(getServerLangName());
    }
    
    //returns false if language has not been set
    public static boolean setPlayerLang(UUID uuid, @Nonnull String lang) {
        if (!lang.equalsIgnoreCase("custom") && !lang.equalsIgnoreCase("server") && !languages.containsKey(lang.toLowerCase())) {
            return false;
        }
        Files config = GettingFiles.getFile(configFileName);
        config.getConfig().set("language.players." + uuid.toString(), lang.toLowerCase());
        config.saveConfig();
        return true;
    }
    
    public static String getPlayerLangName(UUID uuid) {
        Files config = GettingFiles.getFile(configFileName);
        String lang = config.getConfig().getString("language.players." + uuid.toString(), "server");
        if (!languages.containsKey(lang)) {
            if (lang.equals("custom") || lang.equals("server")) return lang;
            setPlayerLang(uuid, "en_us.json");
            return "en_us.json";
        }
        return lang;
    }
    
    @Nullable
    public static JSONObject getPlayerLang(UUID uuid) {
        String playerLang = getPlayerLangName(uuid);
        if (playerLang.equals("server")) {
            return getServerLang();
        } else if (playerLang.equals("custom")) {
            return null;
        } else {
            return languages.get(playerLang);
        }
    }
    
    public static void loadLang() {
        languages = new HashMap<>();
        File langFile = new File(Main.getInstance().getDataFolder(), "lang/en_us.json");
        try {
            langFile.getParentFile().mkdir();
            if (langFile.createNewFile()) {
            }
            {
                InputStream inputStream = Main.getInstance().getResource("lang/en_us.json");
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                new FileOutputStream(new File(Main.getInstance().getDataFolder(), "lang/en_us.json")).write(buffer);
                Main.getInstance().getLogger().log(Level.INFO, "lang/en_us.json did not exist, resetting it...");
            }
//            else {
//                repairLang(langFile);
//            }
        } catch (IOException ignore) {
            try {
                JSONObject newJSON = (JSONObject) new JSONParser().parse(new InputStreamReader(Main.getInstance().getResource("lang/en_us.json"), StandardCharsets.UTF_8));
                languages.put("en_us.json", newJSON);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        
        for (File f : new File(Main.getInstance().getDataFolder().getAbsolutePath() + "\\lang").listFiles()) {
            if (f.isFile()) {
                if (!f.getName().contains(" ")) {
                    if (FilenameUtils.getExtension(f.getName()).equals("json")) {
                        JSONObject json = repairLang(f);
                        if (json != null) {
                            languages.putIfAbsent(f.getName().toLowerCase(), json);
                        }
                    }
                }
            }
        }
    }
    
    public static JSONObject repairLang(File langFile) { //todo not auto save repair
        JSONObject newJSON = null;
        try {
            newJSON = (JSONObject) new JSONParser().parse(new InputStreamReader(Main.getInstance().getResource("lang/en_us.json"), StandardCharsets.UTF_8));
            JSONObject oldJSON = (JSONObject) new JSONParser().parse(new FileReader(langFile));
            
            JSONObject finalNewJSON = newJSON;
            newJSON.keySet().stream().forEach(o -> oldJSON.putIfAbsent(o, finalNewJSON.get(o)));
            
            FileWriter fileWriter = new FileWriter(langFile);
            try {
                fileWriter.write(oldJSON.toJSONString());
                return oldJSON;
            } finally {
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (ParseException e) {
            try {
                if (newJSON == null) return null;
                FileWriter fileWriter = new FileWriter(langFile);
                try {
                    fileWriter.write(newJSON.toJSONString());
                    return newJSON;
                } finally {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException ignore) {
            }
        } catch (IOException ignore) {
        }
        return null;
    }
    
    public static Collection<String> getAvailableLang() {
        return languages.keySet();
    }
    
    public Language() {
        addAction(new Server());
        addAction(new Get());
        addAction(new Set());
        addAction(new Test());
        addAction(new AutoRepair());
    }
    
    @Override
    public void run(String[] args, Player player) {
        // tport language server [language]
        // tport language get
        // tport language set custom
        // tport language set server
        // tport language set <server language>
        // tport language autoRepair [state]
        
        if (args.length > 1) {
            if (runCommands(getActions(), args[1], args, player)) {
                return;
            }
            sendErrorTheme(player, "Usage: %s", "/tport language " + CommandTemplate.convertToArgs(getActions(), false));
            return;
        }
        
        Message message = new Message();
        
        message.addText(textComponent("tport.test.test1").setType(TextType.TRANSLATE).addTranslateWith(textComponent("wait, what?")));
        message.addNewLine();
        message.addText(textComponent("tport.test.test3").setType(TextType.TRANSLATE).addTranslateWith(textComponent("earth", ChatColor.YELLOW)));
        message.addNewLine();
        message.addText(textComponent("tport.test.test4").setType(TextType.TRANSLATE)
                .addTranslateWith(textComponent("tport.test.test3").setType(TextType.TRANSLATE).addTranslateWith(textComponent("world", ChatColor.YELLOW))));
        message.addNewLine();
        message.addText(textComponent("tport.test.test2").setType(TextType.TRANSLATE).addTranslateWith(textComponent("testA", ChatColor.GREEN)));
        
        message.sendAndTranslateMessage(player);
    }
    
    void sendInfoMessage() {
    
    }
}
