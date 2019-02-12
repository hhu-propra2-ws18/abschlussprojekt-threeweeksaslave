package propra2.repositories;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.model.User;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void userRepositoryTestFindById() {
        User user = new User();
        user.setCustomerId(1L);
        user.setUsername("userName");

        userRepository.save(user);

        Optional<User> userOptional = userRepository.findById(1L);

        Assertions.assertThat(userOptional.get().getUsername()).isEqualTo("userName");

        userRepository.delete(userOptional.get());
    }

    @Test
    public void userRepositoryTestFindByUserName() {
        User user = new User();
        user.setCustomerId(1L);
        user.setUsername("userName");
        user.setMail("email@gmx.de");

        userRepository.save(user);

        Optional<User> userOptional = userRepository.findByUsername("userName");

        Assertions.assertThat(userOptional.get().getMail()).isEqualTo("email@gmx.de");

        userRepository.delete(userOptional.get());
    }
}
