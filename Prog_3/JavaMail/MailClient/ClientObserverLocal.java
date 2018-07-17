/*
	Questa classe serve a gestire l'aggiornamento della gui da remoto
*/
import java.util.Observable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

class ClientObserverLocal extends UnicastRemoteObject implements ClientObserver {
    ClientObservable observable;
    ClientObserverLocal(ClientObservable observable) throws RemoteException {
        this.observable = observable;
    }
    //questo metodo viene chiamato da remoto
    public void update(Object extra) throws RemoteException {
        observable.update(extra);
    }
}
