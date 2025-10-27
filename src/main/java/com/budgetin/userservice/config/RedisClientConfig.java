    package com.budgetin.userservice.config;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.web.reactive.function.client.WebClient;

    @Configuration
    public class RedisClientConfig {

        // HAPUS FIELD INI:
        // @Value("${upstash.redis.url}")
        // private String upstashUrl;
        // @Value("${upstash.redis.token}")
        // private String upstashToken;

        // GANTI METODE BEAN MENJADI SEPERTI INI:
        // Suntikkan nilai @Value langsung ke parameter metode
        @Bean
        public WebClient upstashWebClient(
                @Value("${upstash.redis.url}") String resolvedUpstashUrl,
                @Value("${upstash.redis.token}") String resolvedUpstashToken
        ) {
            System.out.println(">>> [DEBUG] Creating 'upstashWebClient' bean with URL: " + resolvedUpstashUrl);

            // Gunakan variabel dari parameter metode
            return WebClient.builder()
                    .baseUrl(resolvedUpstashUrl) // Pastikan URL ini benar saat startup
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + resolvedUpstashToken)
                    .build();
        }
    }
    

