package naem.server.domain.post;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import naem.server.domain.Tag;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    //==생성 메서드==//
    public static PostTag createPostTag(Tag tag) {
        PostTag postTag = new PostTag();
        postTag.setTag(tag);

        return postTag;
    }

    //==삭제 메서드==//
    public static void removePostTag(List<PostTag> postTags) {
        // post_id와 tag_id의 매핑 제거
        for (PostTag postTag : postTags) {
            postTag.setPost(null);
            postTag.setTag(null);
        }
    }

}
