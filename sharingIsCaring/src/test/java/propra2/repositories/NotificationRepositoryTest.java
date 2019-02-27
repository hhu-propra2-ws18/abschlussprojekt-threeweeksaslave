package propra2.repositories;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.database.Notification;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NotificationRepositoryTest {

    @Autowired
    NotificationRepository notificationRepository;

    @Test
    public void testFindById(){
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setNotification("notification");

        notificationRepository.save(notification);

        Optional<Notification> notification1 = notificationRepository.findById(notification.getId());

        Assertions.assertThat(notification1.get().getNotification()).isEqualTo("notification");

        notificationRepository.delete(notification1.get());

    }

    @Test
    public void testFindByBorrowerId(){
        Notification notification = new Notification();
        notification.setBorrowerId(2L);
        notification.setNotification("notification");

        notificationRepository.save(notification);

        List<Notification> notification1 = notificationRepository.findAllByBorrowerId(2L);

        Assertions.assertThat(notification1.size()).isEqualTo(1);
        Assertions.assertThat(notification1.get(0).getNotification()).isEqualTo("notification");

        notificationRepository.delete(notification);
    }

    @Test
    public void testFindByProcessId(){
        Notification notification = new Notification();
        notification.setProcessId(3L);
        notification.setNotification("notification");

        notificationRepository.save(notification);

        Optional<Notification> notification1 = notificationRepository.findByProcessId(3L);

        Assertions.assertThat(notification1.get().getNotification()).isEqualTo("notification");

        notificationRepository.delete(notification);
    }
}
