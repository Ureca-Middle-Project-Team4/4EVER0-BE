# 백엔드 레포

# 🌐 MoonoZ Backend

> 🐙 무너와 함께하는  
> ✨ *MZ세대를 위한 스마트한 LG U+ 혜택 큐레이션 서비스 백엔드* ✨  




## 📦 프로젝트 개요

- **레포지토리명**: 4EVER0-BE  
- **역할**: 프론트엔드 서비스에 필요한 API 제공, 인증 및 사용자 관리, AI 서버와 연동된 데이터 처리  
- **개발 기간**: 2025.06.04 ~ 2025.06.26  




## 🗂️ 폴더 구조
src/main/java/com/team4ever/backend/
├── global/
│   ├── config/             # Swagger 등 설정
│   ├── exception/          # 전역 예외 처리
│   └── response/           # 공통 응답 포맷
├── domain/
│   └── plan/
│       ├── controller/     # REST API 엔드포인트
│       ├── service/        # 비즈니스 로직
│       ├── repository/     # 데이터베이스 처리
│       └── entity/         # 엔티티 정의
└── common/                 # 유틸, 상수 등





## ✅ 공통 응답 및 예외 처리

### 🧾 BaseResponse

모든 API 응답은 `BaseResponse<T>` 형태로 반환되어 프론트엔드에서 일관된 응답 포맷으로 처리됩니다.

```json
{
  "success": true,
  "message": "요청 성공",
  "data": { ... }
}
```


### 🚨 Global Exception Handling

- 전역 예외 핸들러: global.exception.GlobalExceptionHandler
- 에러 코드 Enum: ErrorCode.java
- 실패 응답은 ApiResponse.fail(...) 형태로 통일 처리



### 🔐 인증 및 보안

- **OAuth2 소셜 로그인**: 카카오, 네이버, 구글
- **JWT 기반 인증**: Access / Refresh Token 발급 및 갱신
- **Redis**: 토큰 세션 관리 및 자동 만료 처리
- **Spring Security**: Role 기반 인증 및 경로 보호




## 🧭 주요 기능 요약

#### 📍 위치 기반 혜택 검색
- 네이버 검색 API + 지도 API 사용
- 사용자의 위치 기반 팝업 혜택 탐색 기능 제공

#### ❤️ 좋아요 기능
- 유저와 혜택 간 다대다 관계 테이블 `likes`
- 혜택 좋아요 상태 확인 및 토글 기능 제공

#### 🎯 출석 및 미션 기능
- 출석 체크 → 캘린더 UI에 바로 반영
- 미션 진행률, 보상 수령 처리




## 🛠 기술 스택

| 분야            | 기술 스택                                         |
| ------------- | --------------------------------------------- |
| **Backend**   | Spring Boot, Spring Security, Spring Data JPA |
| **DB**        | MySQL, Redis                                  |
| **Auth**      | OAuth2, JWT                                   |
| **API 문서화**   | Swagger 3.0 (`springdoc-openapi`)             |
| **API 연동**    | Naver Search API, Google Custom Search API    |
| **CI/CD**     | GitHub Actions                                |
| **Infra**     | AWS EC2, AWS RDS                              |
| **Dev Tools** | Gradle, GitHub, Notion, Jira                  |

---

## 📑 프로젝트 문서
- [WBS](https://docs.google.com/spreadsheets/d/1ln5VudFdBKMbaNANwzZyW0CGLYC_R9Xf/edit?usp=sharing&ouid=101077923369398316818&rtpof=true&sd=true)
- [플로우 차트](https://www.figma.com/proto/C1HjN8qg3Vptm2j7k2cT8N/%ED%94%8C%EB%A1%9C%EC%9A%B0%EC%B0%A8%ED%8A%B8?node-id=1-4&t=OH4mgwF8RPp4bDv8-1&scaling=scale-down-width&content-scaling=fixed&page-id=0%3A1)
- [API 명세서](https://hollow-cello-87b.notion.site/1fb3347f51ee81269bceeaad7f3c76f1?v=1fb3347f51ee81719ba1000c67dfe978)
- [ERD](https://dbdiagram.io/d/DB_4ever0-684e577c3cc77757c8eaba7c)
- [Swagger UI] 
