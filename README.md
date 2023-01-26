# Spring Board API

## 프로젝트 계기
차별화된 기능을 구현하기 보다는 테스트 코드, API 문서를 작성, 배포와 성능 개선이 목표

## Technology Stack
* Gradle 7.6
* Spring boot 2.5.9
* Java
* Spring Data Jpa
* Spring Rest Docs
* JUnit 5
* MySQL

## ERD
<img width="948" alt="스크린샷 2023-01-18 오후 9 15 51" src="https://user-images.githubusercontent.com/97818720/213180620-4de803f7-0bf8-49ef-a68a-3a2154e0438b.png">

## API 문서
[Spring Rest Docs로 구현한 API 명세서](http://15.165.82.137:8080/docs/index.html)

## 성능 개선 (진행 중)
* 자신이 작성한 게시글 조회 쿼리 2개 -> 1개

* 게시글 전체 조회 쿼리 3개 -> 1개

* 단건 조회 쿼리 4개 -> 1개

* 게시글 검색 쿼리 2개 -> 1개

* 신고된 게시글 조회 쿼리 2개 -> 1개

* 게시글 좋아요 취소 쿼리 4개 -> 2개

* 게시글 삭제 쿼리 N + 1 -> 해결

* 댓글 삭제 N + 1 -> 해결

## 문제 해결

[no - session](https://hyukk.tistory.com/15)  

[데이터 삭제 N + 1](https://hyukk.tistory.com/16)