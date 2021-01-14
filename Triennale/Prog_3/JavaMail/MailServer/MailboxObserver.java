/*
    Classe che osserva i cambiamenti della rispettiva mailbox
    inoltre i suoi metodi vengono richiamati dal client 
    per modificare la mailbox
*/
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class MailboxObserver extends UnicastRemoteObject implements Mailbox, Observer {

    private MailboxServer mailbox;
    private ClientObserver client;

    MailboxObserver(MailboxServer mailbox, ClientObserver client) throws RemoteException {
        this.mailbox = mailbox;
        this.client = client;
        mailbox.addObserver(this);
    }

    public synchronized List<Email> getAllMessages() throws RemoteException {
        return mailbox.getAllMessages();
    }

    public String getOwner() throws RemoteException {
        return mailbox.getOwner();
    }

    public synchronized void sendEmail(Set<String> destinatari, String argomento, String testo, int priorita)  throws RemoteException  {
        mailbox.sendEmail(destinatari, argomento, testo, priorita);
    }

    public synchronized void addEmail(Email email) throws RemoteException {
        mailbox.addEmail(email);
    }

    public synchronized Email getEmail(int id) throws RemoteException {
        return mailbox.getEmail(id);
    }

    public synchronized void deleteEmail(int id) throws RemoteException {
        mailbox.deleteEmail(id);
    }

    public synchronized void deleteAllEmails() throws RemoteException {
        mailbox.deleteAllEmails();
    }

    public String toString(){
        return mailbox.toString();
    }

    /*viene richiamato quando ci sono cambiamenti nella mailbox, 
      avvia un nuovo thread che chiama update sul client*/
    public void update(Observable ob, Object extra) {
        //try {
            new Thread(new Runnable(){
                public void run(){
                    try {
                        client.update(extra);
                    } catch (RemoteException e){}
                }
            }).start();
        //} catch (RemoteException e){}
    }

}
