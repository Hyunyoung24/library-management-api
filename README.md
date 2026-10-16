# 도서 대출 관리 API

도서의 등록, 대출, 반납을 관리하는 REST API 서버입니다.

## 기능

- 도서 CRUD 및 카테고리 관리
- 대출/반납 처리 및 연체 여부 판별
- JWT 기반 로그인 및 회원 등록
- 역할(USER/ADMIN) 기반 권한 관리
- 페이지네이션
- 입력값 검증 (Bean Validation)
- Swagger UI API 문서 제공

## 기술 스택

| 구분 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 4.1 |
| 보안 | Spring Security, JWT (jjwt) |
| ORM | Spring Data JPA |
| DB | MySQL (운영), H2 (테스트) |
| 문서화 | springdoc-openapi (Swagger UI) |
| 빌드 | Gradle |
| 기타 | Lombok |

## 개발 도구

- IntelliJ IDEA
- Claude Code
- Git / GitHub

## API 엔드포인트

> Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 인증

| 메서드 | 경로 | 설명 | 권한 |
|--------|------|------|------|
| POST | /api/auth/register | 회원가입 | ALL |
| POST | /api/auth/login | 로그인 | ALL |

### 도서

| 메서드 | 경로 | 설명 | 권한 |
|--------|------|------|------|
| GET | /api/books | 도서 목록 조회 | ALL |
| GET | /api/books/{id} | 도서 상세 조회 | ALL |
| POST | /api/books | 도서 등록 | ADMIN |
| PUT | /api/books/{id} | 도서 정보 수정 | ADMIN |
| DELETE | /api/books/{id} | 도서 삭제 | ADMIN |

### 카테고리

| 메서드 | 경로 | 설명 | 권한 |
|--------|------|------|------|
| GET | /api/categories | 카테고리 목록 조회 | ALL |
| POST | /api/categories | 카테고리 등록 | ADMIN |

### 대출

| 메서드 | 경로 | 설명 | 권한 |
|--------|------|------|------|
| POST | /api/loans | 대출 신청 | USER |
| PATCH | /api/loans/{id}/return | 반납 처리 | USER |
| GET | /api/loans | 전체 대출 내역 조회 | ADMIN |
| GET | /api/loans/me | 내 대출 내역 조회 | USER |
| GET | /api/loans/overdue | 연체 목록 조회 | ADMIN |

### 계정

| 메서드 | 경로 | 설명 | 권한 |
|--------|------|------|------|
| GET | /api/users/me | 내 정보 조회 | USER |
| PATCH | /api/users/me | 내 정보 수정 | USER |

### 관리

| 메서드 | 경로 | 설명 | 권한 |
|--------|------|------|------|
| GET | /api/users | 사용자 목록 조회 | ADMIN |
| POST | /api/users | 사용자 임의 등록 | ADMIN |
| GET | /api/users/{userId} | 사용자 정보 조회 | ADMIN |
| PATCH | /api/users/{userId} | 사용자 권한 변경 | ADMIN |
| DELETE | /api/users/{userId} | 사용자 계정 삭제 | ADMIN |

## 테이블 설계

### categories

| 컬럼 | 타입 | 제약조건 |
|------|------|----------|
| id | BIGINT | PK, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL, UNIQUE |

### books

| 컬럼 | 타입 | 제약조건 |
|------|------|----------|
| id | BIGINT | PK, AUTO_INCREMENT |
| title | VARCHAR(255) | NOT NULL |
| author | VARCHAR(255) | |
| publisher | VARCHAR(255) | NOT NULL |
| isbn | VARCHAR(20) | NOT NULL, UNIQUE |
| category_id | BIGINT | FK (categories) |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'AVAILABLE' |

### users

| 컬럼 | 타입 | 제약조건 |
|------|------|----------|
| id | BIGINT | PK, AUTO_INCREMENT |
| username | VARCHAR(255) | NOT NULL, UNIQUE |
| email | VARCHAR(255) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL |
| authority | VARCHAR(20) | NOT NULL, DEFAULT 'USER' |
| reg_date | DATETIME | NOT NULL |

### loans

| 컬럼 | 타입 | 제약조건 |
|------|------|----------|
| id | BIGINT | PK, AUTO_INCREMENT |
| book_id | BIGINT | FK (books) |
| user_id | BIGINT | FK (users) |
| loan_date | DATETIME | NOT NULL |
| due_date | DATETIME | NOT NULL |
| return_date | DATETIME | |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'RENTED' |

## 프로젝트 구조

```
src/main/java/com/hyunyoung/libraryapi/
├── config/           # SecurityConfig, SwaggerConfig
├── controller/       # REST 컨트롤러
├── dto/
│   ├── request/      # 요청 DTO
│   └── response/     # 응답 DTO
├── entity/           # JPA 엔티티
├── enums/            # BookStatus, LoanStatus, Authority
├── exception/        # 커스텀 예외, GlobalExceptionHandler
├── repository/       # JPA 리포지토리
└── service/          # 비즈니스 로직
```

## 실행 방법

### 사전 준비

- Java 17+
- MySQL 8.0+

### MySQL 데이터베이스 생성

```sql
CREATE DATABASE library;
```

### application-example.yaml -> application.yaml 파일명 변경 후 설정

```yaml
spring:
  application:
    name: libraryapi
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/library}
    username: ${SPRING_DATASOURCE_USERNAME:사용자명}
    password: ${SPRING_DATASOURCE_PASSWORD:비밀번호}
  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:create}
    show-sql: true

jwt:
  secret: ${JWT_SECRET:JWT 시크릿 키}
  expiration: ${JWT_EXPIRATION:86400000}
```

### 빌드 및 실행

```bash
./gradlew bootRun
```