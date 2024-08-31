package com.micro.apiaateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
@Configuration
public class GlobalGatewayFilter implements org.springframework.cloud.gateway.filter.GlobalFilter {
		Logger log=LoggerFactory.getLogger(this.getClass());
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		log.info("THIS IS PRE GLOBAL FILTER" + exchange.toString());
		return chain.filter(exchange).then(Mono.fromRunnable(()-> {
			log.info("THIS IS POST GLOBAL FILTER");
		}));
	}

}
