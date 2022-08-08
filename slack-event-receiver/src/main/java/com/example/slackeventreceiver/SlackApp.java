package com.example.slackeventreceiver;

import com.slack.api.bolt.App;
import com.slack.api.model.event.MessageEvent;
import com.slack.api.model.event.ReactionAddedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class SlackApp {
    @Bean
    public App initSlackApp() {
        App app = new App();
        app.command("/hello", (req, ctx) -> {
            return ctx.ack("What's up?");
        });
        app.event(MessageEvent.class, (payload, ctx) -> {
            System.out.println(payload.getEvent().getText());
            Producer producer = new Producer(payload.getEvent().getText());
            producer.send();
            System.out.println(payload.getEvent().getUser());
            System.out.println(payload.getEvent().getChannel());
            System.out.println(payload.getEvent().getEventTs());
            System.out.println(payload.getEvent().getTeam());
            return ctx.ack();
        });
        app.event(ReactionAddedEvent.class, (payload, ctx) -> {
            System.out.println(payload.getEventContext());
            System.out.println(payload.getEvent().getReaction());
            return ctx.ack();
        });
        return app;
    }
}










