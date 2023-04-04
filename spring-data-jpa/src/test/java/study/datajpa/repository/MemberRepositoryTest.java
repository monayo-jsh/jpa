package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
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

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @PersistenceContext
    EntityManager entityManager;

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

    @Test
    void testBulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void testMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        //when N + 1
        //select Member 1
        List<Member> members = memberRepository.findAll();
        for(Member member : members) {
            // N
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            //select Team.getName()
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    void testMemberFetchJoin() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        //when
        List<Member> members = memberRepository.findMembersFetchJoin();

        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

    }

    @Test
    void testMemberEntityGraph() {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        //when N + 1
        //select Member 1
        List<Member> members = memberRepository.findMembersEntityGraph();
        for(Member member : members) {
            // N
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            //select Team.getName()
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    void testMemberEntityGraphByMethodChain() {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        //when N + 1
        //select Member 1
        List<Member> members = memberRepository.findMembersEntityGraphByAgeGreaterThanEqual(10);
        for(Member member : members) {
            // N
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            //select Team.getName()
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    void testMemberNamedEntityGraph() {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        //when N + 1
        //select Member 1
        List<Member> members = memberRepository.findMembersNamedEntityGraphByAgeGreaterThanEqual(10);
        for(Member member : members) {
            // N
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            //select Team.getName()
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    void testQueryHint() {
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //when
        //@QueryHints 를 활용하여 hibernate 에서 제공하는 옵션으로 readOnly 로 조회 시 수정은 하지 않음을 지시하여 메모리에 스냅샷을 만들지 않도록 함
        //따라서 Dirty Checking 하지 않음
        Member findMember = memberRepository.findReadOnlyByUsername(member.getUsername());
        findMember.setUsername("member2");

        entityManager.flush();
    }

    @Test
    void testQueryLock() {
        //given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        entityManager.flush();
        entityManager.clear();

        //when
        //query 끝에 for update 가 붙음으로서 select lock 제공
        Member findMember = memberRepository.findLockByUsername(member.getUsername());

        findMember.setUsername("member2");
    }

    @Test
    void testCustomRepository() {
        List<Member> members = memberRepository.findMemberCustom();
    }

    @Test
    void testCustomRepositoryClass() {
        List<Member> members = memberQueryRepository.findAllMembers();
    }

    @Test
    void testJpaEventBaseEntity() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1); //@PrePersist

        Thread.sleep(100);
        member1.setUsername("member2");

        entityManager.flush(); //@PreUpdate
        entityManager.clear();

        //when
        Member findMember = memberRepository.findById(member1.getId()).get();

        //then
        System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
        System.out.println("findMember.getLastModifiedDate() = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
    }

    @Test
    void queryByExample() {
        //given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        memberRepository.save(m1);
        memberRepository.save(m2);

        entityManager.flush();
        entityManager.clear();

        //when
        //Probe : 필드에 데이터가 있는 실제 도메인 객체
        //ExampleMatcher : 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
        //Example : Probe 와 ExampleMatcher 로 구성, 쿼리를 생성하는데 사용
        //조인은 가능하지만 내부 조인만 가능, 외부 조인 안됨
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> members = memberRepository.findAll(example);

        Assertions.assertThat(members.size()).isEqualTo(1);
    }

    @Test
    void testProjections() {
        //given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m1", 20, teamA);

        memberRepository.save(m1);
        memberRepository.save(m2);

        entityManager.flush();
        entityManager.clear();

        //when
        List<UsernameOnly> usernames = memberRepository.findProjectionByUsername("m1");

        for(UsernameOnly username : usernames) {
            System.out.println("username.getUsername() = " + username.getUsername());
        }

        assertThat(usernames.size()).isEqualTo(2);

        List<UsernameOnlyDto> usernameOnlyDtos = memberRepository.findProjectionDtoByUsername("m1");

        for(UsernameOnlyDto username : usernameOnlyDtos) {
            System.out.println("username.getUsername() = " + username.getUsername());
        }

        assertThat(usernames.size()).isEqualTo(2);

        usernameOnlyDtos = memberRepository.findProjectionDtoByUsername("m1", UsernameOnlyDto.class);

        for(UsernameOnlyDto username : usernameOnlyDtos) {
            System.out.println("username.getUsername() = " + username.getUsername());
        }

        assertThat(usernames.size()).isEqualTo(2);

        List<NestedClosedProjections> nestedClosedProjections = memberRepository.findProjectionDtoByUsername("m1", NestedClosedProjections.class);

        for(NestedClosedProjections nestedClosedProjection : nestedClosedProjections) {
            System.out.println("nestedClosedProjection = " + nestedClosedProjection.getUsername());
            System.out.println("nestedClosedProjection.getTeam().getName() = " + nestedClosedProjection.getTeam().getName());
        }

        assertThat(usernames.size()).isEqualTo(2);
    }
}