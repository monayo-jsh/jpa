package study.datajpa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // @Query가 없으면 
    // Entity의 NamedQuery를 찾아서 실행
    // 없으면 메서드 쿼리를 생성해서 실행
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

}
