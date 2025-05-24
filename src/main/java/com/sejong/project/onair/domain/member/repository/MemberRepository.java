package com.sejong.project.onair.domain.member.repository;

import com.sejong.project.onair.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findMemberByEmail(String email);
}
