package study.datajpa.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

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
}
