package com.poetry.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
@Documented
public @interface StrongPassword {
    String message() default "密码至少8位，且包含字母和数字";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
