package com.gemosity.identity.controller;

import com.gemosity.identity.dto.migration.MigrationResults;
import com.gemosity.identity.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MigrationController {

    private MigrationService migrationService;

    @Autowired
    public MigrationController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @GetMapping(path = "/migrate")
    public MigrationResults startMigration() {
        MigrationResults migrationResults = null;
        migrationResults = migrationService.migrateUsers();
        return migrationResults;
    }
}
