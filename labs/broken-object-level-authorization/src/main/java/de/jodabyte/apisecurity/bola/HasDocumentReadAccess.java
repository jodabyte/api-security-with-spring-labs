package de.jodabyte.apisecurity.bola;

import org.springframework.security.access.prepost.PostAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Security annotation to check if the authenticated user has read access to a document.
 * <p>
 * This annotation uses a post-authorization check to verify that the user is the owner of the document
 * and has the 'ROLE_MANAGER' authority.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PostAuthorize("@ownership.auth(returnObject, authentication) && hasRole('ROLE_MANAGER')")
public @interface HasDocumentReadAccess {

}
