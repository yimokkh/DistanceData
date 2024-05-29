package org.example.distancedata.aspect;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("checkstyle:MissingJavadocType")
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AspectAnnotation {
}