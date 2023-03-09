# jpa


# 참고 도서
* 객체지향의 사실과 오해 (조영호 저)
* 오브젝트 (조용호 저)

# jpa

## 자바 ORM 표준 JPA 프로그래밍 - 기본편 강의 학습 내용

```
# JPA의 중요한 2가지
1. 객체와 관계형 데이터베이스 매핑 (Object Relational Mapping (ORM))
2. 영속성 컨텍스트
```

### 영속성 컨테이너 학습

- 엔티티 매니저 팩토리는 하나만 생성하여 애플리케이션에서 공유
- 엔티티 매니저는 쓰레드간 공유 X (사용 후 폐기)

```
엔티티의 생명 주기 (life cycle)
1. 비영속(new/transient) : 영속성 컨텍스트와 전혀 관계 없는 새로우 상태 -> EntityManager.persist(object) X
2. 영속(managed) : 영속성 컨텍스트에 관리되는 상태 -> EntityManager.persist(object)
3. 준영속(detached) : 영속성 컨텍스트에 저장되었따가 분리되 상태 -> detach
4. 삭제(removed) : 삭제된 상태 -> remove
```

* 영속성 컨테이너의 이점
	* 애플리케이션 <> JPA Interface <> DB
	* 엔티티 조회 시 1차 캐시를 제공
		* 영속성 컨테이너에 데이터가 존재하면 DB Query X, 컨테이너 데이터로 제공
		* 반복 가능한 읽기(Repeatable read) 등급의 트랜잭션 격리 수준을 애플리케이션 차원에서 제공
	* 트랜잭션을 지원하는 쓰기 지연  (transactional write-behind)
		* 커밋 시점에 생성된 쿼리 일괄 수행
	* 변경 감지(Dirty Checking)
		* 커밋 시점에 변경 체크 후 변경된 내용 존재 시 쿼리
	* 지연 로딩(Lazy Loading)
		* 실제 객체를 참조하는 시점에 쿼리
* 플러시(flush)
	* 영속성 컨텍스트를 비우지 않음
	* 영속성 컨텍스트의 변경 내용을 데이터 베이스에 동기화
	* 따라서 트랜잭션 작업 단위가 중요!
	* JPQL 실행 시 적재된 SQL 수행 후 조회를 위해 flush 호출됨
	* FlushModeType
		* AUTO : 커밋이나 쿼리를 실행할 때 플러시 (default)
		* COMMIT : 커밋할 때만 플러시
* 준영속 상태(detach)
	* 영속 상태의 엔티티가 영속성 컨텍스트에서 분리
	   따라서, 커밋 시점에서 이루어지는 행위를 하지 않음
		```
		1. EntityManager.detach(object);
		2. EntityManager.clear(); //영속성 컨텍스트를 완전히 초기화
		3. EntityManager.close(); //영속성 컨텍스트를 종료
		```

## 엔티티 매핑

* 객체와 테이블 매핑
	* @Entity
		* JPA가 관리하는 객체
		* 주의 사항
			1. 기본 생성자 필수(파라미터가 없는 public, protected 생성자)
			2. final 클래스, enum, interface, inner 클래스 사용 불가
			3. 저장할 필드에 final 사용 불가
		* 속성 (name)
			* 기본값 : 클래스 이름 그대로 사용
			* 같은 클래스 이름이 없으면 가급적 기본값 사용
	* @Table
		* 엔티티와 데이터 베이스의 매핑할 테이블을 지정

     | 속성                   | 기능                              | 기본값             |
     | ---------------------- | --------------------------------- | ------------------ |
     | name                   | 매핑할 테이블 이름                | 엔티티 이름을 사용 |
     | catalog                | 데이터베이스 catalog 매핑         |                    |
     | schema                 | 데이터베이스 schema 매핑          |                    |
     | uniqueConstraints(DDL) | DDL 생성 시 유니크 제약 조건 생성 |                    |
    
