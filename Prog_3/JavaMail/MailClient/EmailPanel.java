/*
    Questa classe gestisce il pannello che mostra il contenuto della mail selezionata
*/
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.Set;
import javax.swing.BorderFactory;

public class EmailPanel extends JPanel {

    //empty panel
    private JPanel empty = new JPanel();
    private JLabel emptyMessage;
    private final String DEFAULT_EMPTY_MESSAGE = "Nessuna email selezionata";

    //mail panels
    private JPanel headerPanel = new JPanel();
    private JPanel labelPanel = new JPanel();
    private JPanel textPanel= new JPanel();

    //campi di testo
    private JTextField mittenteTextField = new JTextField();
	private JTextField destinatariTextField = new JTextField();
    private JTextField argomentoTextField = new JTextField();
    private JTextArea testoTextArea = new JTextArea();

    private JLabel data = new JLabel();

    private boolean isEmpty=true;

	EmailPanel(){
		setLayout(new BorderLayout(10,10));
		notEditable();

        empty.setLayout(new GridBagLayout());
        emptyMessage = new JLabel(DEFAULT_EMPTY_MESSAGE);
        empty.add(emptyMessage);

        initHeader();
        initBody();

        setBorder(new EmptyBorder(10,10,10,10));
		add(empty);
	}

    //inizializza l'intestazione
	private void initHeader() {

        //set panels layout
        headerPanel.setLayout(new BorderLayout());
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        // init label
        panLabel("Mittente");
        panLabel("Destinatari");
        panLabel("Argomento");

        //init textfields
        textPanel.add(mittenteTextField);
        textPanel.add(destinatariTextField);
        textPanel.add(argomentoTextField);


        //add panels to header panels
        headerPanel.add(labelPanel, BorderLayout.WEST);
        headerPanel.add(textPanel, BorderLayout.CENTER);

        data.setHorizontalAlignment(SwingConstants.RIGHT);
        headerPanel.add(data, BorderLayout.NORTH);

    }

    //crea le etichette e le aggiunge al pannello delle etichette
    private void panLabel(String lab){
        JLabel label = new JLabel(lab);
        label.setBorder(new CompoundBorder(label.getBorder(), new EmptyBorder(10,10,10,10)));
        labelPanel.add(label);
    }

    //inizializza la textarea
    private void initBody(){
        testoTextArea.setLineWrap(true);

        testoTextArea.setMargin(new Insets(10,10,10,10));
        //panel.add(testoTextArea, BorderLayout.CENTER);

    }

    //imposta tutti i campi come non editabili per non permettere la modifica
    private void notEditable(){
    	mittenteTextField.setEditable(false);
    	destinatariTextField.setEditable(false);
    	argomentoTextField.setEditable(false);
    	testoTextArea.setEditable(false);
    }

    //riempie tutti campi con i dati delle mail
    public void setMail(Email email){

        //se c'è il pannello vuoto, lo rimuove e mette quello per le mail
        if (isEmpty) {
            remove(empty);
            addMail();
            isEmpty=false;
            validate();
            repaint();
        }

        data.setText(email.getData().toString());
        testoTextArea.setText(email.getTesto());
        mittenteTextField.setText(email.getMittente());
        destinatariTextField.setText(setToString(email.getDestinatari()));
        argomentoTextField.setText(email.getArgomento());
    }

    private static String setToString(Set<String> set){
        return String.join(", ", set.toArray(new String[set.size()]));
    }

    //mette il pannello dei messaggi o mostra un messaggio
    public synchronized void setEmpty(String msg){
        if (msg != null)
            emptyMessage.setText(msg);
        else
            emptyMessage.setText(DEFAULT_EMPTY_MESSAGE);

        //se c'è un pannello delle mail, lo rimuove e mette quello vuoto
        if(!isEmpty){
            removeMail();
            add(empty);
            isEmpty=true;
            validate();
            repaint();
        }
    }

    public synchronized void setEmpty(){
        setEmpty(null);
    }

    //aggiunge il pannello di intestazione e la textarea
    private void addMail(){
        add(headerPanel, BorderLayout.NORTH);
        add(testoTextArea, BorderLayout.CENTER);
    }

    //rimuove il pannello di intestazione e la textarea
    private void removeMail(){
        remove(headerPanel);
        remove(testoTextArea);
    }
}
