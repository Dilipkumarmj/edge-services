package com.example.edge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@SpringBootApplication
public class EdgeApplication {
	public static void main(String[] args) {
		SpringApplication.run(EdgeApplication.class, args);
	}

	@Bean
	RedisRateLimiter redisRateLimiter(){
		return new RedisRateLimiter(5,7);
	}

    @Bean
	RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder
				.routes()
				.route(rs -> rs.path("/proxy").and().host("*.spring.io")
						.filters(fs -> fs
								.setPath("/customers")
								.requestRateLimiter(c -> c
										.setRateLimiter(redisRateLimiter())
										.setKeyResolver(exchange -> exchange.getPrincipal().map(Principal::getName))
								)
						)
						.uri("http://localhost:8080/"))
				.build();
	}

	@Bean
	MapReactiveUserDetailsService authentication(){
		return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder().username("dilip")
				.password("pw").roles("USER").build());
	}

	@Bean
	SecurityWebFilterChain authorization(ServerHttpSecurity security){
		return security.authorizeExchange(ae->ae.pathMatchers("/proxy").authenticated()
				.anyExchange().permitAll())
				.httpBasic(Customizer.withDefaults())
				.csrf(c-> c.disable())
				.build();
	}

	@Bean
	WebClient webClient(WebClient.Builder builder){
		return builder.build();
	}

	@Bean
	RSocketRequester rSocketRequester(RSocketRequester.Builder builder){
		return builder.tcp("localhost",8181);
	}

}
