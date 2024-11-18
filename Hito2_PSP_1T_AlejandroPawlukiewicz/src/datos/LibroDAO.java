package datos;

import java.sql.*;
import java.util.*;

public class LibroDAO {
    
    // Busca libros en la base de datos según una clave
    public List<String> buscarLibros(String clave) {
        List<String> resultados = new ArrayList<>();
        String query = "SELECT * FROM libros WHERE titulo LIKE ? OR autor LIKE ? OR categoria LIKE ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String busqueda = "%" + clave + "%";
            pstmt.setString(1, busqueda);
            pstmt.setString(2, busqueda);
            pstmt.setString(3, busqueda);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String libro = String.format("Título: %s, Autor: %s, Categoría: %s, Precio: %.2f€",
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getDouble("precio"));
                    resultados.add(libro);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultados;
    }
}