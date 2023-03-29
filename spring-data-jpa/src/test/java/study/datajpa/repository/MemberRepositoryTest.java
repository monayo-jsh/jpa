package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

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


    @Test
    void testQueryVar() {

        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        Member memberC = new Member("memberC", 30);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);

        List<String> members = memberRepository.findUsernameList();

        assertThat(members.size()).isEqualTo(3);
    }

    @Test
    void testQueryDto() {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        Team teamC = new Team("teamC");

        teamRepository.save(teamA);
        teamRepository.save(teamB);
        teamRepository.save(teamC);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 20, teamB);
        Member memberC = new Member("memberC", 30, teamC);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);

        List<MemberDto> members = memberRepository.findMemberDto();

        assertThat(members.size()).isEqualTo(3);
    }

    @Test
    void testQueryNames() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        Member memberC = new Member("memberC", 30);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberC);

        List<Member> members = memberRepository.findByNames(List.of("memberA", "memberB"));

        assertThat(members.size()).isEqualTo(2);
    }


    @Test
    void testReturnType() {
        String memberName = "memberA";
        Member memberA = new Member(memberName, 10);
        memberRepository.save(memberA);

        List<Member> members = memberRepository.findMembersByUsername(memberName);

        assertThat(members.size()).isEqualTo(1);

        Member member = memberRepository.findMemberByUsername(memberName);

        assertThat(member.getUsername()).isEqualTo(memberName);

        Optional<Member> memberOptional = memberRepository.findOptionalMemberByUsername(memberName);

        assertThat(memberOptional.isPresent()).isTrue();
        assertThat(memberOptional.get().getUsername()).isEqualTo(memberName);
    }

    @Test
    void testPaging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        assertThat(page.getContent().size()).isEqualTo(3); //페이징 컨텐츠 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 컨텐츠 수
        assertThat(page.getNumber()).isEqualTo(0); //현재 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 수
        assertThat(page.isFirst()).isTrue(); //첫번째 페이지 여부
        assertThat(page.hasNext()).isTrue(); //다음 페이지 여부
    }

    @Test
    void testPagingSlice() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));

        //when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        /**
         * 반환 타입이 Slice 일 경우
         * size + 1로 요청해서 다음 페이지 여부를 확인하도록 성능 개선
         * 즉, total count query 하지 않음
         */

        //then
        assertThat(page.getContent().size()).isEqualTo(3); //페이징 컨텐츠 수
        assertThat(page.getNumber()).isEqualTo(0); //현재 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 페이지 여부
        assertThat(page.hasNext()).isTrue(); //다음 페이지 여부
    }
    
}