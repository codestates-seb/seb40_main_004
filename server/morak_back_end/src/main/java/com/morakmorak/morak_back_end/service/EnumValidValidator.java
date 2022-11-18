package com.morakmorak.morak_back_end.service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidValidator implements ConstraintValidator<EnumValid, Enum<?>> {
    @Override
    public void initialize(EnumValid constraint) {

    }

    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null;
    }
}