package com.yello.server.global.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Resolves SecurityContextHolder.getContext().getAuthentication().getDetails();
 * <p>
 * if valid request doesn't include Authorization field in header, return null
 *
 * @see com.yello.server.global.common.annotation.AccessTokenUserResolver
 * @see com.yello.server.domain.authorization.filter.JwtFilter
 * @see com.yello.server.domain.authorization.filter.JwtExceptionFilter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessTokenUser {

}
