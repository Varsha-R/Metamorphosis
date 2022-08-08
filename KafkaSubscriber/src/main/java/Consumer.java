import com.slack.api.methods.SlackApiException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Consumer {
    public static void main(String[] args) throws IOException, ParseException, SlackApiException {
        Logger logger = LoggerFactory.getLogger(Consumer.class.getName());

        ProcessMessage processMessage = ProcessMessage.getInstance();

        String bootstrapServers = "127.0.0.1:9092";
        String topic = "slack-messages";
        String grp_id = "ConsumerExampleKafka";

        //Creating consumer properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, grp_id);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //creating consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        //Subscribing
        consumer.subscribe(Arrays.asList(topic));

        System.out.println("Fetching consumer records: ");

        //polling
        // poll for new data
        while(true){
//            consumer.seekToBeginning(consumer.assignment());
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records){
                logger.info("Key: " + record.key() + ", Value: " + record.value());
                processMessage.lookUpMessage(record.value());
                logger.info("Partition: " + record.partition() + ", Offset:" + record.offset());
            }
        }
    }
}
