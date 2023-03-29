package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");

        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void testCustomQuery() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);
        Member memberC = new Member("memberA", 30);

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);
        memberJpaRepository.save(memberC);

        String username = "memberA";
        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan(username, 10);

        assertThat(members.size()).isEqualTo(2);

        for(Member member : members) {
            assertThat(member.getUsername()).isEqualTo(username);
            assertThat(member.getAge()).isGreaterThan(10);
        }
    }

    @Test
    void testNamedQuery() {
        String username = "memberA";

        Member memberA = new Member(username, 10);
        Member memberB = new Member(username, 20);
        Member memberC = new Member(username, 30);

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);
        memberJpaRepository.save(memberC);

        List<Member> members = memberJpaRepository.findByUsername(username);

        assertThat(members.size()).isEqualTo(3);

        for(Member member : members) {
            assertThat(member.getUsername()).isEqualTo(username);
        }
    }

    @Test
    void testPaging() {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        //when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        //then
        assertThat(totalCount).isEqualTo(5);
        assertThat(members.size()).isEqualTo(3);
    }

    @Test
    void testBulkUpdate() {
        //given
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);
    }
}