package naem.server.domain.member.dto;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import naem.server.domain.member.Member;
import naem.server.domain.member.MemberType;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SignUpReq {

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|]+$", message = "닉네임은 10자 이내의 한글, 영문, 숫자를 사용하세요.")
    @Size(min = 1, max = 10)
    @Column(unique = true)
    private String nickname;

    private String recommenderCode;

    @Builder
    public SignUpReq(MemberType memberType, String username, String password, String nickname) {
        this.memberType = memberType;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    /**
     * Transform to User Entity
     * @return User Entity
     */
    public Member toUserEntity() {
        return Member.builder()
            .memberType(this.getMemberType())
            .username(this.getUsername())
            .password(this.getPassword())
            .nickname(this.getNickname())
            .build();
    }
}
