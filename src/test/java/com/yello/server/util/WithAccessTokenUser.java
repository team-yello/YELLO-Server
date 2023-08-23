package com.yello.server.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccessTokenUserSecurityFactory.class)
public @interface WithAccessTokenUser {

}
