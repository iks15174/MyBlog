package com.jiho.board.springbootaws.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByEmailAndSocial(String email, Social social);

    Optional<Member> findByEmail(String username);

    Optional<Member> findByEmailAndSocial(String username, Social social);

}
