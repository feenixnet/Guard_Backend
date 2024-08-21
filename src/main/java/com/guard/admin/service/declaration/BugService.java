package com.guard.admin.service.declaration;

import com.guard.admin.service.impl.UserDetailsImpl;

public interface BugService {
    public Boolean create(UserDetailsImpl userDetails, String description);
}
