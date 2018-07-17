/*
	Questa classe crae un finto oggetto observable che viene osservato dalla gui
*/
import java.util.Observable;

class ClientObservable extends Observable {
    public void update(Object extra){
        setChanged();
        notifyObservers(extra);
    }
}
