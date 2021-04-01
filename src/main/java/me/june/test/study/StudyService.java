package me.june.test.study;

import me.june.test.domain.Member;
import me.june.test.domain.Study;
import me.june.test.member.MemberService;

public class StudyService {

    private final MemberService memberService;
    private final StudyRepository repository;

    public StudyService(MemberService memberService, StudyRepository repository) {
        assert memberService != null;
        assert repository != null;
        this.memberService = memberService;
        this.repository = repository;
    }

    public Study createNewStudy(Long memberId, Study study) {
        Member findMember = memberService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Member doesn't exist for id: %s", memberId)));
        study.setOwner(findMember);
        return repository.save(study);
    }
}
