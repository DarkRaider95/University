/*
    Gestore delle caselle mail sul server
*/

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

class MailboxManagerServer extends UnicastRemoteObject implements MailboxManager {

    private List<MailboxServer> mailboxes = new ArrayList<MailboxServer>();

    // crea alcune caselle mail
    public MailboxManagerServer() throws RemoteException {
        mailboxes.add(new MailboxServer(this, "lorenzo.santina@gmail.com"));
        mailboxes.add(new MailboxServer(this, "tommaso.toscano@gmail.com"));
        mailboxes.add(new MailboxServer(this, "serena.santina@gmail.com"));
        mailboxes.add(new MailboxServer(this, "marina.cobetto@gmail.com"));
        mailboxes.add(new MailboxServer(this, "bruno.santina@gmail.com"));
    }

    // ritorna la casella del proprietario passato e aggiunge un client come osservatore di questa casella [necessario per Observer Observer tramite RMI]
    public Mailbox getMailboxAddObserver(String emailAddress, ClientObserver client) throws RemoteException {
        System.out.println("Utente: " + emailAddress);
        MailboxServer mb = (MailboxServer) getMailbox(emailAddress);
        if (mb != null && client != null) {
            return new MailboxObserver(mb, client);
        } else {
            return null;
        }
    }

    // ritorna la casella in base al proprietario passato
    public Mailbox getMailbox(String emailAddress) throws RemoteException {
        for (Mailbox mb : mailboxes){
            if (emailAddress.equals(mb.getOwner())){
                return mb;
            }
        }
        return null;
    }

    // ritorna tutti i proprietari delle casella
    public List<String> getAllMailboxNames() throws RemoteException {
        List<String> names = new ArrayList<String>();
        for (MailboxServer mb : mailboxes){
            names.add(mb.getOwner());
        }
        return names;
    }

    // smista una nuova mail nelle caselle dei destinatari
    public void sendEmail(Email email) throws RemoteException {

        String mittente = email.getMittente();

        // aggiunge la mail anche alla casella del mittente
        MailboxServer senderMailbox = (MailboxServer) getMailbox(mittente);
        senderMailbox.addEmail(email);

        System.out.println("Nuova email da: " + mittente + ", Argomento: " + email.getArgomento());

        Set<String> destArray = email.getDestinatari();
        if (destArray.size() > 0){
            for (String dest : destArray){
                MailboxServer mb = (MailboxServer) getMailbox(dest);
                if (mb != null){
                    mb.addEmail(email); // aggiunge la mail alla casella di questo destinatario
                } else { // se il destinatario non esiste, mail di errore
                    sendError(senderMailbox, "Indirizzo email inesistente", "Messaggio automatico.\nL'indirizzo '" + dest + "' non esiste su questo server!\nNon rispondere a questa email.");
                }
            }
        } else { // se non e' fornito nessun destinatario, mail di errore
            sendError(senderMailbox, "Indirizzo email non fornito", "Devi fornire almeno un indirizzo di destinazione!");
        }
    }

    // invia una mail di errore al mittente
    private void sendError(MailboxServer sender, String arg, String text) throws RemoteException {
        Set<String> dest = new HashSet<String>();
        dest.add(sender.getOwner());
        sender.addEmail(new Email("noreply@gmail.com", dest, arg, text, 1, new Date()));
    }
}
