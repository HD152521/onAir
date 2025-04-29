package com.sejong.project.onair.domain.member.service;

import com.sejong.project.onair.domain.member.dto.MemberAuthContext;
import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.domain.member.repository.MemberRepository;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByEmail(username);
        log.info("loadUserByUsername : {}",username);
        if(member== null){
            log.info("[loadUserByUsername] username:{}, {}", username, ErrorCode.MEMBER_NOT_FOUND);
        }
        MemberAuthContext ctx = MemberAuthContext.of(member);
        return new MemberDetails(ctx);
    }
}