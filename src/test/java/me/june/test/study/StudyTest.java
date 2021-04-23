package me.june.test.study;

import me.june.test.FastTest;
import me.june.test.FindSlowTestExtension;
import me.june.test.SlowTest;
import me.june.test.domain.Study;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(FindSlowTestExtension.class) // 선언적인 Extension 사용 방법, 이 방법은 Extension 을 커스터마이징 할 수 없다..
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest {

	// 프로그래밍으로 등록하는 방법
	@RegisterExtension
	static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000L);

	@FastTest
	@DisplayName("스터디 생성") // DisplayName 의 우선순위가 젤 높음
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

	@Order(1)
	@SlowTest
    @Disabled
    void disabled() {

    }

    @Order(2)
    @DisplayName("반복 테스트")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatTest(RepetitionInfo repetitionInfo) {
	    // 현재 몇번째 반복중인지
        // 총 몇번을 반복해야 하는지 정보를 알 수 있음
        System.out.println("repeatTest" + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
    }

    // Junit 5 는 기본 제공, Junit4 는 서드파티 라이브러리 필요
    @DisplayName("파라미터 테스트")
    @ParameterizedTest(name = "{displayName}, {index} {arguments}")
    @ValueSource(strings = {
        "hello",
        "world"
    })
    @NullSource
    @EmptySource
    @NullAndEmptySource
    void parameterizedTest(String value) {
        System.out.println("value = " + value);
    }

    @DisplayName("ArgumentConverter Test")
    @ParameterizedTest
    @ValueSource(ints = {10, 20, 40})
    void studyConverterTest(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println("study = " + study);
    }

    // ArgumentConverter 는 하나의 인자값만 변환 가능
    static class StudyConverter extends SimpleArgumentConverter {

        @Override
        protected Object convert(Object target, Class<?> type) throws ArgumentConversionException {
            assertEquals(Study.class, type, "Can only convert to Study");
            return new Study(Integer.parseInt(target.toString()));
        }
    }

    @ParameterizedTest
    @CsvSource({"10, '자바 스터디'", "20, '스프링'"})
    void studyCsvSourceTest(Integer limit, String name) {
        System.out.println("new Study(limit, name) = " + new Study(limit, name));
    }

    @ParameterizedTest
    @CsvSource({"10, '자바 스터디'", "20, '스프링'"})
    void studyCsvSourceAggregateTest(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println("Study = " + study);
    }

    // 반드시 static inner 혹은 public class 여야 한다.
    static class StudyAggregator implements ArgumentsAggregator {

        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        }
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