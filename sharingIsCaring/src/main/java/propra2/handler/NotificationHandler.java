package propra2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import propra2.database.Notification;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.model.OrderProcessStatus;
import propra2.repositories.NotificationRepository;
import propra2.repositories.OrderProcessRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static propra2.model.OrderProcessStatus.ACCEPTED;

@Component
public class NotificationHandler {
    @Autowired
    OrderProcessRepository orderProcessRepository;
    @Autowired
    NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 20000)
    public void syncNotifications() {
        try {
            List<OrderProcess> processes = orderProcessRepository.findAll();
            notificationRepository.deleteAll();
            Date date = new Date();
            java.sql.Date today = new java.sql.Date(date.getTime());
            for (OrderProcess orderProcess : processes) {
                String product = orderProcess.getProduct().getTitle();
                if(ChronoUnit.DAYS.between(LocalDate.parse(orderProcess.getToDate().toString()),LocalDate.parse(today.toString())) == -1 && orderProcess.getStatus().equals(ACCEPTED)){
                    String message = "You have to return your product: '" + product + "' tomorrow!";
                    createNotification(message, orderProcess);
                }
                else if (orderProcess.getToDate().toString().equals(today.toString()) && orderProcess.getStatus().equals(ACCEPTED)){
                    String message = "You have to return your product: '" + product + "' today!";
                    createNotification(message, orderProcess);
                }
                else if(orderProcess.getToDate().compareTo(today) < 0 && orderProcess.getStatus().equals(ACCEPTED)) {
                    String message = "You forgot to return your product: '" + product + "'. Please return it as fast as possible!";
                    createNotification(message, orderProcess);
                }
            }
        }
        catch (Exception e) {
            e.getMessage();
        }
    }

    private void createNotification(String message, OrderProcess orderProcess){
        Notification notification = new Notification();
        notification.setNotification(message);
        notification.setBorrowerId(orderProcess.getRequestId());
        notification.setProcessId(orderProcess.getId());
        notificationRepository.save(notification);
    }
}
