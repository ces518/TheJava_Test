package me.june.test;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

    @Test
	@DisplayName("스터디 생성")
    void create() {
        Study study = new Study();
        assertNotNull(study);
    }

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