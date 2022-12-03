package com.morakmorak.morak_back_end.service;




import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention( RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy=EnumValidValidator.class)
public @interface EnumValid {
    String message() default "Invalid Enum";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}