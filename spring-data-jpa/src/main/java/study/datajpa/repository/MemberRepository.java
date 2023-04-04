package study.datajpa.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    //select method query
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // @Query가 없으면
    // Entity의 NamedQuery를 찾아서 실행
    // 없으면 메서드 쿼리를 생성해서 실행
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    //select jpql query
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //select custom
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //select dto
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //parameter binding
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    //returnType
    List<Member> findMembersByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalMemberByUsername(String username);

    //select paging
    Page<Member> findByAge(int age, Pageable pageable);
    //select slice
    Slice<Member> findSliceByAge(int age, Pageable pageable);

    //select paging query
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select m from Member m")
    Page<Member> findQueryByAge(int age, Pageable pageable);

    //bulk update query
    //이 후 로직이 있으면 clearAutomatically 로 영속성 컨텍스트 초기화해야 버그로부터 안전
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //fetch join
    //개선 : @EntityGraph
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMembersFetchJoin();

    //기존에 제공하는 인터페이스 오버라이드해서 fetch join 사용
//    @Override
//    @EntityGraph(attributePaths = "team")
//    List<Member> findAll();

    //신규 메서드 생성 후 fetch join 사용
    @Query("select m from Member m")
    @EntityGraph(attributePaths = "team")
    List<Member> findMembersEntityGraph();

    //신규 메서드 체인 생성 후 fetch join 사용
    @EntityGraph(attributePaths = "team")
    List<Member> findMembersEntityGraphByAgeGreaterThanEqual(int age);

    //Entity 에 NamedEntityGraph 를 정의해서 사용
    @EntityGraph("Member.all")
    List<Member> findMembersNamedEntityGraphByAgeGreaterThanEqual(int age);

    //JPA Hint : JPA 쿼리 힌트는 SQL 힌트가 아닌 JPA 구현체에게 제공하는 힌트
    @QueryHints(
        value = @QueryHint(name="org.hibernate.readOnly", value = "true")
    )
    Member findReadOnlyByUsername(String username);

    //select for update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);


}