* 데이터베이스 스키마 자동 생성
	* DDL을 애플리케이션 실행 시점에 자동 생성
	* 테이블 중심 -> 객체 중심
	* 데이터베이스 방언(dialect)을 활용해서 데이터베이스에 맞는 적절한 DDL 생성
	* 생성된 DDL은 개발 장비에서만 사용, 운영서버에서는 사용하지 않거나 적절히 다듬은 후 사용
	* 운영 장비에는 절대로 create, create-drop, update 사용하지 않도록 주의 필요 !!
		* 개발 초기 단계는 create 또는 update
		* 테스트 서버는 update 또는 validate
		* 스테이징과 운영 서버는 validate 또는 none
	* property name="hibernate.hbm2ddl.auto" value="{옵션}" 
	  
	  | 옵션        | 설명                                          |
	  | ----------- | --------------------------------------------- |
	  | create      | 기존 테이블 삭제 후 다시 생성 (DROP + CREATE) |
	  | create-drop | create와 같으나 종료시점에 테이블 DROP        |
	  | update      | 변경분만 반영 (운영 DB에는 사용하면 안됨!!) <br>추가만 제공 삭제는 제공 안함 |
	  | validate    | 엔티티와 테이블이 정상 매핑되었는지 확인      |
	  | none        | 사용하지 않음                                 |

* 기본 키 매핑
  ```
	기본키 제약 조건 : null 아님, 유일, 변하면 안된다.
	미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 
	대리키(대체키)를 사용하자.
	예를 들어, 주민등록번호도 기본 키로 적절하지 않다.
	
	권장 : Long 형 + 대체키 + 키 생성 전략 사용
	```
	* @Id
		* 직접 할당
	* @GeneratedValue
		* 자동 생성
		* strategy
			* IDENTITY : 데이터베이스에 위임
				* Mysql, PostgreSQL, SQL Server, DB2에서 사용
				   ex) Mysql의 AUTO_INCREMENT
				* JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행
				* AUTO_INCREMENT는 데이터베이스에 INSERT SQL을 실행한 이 후에 ID 값을 획득
				* IDENTITY 전략은 persist(object) 시점에 즉시 INSERT SQL을 실행하고 DB에서 ID 값을 획득
			* SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용
				* 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터 베이스 오브젝트(Oracle의 시퀀스)
				* Oracle, PostgreSQL, DB2, H2에서 사용
				* @SequenceGenerator(name = "{시퀀스명}", sequenceName="{매핑할 데이터베이스 시퀀스명}", inintalValue = {초기값}, allocationSize={증가값})
					* 기본 : 초기값 1, 증가값 50
					  시퀀스 획득을 미리 확보하여 메모리상에서 편하게 사용하여 성능 이슈 해결 (성능 최적화)
					  1 ~ 51 까지 사용 후 51이 되는 시점에 다시 시퀀스 획득
				* @TableGenerator(name = "{시퀀스명}", table="{시퀀스 테이블 명}", pkColumnValue="{시퀀스 컬럼명}")
				* persist(object) 시점에 시퀀스 획득 SQL 실행
		* generator
			* 데이터베이스 시퀀스 제너레이터 시퀀스명으로 지정
* 필드와 컬럼 매핑
	* @Column
	  
	  | 속성                   | 설명                                                                                                                                                                                                      | 기본값                         |
	  | ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------ |
	  | name                   | 필드와 매핑할 테이블의 컬럼 이름                                                                                                                                                                          | 객체의 필드 이름               |
	  | insertable, updateable | 등록, 변경 가능 여부                                                                                                                                                                                      | TRUE                           |
	  | nullable(DDL)          | null 값의 허용 여부                                                                                                                                                                                       |                                |
	  | unique(DDL)            | @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제약조건을 지정                                                                                                                               |                                |
	  | columnDefinition(DDL)  | 데이터베이스 컬럼 정보를 직접 줄 수 있다.<br> ex) varchar(100) default 'EMPTY'                                                                                                                            | 필드의 자바 타입과 방언 정보를 |
	  | length(DDL)            | 문자 길이 제약 조건 String 타입에만 사용                                                                                                                                                                  | 255                            |
	  | precision, scale(DDL)  | BigDecimal 타입에서 사용한다.(BigInteger도 사용 가능)<br> precision 소수점을 포함한 전체 자리수<br> scale 소수 자리수<br> 참고로 double, float 타입에는 적용되지 않음. 정밀한 소수를 다루어야할 때만 사용 | precision=19<br> scale=2       | 
	* @Temporal
		* 날짜 타입 매핑
		* java.util.Date 또는 java.util.Calendar 매핑할 때 사용
		* LocalDate, LocalDateTime 사용할 때는 생략 가능 (최신 하이버네이트 지원)
	* @Enumerated
		* enum 타입 매핑
		* defulat ORDINAL => enum index data
	* @Lob
		* BLOB, CLOB 매핑
		* 지정할 수 있는 속성 없음
		* 매핑하는 필드 타입이 문자면 CLOB : String, char[], java.sql.CLBO
		* 나머지는 BLOB 매핑: byte[], java.sql.BLOB
	* @Transient
		* 특정 필드를 컬럼 매핑 하지 않음
		* 데이터베이스에 저장 X, 조회 X
		* 메모리에서 임시로 사용할 때 지정

