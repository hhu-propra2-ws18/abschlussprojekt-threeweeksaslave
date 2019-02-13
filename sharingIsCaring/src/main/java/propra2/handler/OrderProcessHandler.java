package propra2.handler;

import propra2.database.OrderProcess;
import propra2.repositories.OrderProcessRepository;

public class OrderProcessHandler {

    public void updateOrderProcess(OrderProcess orderProcess, OrderProcessRepository orderProcessRepository) {
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
