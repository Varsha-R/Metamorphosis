package com.example.slackeventreceiver;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Scanner;

public class Producer {

    private String message;
    Properties prop;

    public Producer(String message) {
        this.message = message;
        this.prop = new Properties();
        this.prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        this.prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    public void send() {
        final KafkaProducer<String, String> producer = new KafkaProducer<String, String>(prop);
        ProducerRecord<String, String> record = new ProducerRecord<>("slack-messages", message);
        producer.send(record);

        producer.flush();
        producer.close();
    }

//    public static void main(String[] args) {
//        //System.out.println("Hello");
//        while (true) {
//            System.out.println("Enter message");
//            Scanner in = new Scanner(System.in);
//
//            String s = in.nextLine();
//
//            Producer producer = new Producer(s);
//            producer.send();
//        }
//    }

}
