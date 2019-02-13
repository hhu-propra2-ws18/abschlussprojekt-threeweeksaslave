package propra2.model;

import lombok.Data;

@Data
public class UserRegistration {
    private String userName;
    private String emailAddress;
    private String password;
    private String passwordConfirm;
}
