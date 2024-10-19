package dev.waiyanhtet.springcloudgateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("greet", r -> r
                        .method(HttpMethod.GET)
                        .and()
                        .path("/greet")
                        .filters(f -> f
                                .addRequestHeader("gateway-req-header","gtway-hvalue")
                                .addResponseHeader("gateway-resp-header", "gtway-hvalue"))
                        .uri("http://localhost:8081")
                ).route("hello", r -> r
                        .method(HttpMethod.POST)
                        .and()
                        .path("/hello")
                        .filters(f -> f
                                .addRequestHeader("gateway-req-header","gtway-hvalue")
                                .addResponseHeader("gateway-resp-header", "gtway-hvalue")
                                .modifyRequestBody(
                                        HelloRequest.class,
                                        HelloRequest.class,
                                        (exchange, req) -> Mono.just(modifyRequest(req)))
                                .modifyResponseBody(
                                        HelloResponse.class,
                                        HelloResponse.class,
                                        MediaType.APPLICATION_JSON_VALUE,
                                        (exchange, response) -> Mono.just(modifyResponse(response))
                                )
                        )
                        .uri("http://localhost:8081")
                )
                .build();
    }

    private HelloRequest modifyRequest(HelloRequest helloRequest) {
        return new HelloRequest(helloRequest.name().concat(" by gateway"), helloRequest.address());
    }

    private HelloResponse modifyResponse(HelloResponse helloResponse) {
        return new HelloResponse(helloResponse.greet().concat(" ").concat(helloResponse.name()), helloResponse.name());
    }

    public record HelloRequest(String name, String address) {}

    public record HelloResponse(String greet, String name) {}
}
