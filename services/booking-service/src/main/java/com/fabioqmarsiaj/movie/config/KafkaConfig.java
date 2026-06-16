package com.fabioqmarsiaj.movie.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.MOVIE_BOOKING_EVENTS_TOPIC;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic createTransactionTopic() {
        return new NewTopic(MOVIE_BOOKING_EVENTS_TOPIC, 3, (short) 1);
    }

}
