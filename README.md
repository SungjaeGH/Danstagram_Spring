# Danstagram_Spring
춤을 좋아하는 사람들을 위한 커뮤니티 웹 사이트 개발

***
## 목차
1. [서비스 목적](#서비스-목적)
2. [주요 기능](#주요-기능)
3. [Used Stacks](#used-stacks)
4. [API](#api)
5. [Package Structure](#package-structure)
6. [ERD](#erd)
7. [Commit Convention](#commit-convention)

***
## 서비스 목적
* 최근에 Instagram, Tiktok, Youtube 등의 소셜 미디어에서 <u>인기 댄스 챌린지와 같은 춤 컨텐츠</u>가 많은 사용자의 관심을 받고 있음
* 춤과 관련된 여러 정보를 제공함으로써, <u>Instagram을 기반으로 한 춤 커뮤니티 웹</u>을 만들고자 함

*** 
## 주요 기능
* Instagram에서 제공하고 있는 기본 기능
  * feed
  * story
  * DM
* 춤 연습장 위치 제공 및 예약 기능
* 공연 정보 제공 기능

***
## Used Stacks
<div>
    <span style="font-size: 13pt">Language</span>
    <div width="100%">
        <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
    </div>
    <span style="font-size: 13pt">Framework</span>
    <div width="100%">
        <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
    </div>
    <span style="font-size: 13pt">Database</span>
    <div width="100%">
        <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
        <img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">
    </div>
    <span style="font-size: 13pt">Security & Authentication</span>
    <div width="100%">
        <img src="https://img.shields.io/badge/spring security-6DB33F?style=for-the-badge&logo=spring security&logoColor=white">
        <img src="https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=json web tokens&logoColor=white">
        <img src="https://img.shields.io/badge/oauth2-EB5424?style=for-the-badge&logo=oauth2&logoColor=white">
    </div>
    <span style="font-size: 13pt">Data Access</span>
    <div width="100%">
        <img src="https://img.shields.io/badge/spring data jpa-6DB33F?style=for-the-badge&logo=spring boot&logoColor=white">
        <img src="https://img.shields.io/badge/querydsl-4479A1?style=for-the-badge&logo=querydsl&logoColor=white">
    </div>
    <span style="font-size: 13pt">RTC</span>
    <div width="100%">
        <img src="https://img.shields.io/badge/websocket-EB5424?style=for-the-badge&logo=websocket&logoColor=white">
        <img src="https://img.shields.io/badge/stomp-006340?style=for-the-badge&logo=stomp&logoColor=white">
    </div>
    <span style="font-size: 13pt">Search Engine</span>
    <div width="100%">
        <img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white">
    </div>
    <span style="font-size: 13pt">Communication</span>
    <div width="100%">
        <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
        <img src="https://img.shields.io/badge/google sheets-34A853?style=for-the-badge&logo=google sheets&logoColor=white">
        <img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white">
    </div>
</div>

***
## API
> [댄스타그램 API (Swagger UI)]()

***
## Package Structure
```text
│
└─src
    └─main
        ├─java
        │  └─com
        │      └─project
        │          └─danstagram
        │              │  DanstagramApplication.java
        │              │
        │              ├─domain
        │              │  ├─auth
        │              │  │  ├─controller
        │              │  │  ├─entity
        │              │  │  ├─handler
        │              │  │  ├─repository
        │              │  │  └─service
        │              │  │
        │              │  ├─comment
        │              │  │  ├─controller
        │              │  │  ├─dto
        │              │  │  ├─entity
        │              │  │  ├─exception
        │              │  │  ├─repository
        │              │  │  └─service
        │              │  │
        │              │  ├─dm
        │              │  │  ├─controller
        │              │  │  ├─dto
        │              │  │  ├─entity
        │              │  │  ├─exception
        │              │  │  ├─repository
        │              │  │  └─service
        │              │  │
        │              │  ├─follow
        │              │  │  ├─controller
        │              │  │  ├─dto
        │              │  │  ├─entity
        │              │  │  ├─repository
        │              │  │  └─service
        │              │  │
        │              │  ├─member
        │              │  │  ├─controller
        │              │  │  ├─dto
        │              │  │  ├─entity
        │              │  │  ├─repository
        │              │  │  └─service
        │              │  │
        │              │  ├─post
        │              │  │  ├─controller
        │              │  │  ├─dto
        │              │  │  ├─entity
        │              │  │  ├─exception
        │              │  │  ├─repository
        │              │  │  └─service
        │              │  │
        │              │  └─search
        │              │      ├─controller
        │              │      ├─dto
        │              │      ├─entity
        │              │      ├─repository
        │              │      └─service
        │              │
        │              └─global
        │                  ├─auth
        │                  │  ├─jwt
        │                  │  └─oauth2
        │                  ├─config
        │                  ├─file
        │                  ├─response
        │                  ├─scroll
        │                  └─time
        │
        └─resources
            │  application.properties
            │
            ├─elastic
            │  │  es-setting.json
            │  │
            │  ├─hashtag
            │  ├─member
            │  └─place
            ├─static
            └─templates
```

***
## ERD
> [댄스타그램 ERD (ERD Cloud)](https://www.erdcloud.com/d/dBGyg8uznH2qpLnhg)

***
## Commit Convention
### Type
* feat: 기능 추가, 삭제, 변경
* fix: 버그 수정
* refactor: 코드 리팩토링
* style: 코드 형식, 정렬 등의 변경. 동작에 영향 x
* test: 테스트 코드 추가, 삭제 변경
* docs: 문서 추가 삭제 변경. 코드 수정 x
* etc: 위에 해당하지 않는 모든 변경

### Description
* 한 줄당 72자 이내로 작성
* 최대한 상세히 작성(why - what)

### Reference
> [Udacity Git Commit Message Style Guide](https://udacity.github.io/git-styleguide/, "git style guide")