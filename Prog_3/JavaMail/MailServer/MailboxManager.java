import java.rmi.*;
import java.util.*;

public interface MailboxManager extends Remote {
    public Mailbox getMailboxAddObserver(String emailAddress, ClientObserver client) throws RemoteException;
    public Mailbox getMailbox(String emailAddress) throws RemoteException;
    public List<String> getAllMailboxNames() throws RemoteException;
}
