package me.june.test.member;

import me.june.test.domain.Member;
import me.june.test.domain.Study;

import java.util.Optional;

public interface MemberService {

    Optional<Member> findById(Long memberId) throws MemberNotFoundException;

    void validate(Long memberId);

    void notify(Study study);
}
