package dev.michaelgoldman.libraryapp;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;

@AnalyzeClasses(packages = "dev.michaelgoldman.libraryapp", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule coreIsFrameworkFree = noClasses()
            .that()
            .resideInAnyPackage("..domain..", "..application..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..", "jakarta.persistence..", "tools.jackson..", "com.fasterxml.jackson..");

    @ArchTest
    static final ArchRule dependenciesPointInward = noClasses()
            .that()
            .resideInAnyPackage("..domain..", "..application..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..adapter..", "..config..");

    @ArchTest
    static final ArchRule dtosStayInWebAdapter = classes()
            .that()
            .resideInAPackage("..adapter.in.web.api..")
            .should()
            .onlyHaveDependentClassesThat()
            .resideInAPackage("..adapter.in.web..");

    @ArchTest
    static final ArchRule entitiesStayInPersistenceAdapter =
            classes().that().areAnnotatedWith(Entity.class).should().resideInAPackage("..adapter.out.persistence..");
}
