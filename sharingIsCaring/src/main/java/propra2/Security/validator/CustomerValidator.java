package propra2.Security.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import propra2.database.Customer;
import propra2.model.UserRegistration;
import propra2.repositories.CustomerRepository;

@Component
public class CustomerValidator implements Validator {
    @Autowired
    CustomerRepository customerRepo;

    @Override
    public boolean supports(Class<?> aClass) {
        return Customer.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserRegistration user = (UserRegistration) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty");
        if (user.getUserName().length() < 6 || user.getUserName().length() > 32) {
            errors.rejectValue("userName", "Size.userForm.userName");
        }
        if (customerRepo.findByUsername(user.getUserName()).isPresent()) {
            errors.rejectValue("userName", "Duplicate.userForm.userName");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailAddress", "NotEmpty");
        if (user.getEmailAddress().length() < 6 || user.getEmailAddress().length() > 32) {
            errors.rejectValue("emailAddress", "Size.userForm.emailAddress");
        }
        if (customerRepo.findByMail(user.getEmailAddress()).isPresent()) {
            errors.rejectValue("emailAddress", "Duplicate.userForm.emailAddress");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "NotEmpty");
        if(user.getPassword().length() < 6 || user.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.userForm.password");
        }
        if(!user.getPassword().equals(user.getPasswordConfirm())){
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}
