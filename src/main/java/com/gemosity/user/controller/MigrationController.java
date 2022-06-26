package com.gemosity.user.controller;

import com.gemosity.user.dto.migration.MigrationResults;
import com.gemosity.user.service.MigrationService;
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