* 연관관계 매핑
	```
	객체와 테이블 연관관계의 차이를 이해
	객체의 참조와 테이블의 외래 키를 매핑
	
	1. 방향(Direction): 단방향, 양방향
	2. 다중성(Multiplicity): 일대일(1:1), 일대다(1:N), 다대일(N:1), 다대다(N:M)
	3. 연관관계의 주인(Owner): 객체 양방향 연관관계는 관리 주인 필요
	```
	* 양방향 연관관계와 연관관계의 주인
		* mappedBy 
		   : 객체와 테이블의 관계를 맺는 차이
		   예를 들어, 회원과 팀이 있다.
		   * 객체 연관관계 = 2 개
			   * 회원 -> 팀 : 연관관계 1개 (단방향)
			   * 팀 -> 회원 : 연관관계 1개 (단방향)
			   * 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 함
		   * 테이블 연관 관계 = 1개
			   * 회원 <-> 팀 : 연관관계 1개 (양방향)
		* 연관관계의 주인(Owner)
			* 양방향 매핑 규칙
			* 객체의 두 관계중 하나를 연관관계의 주인으로 지정
			* 연관관계의 주인만이 외래 키를 관리 (등록, 수정)
			* 주인이 아닌 쪽은 읽기만 가능
			* 주인은 mappedBy 속성 사용 X
			* 주인이 아니면 mappedBy 속성으로 주인 지정
			* 외래 키가 있는 곳을 주인으로 정하기
				* 회원과 팀을 기준으로 회원이 팀의 외래 키를 가지고 있으므로 주인으로 지정
				* 따라서 팀의 회원 목록은 읽기만 하는 참조 매핑

	* @ManyToOne
		* 외래 키가 있는 곳을 주인으로 설정 !
		  
			| 속성         | 설명                                                                                                                      | 기본값                                                   |
			| ------------ | ------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------- |
			| optional     | false로 설정하면 연관된 엔티티가 항상 있어야 함                                                                           | TRUE                                                     |
			| fetch        | 글로벌 패치 전략을 설정                                                                                                   | @ManyToOne=FetchType.EAGER<br> @OneToMany=FetchType.LAZY |
			| cascade      | 영속성 전이 기능을 사용                                                                                                   |                                                          |
			| targetEntity | 연관된 엔티티의 타입 정보를 설정<br> 컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있음<br> 이 기능은 거의 사용하지 않음 |                                                          |

	* @OneToMany
		* 일대다 단방향 매핑보다는 다대일 양방향 매핑을 사용하도록 설계하는것이 깔끔
		* 반대편은 mappedBy 지정
		  
		  | 속성         | 설명                        | 기본값                                                  |
		  | ------------ | --------------------------- | ------------------------------------------------------- |
		  | mappedBy     | 연관관계의 주인 필드를 선택 |                                                         |
		  | fetch        | 글로벌 패치 전략을 설정     | @ManyToOn=FetchType.EAGER<br> @OneToMany=FetchType.LAZY |
		  | cascade      | 영속성 전이 기능을 사용     |                                                         |
		  | targetEntity | 연관된 엔티티의 타입 정보를 설정<br> 컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있음<br> 이 기능은 거의 사용하지 않음 |                            |                                                         |

	* @OneToOne
		* 주 테이블에 외래키
			* 주 객체가 대상 객체의 참조를 가지는 것 처럼 주 테이블에 외래 키를 두고 대상 테이블을 찾음
			* JPA 매핑 편리
			* 주 테이블만 조회해도 대상 테이블에 데이터 확인 가능
			* 값이 없으면 외래 키에 null 허용
		* 대상 테이블에 외래 키
			* 대상 테이블에 외래 키가 존재
			* 전통적인 데이터베이스 개발자 선호
			* 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지
			* 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩
	* @ManyToMany
		* 데이터베이스는 정규화된 테이블 2개로 다대다 관계 표현 불가
		* 객체는 컬렉션을 사용해서 객체 2개로 다대다 관계 가능
		* 연결 테이블을 추가해서 다대다 => 일대다, 다대일 관계로 풀어야함
		* 연결 테이블에는 단순히 연결만 하지 않고 주문 시간, 수량 같은 추가적인 데이터가 존재 (따라서, 다대다는 사용하지 않는게..)
		* 연결 테이블용 엔티티를 추가해서 일대다, 다대일 관계로 설계를 가져가는게 맞음
	* @JoinTable
		* 연결 테이블을 지정 가능
	* @JoinColumn
	  
		| 속성                                                                            | 설명                                    | 기본값                                        |
		| ------------------------------------------------------------------------------- | --------------------------------------- | --------------------------------------------- |
		| name                                                                            | 매핑할 외래 키 이름                     | 필드명 + _ + 참조하는 테이블의 기본 키 컬럼명 |
		| referencedColumnName                                                            | 외래 키가 참조하는 대상 테이블의 컬럼명 | 참조하는 테이블의 기본 키 컬럼명              |
		| foreignKey(DDL)                                                                 | 외래 키 제약조건을 직접 지정            |                                               |
		| unique<br> nullable<br> insertable<br> updatable<br> columnDefinition<br> table | @Column 속성과 동일                     |                                               |
