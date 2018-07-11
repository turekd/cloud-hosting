package it.dturek.cloudhosting.form.validator.rule;

@FunctionalInterface
public interface UniqueValidatorRule {

    boolean isUnique(String value);

}
