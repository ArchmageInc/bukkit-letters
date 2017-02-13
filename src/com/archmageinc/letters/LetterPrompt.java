package com.archmageinc.letters;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.conversations.Prompt;

public class LetterPrompt extends StringPrompt {

    @Override
    public String getPromptText(ConversationContext context) {
        return "Open chat and type your letter";
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String message) {
        context.setSessionData("message", message);
        return Prompt.END_OF_CONVERSATION;
    }
    
}
