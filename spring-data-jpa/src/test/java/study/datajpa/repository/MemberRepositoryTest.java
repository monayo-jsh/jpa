package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");

        Member savedMember = memberRepository.save(member);

        Optional<Member> findMember = memberRepository.findById(savedMember.getId());

        assertThat(findMember.isPresent()).isTrue();

        Member dbMember = findMember.get();

        assertThat(dbMember.getId()).isEqualTo(savedMember.getId());
        assertThat(dbMember.getUsername()).isEqualTo(savedMember.getUsername());
        assertThat(dbMember).isEqualTo(savedMember);
    }

    @Test
    void testCustomQuery() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);
        Member memberC = new Member("memberA", 30);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);

        String username = "memberA";
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan(username, 10);

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

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);

        List<Member> members = memberRepository.findByUsername(username);

        assertThat(members.size()).isEqualTo(3);

        for(Member member : members) {
            assertThat(member.getUsername()).isEqualTo(username);
        }
    }

    @Test
    void testQuery() {
        String username = "memberA";

        Member memberA = new Member(username, 10);
        Member memberB = new Member(username, 20);
        Member memberC = new Member(username, 30);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);

        List<Member> members = memberRepository.findUser(username, 10);

        assertThat(members.size()).isEqualTo(1);

        for(Member member : members) {
            assertThat(member.getUsername()).isEqualTo(username);
        }
    }
}