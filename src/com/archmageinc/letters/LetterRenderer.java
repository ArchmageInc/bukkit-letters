package com.archmageinc.letters;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class LetterRenderer extends MapRenderer {
    
    private String message = "";
    
    public LetterRenderer(String message) {
        this.message = formatMessage(message);
    }
    
    public LetterRenderer(String message, Boolean formatted) {
        if (formatted) {
            this.message = message;
        } else {
            this.message = formatMessage(message);
        }
    }
    
    public String getMessage() {
        return message;
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        mapCanvas.drawText(5, 5, MinecraftFont.Font, message);
    }
    
    private String formatMessage(String string) {
        String newString = "";
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            newString += c;
            if (i != 0 && i % 22 == 0) {
                int index = newString.lastIndexOf(" ");
                String firstPart = newString.substring(0,  index);
                String secondPart = newString.substring(index + 1);
                newString = firstPart + "\n" + secondPart;
            }
        }
        return newString;
    }
    
}
