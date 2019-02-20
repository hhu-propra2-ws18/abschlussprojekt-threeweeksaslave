package propra2.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import propra2.database.Notification;
import propra2.database.OrderProcess;
import propra2.database.Product;
import propra2.repositories.NotificationRepository;
import propra2.repositories.OrderProcessRepository;

import java.util.Date;
import java.util.List;

public class NotificationHandler {
    @Autowired
    OrderProcessRepository orderProcessRepository;
    @Autowired
    NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 86400000)
    public void syncNotifications() {
        try {
            List<OrderProcess> processes = orderProcessRepository.findAll();
            Date date = new Date();
            java.sql.Date today = new java.sql.Date(date.getTime());
            for (OrderProcess orderProcess : processes) {
                String product = orderProcess.getProduct().getTitle();
                if(orderProcess.getToDate().compareTo(today) == -1){
                    String message = "You have to return your product: " + product + "tomorrow!";
                    createNotification(message, orderProcess);
                }
                else if (orderProcess.getToDate().compareTo(today) == 0){
                    String message = "You have to return your product: " + product + "today!";
                    createNotification(message, orderProcess);
                }
                else if(orderProcess.getToDate().compareTo(today) > 0) {
                    String message = "You forgot to return your product: " + product + ". Please return it as fast as possible!";
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
        notificationRepository.save(notification);
    }
}
