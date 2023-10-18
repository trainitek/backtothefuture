package com.trainitek.backtothefuture.arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Date;

/**
 * Uncomment code in {@link com.trainitek.backtothefuture.HowDoWeCreateDates#main(String[])} to see this test in action.
 */
public class DateRulesArchTest {
    JavaClasses prodClasses = new ClassFileImporter().withImportOption(new DoNotIncludeTests()).importPackages("com.trainitek");

    @Test
    void checkThatClockIsUsedToCreateDateInProdCode() {
        ArchRuleDefinition.noClasses()
                .should().callMethod(LocalDate.class, "now")
                .orShould().callMethod(LocalDate.class, "now", ZoneId.class)
                .orShould().callMethod(LocalDateTime.class, "now")
                .orShould().callMethod(LocalDateTime.class, "now", ZoneId.class)
                .orShould().callMethod(ZonedDateTime.class, "now")
                .orShould().callMethod(ZonedDateTime.class, "now", ZoneId.class)
                .orShould().callMethod(OffsetDateTime.class, "now")
                .orShould().callMethod(OffsetDateTime.class, "now", ZoneId.class)
                .orShould().callMethod(Instant.class, "now")
                .orShould().callConstructor(Date.class)
                .because("In prod code we should ALWAYS use Clock to create dates (Our rules in HERE_LIK_TO_WIKI )")
                .check(prodClasses);
    }
}