* 고급 매핑
	1. 상속관계 매핑
		* 관계형 데이터베이스는 상속 관계가 없음
		* 슈퍼 타입, 서브 타입 관계라는 모델링 기법이 객체 상속과 유사
		* 상속관계 매핑 : 객체의 상속 구조와 DB의 슈퍼/서브 타입 관계를 매핑
		* 슈퍼/서브 타입 논리 모델을 실제 물리 모델로 구현하는 방법
			* 각각 테이블로 변환 -> 조인 전략
			* 통합 테이블로 변환 -> 단일 테이블 전략
			* 서브 타입 테이블로 변환 -> 구현 클래스마다 테이블 전략
		* 주요 어노테이션
			* @Inheritance(strategy=InheritanceType.XXX) -> 부모 엔티티에 선언
				* JOINED : 조인 전략 -> 객체 지향 및 설계가 훌륭하게 나옴
					* 장점
						* 테이블 정규화
						* 외래 키 참조 무결성 제약조건 활용 가능
						* 저장공간 효월화
					* 단점
						* 조회 시 조인을 많이 사용 -> 성능 저하
						* 조회 쿼리가 복잡
						* 데이터 저장 시 INSERT SQL 2번 호출
				* SINGLE_TABLE : 단일 테이블 전략
					* @DiscriminatorColumn
					   부모 엔티티의 자식 엔티티 식별 컬럼이 필수로 추가 됨. 
					   운영 시 DB에서 확인할 수 있도록
					* 장점
						* 조인이 필요 없으므로 조회 성능 빠름
						* 조회 쿼리가 단순
					* 단점
						* 자식 엔티티가 매핑한 컬럼은 모두 NULL 허용
						* 단일 테이블에 모든것을 저장하므로 테이블이 커질 수 있는 상황에 따라서 조회 성능이 느려질 수 있음
				* TABLE_PER_CLASS : 구현 클래스마다 테이블 전략
					* 이 전략은 데이터베이스 설계자와 ORM 전문가 둘 다 추천하지 않음
					* 장점
						* 서브 타입을 명확하게 구분해서 처리할 때는 효과적
						* NOT NULL 제약 조건 사용 가능
					* 단점
						* 여러 자식 테이블을 함께 조회할 때 성능이 느림 (UNION SQL)
						* 자식 테이블을 통합해서 쿼리하기 어려움
			* @DiscriminatorColumn(name="DTYPE") -> 부모 엔티티의 자식 엔티티 식별 컬럼
			* @DiscriminatorValue("XXX") -> 자식 엔티티에 설정 (부모 엔티티의 식별 컬럼에 저장되는 값)
			* @MappedSuperclass
				* 공통 매핑 정보가 필요할 때 사용, ex) createdBy, createDate, lasyModifedBy, lastModifiedDate, ...
				* 상속관계 매핑 X
				* 엔티티 X, 테이블과 매핑 X
				* 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
				* 조회, 검색 불가 => find(Object) 불가
				* 직접 생성해서 사용할 일이 없으므로 추상 클래스 권장
				* 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할 (테이블 관계 X)
				* 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통으로 적용하는 정보를 모을 때 사용
				* JPA 에서는 @Entity 클래스는 @Entity나 @MappedSuperclass로 지정한 클래스만 상속 가능
