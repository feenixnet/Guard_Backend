package com.guard.admin.service.declaration;

import com.guard.admin.database.entities.Setting;
import com.guard.admin.payload.request.SettingRequest;

public interface SettingService {

    Setting put(SettingRequest setting);

    Setting get();
}
