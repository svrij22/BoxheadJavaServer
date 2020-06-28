package nl.hu.bep.webservices;

import nl.hu.bep.model.ServerManager;
import nl.hu.bep.security.Account;
import org.apache.catalina.Server;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.nimbus.State;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StateWriterTest {

    @Test
    void stateTest() {

        //Schrijf alle accounts weg
        StateWriter.writeObjects("accountdatatest", Account.geefAlle());
        assertEquals(0, Account.geefAlle().size());

        Account account = new Account("svrij22", "1234", "Admin", null);
        Account account2 = new Account("svrij23", "1234", "Admin", null);
        Account account3 = new Account("svrij24", "1234", "Admin", null);

        StateWriter.writeObjects("accountdata", Account.geefAlle());
        assertEquals(3, Account.geefAlle().size());

        var readObject =  StateWriter.readObjects("accountdatatest");
        if (readObject != null) {
            Account.setAccounts((ArrayList<Account>)readObject);
        }
        assertEquals(3, Account.geefAlle().size());
    }

}