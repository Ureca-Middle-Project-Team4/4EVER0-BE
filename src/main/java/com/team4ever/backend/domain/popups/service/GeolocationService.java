package com.team4ever.backend.domain.popups.service;

import com.team4ever.backend.domain.popups.dto.GeolocationResponse;
import com.team4ever.backend.global.config.NaverApiConfig;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeolocationService {

	private final NaverApiConfig naverApiConfig;
	private final RestTemplate restTemplate;

	/**
	 * IP 주소로 위치 정보 조회
	 */
	public GeolocationResponse getLocationByIp(String clientIp) {
		try {
			// 로컬 IP 처리
			if (isLocalIp(clientIp)) {
				log.warn("로컬 IP 감지: {}. 기본 위치 사용", clientIp);
				return createDefaultLocation();
			}

			log.info("IP 기반 위치 조회 시작 - IP: {}", clientIp);

			String timestamp = String.valueOf(System.currentTimeMillis());
			String method = "GET";
			String uri = "/geolocation/v2/geoLocation";
			String queryString = "ip=" + clientIp + "&ext=t&responseFormatType=json"; // ext=t로 좌표 포함

			// 네이버 클라우드 API 서명 생성
			String signature = makeSignature(method, uri, timestamp, queryString);

			// 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.set("x-ncp-apigw-timestamp", timestamp);
			headers.set("x-ncp-iam-access-key", naverApiConfig.getAccessKey());
			headers.set("x-ncp-apigw-signature-v2", signature);
			headers.setContentType(MediaType.APPLICATION_JSON);

			// API 호출
			String url = naverApiConfig.getGeolocationUrl() + "?" + queryString;
			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<GeolocationResponse> response = restTemplate.exchange(
					url, HttpMethod.GET, entity, GeolocationResponse.class);

			GeolocationResponse result = response.getBody();

			if (result != null && "0".equals(result.getReturnCode())) {
				GeolocationResponse.GeoLocationData location = result.getGeoLocation();
				log.info("위치 조회 성공 - lat: {}, lng: {}, address: {}-{}-{}",
						location.getLat(), location.getLongitude(),
						location.getR1(), location.getR2(), location.getR3());
				return result;
			} else {
				log.warn("위치 조회 실패 - returnCode: {}", result != null ? result.getReturnCode() : "null");
				return createDefaultLocation();
			}

		} catch (Exception e) {
			log.error("IP 기반 위치 조회 중 오류 발생", e);
			return createDefaultLocation();
		}
	}

	/**
	 * 로컬 IP 판별
	 */
	private boolean isLocalIp(String ip) {
		return ip == null ||
				"127.0.0.1".equals(ip) ||
				"0:0:0:0:0:0:0:1".equals(ip) ||
				"localhost".equals(ip) ||
				ip.startsWith("192.168.") ||
				ip.startsWith("10.") ||
				ip.startsWith("172.");
	}

	/**
	 * 기본 위치 생성 (선릉 멀티캠퍼스 유레카 위치로 설정)
	 */
	private GeolocationResponse createDefaultLocation() {
		GeolocationResponse response = new GeolocationResponse();
		response.setReturnCode("0");

		GeolocationResponse.GeoLocationData location = new GeolocationResponse.GeoLocationData();
		location.setLat(37.503298369423);
		location.setLongitude(127.04979962846);
		location.setR1("서울특별시");
		location.setR2("강남구");
		location.setR3("선릉로428");

		response.setGeoLocation(location);

		return response;
	}


	/**
	 * 네이버 클라우드 API 서명 생성
	 */
	private String makeSignature(String method, String uri, String timestamp, String queryString) {
		try {
			String space = " ";
			String newLine = "\n";

			String message = method +
					space +
					uri +
					(queryString != null ? "?" + queryString : "") +
					newLine +
					timestamp +
					newLine +
					naverApiConfig.getAccessKey();

			SecretKeySpec signingKey = new SecretKeySpec(
					naverApiConfig.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);

			byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(rawHmac);

		} catch (Exception e) {
			throw new RuntimeException("서명 생성 실패", e);
		}
	}
}
