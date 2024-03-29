# Spring Board API

## 프로젝트 계기
차별화된 기능을 구현하기 보다는 기존에 해보지 않았던 테스트 코드, API 문서 작성, 배포, 성능 개선이 목표

## Technology Stack
* Gradle 7.6
* Spring boot 2.5.9
* Java
* Spring Data Jpa
* Spring Rest Docs
* JUnit 5
* MySQL

## 서비스 아키텍처
<img src="https://user-images.githubusercontent.com/97818720/214824054-1f00f696-f1b2-4434-bfaa-d4910d9debb9.png">


## ERD
<img width="948" alt="스크린샷 2023-01-18 오후 9 15 51" src="https://user-images.githubusercontent.com/97818720/218247969-2a3ff475-2cb3-4b00-9ea5-46c25210d054.png">

## 성능 개선
* 자신이 작성한 게시글 조회 쿼리 2개 -> 1개

* 게시글 전체 조회 쿼리 3개 -> 1개

* 단건 조회 쿼리 4개 -> 1개

* 게시글 검색 쿼리 2개 -> 1개

* 신고된 게시글 조회 쿼리 2개 -> 1개

* 게시글 좋아요 취소 쿼리 4개 -> 2개

* 게시글 삭제 N + 1 -> 해결

* 댓글 삭제 N + 1 -> 해결

* 회원 삭제 N + 1 -> 해결

## 문제 해결

[no - session](https://hyukk.tistory.com/15)  

[데이터 삭제 N + 1](https://hyukk.tistory.com/16)
