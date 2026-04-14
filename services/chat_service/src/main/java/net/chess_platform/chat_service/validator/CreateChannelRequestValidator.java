package net.chess_platform.chat_service.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.util.StringUtils;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import net.chess_platform.chat_service.dto.CreateChannelRequest;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.validator.CreateChannelRequestValidator.CreateChannelRequestConstraint;

public class CreateChannelRequestValidator
        implements ConstraintValidator<CreateChannelRequestConstraint, CreateChannelRequest> {

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = { CreateChannelRequestValidator.class })
    public @interface CreateChannelRequestConstraint {
        String message() default "";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    @Override
    public boolean isValid(CreateChannelRequest request, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        var recipients = request.recipients();
        if (recipients == null || recipients.isEmpty()) {
            context.buildConstraintViolationWithTemplate("recipients cannot be empty").addConstraintViolation();
            return false;
        }

        var type = request.type();
        if (!StringUtils.hasText(type)) {

            if (recipients.size() == 1) {
                return true;
            }

            context.buildConstraintViolationWithTemplate("type can only be empty if there is only one recipient")
                    .addConstraintViolation();
            return false;
        }

        try {
            var t = Channel.Type.valueOf(type);
            if (t == Channel.Type.DM && recipients.size() > 1) {
                context.buildConstraintViolationWithTemplate("DM channels can only have one recipient")
                        .addConstraintViolation();
                return false;
            }
        } catch (IllegalArgumentException e) {
            context.buildConstraintViolationWithTemplate("invalid type. must be DM or GROUP").addConstraintViolation();
            return false;
        }

        return true;
    }

}
