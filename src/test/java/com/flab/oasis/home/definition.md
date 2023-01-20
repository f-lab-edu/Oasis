# 기능 정의

## Home API

> 유저의 관심 카테고리에 해당하는 책 추천 정보를 제공한다.

* Input : UID_String, SuggestionType_String[recommend/newBook/bestSeller]
* Output : BookSuggestionList_List&lt;Book&gt;

### Process

> recommend/newBook/bestSeller 중 요청된 타입의 book 데이터를 return 한다.

1. DB의 USER_CATEGORY에서 요청 UID에 해당하는 데이터를 Select한다. 
   * 조회된 데이터는 캐싱된다.
2. Redis에서 요청 책 추천 타입에 해당하는 데이터를 조회한다.
   1. Redis 조회 결과가 Empty일 경우, DB의 BOOK_SUGGESTION 관련 데이터를 모두 가져온다.
   2. 2-1의 데이터를 Redis에 맞게 Parsing 한다.
   3. 2-2의 데이터를 Redis에 Push 한다.
3. 2의 데이터에서 user의 category에 해당하는 데이터를 찾아 response 한다.
   * 1의 데이터가 Empty면 요청 책 추천 타입에 해당하는 모든 데이터를 response 한다.

#### Redis 구조

Structure Type: Hash<br>
Key: home<br>
Field: SuggestionType[recommend/newBook/bestSeller]<br>
Value: JsonArray(<br>
&nbsp;&nbsp;&nbsp;&nbsp;categoryId: JsonArray(<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;title, author, publisher, translator, publishDate, imageUrl, description, categoryName<br>
&nbsp;&nbsp;&nbsp;&nbsp;)<br>
)