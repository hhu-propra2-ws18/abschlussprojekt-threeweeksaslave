package propra2.handler;

import propra2.model.OrderProcess;
import propra2.model.Reservation;
import propra2.repositories.OrderProcessRepository;

import java.util.List;
import java.util.Optional;

public class OrderProcessHandler {

    public void updateOrderProcess(Long id, OrderProcess orderProcess, OrderProcessRepository orderProcessRepository) {
        Optional<OrderProcess> orderProcessOptional = orderProcessRepository.findById(id);
        List<String> messages = orderProcessOptional.get().getMessages();

        orderProcess.addMessages(messages);

        switch (orderProcess.getStatus()) {
            case DENIED:
                orderProcessRepository.save(orderProcess);
                break;
            case ACCEPTED:
                orderProcessRepository.save(orderProcess);
                //TODO: Propay Kautionsbetrag blocken
                break;
            case FINISHED:
                orderProcessRepository.save(orderProcess);
                //TODO: Konfliktlöser/ProPay Betrag abbuchen
                break;
            default:
                throw new IllegalArgumentException("Bad Request: Unknown Process Status");
        }
    }
}
