package servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServidorManager {
    private static final int PUERTO = 1099;
    private Registry registry;
    private ServidorLibros servidor;

    // Verifica si el puerto está bloqueado
    private boolean puertoBloqueado() {
        try {
            // Ejecuta un comando para verificar si el puerto está en uso
            ProcessBuilder pb = new ProcessBuilder(
                "cmd", "/c", "netstat", "-ano", "|", "findstr", String.valueOf(PUERTO));
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            
            // Si se encuentra una línea, el puerto está en uso
            return line != null && !line.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Libera el puerto especificado matando el proceso que lo está usando
    private void liberarPuerto(String pid) {
        try {
            if (pid != null && !pid.isEmpty()) {
                // Ejecuta un comando para matar el proceso que está usando el puerto
                ProcessBuilder pb = new ProcessBuilder(
                    "taskkill", "/F", "/PID", pid);
                pb.start();
            }
        } catch (Exception e) {
            System.err.println("Error al liberar el puerto: " + e.getMessage());
        }
    }

    // Inicia el servidor RMI
    public void iniciarServidor() throws Exception {
        try {
            // Primero intentamos limpiar cualquier registro existente
            try {
                Registry registroExistente = LocateRegistry.getRegistry(PUERTO);
                try {
                    registroExistente.unbind("ServicioLibros");
                } catch (Exception e) {
                    // Ignoramos si el servicio no existe
                }
            } catch (Exception e) {
                // Ignoramos si no hay registro existente
            }

            // Esperamos un momento para asegurar que los recursos se liberan
            Thread.sleep(100);

            // Creamos una nueva instancia del servidor
            servidor = new ServidorLibros();
            
            // Intentamos crear un nuevo registro
            try {
                registry = LocateRegistry.createRegistry(PUERTO);
            } catch (Exception e) {
                // Si falla, obtenemos el registro existente
                registry = LocateRegistry.getRegistry(PUERTO);
            }

            // Exportamos el objeto y lo vinculamos al registro
            IServicioLibros stub = (IServicioLibros) UnicastRemoteObject.exportObject(servidor, 0);
            registry.rebind("ServicioLibros", stub);

        } catch (Exception e) {
            // Si algo falla, intentamos limpiar todo
            if (servidor != null) {
                try {
                    UnicastRemoteObject.unexportObject(servidor, true);
                } catch (Exception ex) {
                    // Ignoramos errores de limpieza
                }
            }
            throw new Exception("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    // Detiene el servidor RMI
    public void detenerServidor() throws Exception {
        try {
            if (registry != null) {
                try {
                    registry.unbind("ServicioLibros");
                } catch (Exception e) {
                    // Ignoramos si el servicio ya no existe
                }
                
                if (servidor != null) {
                    UnicastRemoteObject.unexportObject(servidor, true);
                }
                UnicastRemoteObject.unexportObject(registry, true);
                registry = null;
                servidor = null;
            }
        } catch (Exception e) {
            throw new Exception("Error al detener el servidor: " + e.getMessage());
        }
    }
}