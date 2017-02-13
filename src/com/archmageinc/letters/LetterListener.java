package com.archmageinc.letters;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.Conversation;
import org.bukkit.map.MapView;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class LetterListener implements Listener, ConversationAbandonedListener {
    private Letters plugin = null;
    private final HashMap<Player,Conversation> conversations = new HashMap<>();
    
    public LetterListener(Letters plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (conversations.containsKey(player)) {
            return;
        }
        ItemStack mainItem = player.getInventory().getItemInMainHand();
        ItemStack offItem = player.getInventory().getItemInOffHand();
        
        if(Material.PAPER.equals(mainItem.getType()) && mainItem.getAmount() == 1 && Material.INK_SACK.equals(offItem.getType())) {
            Conversation conversation = plugin.getConversationFactory().buildConversation(player);
            conversations.put(player, conversation);
            conversation.begin();
        } 
    }
    
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Conversation conversation = conversations.get(event.getPlayer());
        if(conversation != null) {
            conversation.abandon();
        }
    }
    
    private ItemStack createLetter(Player player, String message) {
        MapView mapView = plugin.getServer().createMap(player.getWorld());
        ItemStack letter = new ItemStack(Material.MAP, 1, mapView.getId());
        ItemMeta itemMeta = letter.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        
        lore.add("From: " + player.getDisplayName());
        
        itemMeta.setDisplayName("Letter");
        itemMeta.setLore(lore);
        letter.setItemMeta(itemMeta);
        
        
        plugin.activateLetter(mapView, message, true);
        
        return letter;
    }
    
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent event) {
        if (event.gracefulExit()) {
            Player player = (Player) event.getContext().getForWhom();
            ItemStack mainItem = player.getInventory().getItemInMainHand();
            ItemStack offItem = player.getInventory().getItemInOffHand();
            
            ItemStack letter = createLetter(player, (String) event.getContext().getSessionData("message"));
            player.getInventory().remove(mainItem);
            offItem.setAmount(offItem.getAmount() - 1);
            player.getInventory().setItemInMainHand(letter);
        } else {
            event.getContext().getForWhom().sendRawMessage("You are no longer interested in writing a letter");
        }
        
        conversations.remove((Player) event.getContext().getForWhom());
    }
}
