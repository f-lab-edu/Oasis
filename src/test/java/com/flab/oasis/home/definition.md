# 기능 정의

## HomeController

> 유저의 관심 카테고리에 해당하는 책 추천 정보를 제공한다.

* Input Value들이 null 혹은 Empty라면 에러 코드를 response 한다.
* Input : UID, SuggestionType[recommend/newBook/bestSeller]
* Output : BookSuggestionList;

## HomeService

1. DB의 USER_CATEGORY에서 요청 UID에 해당하는 데이터를 Select 한다.
   * 조회된 데이터는 캐싱된다.
   * input : UID
   * output : UserCategoryList
2. Redis에서 요청 책 추천 타입에 해당하는 데이터를 조회한다.
   * 조회 결과가 Empty일 경우, DB에서 Book Suggestion 데이터를 가져와 Redis에 반영한다.
   * input : SuggestionType[recommend/newBook/bestSeller]
   * output : -
4. 2의 데이터에서 user의 category에 해당하는 데이터를 찾아 response 한다.
   * 1의 데이터가 Empty면 요청 책 추천 타입에 해당하는 모든 데이터를 response 한다.
   * input : user category
   * output : BookList

### RedisUtil

#### insertBookSuggestionDBToRedis

1. DB의 BOOK_SUGGESTION 관련 데이터를 모두 가져온다.
   * input : -
   * output : BookSuggestionList
2. 1의 데이터를 Redis에 사용할 형식에 맞게 Parsing 한다.
   * input : BookSuggestionList
   * output : JsonArrayStringList
3. 2의 데이터를 Redis에 Push 한다.

#### Redis 구조

Structure Type: Hash<br>
Key: home<br>
Field: SuggestionType[recommend/newBook/bestSeller]<br>
Value: JsonArray(<br>
&nbsp;&nbsp;&nbsp;&nbsp;categoryId: JsonArray(<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;title, author, publisher, translator, publishDate, imageUrl, description, categoryName<br>
&nbsp;&nbsp;&nbsp;&nbsp;)<br>
)