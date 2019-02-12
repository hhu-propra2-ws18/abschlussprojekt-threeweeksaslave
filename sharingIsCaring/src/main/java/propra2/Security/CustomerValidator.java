package propra2.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import propra2.model.Customer;
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
        Customer user = (Customer) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        if (customerRepo.findByUsername(user.getUsername()).isPresent()) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "NotEmpty");
        if (user.getMail().length() < 6 || user.getMail().length() > 32) {
            errors.rejectValue("mail", "Size.userForm.mail");
        }
        if (customerRepo.findByMail(user.getMail()).isPresent()) {
            errors.rejectValue("mail", "Duplicate.userForm.mail");
        }
    }
}
