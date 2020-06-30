package org.pegadaian.dev.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.pegadaian.dev.model.Account;
import org.pegadaian.dev.model.Accounts;

public class FindAccount implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		List<String> emailOfAccounts = (List<String>) exchange.getIn().getHeader("emailOfAccounts");
		
		List<Account> accounts = exchange.getIn().getBody(Accounts.class).getAccounts();
		List<String> emails = new ArrayList<String>();
		for (Account account : accounts) {
			emails.add(account.getUsers().get(0).getEmail());
		}
		emails.removeAll(emailOfAccounts);
		exchange.getIn().setHeader("emailOfAccounts", emails);

		for (Account account : accounts) {
			if (account.getUsers().get(0).getEmail().equals(emails.get(0))) {
				exchange.getIn().setBody(account, Account.class);
			}
		}
	}

}
