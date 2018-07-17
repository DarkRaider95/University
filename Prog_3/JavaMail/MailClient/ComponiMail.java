/*
    classe che gestisce la finestra per la composizione delle mail
*/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.*;
import java.rmi.RemoteException;

class ComponiMail extends JFrame implements ActionListener {

    private JTextField destinatariTextField = new JTextField();
    private JTextField argomentoTextField = new JTextField();
    private JTextArea testoTextArea;
    private Mailbox model;
    JPanel buttonsPanel;
    JComponent focusElement = null;

    public ComponiMail(Mailbox m){
        this.model = m;

        setTitle("Nuovo messaggio");
        setLayout(new BorderLayout());
        setSize(new Dimension(500, 500));
        setMinimumSize(getSize());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  // centra il frame

        initHeader();
        initBody();
        initButtons();
    }

    public ComponiMail(Mailbox m, Set<String> dest){
        this(m);
        if(dest!=null){
            String d = String.join(", ", dest.toArray(new String[dest.size()]));
            destinatariTextField.setText(d);
        }
        focusElement = argomentoTextField;
    }

    public ComponiMail(Mailbox m, Set<String> dest, String arg){
        this(m, dest);
        argomentoTextField.setText(arg);
        focusElement = testoTextArea;
    }

    public ComponiMail(Mailbox m, Set<String> dest, String arg, String testo){
        this(m, dest, arg);
        testoTextArea.setText(testo);
        focusElement = destinatariTextField;
    }

    //inizializza l'intestazione
    private void initHeader() {

        // init panels
        JPanel headerPanel = new JPanel();
        JPanel labelPanel = new JPanel();
        JPanel textPanel= new JPanel();

        //set panels layout
        headerPanel.setLayout(new BorderLayout());
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // init label
        panLabel(labelPanel, "Destinatari");
        panLabel(labelPanel, "Argomento");

        //init textfields
        textPanel.add(destinatariTextField);
        textPanel.add(argomentoTextField);

        //add panels to header panels
        headerPanel.add(labelPanel, BorderLayout.WEST);
        headerPanel.add(textPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
    }

    //crea le etichette e le aggiunge al pannello delle etichette
    private void panLabel(JPanel labelPanel, String lab){
        JLabel label = new JLabel(lab);
        label.setBorder(new CompoundBorder(label.getBorder(), new EmptyBorder(10,10,10,10)));
        labelPanel.add(label);
    }

    //inizializza la textarea
    private void initBody(){
        testoTextArea = new JTextArea();
        testoTextArea.setLineWrap(true);
        testoTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(testoTextArea, BorderLayout.CENTER);
    }

    //inizializza i bottoni
    private void initButtons(){
        buttonsPanel = new JPanel(new FlowLayout());
        add(buttonsPanel, BorderLayout.SOUTH);

        JButton annulla = new JButton("Annulla");
        annulla.addActionListener(this);
        buttonsPanel.add(annulla);

        JButton invia = new JButton("Invia");
        invia.addActionListener(this);
        buttonsPanel.add(invia);
    }

    //prende l'elenco dei destinatari dal campo di testo e li mette in un HashSet
    private Set<String> getDestinatari(){
         Set<String> dest = new HashSet<String>(Arrays.asList(destinatariTextField.getText().replace(" ", "").split(",")));
         dest.remove("");
         return dest;
    }

    //mostra un messaggio durante l'invio della mail
    private void showSendingLabel(){
        destinatariTextField.setEnabled(false);
        argomentoTextField.setEnabled(false);
        testoTextArea.setEnabled(false);

        remove(buttonsPanel);
        JPanel sendPanel = new JPanel(new GridBagLayout());
        add(sendPanel, BorderLayout.SOUTH);
        sendPanel.add(new JLabel("Invio..."));
        sendPanel.setPreferredSize(buttonsPanel.getSize());
        revalidate();
        repaint();
    }

    //la funzione per l'invio della mail
    private void send(){
        showSendingLabel();

        new Thread(){
            public void run(){
                //raccoglie i dati dalla finestra
                Set<String> destinatari = getDestinatari();
                String argomento = argomentoTextField.getText();
                String testo = testoTextArea.getText();
                int priorita = 1;
                //System.out.println("Invio");
                try {
                    //Thread.sleep(7000);
                    //effettua la chiamata remota sul server per spedire la mail
                    model.sendEmail(destinatari, argomento, testo, priorita);
                } catch (RemoteException e){
                    JOptionPane.showMessageDialog(null, "Errore di invio", "Errore", JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                }// catch (InterruptedException e){}
                finally {
                    dispose();
                }
            }
        }.start();
    }

    //mette il focus su un elemento della finestra
    public void setFirstFocus(){
        if (focusElement != null){
            //focusElement.grabFocus();
            focusElement.requestFocus();
        }
    }

    //controlla che il campo destinatari non sia vuoto
    private boolean destPresent(){
        if (getDestinatari().size() > 0){
            return true;
        } else {
            return false;
        }
    }

    //controlla se c'è un argomento
    private boolean argPresent(){
        String arg = argomentoTextField.getText();
        if (arg.replace(" ", "").length() > 0){
            return true;
        } else {
            return false;
        }
    }

    // Controller bottone invia
    public void actionPerformed(ActionEvent e){
        switch(e.getActionCommand()){

            case "Invia":{
                if (destPresent()){
                    if (argPresent()){
                        send();
                        //dispose();
                    } else {
                        int option = JOptionPane.showConfirmDialog(this, "Non hai inserito nessun argomento!\nInviare lo stesso?", "Argomento mancante", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION){
                            send();
                            //dispose();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Inserisci almeno un destinatario", "Destinatario mancante", JOptionPane.ERROR_MESSAGE);
                }
                break;
            }

            case "Annulla":{
                dispose();
                break;
            }

            default:{
                System.out.println("Unknown command");
                break;
            }
        }
    }

}
