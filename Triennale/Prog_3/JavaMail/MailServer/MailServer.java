/*
    Classe principale del server
    Gestisce la creazione del server e la creazoine della gui per i log
*/

import javax.swing.*;
import java.net.*;
import java.rmi.*;
import java.util.*;
import javax.naming.*;

public class MailServer{
    public static void main(String[] args) throws RemoteException, NamingException, SocketException {

        String ip = selectListeningIP(); // mostra all'utente le interfacce disponibili e permette di sceglierne una

        System.setProperty("java.rmi.server.hostname", ip); // velocizza la connessione
        java.rmi.registry.LocateRegistry.createRegistry(7777); // crea il server RMI sulla porta 7777
        MailboxManagerServer mms = new MailboxManagerServer(); // crea l'oggetto Remote che gestisce le caselle mail
        Context namingContext = new InitialContext();
        namingContext.bind("rmi://" + ip + ":7777/mms", mms); // pubblicizza l'oggetto Remote che gestisce le caselle mail

        ServerConsole serverView = new ServerConsole(ip);  // mostra la gui dei log
        serverView.setVisible(true);
    }

    // mostra tutte le interfacce disponibili e i relativi IP, ritorna l'ip dell'interfaccia scelta
    private static String selectListeningIP() throws SocketException {
        final String separator = " - ";
        List<String> strNameIP = new ArrayList<String>();

        Enumeration<NetworkInterface> allInt = NetworkInterface.getNetworkInterfaces(); // ottiene tutte le interfacce
        while(allInt.hasMoreElements()){
            NetworkInterface netInt = allInt.nextElement();
            String name = netInt.getDisplayName().toString();

            // per ogni interfaccia mostra gli ip disponibili
            Enumeration netIntIP = netInt.getInetAddresses();
            while (netIntIP.hasMoreElements()){
                InetAddress ip = (InetAddress) netIntIP.nextElement();
                if (ip instanceof Inet4Address){
                    String ipstr = ip.getHostAddress().toString();
                    strNameIP.add(name + separator + ipstr);
                }
            }
        }

        String selectedInt = (String) JOptionPane.showInputDialog(
                                null,
                                "Seleziona una interfaccia su cui ascoltare",
                                "Interfacce",
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                strNameIP.toArray(),
                                null);

        return selectedInt.split(separator)[1]; // ritorna l'ip dell'interfaccia scelta
    }
}
