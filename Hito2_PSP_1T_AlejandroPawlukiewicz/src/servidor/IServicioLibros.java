package servidor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IServicioLibros extends Remote {
    // Método remoto para buscar libros por una clave
    List<String> buscarLibros(String clave) throws RemoteException;
}