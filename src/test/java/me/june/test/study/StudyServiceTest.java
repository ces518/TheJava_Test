package me.june.test.study;

import lombok.extern.slf4j.Slf4j;
import me.june.test.domain.Member;
import me.june.test.domain.Study;
import me.june.test.member.MemberService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Testcontainers
@Slf4j
class StudyServiceTest {

    // 구현체는 없지만, 의존 하는 클래스들에 대한 인터페이스 기반으로 구현해야 하는경우
    // Mocking 하기 가장 좋다.

    // 애노테이션을 이용한 Mocking 방법
    // MockitoExtension 이 존재해야 한다.
    @Mock MemberService memberService;

    @Autowired StudyRepository studyRepository;

    // 필드 일 경우 모든 테스트 마다 컨테이너를 새로 띄운다.
	// 스태틱 필드일 경우 해당 테스트 클래스 전역에서 공유한다.
//	@Container
//    static PostgreSQLContainer container = new PostgreSQLContainer()
//			.withDatabaseName("studytest")
//			.withUsername("studytest")
//			.withPassword("studytest");

	@Container
	static GenericContainer container = new GenericContainer("postgres") // imageName 은 로컬에서 찾아보고 없다면 원격에서 찾아온다.
			.withExposedPorts(5432) // port는 설정이 불가능하고, 사용가능한 포트중에서 랜덤하게 매핑한다.
			.withEnv("POSTGRES_DB", "studytest")
			.waitingFor(Wait.forListeningPort()) // 컨테이너가 사용 가능한지 대기했다가 사용하는 옵션
			.waitingFor(Wait.forHttp("/hello")) // 컨테이너가 사용 가능한지 대기했다가 사용하는 옵션
			;

    @BeforeAll
	static void setUp() {
		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
		container.followOutput(logConsumer); // 컨테이너 내부의 로그를 스트리밍 한다.
    	container.getMappedPort(5432); // 컨테이너 포트와 매핑된 로컬 포트 확인
		container.getLogs(); // 모든 로그들 출력
    	container.start();
	}

	@AfterAll
	static void clean() {
    	container.stop();
	}

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
//        memberService.validate(1L);


        // Stubbing : Mocking 한 객체가 어떤 행위를 할때 어떤 행동을 할지를 정하는것
        // BDD Style 은 when -> given , then -> willReturn
        // BDDMockito 클래스로 제공됨
        given(memberService.findById(any())).willReturn(Optional.of(member));

        // void method Stubbing 은 방법이 다르다.
//        doThrow(RuntimeException.class).when(memberService).validate(any());


        Study study = new Study(10, "java");

        // when
        Study newStudy = studyService.createNewStudy(1L, study);


        // then

        assertEquals(study.getOwner(), member);

        // 특정 메소드 호출
        verify(memberService).notify(newStudy);

        // BDD Style verify -> then
        then(memberService).should().notify(newStudy);

        // 아무것도 하지 않는지 검증
//        verifyNoInteractions(memberService);

        then(memberService).shouldHaveNoMoreInteractions();

        // 순서대로 호출이 되었는지 검증
        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(newStudy);
    }
}