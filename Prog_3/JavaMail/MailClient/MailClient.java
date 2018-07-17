import javax.swing.*;
import java.util.List;
import java.rmi.*;
import javax.naming.*;

class MailClient {
    public static void main(String[] args) {

        // ottiene il riferimento all'oggetto remoto (stub)
        MailboxManager mms = (MailboxManager) getObjFromServerGUI("mms");

        // ottiene la lista di caselle presenti sul server
        List<String> mailboxeNames = null;
        try {
            mailboxeNames = mms.getAllMailboxNames();
        } catch (RemoteException e){}

        // mostra e permette di scegliere una casella
        String nomeCasella = (String) JOptionPane.showInputDialog(
                                null,
                                "Seleziona una casella",
                                "Caselle",
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                mailboxeNames.toArray(),
                                null);

        if (nomeCasella == null) {
            System.exit(0);
        }

        // crea un finto server sul client per implementare Observer Observable tramite RMI
        ClientObservable fakeModel = new ClientObservable();
        Mailbox mb = null;

        try {
            // crea un finto osservatore sul client e lo passa al server, cosi che il server possa richiamare i suoi metodi
            // quando avvengono modifiche al modello in remoto
            ClientObserver fakeObserver = new ClientObserverLocal(fakeModel);
            // ottiene la casella e passa l'osservatore
            mb = mms.getMailboxAddObserver(nomeCasella, fakeObserver);
        } catch (RemoteException e){
            JOptionPane.showMessageDialog(null, "Errore durante l'acquisizione della casella", "Errore", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        if (mb != null){
            // mostro la gui del client
            ClientView v = new ClientView(nomeCasella, mb);
            fakeModel.addObserver(v);
        } else {
            JOptionPane.showMessageDialog(null, "Casella non trovata!", "Errore", JOptionPane.ERROR_MESSAGE);
        }

    }

    // richiede l'ip del server e ritorna l'oggetto stub relativo al nome dell'oggetto remoto passato
    private static Object getObjFromServerGUI(String className){
        boolean notConnected = true;
        Object obj = null;

        while (notConnected) {
            String serverIP = (String) JOptionPane.showInputDialog(
                                        null,
                                        "Inserisci l'indirizzo del server",
                                        "IP Server",
                                        JOptionPane.INFORMATION_MESSAGE,
                                        null,
                                        null,
                                        null);

            if (serverIP == null){
                System.exit(0);
            }



            try{
                Context namingContext = new InitialContext();
                // ottiene il riferimento all'oggetto remoto (stub)
                obj = (MailboxManager) namingContext.lookup("rmi://" + serverIP + ":7777/" + className);
                notConnected = false;
            } catch (NamingException e){
                JOptionPane.showMessageDialog(null, "Errore di connessione al server", "Errore", JOptionPane.ERROR_MESSAGE);
            }

        }
        //System.out.println("Connesso!");
        return obj;
    }

}

// con observer observable
/*
alla view è passato:
    - il model, per poter andare a leggere i dati e registrarsi come suo osservatore

al controller è passato:
    - il model, per poter modificare i dati

Il modello estende Observable
La vista implementa Observer

In questo modo è il modello a notificare direttamente la vista quando viene modificato
Il controller è del tutto ignaro della vista
*/

/*
    alla view serve:
        - il modello, per leggere i dati e registrarsi come suo osservatore
        - il controller, per registrare le azioni dei bottoni

    al controller serve:
        - il modello, per modificare i dati
*/
