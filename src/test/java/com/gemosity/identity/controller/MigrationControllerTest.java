package com.gemosity.identity.controller;

import com.gemosity.identity.dto.migration.MigrationResults;
import com.gemosity.identity.service.MigrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MigrationControllerTest {

    private MigrationController migrationController;

    @Mock
    private MigrationService migrationService;


    @BeforeEach
    void setUp() {
        migrationController = new MigrationController(migrationService);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void startMigrationEndpoint() {
        MigrationResults serviceResults = new MigrationResults();
        serviceResults.setTotalUsersMigrated(2);
        Mockito.when(migrationService.migrateUsers()).thenReturn(serviceResults);
        MigrationResults migrationResults = migrationController.startMigration();
        Assertions.assertEquals(2, migrationResults.getTotalUsersMigrated());
    }
}
