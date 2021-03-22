package me.june.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

	@Tag("fast")
    @Test
	@DisplayName("스터디 생성")
	@EnabledOnOs(OS.MAC) // Annotation 으로 제공
    void create() {
    	// 특정 환경변수에 따라 테스트를 실행
    	assumeTrue("LOCAL".equalsIgnoreCase(System.getProperty("TEST_ENV")));

    	// 특정 환경에 따라 실행
		assumingThat("LOCAL".equalsIgnoreCase(System.getProperty("TEST_ENV")), () -> {
			Study study = new Study(100);
		});

        Study study = new Study(1);
        assertNotNull(study);

        // 람다식도 지원
        assertEquals(Study.Status.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 DRAFT 여야 한다.");
        assertEquals(Study.Status.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 DRAFT 여야 한다.");
        assertTrue(study.getLimit() > 0, "스터디 참석 인원은 0 보다 커야합니다.");

        // 각 테스트 들을 한번에 검증
		assertAll(
			() -> assertEquals(Study.Status.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 DRAFT 여야 한다."),
			() -> assertEquals(Study.Status.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 DRAFT 여야 한다."),
			() -> assertTrue(study.getLimit() > 0, "스터디 참석 인원은 0 보다 커야합니다.")
		);

		// 예외 검증
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
		assertEquals(ex.getMessage(), "limit 은 0보다 커야합니다.");

		// 실행 시간내에 완료되어야 하는지 검증
		assertTimeout(Duration.ofSeconds(10), () -> new Study(10));

		// 지정된 시간이 끝나면 테스트 실패로 간주하고 그냥 실패시키고 싶을경우
		// 지정한 시간이 10초가 지나면 테스트는 실패할것이기 때문에 종료시킨다.
		// 람다 코드블록 내에 존재하는 코드는 별도의 스레드에서 동작한다.
		// 따라서 ThreadLocal 기반 기능 (Security, Transactional) 등이 예상과 다르게 동작할 수 있다.
		assertTimeoutPreemptively(Duration.ofSeconds(10), () -> new Study(10));
	}

	@Tag("fast")
    @Test
    @Disabled
    void disabled() {

    }

    /**
        @BeforeAll
        모든 테스트 클래스가 실행되기 전에 딱 1회만 실행된다.
        static 메소드로 정의 되어야 한다.
        private 접근 제어자는 사용할 수 없다.
     */
    @BeforeAll
    static void beforeAll() {
        System.out.println("beforeAll");
    }

    /**
        @AfterAll
        모든 테스트 클래스가 종료된 후 딱 1회만 실행된다.
        static 메소드로 정의 되어야 한다.
        private 접근 제어자는 사용할 수 없다.
    */
    @AfterAll
    static void afterAll() {
        System.out.println("afterAll");
    }

    /**
        @BeforeEach
        각 테스트가 실행되기전 실행된다.
        static 메소드일 필요가 없다.
     */
    @BeforeEach
    void beforeEach() {
        System.out.println("beforeEach");
    }

    /**
         @AfterEach
         각 테스트가 실행된 후 실행된다.
         static 메소드일 필요가 없다.
     */
    @AfterEach
    void afterEach() {
        System.out.println("afterEach");
    }
}