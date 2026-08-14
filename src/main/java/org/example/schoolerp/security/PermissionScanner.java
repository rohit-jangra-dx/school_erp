package org.example.schoolerp.security;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 * PermissionScanner scans the base package for hasAuthority annotations in order to collect permissions
 */
@Component
@Slf4j
public class PermissionScanner {

    private static final Pattern AUTHORITY_EXPRESSION = Pattern.compile(
        "has(?:Any)?Authority\\(([^)]*)\\)"
    );

    private static final Pattern AUTHORITY_NAME = Pattern.compile(
        "['\"]([^'\"]+)['\"]"
    );

    // welp this guy needs to be inside properties
    private final String basePackage;

    public PermissionScanner(
        @Value("${school-erp.security.permission-scan-package}")
        String basePackage
    ) {
        this.basePackage = basePackage;
    }

    public Set<String> scan() {
        Set<String> permissions = new HashSet<>();

        ClassPathScanningCandidateComponentProvider scanner = 
            new ClassPathScanningCandidateComponentProvider(false);
        
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));

        for (BeanDefinition definition : scanner.findCandidateComponents(basePackage)) {

            try {
                Class<?> clazz = ClassUtils.forName(
                    definition.getBeanClassName(),
                    ClassUtils.getDefaultClassLoader()
                );
                
                scanClass(clazz, permissions);
            
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(
                    "Could not scan class: " + definition.getBeanClassName(),
                    e
                );
            }
        }

        return permissions;
    }

    private void scanClass(Class<?> clazz, Set<String> permissions) {
        PreAuthorize classAnnotation = 
            AnnotationUtils.findAnnotation(clazz, PreAuthorize.class);

        if (classAnnotation != null) {
            extractPermissions(classAnnotation, permissions);
        }

        for (var method : clazz.getDeclaredMethods()) {
            PreAuthorize methodAnnotation = 
                AnnotationUtils.findAnnotation(method, PreAuthorize.class);
            
            if (methodAnnotation != null) {
                extractPermissions(methodAnnotation, permissions);
            }
        }
    }

    private void extractPermissions(PreAuthorize annotation, Set<String> permissions) {
        Matcher expressionMatcher = 
            AUTHORITY_EXPRESSION.matcher(annotation.value());

        while (expressionMatcher.find()) {
            
            String arguments = expressionMatcher.group(1);

            Matcher authorityMatcher = 
                AUTHORITY_NAME.matcher(arguments);

            while (authorityMatcher.find()) {
                permissions.add(authorityMatcher.group(1));
            }
        }
    }
}
