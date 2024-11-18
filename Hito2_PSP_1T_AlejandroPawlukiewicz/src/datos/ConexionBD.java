package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/libreria";
    private static final String USER = "root";
    private static final String PASSWORD = "Tcachuk93";
    
    static {
        try {
            // Registrar el driver explícitamente
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL registrado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al registrar el driver MySQL: " + e.getMessage());
            throw new RuntimeException("No se pudo registrar el driver MySQL", e);
        }
    }
    
    // Obtiene una conexión a la base de datos
    public static Connection obtenerConexion() throws SQLException {
        try {
            Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida con éxito a la base de datos");
            return conexion;
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            throw e;
        }
    }
}