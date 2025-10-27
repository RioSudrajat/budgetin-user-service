    package com.budgetin.userservice.service;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.web.reactive.function.client.WebClient;
    import org.springframework.web.util.UriComponentsBuilder; // <-- Tambah import
    import reactor.core.publisher.Mono;

    import java.net.URI; // <-- Tambah import

    @Service
    public class RedisOtpService {

        private final WebClient webClient;
        // Kita simpan URL ASLI dari properties, tanpa modifikasi
        private final String baseUpstashUrl; 

        @Autowired
        public RedisOtpService(
                @Qualifier("upstashWebClient") WebClient upstashWebClient,
                @Value("${upstash.redis.url}") String upstashUrl 
        ) {
            System.out.println(">>> [DEBUG] RedisOtpService injected with WebClient: " + upstashWebClient.toString());
            this.webClient = upstashWebClient;
            // Simpan URL asli
            this.baseUpstashUrl = upstashUrl; 
        }

        public void setOtp(String key, String value) {
             try {
                 // MEMBANGUN URI DENGAN UriComponentsBuilder
                 URI uri = UriComponentsBuilder.fromHttpUrl(baseUpstashUrl) // Mulai dari base URL
                         .pathSegment("set", key, value) // Tambah path segments (otomatis encode jika perlu)
                         .queryParam("ex", 300) // Tambah query parameter
                         .build(true) // Build (encode path variables)
                         .toUri(); // Konversi ke objek URI

                 System.out.println(">>> [DEBUG] Upstash SET URI: " + uri.toString()); // Log URI yang akan dipanggil

                 webClient.post()
                         .uri(uri) // Pakai objek URI
                         .retrieve()
                         .bodyToMono(String.class)
                         .block();
             } catch (Exception e) {
                 System.err.println("Error setting OTP in Upstash for key: " + key);
                 e.printStackTrace(); 
             }
         }

         public String getOtp(String key) {
             try {
                 // MEMBANGUN URI DENGAN UriComponentsBuilder
                 URI uri = UriComponentsBuilder.fromHttpUrl(baseUpstashUrl)
                         .pathSegment("get", key)
                         .build(true)
                         .toUri();

                 System.out.println(">>> [DEBUG] Upstash GET URI: " + uri.toString()); // Log URI

                 Mono<OtpResponse> responseMono = webClient.get()
                         .uri(uri) // Pakai objek URI
                         .retrieve()
                         .bodyToMono(OtpResponse.class);
                 OtpResponse response = responseMono.block();
                 return (response != null) ? response.getResult() : null;
             } catch (Exception e) {
                 System.err.println("Error getting OTP from Upstash for key: " + key);
                 e.printStackTrace(); 
                 return null;
             }
         }

         public void deleteOtp(String key) {
             try {
                 // MEMBANGUN URI DENGAN UriComponentsBuilder
                 URI uri = UriComponentsBuilder.fromHttpUrl(baseUpstashUrl)
                         .pathSegment("del", key)
                         .build(true)
                         .toUri();

                 System.out.println(">>> [DEBUG] Upstash DEL URI: " + uri.toString()); // Log URI

                 webClient.post()
                         .uri(uri) // Pakai objek URI
                         .retrieve()
                         .bodyToMono(String.class)
                         .block();
             } catch (Exception e) {
                 System.err.println("Error deleting OTP from Upstash for key: " + key);
                 e.printStackTrace(); 
             }
         }

         private static class OtpResponse {
             private String result;
             public String getResult() { return result; }
             public void setResult(String result) { this.result = result; }
         }
    }
    

