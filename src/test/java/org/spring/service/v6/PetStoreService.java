package org.spring.service.v6;

import org.spring.stereotype.Component;
import org.spring.util.MessageTracker;

@Component(value = "petStoreService")
public class PetStoreService implements IPetStoreService {
    public PetStoreService() {
    }

    public void placeOrder() {
        System.out.println("place order");
        MessageTracker.addMsg("place order");
    }
}