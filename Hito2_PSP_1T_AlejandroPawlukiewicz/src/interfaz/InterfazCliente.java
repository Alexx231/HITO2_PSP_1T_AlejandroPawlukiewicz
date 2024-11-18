package interfaz;

import servidor.IServicioLibros;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class InterfazCliente extends JFrame {
    private JTextField txtBusqueda;
    private JTextArea areaResultados;
    private JButton btnBuscar;
    private JButton btnLimpiar;
    private JComboBox<String> filtroBox;
    private IServicioLibros servicioLibros;
    private JPanel panelResultados;

    public InterfazCliente() {
        configurarVentana();
        inicializarComponentes();
        btnBuscar.setEnabled(false);
        conectarServidor();
    }

    // Conecta al servidor RMI
    private void conectarServidor() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            servicioLibros = (IServicioLibros) registry.lookup("ServicioLibros");
            btnBuscar.setEnabled(true);
            JOptionPane.showMessageDialog(this,
                "Conexión exitosa con el servidor",
                "Conexión Establecida",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al conectar con el servidor: " + e.getMessage(),
                "Error de Conexión",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    // Configura la ventana principal
    private void configurarVentana() {
        setTitle("Biblioteca Virtual - Cliente");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        getContentPane().setBackground(new Color(240, 242, 245));
    }

    // Limpia los resultados de búsqueda
    private void limpiarResultados() {
        txtBusqueda.setText("");
        panelResultados.removeAll();
        panelResultados.revalidate();
        panelResultados.repaint();
        filtroBox.setSelectedIndex(0);
    }
    
    // Inicializa los componentes de la interfaz
    private void inicializarComponentes() {
        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(new Color(25, 118, 210));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel lblTitulo = new JLabel("Sistema de Búsqueda de Libros");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Biblioteca Virtual");
        lblSubtitulo.setFont(new Font("Segoe UI Light", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(224, 224, 224));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createRigidArea(new Dimension(0, 5)));
        panelTitulo.add(lblSubtitulo);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new GridBagLayout());
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo de búsqueda con icono
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(Color.WHITE);
        txtBusqueda = new JTextField(30);
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtBusqueda.addActionListener(e -> buscarLibros());
        
        // ComboBox personalizado
        String[] opciones = {"Todos", "Título", "Autor", "Precio"};
        filtroBox = new JComboBox<>(opciones);
        filtroBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filtroBox.setPreferredSize(new Dimension(150, 38));
        filtroBox.setBackground(Color.WHITE);

        // Botones modernos
        btnBuscar = new JButton("Buscar");
        btnLimpiar = new JButton("Limpiar");
        
        // Eventos
        btnBuscar.addActionListener(e -> buscarLibros());
        btnLimpiar.addActionListener(e -> limpiarResultados());

        // Estilo botones
        configurarBoton(btnBuscar, new Color(25, 118, 210));
        configurarBoton(btnLimpiar, new Color(158, 158, 158));

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        panelBusqueda.add(new JLabel("Buscar:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panelBusqueda.add(txtBusqueda, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panelBusqueda.add(new JLabel("Filtrar por:"), gbc);

        gbc.gridx = 3;
        panelBusqueda.add(filtroBox, gbc);

        gbc.gridx = 4;
        panelBusqueda.add(btnBuscar, gbc);

        gbc.gridx = 5;
        panelBusqueda.add(btnLimpiar, gbc);

        // Área de resultados
        panelResultados = new JPanel();
        panelResultados.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columnas, espaciado 10px
        panelResultados.setBackground(new Color(240, 242, 245));

        JScrollPane scrollResultados = new JScrollPane(panelResultados);
        scrollResultados.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(null, "Resultados", 
                TitledBorder.DEFAULT_JUSTIFICATION, 
                TitledBorder.DEFAULT_POSITION, 
                new Font("Segoe UI", Font.BOLD, 14)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollResultados.setPreferredSize(new Dimension(0, 400));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.add(panelTitulo, BorderLayout.NORTH);
        mainPanel.add(panelBusqueda, BorderLayout.CENTER);
        mainPanel.add(scrollResultados, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Añadir método auxiliar para configurar botones
    private void configurarBoton(JButton btn, Color color) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
    }

    // Realiza la búsqueda de libros
    private void buscarLibros() {
        String termino = txtBusqueda.getText().trim();
        String filtro = (String) filtroBox.getSelectedItem();
        
        if (termino.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese un término de búsqueda",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<String> resultados = servicioLibros.buscarLibros(termino);
            mostrarResultadosFiltrados(resultados, filtro);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al buscar libros: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Muestra los resultados filtrados en la interfaz
    private void mostrarResultadosFiltrados(List<String> resultados, String filtro) {
        panelResultados.removeAll();
        
        if (resultados.isEmpty()) {
            JLabel noResultados = new JLabel("No se encontraron libros.");
            noResultados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panelResultados.add(noResultados);
            panelResultados.revalidate();
            panelResultados.repaint();
            return;
        }

        for (String libro : resultados) {
            JPanel card = crearCard(libro, filtro);
            if (card != null) {
                panelResultados.add(card);
            }
        }

        panelResultados.revalidate();
        panelResultados.repaint();
    }

    // Crea una tarjeta para mostrar la información de un libro
    private JPanel crearCard(String libro, String filtro) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Cambiar el split para usar una expresión regular que mantenga el precio completo
        String[] partes = libro.split("(?<=\\D),|,(?=\\D)");
        boolean mostrarCard = false;

        switch (filtro) {
            case "Todos":
                for (String parte : partes) {
                    agregarCampoACard(card, parte.trim());
                }
                mostrarCard = true;
                break;

            case "Título":
                for (String parte : partes) {
                    if (parte.trim().startsWith("Título:")) {
                        agregarCampoACard(card, parte.trim());
                        mostrarCard = true;
                    }
                }
                break;

            case "Autor":
                for (String parte : partes) {
                    if (parte.trim().startsWith("Autor:")) {
                        agregarCampoACard(card, parte.trim());
                        mostrarCard = true;
                    }
                }
                break;

            case "Precio":
                for (String parte : partes) {
                    if (parte.trim().startsWith("Precio:")) {
                        agregarCampoACard(card, parte.trim());
                        mostrarCard = true;
                    }
                }
                break;
        }

        return mostrarCard ? card : null;
    }

    // Añade un campo a la tarjeta
    private void agregarCampoACard(JPanel card, String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        
        // Aplicar estilos según el tipo de información
        if (texto.startsWith("Título:")) {
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            label.setForeground(new Color(25, 118, 210));
        } else if (texto.startsWith("Autor:")) {
            label.setForeground(new Color(0, 100, 0));
        } else if (texto.startsWith("Precio:")) {
            label.setForeground(new Color(178, 34, 34));
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
        
        card.add(label);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazCliente cliente = new InterfazCliente();
            cliente.setLocationRelativeTo(null);
            cliente.setVisible(true);
        });
    }
}