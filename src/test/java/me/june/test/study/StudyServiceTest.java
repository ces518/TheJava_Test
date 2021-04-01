package me.june.test.study;

import me.june.test.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    // 구현체는 없지만, 의존 하는 클래스들에 대한 인터페이스 기반으로 구현해야 하는경우
    // Mocking 하기 가장 좋다.

    // 애노테이션을 이용한 Mocking 방법
    // MockitoExtension 이 존재해야 한다.
    @Mock MemberService memberService;

    @Mock StudyRepository studyRepository;


    @Test
    void createStudyService(
        // 특정 메소드에서만 쓰인다면, 메소드 파라미터로 선언해서 스코프를 제한할 수 있다.
        @Mock MemberService memberService,
        @Mock StudyRepository studyRepository
    ) throws Exception {
        // given

        /*
            코드 레벨에서 Mocking 하는 방법
            MemberService memberService = mock(MemberService.class);
            StudyRepository studyRepository = mock(StudyRepository.class);
         */

        // when
        StudyService studyService = new StudyService(memberService, studyRepository);

        // then
        assertNotNull(studyService);
    }
}