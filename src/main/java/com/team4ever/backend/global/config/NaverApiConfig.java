package com.team4ever.backend.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "naver.cloud")
@Getter
@Setter
public class NaverApiConfig {
	private String accessKey;
	private String secretKey;
	private String geolocationUrl = "https://geolocation.apigw.ntruss.com/geolocation/v2/geoLocation";
}
