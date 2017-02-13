package com.archmageinc.letters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Letters extends JavaPlugin {
    
    private final Logger log = Logger.getLogger("Minecraft");
    private final LetterListener letterListener = new LetterListener(this);
    private final ConversationFactory conversationFactory;
    private final HashMap<MapView,LetterRenderer> activeMaps = new HashMap<>();
    
    public Letters() {
        this.conversationFactory = new ConversationFactory(this)
                .withFirstPrompt(new LetterPrompt())
                .withModality(true)
                .addConversationAbandonedListener(letterListener);
    }
    
    @Override
    public void onDisable() {
        this.saveLetters();
        this.logMessage("Disabled");
    }
    
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(letterListener, this);
        initConfig();
        this.loadLetters();
    }
    
    public void logMessage(String msg) {
        PluginDescriptionFile pdFile	=	this.getDescription();
        log.log(Level.INFO, "[{0} {1}]: {2}", new Object[]{pdFile.getName(), pdFile.getVersion(), msg});
    }
    
    public ConversationFactory getConversationFactory() {
        return conversationFactory;
    }
    
    public void activateLetter(MapView mapView, String message) {
        this.activateLetter(mapView, message, false);
    }
    
    public void activateLetter(MapView mapView, String message, Boolean withSave) {
        LetterRenderer letterRenderer = new LetterRenderer(message);
        activeMaps.put(mapView, letterRenderer);
        for (MapRenderer renderer : mapView.getRenderers() ) {
            mapView.removeRenderer(renderer);
        }

        mapView.addRenderer(letterRenderer);
        
        if (withSave) {
            this.saveLetters();
        }
    }
    
    private void saveLetters() {
        Iterator<MapView> iterator = activeMaps.keySet().iterator();
        JSONObject maps = new JSONObject();
        String fileName = this.getDataFolder() + "/" + this.getConfig().getString("letterRecordFile");
        
        while(iterator.hasNext()) {
            MapView mapView = iterator.next();
            LetterRenderer typeRenderer = activeMaps.get(mapView);
            maps.put(mapView.getId(), typeRenderer.getMessage());
        }
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            String jsonString = maps.toJSONString();
            writer.write(jsonString);
            writer.close();
        } catch (IOException ex) {
            this.logMessage("Unable to write to "+fileName+": "+ex.getMessage());
        }
    }
    
    private void loadLetters() {
        JSONParser parser = new JSONParser();
        String fileName = this.getDataFolder() + "/" + this.getConfig().getString("letterRecordFile");
        try {
            JSONObject maps = (JSONObject) parser.parse(new FileReader(fileName));
            for(Object id : maps.keySet()) {
                MapView mapView = this.getServer().getMap(new Short((String) id));
                String message = (String) maps.get(id);
                this.activateLetter(mapView, message);
            }
        } catch (FileNotFoundException ex) {
            this.logMessage("Letter record file not found");
        } catch (IOException ex) {
            this.logMessage("Error reading letter record file");
        } catch (ParseException ex) {
            this.logMessage("Error parsing letter record file");
        }
        
    }
    
    private void initConfig() {
        if(!new File(this.getDataFolder(), "config.yml").exists()) {
            logMessage("Saving default config");
            this.saveDefaultConfig();
        }
    }

    
}
