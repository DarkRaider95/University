import java.text.*;
import java.util.*;
import java.io.*;

class Email implements Serializable  {
    private static int identifier = 0; // id incrementale per identifcare le mail

    private int ID;
    private String mittente;
    private Set<String> destinatari;
    private String argomento;
    private String testo;
    private int priorita;
    private Date data;

    // crea una nuova mail
    Email(String mittente, Set<String> destinatari, String argomento, String testo, int priorita, Date data) {
        ID = identifier++;

        destinatari.remove(""); // remove empty string
        this.mittente = mittente;
        this.destinatari = destinatari;
        this.argomento = argomento;
        this.testo = testo;
        this.priorita = priorita;
        this.data = data;
    }

    // crea una nuova mail con il formato data in stringa
    Email(String mittente, Set<String> destinatari, String argomento, String testo, int priorita, String data) {
        this(mittente, destinatari, argomento, testo, priorita, new Date(0));
        try {
            this.data = new SimpleDateFormat("dd/MM/yyyy").parse(data);
        } catch (ParseException e){
            this.data = new Date(0);
        }
    }

    // ritorna l'id della mail
    public int getID()  {
        return ID;
    }

    // ritorna il mittente della mail
    public String getMittente()  {
        return mittente;
    }

    // ritorna i destinatari della mail
    public Set<String> getDestinatari()  {
        return destinatari;
    }

    // ritorna l'argomento della mail
    public String getArgomento()  {
        return argomento;
    }

    // ritorna il testo della mail
    public String getTesto()  {
        return testo;
    }

    // ritorna la priorita' della mail
    public int getPriorita()  {
        return priorita;
    }

    // ritorna la data di invio della mail
    public Date getData()  {
        return data;
    }
}
