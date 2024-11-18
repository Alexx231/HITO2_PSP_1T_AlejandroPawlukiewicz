package servidor;

import datos.LibroDAO;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ServidorLibros implements IServicioLibros {
    private LibroDAO libroDAO;
    private IServicioLibros stub;

    // Constructor modificado que no extiende UnicastRemoteObject
    public ServidorLibros() {
        libroDAO = new LibroDAO();
    }

    // Método para exportar el objeto
    public IServicioLibros exportar() throws RemoteException {
        if (stub == null) {
            stub = (IServicioLibros) UnicastRemoteObject.exportObject(this, 0);
        }
        return stub;
    }

    // Método para desexportar el objeto
    public void desexportar() {
        try {
            if (stub != null) {
                UnicastRemoteObject.unexportObject(this, true);
                stub = null;
            }
        } catch (Exception e) {
            // Ignorar errores de desexportación
        }
    }

    // Implementación del método remoto para buscar libros
    @Override
    public List<String> buscarLibros(String clave) throws RemoteException {
        return libroDAO.buscarLibros(clave);
    }
}