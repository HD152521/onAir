package com.sejong.project.onair.domain.member.repository;

import com.sejong.project.onair.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findMemberByEmail(String email);
}
