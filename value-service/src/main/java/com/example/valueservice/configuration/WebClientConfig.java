package com.example.valueservice.configuration;

import com.example.valueservice.client.SettingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(this.filterFunction)
                .baseUrl("http://SETTING-SERVICE")
                .build();
    }


    @Bean
    public SettingClient settingClient() {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient()))
                .build();
        return httpServiceProxyFactory.createClient(SettingClient.class);
    }
}