* 프록시와 연관관계 관리 
	* find()
		* 데이터베이스를 통해서 실제 엔티티 객체 조회
	* getReference()
		* 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회
			* 실제 클래스를 상속 받아 만들어짐
			* 프록시 객체는 실제 객체의 참조(target)을 보관
			* 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드 호출
			   실제 객체가 없을 경우 영속성 컨텍스트로 초기화 요청, 이 때 없으면 DB 조회 후 엔티티 설정 및 참조
			* 타입 체크는 instance of 로 진행
			* 영속성 컨텍스트에 존재하면 실제 엔티티 반환 (프록시 X)
			* 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 예외 발생
	* 프록시 확인
		* 프록시 인스턴스의 초기화 여부 확인
		   emf.getPersistenceUnitUtil.isLoaded(Object Entity);
		* 프록시 클래스 확인 방법
		   entity.getClass().getName();
		* 프록시 강제 초기화
		   org.hibernate.Hibernate.initalize(entity);
		* JPA 표준에는 강제 초기화 없음
		   즉, 메소드 강제 호출 ex) entity.getUsername();
* 즉시 로딩과 지연 로딩
	* 즉시 로딩
		* 설정 : fetch=FetchType.EAGER
		* 실무에서는 가급적 지연 로딩으로 사용
		* JPQL에서는 N+1 문제를 일으킨다.
		   ex) createQuery("select m from Member m", Member.class) 실행 후 객체 설정 과정에서 
		   객체에 FetchType.EAGER인 경우 즉시 로딩이므로 Member 객체 만큼 SQL을 다시 수행한다.
		   따라서, 연관관계 N만큼 반복적으로 쿼리를 날리게되어 엄청난 성능 이슈가 발생될 수 있음
		   
		   필요한 경우 fetch join 사용하여 해소 가능
		   ex) createQuery("select m from Member m join fetch m.team", Member.class)
		   
		* @ManyToOne, @OneToOne은 기본이 즉시 로딩 -> LAZY 설정 필요
	* 지연 로딩
		* 설정 : fetch=FetchType.LAZY
		* 프록시 객체 상태로 제공
		* 실제 객체를 참조하는 시점에 SQL 실행
		* @OneToMany, @ManyToMany는 기본이 지연 로딩
	* 모든 연관관계는 지연 로딩을 사용
	* 즉시 로딩은 독이 될 수 있다.
	* JPQL fetch join, entity graph 기능을 사용해서 해결
