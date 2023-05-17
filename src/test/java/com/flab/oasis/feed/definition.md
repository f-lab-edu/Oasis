# 기능 정의

## 책 찾기(ehcache 사용)

* 제목 검색
* 저자 검색
* 번역자 검색
* 출판사 검색

## 피드 작성(POST)

1. 테이블에서 uid의 MAX(feed_id)를 검색
   * 결과값이 존재하지 않으면 0을 신규 feed의 id로 부여
   * 존해하면 (MAX + 1)을 신규 feed의 id로 부여
2. write_date는 'now()', feed_like는 0

## 피드 수정(PATCH)

1. feed_id를 통해 피드 수정

## 피드 삭제(DELETE)

1. feed_id를 통해 피드 삭제 - cascade를 통해 해당 피드의 댓글도 모두 삭제된다.

## 피드 보기(GET)

1. redis에서 follow하는 uid 정보들을 가져온다.
2. redis에서 1에 해당하는 uid들의 feed를 가져온다.
3. 2의 데이터를 합치고 write_date 순으로 정렬한다.

### redis

#### feed::uid

* structure: sorted set
* field: feed_id
* value: feed data

#### relation::{follow | black_list}

* structure: hash
* field: uid
* value: all relation_user