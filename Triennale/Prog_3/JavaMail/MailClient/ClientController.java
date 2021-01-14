/*
    Questa classe gestisce gli eventi della gui
*/
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import java.rmi.RemoteException;

import java.lang.reflect.*;

class ClientController implements ActionListener, ListSelectionListener {
    private Mailbox casella;
    private ClientView view;
    private String owner;

    public ClientController(String owner, Mailbox casella, ClientView view){
        this.casella = casella;
        this.view = view;
        this.owner = owner;
    }

    // controller lista messaggi
    public void valueChanged(ListSelectionEvent e) {
        DefaultListSelectionModel m = (DefaultListSelectionModel) e.getSource();
        int index = m.getMaxSelectionIndex(); //m.getLeadSelectionIndex();
        if(!e.getValueIsAdjusting() && index > -1){
            //System.out.println(index);
            view.showEmailIndex(index);
        }
    }

    //cancella una mail o nel caso all sia true le cancella tutte
    private void deleteEmail(boolean all){
        new Thread(){
            public void run(){
                view.info.setEmpty("Cancello email...");
                try {
                    //Thread.sleep(3000);
                    if (all){
                        casella.deleteAllEmails();
                    } else {
                        Email email = view.getSelectedEmail();
                        if (email != null)
                            casella.deleteEmail(email.getID());
                    }
                } catch (RemoteException ex){
                    if (all)
                        JOptionPane.showMessageDialog(null, "Error del server svuotando la casella", "Errore", JOptionPane.ERROR_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(null, "Error del server cancellando una email", "Errore", JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                } //catch (InterruptedException e){}
            }
        }.start();
    }

    //mostra una finestra per comporre una mail con i destinatari già impostati
    public void showRispondi(boolean all){
        Email mail = view.getSelectedEmail();
        String mittente = mail.getMittente();
        String argomento = "RE: " + mail.getArgomento();
        Set<String> destinatari;

        //se all è true rimuove dai destinatari l'owner
        if (all) {
            destinatari = mail.getDestinatari();
            destinatari.remove(owner);
        } else {
            destinatari = new HashSet<String>();
        }
        destinatari.add(mittente);

        ComponiMail v = new ComponiMail(casella, destinatari, argomento);
        v.setVisible(true);
        v.setFirstFocus();
    }

    // controller bottoni
    public void actionPerformed(ActionEvent e){
        switch(e.getActionCommand()){
            case "Cancella":{
                deleteEmail(false);
                break;
            }
            case "Svuota":{
                deleteEmail(true);
                break;
            }
            case "Nuova":{
                ComponiMail v = new ComponiMail(casella);
                v.setVisible(true);
                break;
            }
            case "Rispondi":{
                showRispondi(false);
                break;
            }
            case "Rispondi a tutti":{
                showRispondi(true);
                break;
            }
            case "Inoltra":{
                Email mail = view.getSelectedEmail();
                String argomento = "FWD: " + mail.getArgomento();
                String testo = mail.getTesto();
                ComponiMail v = new ComponiMail(casella, null, argomento, testo);
                v.setVisible(true);
                v.setFirstFocus();
                break;
            }
            default:{
                System.out.println("Unknown command");
                break;
            }
        }
    }

}
