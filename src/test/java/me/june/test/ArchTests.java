package me.june.test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = TestApplication.class)
class ArchTests {

    /**
     * ArchTest 애노테이션을 사용하면, @DisplayName 을 지정할 수 없다는 점이 단점
     *
     * ArchUnit 은 Junit Engine 을 확장해서, ArchUnit JUnit Module 을 만들었다 (순수한 Jupiter Engine 을 사용하는 것이 아님)
     * -> 일반적으로 ExtendWith (Extension 확장) Register, RegisterExtension 을 활용한 Extension 확장 방식을 사용
     */

    @ArchTest
    ArchRule domainPackageRule = classes().that().resideInAPackage("..domain..")
        .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..study..", "..member..", "..domain..");

    @ArchTest
    ArchRule memberPackageRule = noClasses().that().resideInAPackage("..domain..")
        .should().accessClassesThat().resideInAPackage("..member..");

    @ArchTest
    ArchRule studyPackageRule = noClasses().that().resideOutsideOfPackage("..study..")
        .should().accessClassesThat().resideInAPackage("..study..");

    @ArchTest
    ArchRule freeOfCycles = slices().matching("..test.(*)..")
        .should().beFreeOfCycles();

    @Test
    void packageDependencyTests() {
        // 클래스 목록 읽어오기
        JavaClasses classes = new ClassFileImporter().importPackages("me.june.test");

        /**
         * ..domain.. 패키지에 있는 클래스는 ..study.., ..member.., ..domain에서 참조 가능.
         * ..member.. 패키지에 있는 클래스는 ..study..와 ..member..에서만 참조 가능.
         * (반대로) ..domain.. 패키지는 ..member.. 패키지를 참조하지 못한다.
         * ..study.. 패키지에 있는 클래스는 ..study.. 에서만 참조 가능.
         * 순환 참조 없어야 한다.
         */

        // domain 패키지 는 study, member, domain 패키지에서만 참조할 수 있다.
        ArchRule domainPackageRule = classes().that().resideInAPackage("..domain..")
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage("..study..", "..member..", "..domain..");

        domainPackageRule.check(classes);

        // domain 패키지에 있는 어떤 클래스도 member 클래스내에 있는 클래스에 접근할 수 없다.
        ArchRule memberPackageRule = noClasses().that().resideInAPackage("..domain..")
            .should().accessClassesThat().resideInAPackage("..member..");

        memberPackageRule.check(classes);

        // study 패키지 외에서는 study 패키지에 접근할 수 없다.
        ArchRule studyPackageRule = noClasses().that().resideOutsideOfPackage("..study..")
            .should().accessClassesThat().resideInAPackage("..study..");

        studyPackageRule.check(classes);

        // 순환참조 체크
        ArchRule freeOfCycles = slices().matching("..test.(*)..")
            .should().beFreeOfCycles();

        freeOfCycles.check(classes);
    }
}