* 영속성 전이 (cascade)
	* 특정 엔티티를 영속 상태로 만들 때 연관된 엔티티도 함께 영속 상태로 만들고 싶을 때
	* 주의 사항
		* 영속성 전이는 연관관계를 매핑하는 것과 아무 관련 없음
		* 엔티티를 영속화할 때 연관된 엔티티도 함께 영속화하는 편리함만 제공
		* 라이프 사이클이 유사할 때나 단일 소유자 일 때 사용
		   (parent 1 --> N child)
	* 종류
		* ALL : 모두 적용
		* PERSIST : 영속
		* REMOVE : 삭제
	* 고아 객체
		* 부모 엔티티와 연관관계가 끊어진 자식 엔티티를 자동으로 삭제
		* 참조가 제거된 엔티티는 다른 곳에서 참조하지 않는 고아 객체로 보고 삭제하는 기능
		* 참조하는 곳이 하나일 때 사용할 것 (특정 엔티티가 개인 소유할 때 사용)
		* @OneToOne, @OneToMany 만 가능
		* 참고
		   개념적으로 부모를 제거하면 자식은 고아가 된다.
		   따라서, 고아 객체 제거 기능을 활성화 하면, 부모를 제거할 떄 자식도 함께 제거된다.
		   이것은 CascadeType.REMOVE 처럼 동작
		* orphanRemoval = true
		  ```
		  Parent parent1 = em.find(Parent.class, id);
		  parent1.getChildren().remove(0); //자식 엔티티를 컬렉션에서 제거
		  
		  DELETE FROM CHILD WHERE ID = ?
		  ```
		*  영속성 전이 + 고아 객체, 생명 주기
			* 도메인 주도 설계(DDD)의 Aggregate Root 개념을 구현할 때 유용
			* 두 옵션을 모두 활성화하면 부모 엔티티를 통해서 자식의 생명 주기를 관리할 수 있음
			* CascadeType.ALL + orphanRemoval = true
	* 값 타입
		* 데이터 타입 분류
			* 엔티티 타입
				* @Entity 정의하는 객체
				* 데이터가 변해도 식별자로 지속해서 추적 가능
				* ex) 회원 엔티티의 키나 나이 값을 변경해도 식별자로 인식 가능
			* 기본값 타입
				* int, Integer, String 처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
				* 식별자가 없고 값만 있으므로 변경 시 추적 불가
				* ex) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체
				* 기본값 타입 종류
				  ```
				   1. 자바 기본 타입(int, double, ..)
				   2. 래퍼 클래스(Integer, Long, ..)
				   3. String
					```
					* 생명 주기를 엔티티의 의존
					   ex) 회원을 삭제하면 이름, 나이 필드도 함께 삭제
					* 공유하면 안됨
					   ex) 회원 이름 변경 시 다른 회원의 이름도 함께 변경되면 안됨
					* 참고
					   int, double 같은 기본 타입(primitive type)은 절대 공유 안됨
					   기본 타입은 항상 값을 복사함
					   Integer 같은 래퍼 클래스나 String 같은 특수한 클래스는 공유 가능한 객체이지만 변경 안됨
				* 임베디드 타입(embedded type, 복합 값 타입)
					* 새로운 값 타입을 직접 정의할 수 있음
					* JPA는 임베디드 타입이라고 함
					* 기본 값 타입을 모아서 만들어 복합 값 타입이라고도 함
					* int, String과 같은 값 타입
					* ex) 회원 엔티티는 이름, 근무 시작일, 근무 종료일, 주소 도시, 주소 번지, 주소 우편번호를 가진다.
					   즉, 회원 엔티티는 이름, 근무 기간, 집 주소를 가진다.
					* 사용법
						* @Embeddable : 값 타입을 정의하는 곳에 표시
						* @Embbeded : 값 타입을 사용하는 곳에 표시
						* 기본 생성자 필수
					* 장점
						* 재사용
						* 높은 응집도
						* Period.isWork() 처럼 해당 값 타입만 사용하는 의미 있는 메소드 생성 가능
						* 임베디드 타입을 포함한 모든 값 타입은 값 타입을 소유한 엔티티의 생명주기에 의존
						* 엔티티의 값일 뿐
						* 매핑하는 테이블은 동일
						* 객체와 테이블을 아주 세밀하게(find-grained) 매핑하는 것이 가능
						* 잘 설계한 ORM 애플리케이션은 매핑한 테이블의 수보다 클래스의 수가 더 많음 !
				* 컬렉션 값 타입(collection value type)
					* 값 타입을 하나 이상 저장할 때 사용
					* @ElementCollection, @CollectionTable 사용
					* 데이터베이스는 컬렉션을 같은 테이블에 저장하지 않음, 즉 관계 테이블로 별도 구성
					* 값 타입 컬렉션도 지연 로딩 전략 사용
					* 참고
						* 값 타입 컬렉션은 영속성 전이(cascade) + 고아 객체 제거 기능을 필수로 가진다고 볼 수 있음
						* 객체 값을 지우면 테이블 데이터 전체 삭제 후 존재하는 데이터를 다시 인서트하는 방식
						* 값을 수정할 때는 값 타입 데이터를 setter가 아닌 신규 객체를 초기화하여 주입하여야 사이드 이펙트를 예방할 수 있음
					* 제약 사항
						* 엔티티와 다르게 식별자 개념이 없다.
						* 값은 변경하면 추적이 어렵다.
						* 값 타입 컬렉션에 변경 사항이 발생하면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장한다.
						* 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본키를 구성해야 함.
						   NULL 입력 X, 중복 저장 X
					* 값 타입 컬렉션 대안
						* 값 타입 컬렉션 대신에 일대다 관계를 고려
						* 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용
						* 영속성 전이(Cascade) + 고아 객체 제거를 사용해서 값 타입 컬렉션처럼 사용
						* ex) AddressEntity

