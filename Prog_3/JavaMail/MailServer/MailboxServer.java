/*
    Casella mail
*/

import java.rmi.*;
import java.util.*;

public class MailboxServer extends Observable implements Mailbox { // MODEL
    private List<Email> messaggi = new ArrayList<Email>();
    private String owner;
    private MailboxManagerServer manager;

    MailboxServer(MailboxManagerServer manager, String owner) throws RemoteException {
        this.manager = manager;
        this.owner = owner;

        //addEmail(new Email("Lory", "Tommy", "Universita'", "andiamo a lezione domani?", 1, "27/06/2012"));
    }

    // ritorna tutti i messaggi di questa casella
    public synchronized List<Email> getAllMessages() throws RemoteException {
        return messaggi;
    }

    // ritorna il proprietario di questa casella mail
    public String getOwner() throws RemoteException {
        return owner;
    }

    // crea e invia una nuova mail
    public synchronized void sendEmail(Set<String> destinatari, String argomento, String testo, int priorita)  throws RemoteException  {
        manager.sendEmail(new Email(owner, destinatari, argomento, testo, priorita, new Date()));
    }

    // aggiunge una mail alla casella
    public synchronized void addEmail(Email email) throws RemoteException{
        messaggi.add(email);
        setChanged();
        if (email.getDestinatari().contains(owner)){
            notifyObservers(email); // se e' una mail ricevuta notifica al ricevente che e' in arrivo una nuova mail
        } else {
            notifyObservers();
        }
    }

    // ritorna la mail con if passato (se esiste)
    public synchronized Email getEmail(int id) throws RemoteException {
        for (Email email : messaggi){
            if (email.getID() == id){
                return email;
            }
        }
        return null;
    }

    // cancella la mail con id passato (se esiste)
    public synchronized void deleteEmail(int id) throws RemoteException {
        Email email = getEmail(id);
        if (email != null && messaggi.size() > 0){
            System.out.println(owner + " ha cancellato l'email: " + email.getID());
            messaggi.remove(email);
            setChanged();
            notifyObservers();
        }
    }

    // svuota la casella
    public synchronized void deleteAllEmails() throws RemoteException {
        if (messaggi.size() > 0){
            System.out.println(owner + " ha svuotato la casella");
            messaggi.clear();
            setChanged();
            notifyObservers();
        }
    }

    public String toString(){
        String res = "";
        for(Email email : messaggi){
            res += email.toString();
        }
        return res;
    }

}
