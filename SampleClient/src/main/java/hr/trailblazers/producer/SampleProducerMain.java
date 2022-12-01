package hr.trailblazers.producer;

import hr.trailblazers.producer.SampleProducer;

public class SampleProducerMain {

    public static void main(String[] args) throws InterruptedException {
        SampleProducer producer = new SampleProducer();
        for(int i = 0; i< 100; i++){
            producer.sendMessage();
            Thread.sleep(1000);
        }
    }
}
