// ClienteLibros.java
package cliente;

import servidor.IServicioLibros;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class ClienteLibros {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            IServicioLibros servicioLibros = (IServicioLibros) registry.lookup("ServicioLibros");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Ingrese término de búsqueda (o 'salir' para terminar): ");
                String clave = scanner.nextLine();
                
                if (clave.equalsIgnoreCase("salir")) break;

                List<String> resultados = servicioLibros.buscarLibros(clave);
                if (resultados.isEmpty()) {
                    System.out.println("No se encontraron libros.");
                } else {
                    System.out.println("Libros encontrados:");
                    resultados.forEach(System.out::println);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}