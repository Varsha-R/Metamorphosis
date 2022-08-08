package com.example.slackeventreceiver;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.io.IOException;

@SpringBootApplication
@ServletComponentScan
public class SlackEventReceiverApplication {
	public static void main(String[] args) {
		SpringApplication.run(SlackEventReceiverApplication.class, args);
	}

}
