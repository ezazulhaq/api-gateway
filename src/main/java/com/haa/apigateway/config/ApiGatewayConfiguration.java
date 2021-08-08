package com.haa.apigateway.config;

import java.util.function.Function;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        Function<PredicateSpec, Buildable<Route>> routeFunction = p -> p.path("/get")
                .filters(f -> f.addRequestHeader("MyHeader", "MyURI").addRequestParameter("Param", "MyValue"))
                .uri("http://httpbin.org:80");

        Function<PredicateSpec, Buildable<Route>> currencyExchangeRoute = p -> p.path("/currency-exchange/**")
                .uri("lb://currency-exchange-service");

        Function<PredicateSpec, Buildable<Route>> currencyConversionRoute = p -> p.path("/currency-conversion/**")
                .uri("lb://currency-conversion-service");

        Function<PredicateSpec, Buildable<Route>> currencyConversionRouteFeign = p -> p
                .path("/currency-conversion-feign/**").uri("lb://currency-conversion-service");

        Function<PredicateSpec, Buildable<Route>> currencyConversionRouteNew = p -> p
                .path("/currency-conversion-new/**")
                .filters(f -> f.rewritePath("/currency-conversion-new/(?<segment>.*)",
                        "/currency-conversion-feign/${segment}"))
                .uri("lb://currency-conversion-service");

        return builder.routes().route(routeFunction).route(currencyExchangeRoute).route(currencyConversionRoute)
                .route(currencyConversionRouteFeign).route(currencyConversionRouteNew).build();
    }

}
