package naem.server.service;

import naem.server.domain.post.dto.PostResDto;
import naem.server.domain.post.dto.PostSaveReqDto;

public interface PostService {

    void save(PostSaveReqDto requestDto);

    PostResDto getPost(Long id);

    void update(Long id, PostSaveReqDto requestDto);

    Long getAuthorId(Long id);
}
