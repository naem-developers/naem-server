package naem.server.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import naem.server.domain.post.dto.BriefPostInfoDto;
import naem.server.domain.post.dto.PostReadCondition;

public interface LikeService {

    void postsLike(long postId);

    Slice<BriefPostInfoDto> getMyLikedPostList(Long cursor, PostReadCondition condition, Pageable pageRequest);
}
