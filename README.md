# SimpleTodo
간단한 TODO 어플리케이션.
이 프로젝트는 간단한 할일목록관리 웹어플리케이션이며, 과제수행을 위하여 작성하는 것입니다.

### [과제요구사항](requirements.md)

### 문제해결전략
* 개발언어: Java + SpringBoot
* 데이터베이스: Redis

* 자료구조
  - 요구사항에 따르면, 할일목록 관리를 위한 CRUD 기능이 필요하며
  - 특이사항으로는 할일 간에 참조를 할 수 있어야 한다는 것이다.
  - 참조 당한(?) 할일은 참조하는 할일이 완료되기 전에는 완료할 수 없다.
  - 따라서 참조하는 입장에서는 당연히 참조 대상 할일의 목록을 가지고 있어야 하겠지만
  - 참조 당하는 입장에서도 참조하고 있는 할일의 목록을 관리하고 있어야 한다.
  - 상호간에 링크를 가지고 있어야 하는 것이다.

* Redis 데이터 구조 설계
  - Redis는 key-value 구조의 데이터를 보관하는 메모리 데이터베이스이다.
  - Todo의 기본 데이터는 할일 ID를 키로하는 hash에 저장을 한다. (ID, 할일, 생성일자, 수정일자, 완료여부) 예) key = "todos:33"
  - hash는 key-map 구조의 데이터 저장소이다.
  - ID 생성을 위하여 key-value 구조에 최종 ID값을 보관한다. 예) key = "todos:lastId"
  - 목록 관리를 위하여 sorted set에 ID를 저장한다. (ID순으로 목록을 관리한다) 예) key = "todos"
  - Sorted set은 key-set 구조의 데이터 저장소인데, 순서정렬을 할 수 있도록 별도의 수치 값을 함께 지정한다. 여기서는 ID값을 바로 수치화하여 정렬값으로 지정한다.
  - 참조하는 할일 입장에서, sorted set에 자신의 ID를 key로 하는 참조 할일 목록을 관리한다. 예) key = "todos:refs:33"
  - 참조당하는 할일 입장에서, sorted set에 자신의 ID를 key로 하는 피참조 할일 목록을 관리한다. 예) key = "todos:subs:22"
 
* 어플리케이션 구조
  - 비지니스 로직: RESTful API로 구현
  - 데이터베이스: Redis 메모리 DB
  - 화면: 스프링에서 지원하고 있는 Thymeleaf 서버사이드 템플릿을 이용하여 구현하되 API 호출은 서버사이드와 클라이언트사이드 양쪽에서 병행

* 참고관계 관련 고려할 사항
  - 요구사항에는 참조 받는 할일을 완료하기 위해서는 참조하는 할일이 완료되어야만 한다고 되어 있지만 이외에도 고려해야할 사항이 많이 발생함
  - 새로운 할일을 등록하는 경우
    . 피참조 할일이 완료되어 있다면 완료를 취소시켜야 한다.(생성되는 할일은 미완료이기 때문에 상위 할일도 완료일 수 없음)
  - 할일을 수정하는 경우
    . 참조목록이 수정되면 기존의 링크 구조도 찾아서 지운 후 다시 설정할 필요가 있다
    . 참조관계가 끊어진다면 상위 할일의 피참조 목록에서 해당 ID를 지워주기만 하며 된다
    . 참조관계가 새로 발생한다면 상위 할일의 피참조 목록에 해당 ID를 추가해주고 미완료 상태가 되어야 한다
  - 하위 할일의 완료상태를 취소하는 경우 상위 할일의 완료상태도 취소하여야 한다
  - 완료 취소시 상위 할일이 있는 경우 에러 메시지 노출보다는 자동으로 같이 취소시키는 것이 좋을 것으로 판단(선택의 문제)
  

### 빌드

### 실행
