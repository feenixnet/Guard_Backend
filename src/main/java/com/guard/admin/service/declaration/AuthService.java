package com.guard.admin.service.declaration;

import com.guard.admin.service.impl.UserDetailsImpl;

public interface AuthService {

    UserDetailsImpl getInfo();

    boolean isDuplicate(String email);
}
