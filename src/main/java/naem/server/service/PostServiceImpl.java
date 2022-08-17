package naem.server.service;

import static naem.server.exception.ErrorCode.*;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import naem.server.domain.member.Member;
import naem.server.domain.post.dto.PostSaveReqDto;
import naem.server.exception.CustomException;
import naem.server.repository.MemberRepository;
import naem.server.repository.PostRepository;
import naem.server.service.util.SecurityUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void save(PostSaveReqDto requestDto) {

        Optional<Member> oMember = memberRepository.findByUsername(SecurityUtil.getLoginUsername());

        if (oMember.isPresent()) {

            Member member = oMember.get();

            postRepository.save(requestDto.toEntity(member));
        } else {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
    }
}
