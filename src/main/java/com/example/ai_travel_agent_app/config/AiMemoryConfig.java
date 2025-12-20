package com.example.ai_travel_agent_app.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiMemoryConfig {
	
	 @Bean
	    public ChatMemory chatMemory(ChatMemoryRepository repository) {
	        return MessageWindowChatMemory.builder()
	                .chatMemoryRepository(repository)
	                .maxMessages(20)
	                .build();
	    }

	    /** Memory advisor for chat client */
	    @Bean
	    public MessageChatMemoryAdvisor memoryAdvisor(ChatMemory chatMemory) {
	        return MessageChatMemoryAdvisor.builder(chatMemory).build();
	    }

	    

}
