# Oasis

책을 읽고 생각을 공유하는 SNS.

개발자에도 인문학적 감성이 필요하다.

## Project Management

### Branch Strategy

단독으로 진행하는 프로젝트이므로 [Github-Flow](https://docs.github.com/ko/get-started/quickstart/github-flow)에 기반한 단순한 구성으로 진행합니다.

* main : 배포용 브랜치.
* {분류}/{내용}: fork repository에서 사용하는 개발용 브랜치.<br>Pull Request를 통해 코드 리뷰 후 origin.main 브랜치로 merge 한다.<br><br>- 분류 : hotfix(버그 수정), feature(기능 개발)<br>- 내용 : 작업 내용

### Commit Convention

* Type
  * feat : 새로운 기능 추가
  * fix : 버그 수정
  * docs : 문서 수정
  * style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
  * refactor : 코드 리펙토링
  * test : 테스트 코드, 리펙토링 테스트 코드 추가
  * chore : 빌드 업무 수정, 패키지 매니저 수정
* Subject : 50자를 넘지 않는 선에서 요점만 간결하게 작성
* Body : 한 줄당 72자를 넘지 않고, 무엇을 어떻게 변경했는지 작성한다.

### Pull Request Convention

* Subject : ***"[분류] 제목"*** 형식. 단문으로 간결하게 작성한다.
* 작업 내용 : 작업한 내용 설명과 추가 전달 사항 등을 작성한다.
* 이슈 번호 : PR과 관련된 이슈 번호를 작성한다.

