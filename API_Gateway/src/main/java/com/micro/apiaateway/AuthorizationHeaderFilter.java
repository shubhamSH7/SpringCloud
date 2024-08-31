package com.micro.apiaateway;

import java.util.Base64;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config>{

	Logger log=LoggerFactory.getLogger(this.getClass());
	
	@Value("${secret.key.jwt}")
	String secretKey;
	 public AuthorizationHeaderFilter() {
	        super(Config.class);
	    }
	 public static class Config {
	        // Configuration properties can go here if needed
	    }
	 
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange,chain)->{
			ServerHttpRequest req=exchange.getRequest();
			if(!req.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange,"NOT AUTHORIZED HEADER",HttpStatus.UNAUTHORIZED);
			}
			String authHeader=req.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "INVALID AUTHORIZATION HEADER FORMAT", HttpStatus.UNAUTHORIZED);
            }
			String jwt=authHeader.substring(7);
			log.info(jwt);
			if(!isJWTValid(jwt)) {
				return onError(exchange,"INVALID JWT TOKEN",HttpStatus.UNAUTHORIZED);				
			}
			return(chain.filter(exchange));
		};
	}

	  private boolean isJWTValid(String jwt) {
	        try {
	            byte[] secretKeyBytes = Base64.getDecoder().decode(secretKey); // Decode the base64-encoded secret key
	            SecretKey key = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());

	            JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
	            parser.parseClaimsJws(jwt); // Validate token

	            return true; // Token is valid
	        } catch (Exception e) {
	            return false; // Token is invalid or parsing error occurred
	        }
	    }

	private Mono<Void> onError(ServerWebExchange exchange, String string, HttpStatus status) {
		ServerHttpResponse res=exchange.getResponse();
		res.setStatusCode(status);
		return res.setComplete();
	}

	

}

//	private boolean isJWTValid(String jwt) {
//		String Subject=null;
////		String secretKey = "63a94a7cc7954a798ce5b3d081fc3ae684eba8f2f722ff5f90018a579494bddb";
//		byte[] secretKeyBytes=Base64.getEncoder().encode(secretKey.getBytes());
//		SecretKey key=new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
//		
//		JwtParser parser=Jwts.parserBuilder().setSigningKey(key).build();
//		
//		try {
//			Jwt<Header, Claims> parsedToken= parser.parse(jwt);
//			Subject=parsedToken.getBody().getSubject();
//			
//		} catch (Exception e) {
//			return false;
//		}
//		if(Subject.isEmpty() || Subject == null) {
//			return false;
//		}
//		 return true;
//	}