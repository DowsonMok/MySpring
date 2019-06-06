package org.spring.service.v5;


import org.spring.beans.factory.annotation.Autowired;
import org.spring.dao.v5.AccountDao;
import org.spring.dao.v5.ItemDao;
import org.spring.stereotype.Component;
import org.spring.util.MessageTracker;

@Component(value="petStore")
public class PetStoreService {		
	@Autowired
	AccountDao accountDao;
	@Autowired
	ItemDao itemDao;
	
	public PetStoreService() {		
		
	}
	
	public ItemDao getItemDao() {
		return itemDao;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}
	
	public void placeOrder(){
		System.out.println("place order");
		MessageTracker.addMsg("place order");
		
	}	
	public void placeOrderWithException(){
		throw new NullPointerException();
	}
}
