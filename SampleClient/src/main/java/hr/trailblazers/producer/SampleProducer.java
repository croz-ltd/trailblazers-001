package hr.trailblazers.producer;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SampleProducer {

    private final String BOOSTRAP_SERVER = "cluster-simple-auth-kafka-routed-bootstrap-trailblazers.apps-crc.testing:443";
    private final String TOPIC_NAME = "trailblazers-topic";
    private final String KAFKA_USERNAME = "trailblazer";

    private static final Logger LOG = LogManager.getLogger(SampleProducer.class);

    private KafkaProducer<String, String> producer;

    public void sendMessage() {
        String key = UUID.randomUUID().toString();
        List<Header> headers = new ArrayList<>();

        headers.add(new RecordHeader("Header_key", "Header_value".getBytes()));

        ProducerRecord<String, String> pr = new ProducerRecord<String, String>(TOPIC_NAME, null, key, "someValue " + UUID.randomUUID().toString(), headers);

        try {
            String topic = producer.send(pr).get().topic();
            LOG.info("Sample message sent " + topic + " with key " + key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public SampleProducer() {
        Properties props = new Properties();

        // Configure Strimzi config providers
        props.putIfAbsent("config.providers", "secrets,configmaps");
        props.putIfAbsent("config.providers.secrets.class", "io.strimzi.kafka.KubernetesSecretConfigProvider");
        props.putIfAbsent("config.providers.configmaps.class", "io.strimzi.kafka.KubernetesConfigMapConfigProvider");

        // Configure kafka settings
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOSTRAP_SERVER);
        props.putIfAbsent(ProducerConfig.CLIENT_ID_CONFIG, KAFKA_USERNAME);
        props.putIfAbsent(ProducerConfig.ACKS_CONFIG, "all");
        props.putIfAbsent(ProducerConfig.BATCH_SIZE_CONFIG, 0);
        props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //configure the following settings for SSL Encryption
        props.putIfAbsent(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.putIfAbsent(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PEM");
        props.putIfAbsent(SslConfigs.SSL_KEYSTORE_CERTIFICATE_CHAIN_CONFIG, "${secrets:trailblazers/" + KAFKA_USERNAME + ":user.crt}");
        props.putIfAbsent(SslConfigs.SSL_KEYSTORE_KEY_CONFIG, "${secrets:trailblazers/" + KAFKA_USERNAME + ":user.key}");
        props.putIfAbsent(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
        props.putIfAbsent(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, "${secrets:trailblazers/cluster-simple-auth-cluster-ca-cert:ca.crt}");

        // Create the Kafka producer
        producer = new KafkaProducer<String, String>(props);
    }

}

