package study.datajpa.dto;

import lombok.Builder;
import lombok.Getter;
import study.datajpa.entity.Member;

@Getter
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    @Builder
    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    public static MemberDto of(Member member) {
        return MemberDto.builder()
                        .id(member.getId())
                        .username(member.getUsername())
                        .build();
    }
}
