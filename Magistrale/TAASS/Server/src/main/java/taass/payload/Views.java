package taass.payload;

public class Views {

    // base per le altre
    public interface Minimal {} // permette di vedere solo l'id degli oggetti annidati
    public interface Summary extends Minimal {} // permette di vedere l'id piu' altre info degli oggetti annidati

    // piu' semplici
    public interface Category extends Minimal {}
    public interface CreditCard extends Minimal {}

    // due livelli di view
    public interface User extends Minimal {}
    public interface PrivateUser extends User {}

    // per ottenere un po' piu' di info degli oggetti annidati (altrimenti non mi dava nome prodotto e image)
    public interface Product extends Summary {}
    public interface Rent extends Summary {}
}
