/*
    GUI che mostra i log del server
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;
import javax.swing.table.*;

class ServerConsole extends JFrame {

    private JTable table = new JTable(); // tabella per mostrare i log
    private JScrollPane scroller;        // scoller per visualizzare sempre l'ultimo log aggiunto
    DefaultTableModel tableModel;        // modello a cui si aggiungono i log per mostrare una riga in piu nella tabella
    static private int counter = 0;      // contatore incrementale dei log

    public ServerConsole(String ip){
        setTitle("MailServer");
        setLayout(new BorderLayout());
        setSize(new Dimension(500, 500));
        setMinimumSize(getSize());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // gestore chiusura del server
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int option = JOptionPane.showConfirmDialog(e.getWindow(), "Sei sicuro di voler uscire?", "Esci", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });

        JLabel ipL = new JLabel("Server in ascolto: " + ip); // mostra l'ip su cui e' in ascolto il server
        ipL.setPreferredSize(new Dimension(50, 50));
        ipL.setHorizontalAlignment(JLabel.CENTER);
        add(ipL, BorderLayout.NORTH);

        // rende la tabella read only
        table.setRowSelectionAllowed(false);
        table.setFocusable(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultEditor(Object.class, null);
        scroller = new JScrollPane(table);
        add(scroller, BorderLayout.CENTER);

        // imposta le colonne della tabella
        String[] cols = {"#", "Data", "Messaggio"};
        tableModel = new DefaultTableModel(cols, 0);
        table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(30);

        // sostituisce lo standard output con questa gui
        PrintStream printStream = new TablePrintStream(System.out, this);
        System.setOut(printStream);
    }

    private synchronized Object[] messageToRow(String msg){
        Object[] array = new Object[3];
        counter++;
        array[0] = Integer.valueOf(counter);
        array[1] = new Date().toString();
        array[2] = msg;
        return array;
    }

    public void addMessage(String msg){
        tableModel.addRow(messageToRow(msg));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JScrollBar bar = scroller.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            }
        });
    }
}

// PrintStream da sostituire a stdout per intercettare le chiamate a println
// e redirigerle nella tabella della gui al posto del terminale
class TablePrintStream extends PrintStream {
    private ServerConsole console;

    public TablePrintStream(OutputStream os, ServerConsole console) {
        super(os);
        this.console = console;
    }

    public synchronized void print(String msg) {
        // replace \n con spazio?
        console.addMessage(msg);
    }

    public synchronized void println(String msg){
        print(msg);
    }
}
