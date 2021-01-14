import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientObserver extends Remote {
    public void update(Object extra) throws RemoteException;
}
