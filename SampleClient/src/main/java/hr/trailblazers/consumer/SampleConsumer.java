package hr.trailblazers.consumer;

import hr.trailblazers.producer.SampleProducer;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificData;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;

public class SampleConsumer {

    private final String BOOSTRAP_SERVER = "cluster-simple-auth-kafka-routed-bootstrap-trailblazers.apps-crc.testing:443";
    private final String TOPIC_NAME = "trailblazers-topic";
    private final String KAFKA_USERNAME = "trailblazer";

    private static final Logger LOG = LogManager.getLogger(SampleConsumer.class);

    private KafkaConsumer<String, String> consumer;

    public void consume(){
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                final ConsumerRecords<String, String> records = consumer.poll(Duration.of(10, ChronoUnit.SECONDS));

                if (records.count() > 0) {
                    records.forEach(record -> {
                        String value = record.value();
                        LOG.info("Read message: " + value);
                        record.headers().forEach(header -> {
                            LOG.info("Header Key: " + header.key() + ", Header value:" + new String(header.value()));
                        });
                    });
                } else {
                    LOG.info(Thread.currentThread().getName() + " - No messages!");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }

    public SampleConsumer() {
        Properties props = new Properties();

        // Configure Strimzi config providers
        props.putIfAbsent("config.providers", "secrets,configmaps");
        props.putIfAbsent("config.providers.secrets.class", "io.strimzi.kafka.KubernetesSecretConfigProvider");
        props.putIfAbsent("config.providers.configmaps.class", "io.strimzi.kafka.KubernetesConfigMapConfigProvider");

        // Configure Kafka
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOSTRAP_SERVER);
        props.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, "trailblazers-group");
        props.putIfAbsent(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "Instance-1");
        props.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.putIfAbsent(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        //configure the following settings for SSL Encryption
        props.putIfAbsent(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.putIfAbsent(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PEM");
        props.putIfAbsent(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG, "${secrets:trailblazers/" + KAFKA_USERNAME + ":user.crt}");
        props.putIfAbsent(SslConfigs.SSL_KEYSTORE_KEY_CONFIG, "${secrets:trailblazers/" + KAFKA_USERNAME + ":user.key}");
        props.putIfAbsent(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
        props.putIfAbsent(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, "${secrets:trailblazers/cluster-simple-auth-cluster-ca-cert:ca.crt}");


        // Create the Kafka Consumer
        consumer = new KafkaConsumer<String, String>(props);
    }
}
