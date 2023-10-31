# 유저 회원 가입

## 기본 절차

### 회원가입 화면

1. 회원가입 의사 질의(client)
   - 기본 가입 : email, password 
   - 소셜 가입 : email
2. user_auth에 유저 생성

### 프로필 정보 생성

1. UserInfo가 존재하는지 확인 
2. 닉네임 유효성 검사 
3. 한줄소개 작성(client)
4. 관심 카테고리 선택(client)
5. user_info에 유저 생성


## 회원가입은 했으나 프로필 생성이 안되었을 경우

1. 로그인
2. UserInfo가 존재하는지 확인
3. 존재하지 않으면 프로필 생성 화면 이동

---

# 팔로우

## 팔로우 추천 화면

1. 추천 유저 목록 30명을 가져온다.
2. 팔로우를 누르면 user_relation에 relation_type을 FOLLOW로 추가한다.
3. 블랙리스트로 분류하면 user_relation에 relation_type을 BLACK_LIST로 추가한다

### 추천 유저 가져오기

1. user_category 목록을 가져온다.
2. user_relation 목록을 가져온다.
3. user_category 목록이 1개 이상 겹치면서 user_relation에 해당하지 않은 유저 목록을 가져온다.
4. 3의 결과가 2명 이상이면 공통된 관심 카테고리를 제외한 나머지 카테고리의 개수가 많으면서, 작성한 글이 많은 순으로 정렬한다.
5. 3-4의 결과가 30명보다 적을 경우, 작성글이 많은 순으로 부족한만큼 가져온다.
