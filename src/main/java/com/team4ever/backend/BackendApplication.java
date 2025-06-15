package com.team4ever.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
		basePackages = {
				"com.team4ever.backend.domain.common.brand",
				"com.team4ever.backend.domain.plan.repository",
				"com.team4ever.backend.domain.benefit.repository",
				"com.team4ever.backend.domain.common.couponlike",
				"com.team4ever.backend.domain.attendance.repository",
				"com.team4ever.backend.domain.coupon.repository",
				"com.team4ever.backend.domain.popups.repository",
				"com.team4ever.backend.domain.subscriptions.repository",
				"com.team4ever.backend.domain.user.repository",
		}
)
public class BackendApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().load();

		System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
		System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));

		System.setProperty("SPRING_DATA_REDIS_HOST", dotenv.get("SPRING_DATA_REDIS_HOST"));
		System.setProperty("SPRING_DATA_REDIS_PORT", dotenv.get("SPRING_DATA_REDIS_PORT"));

		System.setProperty("APP_AUTH_TOKEN_SECRET", dotenv.get("APP_AUTH_TOKEN_SECRET"));
		System.setProperty("APP_AUTH_ACCESS_TOKEN_EXPIRATION_MSEC", dotenv.get("APP_AUTH_ACCESS_TOKEN_EXPIRATION_MSEC"));
		System.setProperty("APP_AUTH_REFRESH_TOKEN_EXPIRATION_MSEC", dotenv.get("APP_AUTH_REFRESH_TOKEN_EXPIRATION_MSEC"));

		// --- OAuth2 클라이언트 (Google, Kakao, Naver) ---
		System.setProperty(
				"SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID",
				dotenv.get("SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID")
		);
		System.setProperty(
				"SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET",
				dotenv.get("SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET")
		);

		System.setProperty(
				"SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_CLIENT_ID",
				dotenv.get("SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_CLIENT_ID")
		);
		System.setProperty(
				"SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_CLIENT_SECRET",
				dotenv.get("SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_CLIENT_SECRET")
		);

		System.setProperty(
				"SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_NAVER_CLIENT_ID",
				dotenv.get("SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_NAVER_CLIENT_ID")
		);
		System.setProperty(
				"SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_NAVER_CLIENT_SECRET",
				dotenv.get("SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_NAVER_CLIENT_SECRET")
		);

		System.setProperty("NAVER_CLOUD_ACCESS_KEY", dotenv.get("NAVER_CLOUD_ACCESS_KEY"));
		System.setProperty("NAVER_CLOUD_SECRET_KEY", dotenv.get("NAVER_CLOUD_SECRET_KEY"));

		SpringApplication.run(BackendApplication.class, args);
	}
}
