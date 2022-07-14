package naem.server.service;

import lombok.extern.slf4j.Slf4j;
import naem.server.domain.Member;
import naem.server.domain.Salt;
import naem.server.repository.MemberRepository;
import naem.server.service.util.SaltUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    @Autowired
    private MemberRepository memberRepository;

//    @Autowired
//    private RedisUtil redisUtil;

    @Autowired
    private SaltUtil saltUtil;

    @Override
    @Transactional
    public void signUpUser(Member member) {

        String password = member.getPassword();
        String salt = saltUtil.genSalt();
        log.info(salt);

        member.setSalt(new Salt(salt));
        member.setPassword(saltUtil.encodePassword(salt,password));
        memberRepository.save(member);
    }

    @Override
    public Member loginUser(String id, String password) throws Exception{

        Member member = memberRepository.findByUsername(id);
        if(member==null) throw new Exception ("멤버가 조회되지 않음");

        String salt = member.getSalt().getSalt();
        password = saltUtil.encodePassword(salt,password);
        if(!member.getPassword().equals(password))
            throw new Exception ("비밀번호가 틀립니다.");

        return member;
    }

}