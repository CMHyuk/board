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
<img width="948" alt="스크린샷 2023-01-18 오후 9 15 51" src="https://user-images.githubusercontent.com/97818720/216584014-86c47a28-05a9-467e-82b6-dd74afd572c0.png">

## API 문서
[Spring Rest Docs로 구현한 API 명세서](http://post.o-r.kr/docs/index.html)

## 관리자 페이지
[관리자 페이지](http://post.o-r.kr/admin)  
아이디 : 123  
비밀번호 : 123

## 성능 개선
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