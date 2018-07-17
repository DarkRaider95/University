/*
    Questa classe gestisce la finestra principale del client
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.*;
import java.rmi.RemoteException;
import java.util.concurrent.locks.*;

class ClientView extends JFrame implements Observer {
    private String owner;
    private Mailbox casella;
    private java.util.List<Email> messaggi;
    DefaultTableModel listaMessaggiModel;

    //tabella per mostrare la lista di mail da selezionare
    private JTable listaMessaggi = new JTable();
    
    //pannello per le mail da mostrare
    EmailPanel info = new EmailPanel();
    
    //bottoni
    JButton cancella, svuota, rispondi, rispondi_tutti, inoltra;
    
    //lock di lettura e scrittura
    private Lock readLockMsg;
    private Lock writeLockMsg;

    public ClientView(String owner, Mailbox casella) {
        this.owner = owner;
        this.casella = casella;

        ClientController controllerMail = new ClientController(owner, casella, this);

        //inizializza il lock per lettura e scrittura
        ReadWriteLock rwl = new ReentrantReadWriteLock();
        readLockMsg = rwl.readLock();
        writeLockMsg = rwl.writeLock();

        setTitle("MailClient");
        setLayout(new BorderLayout());
        setSize(1000, 500);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);  // centra il frame
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int option = JOptionPane.showConfirmDialog(e.getWindow(), "Sei sicuro di voler uscire?", "Esci", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });

        // SET ACCOUNT INFO
        JLabel account = new JLabel(owner);
        account.setPreferredSize(new Dimension(50, 50));
        account.setHorizontalAlignment(JLabel.CENTER);
        add(account, BorderLayout.NORTH);

        // LISTA MESSAGGI
        //listaMessaggi.setPreferredSize(new Dimension(500, 50));
        String[] cols = {"Mittente", "Argomento", "Data"};
        listaMessaggiModel = new DefaultTableModel(cols, 0);
        listaMessaggi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // single selection
        listaMessaggi.setFocusable(false); //
        listaMessaggi.getTableHeader().setReorderingAllowed(false);
        listaMessaggi.setDefaultEditor(Object.class, null); // desable cell editing
        listaMessaggi.getSelectionModel().addListSelectionListener(controllerMail);
        listaMessaggi.setModel(listaMessaggiModel);
        add(new JScrollPane(listaMessaggi), BorderLayout.WEST);

        // VISUALIZZAZIONE MESSAGGIO
        add(info, BorderLayout.CENTER);

        // CONTROLLI
        JPanel controls = new JPanel(new FlowLayout());
        //JButton aggiorna = new JButton("Aggiorna_RIMUOVI");
        JButton nuova = new JButton("Nuova");
        svuota = new JButton("Svuota");
        cancella = new JButton("Cancella");
        rispondi = new JButton("Rispondi");
        rispondi_tutti = new JButton("Rispondi a tutti");
        inoltra = new JButton("Inoltra");

        //aggiorna.addActionListener(controllerMail);
        nuova.addActionListener(controllerMail);

        svuota.addActionListener(controllerMail);
        cancella.addActionListener(controllerMail);
        rispondi.addActionListener(controllerMail);
        rispondi_tutti.addActionListener(controllerMail);
        inoltra.addActionListener(controllerMail);

        //controls.add(aggiorna);
        controls.add(cancella);
        controls.add(svuota);
        controls.add(nuova);
        controls.add(inoltra);
        controls.add(rispondi);
        controls.add(rispondi_tutti);
        add(controls, BorderLayout.SOUTH);

        setVisible(true); // va messo qui altrimenti si blocca chiamando prima updateView
        updateView();
    }

    //mostra il messaggio selezionato
    public void showEmailIndex(int i){
        readLockMsg.lock();
        info.setMail(messaggi.get(i));
        readLockMsg.unlock();
        toggleButtons();
    }

    //restituisce la mail selezionata dalla lista
    public Email getSelectedEmail(){
        int i = getSelectedEmailIndex();
        if (i > -1){
            readLockMsg.lock();
            Email email = messaggi.get(i);
            readLockMsg.unlock();
            return email;
        }
        return null;
    }

    //restituisce l'indice della lista selezionato
    public synchronized int getSelectedEmailIndex(){
        return listaMessaggi.getSelectedRow();
    }

    //oscura i bottoni quando necessario
    public synchronized void toggleButtons(){
        readLockMsg.lock();
        int size = messaggi.size();
        readLockMsg.unlock();

        //se ci sono mail abilita il tasto svuota
        if (size > 0){
            svuota.setEnabled(true);

            Email mail = getSelectedEmail();

            //se ci sono mail selezionate abilita cancella e inoltra
            if (mail != null){
                cancella.setEnabled(true);
                inoltra.setEnabled(true);

                Boolean isSent = mail.getMittente().equals(owner);

                /*se la mail selezionata ha il mittente uguale all'owner della cassella disabilita
                  disabilita il tasto rispondi e rispondi a tutti */
                if (isSent){
                    rispondi.setEnabled(false);
                    rispondi_tutti.setEnabled(false);
                } else {
                    rispondi.setEnabled(true);
                    rispondi_tutti.setEnabled(true);
                }

            //non ci sono mail selezionate disabilita tutto tranne svuota
            } else {
                cancella.setEnabled(false);
                rispondi.setEnabled(false);
                rispondi_tutti.setEnabled(false);
                inoltra.setEnabled(false);

            }

        //non ci sono mail disabilita tutto
        } else {
            svuota.setEnabled(false);
            cancella.setEnabled(false);
            rispondi.setEnabled(false);
            rispondi_tutti.setEnabled(false);
            inoltra.setEnabled(false);
        }
    }

    private Object[] emailToArray(Email email){
        Object[] array = new Object[3];
        array[0] = email.getMittente();
        array[1] = email.getArgomento();
        array[2] = email.getData().toString();
        return array;
    }

    //aggiorna la vista
    public void updateView(){
        new Thread(){
            public void run(){
                java.util.List<Email> nuovi_messaggi = null;
                try {
                    //Thread.sleep(4000);
                    nuovi_messaggi = casella.getAllMessages(); // copia LOCALE della lista di messaggi
                } catch (RemoteException e){
                    JOptionPane.showMessageDialog(null, "Errore scaricando le email", "Errore", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    System.exit(-1);
                } //catch (InterruptedException e){}

                //riordina i messaggi per data
                Collections.sort(nuovi_messaggi, new Comparator<Email>(){
                    public int compare(Email e1, Email e2){
                        return e2.getData().compareTo(e1.getData());
                    }
                });

                //aggiorna la lista mostrata nella tabella
                writeLockMsg.lock();
                messaggi = nuovi_messaggi;
                writeLockMsg.unlock();

                //la gui viene aggiornata dopo che i messaggi sono stati scaricati
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateTableModel();
                        toggleButtons();
                        info.setEmpty();
                    }
                });

            }
        }.start();
    }

    //aggiorna il contenuto della tabella
    private synchronized void updateTableModel(){
            listaMessaggiModel.setRowCount(0);
            for(Email email : messaggi){
                Object[] mailArray = emailToArray(email);
                if (mailArray != null)
                    listaMessaggiModel.addRow(mailArray);
            }
    }

    // METODO RICHIAMATO QUANDO UN OBSERVABLE (di cui si è observer) LANCIA notifyAll
    public void update(Observable ob, Object extra){ // ob permette di distinguere quale observable ha lanciato notifyAll, nel caso si fosse observer di piu observable
        updateView();
        //se c'è una mail dentro extra mostro un alert con le informazioni della mail
        if (extra instanceof Email) { // instanceof
            Email m = (Email) extra;
            String mit = m.getMittente();
            String arg = m.getArgomento();
            JOptionPane.showMessageDialog(this, "Mittente: " + mit + "\nArgomento: " + arg, "Nuovo messaggio!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /*
    public void showSelectedEmail(){
        //System.out.println("showSelectedEmail");
        Email mail = getSelectedEmail();
        if (mail != null){
            info.setMail(mail);
        }
        toggleButtons();
    }
    */
}
