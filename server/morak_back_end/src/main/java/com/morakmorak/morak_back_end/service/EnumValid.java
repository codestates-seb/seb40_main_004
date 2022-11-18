package com.morakmorak.morak_back_end.service;




import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention( RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy=EnumValidValidator.class)
public @interface EnumValid {
    String message() default "Invalid Enum";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}