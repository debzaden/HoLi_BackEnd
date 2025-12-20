package com.example.ai_travel_agent_app.event;

import com.example.ai_travel_agent_app.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;


@Setter
@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private User user;
    private  String applicationUrl;
    public RegistrationCompleteEvent(User user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