* 객체지향 쿼리 언어(JPQL)
	1. JPQL (객체지향 쿼리 언어)
	    : 가장 단순한 조회 방법
	     * EntityManager.find()
	     * 객체 그래프 탐색 => a.getB().getC()
	     * JPA를 사용하면 엔티티 객체를 중심으로 개발
	     * 테이블이 아닌 엔티티 객체를 대상으로 검색
	     * 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
	     * 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL 필요
	     * ANSI 표준 문법 SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
	     * 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
	     * SQL을 추상화해서 특정 데이터베이스 SQL에 의존 X
	2. Criteria
	    : 자바 코드로 JPQL 작성
	    * JPQL 빌더 역할, JPA 공식 기능 
	    * 너무 복잡하고 실용성이 없다.
	    * 따라서 QueryDSL 사용 권장
	3. QueryDSL
	    : 자바 코드로 JPQL 작성
	    * JPQL 빌더 역할
	    * 컴파일 시점에 문법 오류를 찾을 수 있음
	    * 동적 쿼리 작성이 편리함
	    * 단순하고 쉬워 실무 사용 권장
	4. 네이티브 SQL
	    : JPA가 제공하는 SQL을 직접 사용하는 기능
	    * JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능
	    * ex) Oracle CONNECT BY, 특정 DB만 사용하는 SQL 힌트 등
	5. JDBC 직접 사용, SpringJdbcTemplate 등
	    : JPA를 사용하면서 JDBC 커넥션을 직접 사용하거나, 스프링 JdbcTemplate, 마이바티스 등을 함께 사용 가능
	    * 영속성 컨텍스트를 적절한 시점에 강제로 flush 필요 !
	    * ex) JPA를 우회해서 SQL을 실행하기 직전에 영속성 컨텍스트 수동 flush
	* JPQL (Java Persistence Query Language)
		* 기본 문법
			* 엔티티와 속성은 대소문자 구분
			* 키워드는 대소문자 구분 X (SELECT, FROM, where)
			* 엔티티 이름 사용, 테이블 이름이 아님 !
			* 별칭은 필수 (as 생략 가능)
			* count, sum, avg, max, min
			* group by, having, order by
			* TypeQuery
				* 반환 타입이 명확할 때 사용
				  ```
				  TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
					```
			* Query
				* 반환 타입이 명확하지 않을 때 사용
				  ```
				  Query query = em.createQuery("select m.username, m.age from Member m");
					```
		* 결과 조회 API
			* query.getSingleResult()
				* 결과가 정확히 하나일 때
				* 단일 객체 반환
				* 결과가 없으면 : javax.persistence.NoResultException
				* 둘 이상이면 : javax.persistence.NonUniqueResultException
			  
			* query.getResultList()
				* 결과가 하나 이상일 때
				* 리스트 반환 
				* 결과가 없으면 빈 리스트 반환
		* 파라미터 바인딩
			* 이름 기반
			```
			TypedQuery<Member> query = em.createQuery("select m from Member m where m.age = :age");
			query.setParamter("age", 10);
			```
			* 위치 기반은 포지션 위치를 기준으로 사용
			```
			TypedQuery<Member> query = em.createQuery("select m from Member m where m.age = ?1");
			query.setParamter(1, 10);
			```
			
		* 프로젝션
			* select 절에 조회할 대상을 지정하는 것
			* 대상 : 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본데이터 타입)
			* select m from Member m => 엔티티 프로젝션
			* select m.team from Member m => 엔티티 프로젝션 (결과가 TEAM 엔티티)
			* select m.address from Member m => 임베디드 타입 프로젝션
			* select m.username, m.age from Member m => 스칼라 타입 프로젝션
			* distinct로 중복 제거 가능
			* 여러값 조회
				```
				select m.username, m.age from Member m
				```
				* Query 타입으로 조회
				* Object[] 타입으로 조회
				  ```
				  List<Object[]> result = call query..
				  username = result[0];
				  userage = result[1];
					```
				* new 명령어로 조회
					* 단순 값을 DTO로 바로 조회
					  ```
					  select new domain.MemberDTO(m.username, m.age) from Member m
						```
					* 패키지 명을 포함한 전체 클래스 명 입력
					* 순서와 타입이 일치하는 생성자 필요
		* 페이징 API
			* setFirstResult(int startPosition) : 조회 시작 위치(0부터 시작)
			* setMaxResult(int maxResult) : 조회할 데이터 수
			```
			List<Member> members = entityManager.createQuery("select m from Member m order by m.id desc", Member.class)
            	                        .setFirstResult(10)
                	                    .setMaxResults(10)
                    	                .getResultList();
			```
		* 조인
			* 내부 조인 (inner)
			```
			select m from Member m join m.team t
			```
			* 외부 조인 (outer)
			```
			select m from Member m left join m.team t
			```
			* 세타 조인
			```
			select m from Member m, Team t where m.username = t.name
			```
			*  ON 절
				* ON절을 활용한 조인(JPA 2.1부터 지원)
					* 조인 대상 필터링
					  ex) 회원과 팀을 조인하면서 팀 이름이 A인 팀만 조인
					```
					select m from Member m join m.team t on t.name = 'A'
					```
					* 연관관계 없는 엔티티 외부 조인(하이버네이크 5.1부터)
					```
					select m from Member m left join Team t on m.username = t.name
					```
		* 서브쿼리
			* 나이가 평균보다 많은 회원
			```
			select m from Member m where m.age > (select avg(m2.age) from Member m2)
			```
			* 한 건이라도 주문한 고객
			```
			select m from Member m where (select count(o) from Order o where m = o.member) > 0
			```
			* 서브쿼리 지원 함수
				* [NOT] EXISTS (subquery) : 서브쿼리에 결과가 존재하면 참
					* {ALL | ANY | SOME} (subquery)
					* ALL : 모두 만족하면 참
					* ANY, SOME : 같은 의미, 조건을 하나라도 만족하면 참
				* [NOT] IN (subquery) : 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참
				* 팀 A 소속인 회원
				```
				select m from Member m where exists (select t from m.team t where t.name = '팀A')
				```
				* 전체 상품 각각의 재고보다 주문량이 많은 주문들
				```
				select o from Order o where o.orderAmount > ALL (select p.stockAmount from Product p)
				```
				* 어떤 팀이든 팀에 소속된 회원
				```
				select m from Member m where m.team ANY (select t from Team t)
				```
				* JPA 서브 쿼리 한계
					* WHERE, HAVING 절에서만 서브 쿼리 사용 가능
					* SELECT 절도 가능(하이버네이트에서 지원)
					* FROM 절의 서브 쿼리는 현재 JPQL에서 불가능
						* 조인으로 풀 수 있으면 풀어서 해결
