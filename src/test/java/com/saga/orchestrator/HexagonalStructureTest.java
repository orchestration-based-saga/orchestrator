package com.saga.orchestrator;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class HexagonalStructureTest {

    @Test
    public void domain_should_not_depend_on_other_packages() {
        JavaClasses classes = new ClassFileImporter().importPackages("com.saga.orchestrator");
        ArchRule rule = noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..application..")
                .orShould()
                .dependOnClassesThat()
                .resideInAPackage("..infra..")
                .orShould()
                .dependOnClassesThat()
                .resideInAPackage("org.springframework..");
        rule.check(classes);
    }
}
