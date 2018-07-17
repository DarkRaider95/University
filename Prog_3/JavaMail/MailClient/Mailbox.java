import java.rmi.*;
import java.util.*;

public interface Mailbox extends Remote {
    public List<Email> getAllMessages() throws RemoteException;
    public String getOwner() throws RemoteException;
    public void sendEmail(Set<String> destinatari, String argomento, String testo, int priorita) throws RemoteException;
    public Email getEmail(int id) throws RemoteException;
    public void deleteEmail(int id) throws RemoteException;
    public void deleteAllEmails() throws RemoteException;
}
