package study.datajpa.controller;

import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        for(int i=0; i<100; i++) {
            memberRepository.save(new Member("member" + i, i));
        }
    }

    @GetMapping("/members/{id}")
    public Member findMember(@PathVariable("id") Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isEmpty()) {
            return new Member("사용자 없음");
        }

        return member.get();
    }

    //Web 확장 - 도메인 클래스 컨버터
    //도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환
    //리파지토리를 사용해서 엔티티를 찾음
    @GetMapping("/members2/{id}")
    public Member findMember2(@PathVariable("id") Member member) {
        return member;
    }

    /**
     * Web 확장 - 페이징과 정렬
     * ?size=10
     * ?page=0
     * ?sort=id,desc
     * defulat size 수정 - application.yml
     * data:
     *     web:
     *       pageable:
     *         default-page-size: 10
     *         max-page-size: 1000
     * 또는
     *  @PageableDefault(size=5)
     * * Page 반환할 때는 map을 통해 DTO 반환
     */
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
         return memberRepository.findAll(pageable)
                                .map(MemberDto::of);
    }

}
