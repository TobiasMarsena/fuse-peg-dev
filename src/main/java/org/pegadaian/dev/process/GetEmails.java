package org.pegadaian.dev.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.pegadaian.dev.model.Account;
import org.pegadaian.dev.model.Accounts;

public class GetEmails implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		List<Account> accounts = exchange.getIn().getBody(Accounts.class).getAccounts();
		List<String> emails = new ArrayList<String>();
		for (Account account : accounts) {
			emails.add(account.getUsers().get(0).getEmail());
		}
		exchange.getIn().setHeader("emailOfAccounts", emails);
	}

}
