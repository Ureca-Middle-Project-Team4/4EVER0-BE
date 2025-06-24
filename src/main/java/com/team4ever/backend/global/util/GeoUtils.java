package com.team4ever.backend.global.util;

public class GeoUtils {

	private static final double EARTH_RADIUS_KM = 6371.0;

	/**
	 * 두 지점 간의 거리 계산 (Haversine formula)
	 * @param lat1 첫 번째 지점의 위도
	 * @param lng1 첫 번째 지점의 경도
	 * @param lat2 두 번째 지점의 위도
	 * @param lng2 두 번째 지점의 경도
	 * @return 거리 (km)
	 */
	public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
		double latDistance = Math.toRadians(lat2 - lat1);
		double lngDistance = Math.toRadians(lng2 - lng1);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
						Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS_KM * c;
	}

	/**
	 * 지정된 반경 내의 경계 좌표 계산
	 * @param centerLat 중심점 위도
	 * @param centerLng 중심점 경도
	 * @param radiusKm 반경 (km)
	 * @return [minLat, maxLat, minLng, maxLng]
	 */
	public static double[] getBoundingBox(double centerLat, double centerLng, double radiusKm) {
		double latOffset = radiusKm / 111.0; // 1도당 약 111km
		double lngOffset = radiusKm / (111.0 * Math.cos(Math.toRadians(centerLat)));

		return new double[] {
				centerLat - latOffset,  // minLat
				centerLat + latOffset,  // maxLat
				centerLng - lngOffset,  // minLng
				centerLng + lngOffset   // maxLng
		};
	}
}