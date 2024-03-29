package naem.server.domain.post.dto;

import lombok.Data;
import naem.server.domain.BoardType;
import naem.server.domain.member.Member;

@Data
public class PostReadCondition {

    private Member member; // 작성자 아이디
    private String keyword; // 검색 키워드
    private Boolean isDeleted;
    private BoardType boardType; // 게시판 타입

    // 내가 쓴 게시글 조회
    public PostReadCondition(Member member) {
        this.member = member;
        this.isDeleted = false;
    }

    // 전체 게시글 조회
    public PostReadCondition() {
        this.isDeleted = false;
    }

    // 전체 게시글 검색
    public PostReadCondition(String keyword) {
        this.keyword = keyword;
        this.isDeleted = false;
    }

    // 게시판 타입별 조회
    public PostReadCondition(BoardType boardType) {
        this.boardType = boardType;
        this.isDeleted = false;
    }

    // 게시판 타입별 검색
    public PostReadCondition(BoardType boardType, String keyword) {
        this.boardType = boardType;
        this.keyword = keyword;
        this.isDeleted = false;
    }
}
