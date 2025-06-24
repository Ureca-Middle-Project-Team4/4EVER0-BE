# OpenJDK 17 Alpine 이미지 사용
FROM openjdk:17-jdk-alpine

# 작업 디렉터리 설정
WORKDIR /app

# JAR 파일 복사 (빌드 후 target 폴더 내 JAR 이름에 맞게 변경)
COPY backend.jar app.jar

COPY .env /app/.env

# 컨테이너 시작 시 JAR 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
