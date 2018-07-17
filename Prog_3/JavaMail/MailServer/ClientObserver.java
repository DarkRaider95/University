import java.rmi.*;

public interface ClientObserver extends Remote {
    public void update(Object extra) throws RemoteException;
}
