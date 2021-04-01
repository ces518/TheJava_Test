package me.june.test.study;

import me.june.test.domain.Member;
import me.june.test.domain.Study;
import me.june.test.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    // 구현체는 없지만, 의존 하는 클래스들에 대한 인터페이스 기반으로 구현해야 하는경우
    // Mocking 하기 가장 좋다.

    // 애노테이션을 이용한 Mocking 방법
    // MockitoExtension 이 존재해야 한다.
    @Mock MemberService memberService;

    @Mock StudyRepository studyRepository;

      /*
        코드 레벨에서 Mocking 하는 방법
        MemberService memberService = mock(MemberService.class);
        StudyRepository studyRepository = mock(StudyRepository.class);
     */


    @Test
    void createStudy(
        // 특정 메소드에서만 쓰인다면, 메소드 파라미터로 선언해서 스코프를 제한할 수 있다.
        @Mock MemberService memberService,
        @Mock StudyRepository studyRepository
    ) throws Exception {
        // given
        StudyService studyService = new StudyService(memberService, studyRepository);

        Member member = new Member();
        member.setId(1L);

        // Mocking 된 객체의 void 메소드는 기본적으로 아무런 행위도 하지 않는다.
        memberService.validate(1L);


        // Stubbing : Mocking 한 객체가 어떤 행위를 할때 어떤 행동을 할지를 정하는것
        when(memberService.findById(any())).thenReturn(Optional.of(member));

        // void method Stubbing 은 방법이 다르다.
        doThrow(RuntimeException.class).when(memberService).validate(any());


        Study study = new Study(10, "java");

        // when
        studyService.createNewStudy(1L, study);


        // then
        assertNotNull(studyService);
    }
}