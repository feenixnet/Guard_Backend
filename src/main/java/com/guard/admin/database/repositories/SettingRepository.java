package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Site;
import com.guard.admin.database.entities.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Integer> {
}
