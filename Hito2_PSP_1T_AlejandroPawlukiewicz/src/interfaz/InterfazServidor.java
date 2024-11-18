package interfaz;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import servidor.IServicioLibros;
import servidor.ServidorLibros;

public class InterfazServidor extends JFrame {
    private JButton btnIniciar;
    private JButton btnDetener;
    private JTextArea areaLogs;
    private JLabel lblEstado;
    private JLabel lblConexion;
    private Registry registry;
    private IServicioLibros stub;
    private ServidorLibros servicioLibros;
    private boolean servidorIniciado = false;

    public InterfazServidor() {
        // Configura la ventana principal
        configurarVentana();
        // Inicializa los componentes de la interfaz
        inicializarComponentes();
        // Configura los eventos de los botones
        configurarEventos();
        // Verifica el estado del servidor al iniciar
        verificarEstadoServidor();
    }

    // Añadir nuevo método para verificación
    private void verificarEstadoServidor() {
        try {
            // Intenta conectarse al registro RMI en el puerto 1099
            Registry testRegistry = LocateRegistry.getRegistry(1099);
            try {
                // Intenta buscar el servicio
                testRegistry.lookup("ServicioLibros");
                // Si no lanza excepción, el servidor está activo
                servidorIniciado = true;
                btnIniciar.setEnabled(false);
                btnDetener.setEnabled(true);
                lblEstado.setText("Estado: Ejecutando");
                lblEstado.setForeground(new Color(46, 139, 87));
                lblConexion.setText("Base de datos: Conectada");
                lblConexion.setForeground(new Color(46, 139, 87));
                agregarLog("Servidor detectado en ejecución");
            } catch (Exception e) {
                // El registro existe pero el servicio no está disponible
                agregarLog("Puerto 1099 en uso pero el servicio no está disponible");
            }
        } catch (Exception e) {
            // El registro no existe, el servidor está completamente detenido
            servidorIniciado = false;
            agregarLog("Servidor no detectado");
        }
    }

    // Modificar el método iniciarServidor()
    private void iniciarServidor() throws Exception {
        if (servidorIniciado) {
            agregarLog("Aviso: El servidor ya está en ejecución");
            JOptionPane.showMessageDialog(this,
                "El servidor ya está en ejecución",
                "Aviso",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Primero intentamos limpiar cualquier instancia anterior
            try {
                Registry registroExistente = LocateRegistry.getRegistry(1099);
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
            
            // Creamos una nueva instancia del servicio
            servicioLibros = new ServidorLibros();
            
            // Intentamos crear un nuevo registro
            try {
                registry = LocateRegistry.createRegistry(1099);
            } catch (Exception e) {
                // Si falla, obtenemos el registro existente
                registry = LocateRegistry.getRegistry(1099);
            }
            
            // Exportamos el objeto y lo vinculamos al registro
            stub = (IServicioLibros) UnicastRemoteObject.exportObject(servicioLibros, 0);
            registry.rebind("ServicioLibros", stub);
            
            servidorIniciado = true;
            lblConexion.setText("Base de datos: Conectada");
            lblConexion.setForeground(new Color(46, 139, 87));
            agregarLog("Servidor iniciado correctamente");
            
        } catch (Exception e) {
            // Si algo falla, intentamos limpiar todo
            if (servicioLibros != null) {
                try {
                    UnicastRemoteObject.unexportObject(servicioLibros, true);
                } catch (Exception ex) {
                    // Ignoramos errores de limpieza
                }
            }
            throw new Exception(e.getMessage());
        }
    }

    // Modificar el método detenerServidor()
    private void detenerServidor() throws Exception {
        try {
            if (!servidorIniciado) {
                throw new Exception("El servidor no está en ejecución");
            }
            if (registry != null) {
                // Desvincula el objeto del servicio del registro
                registry.unbind("ServicioLibros");
                if (stub != null) {
                    // Desexporta el objeto del servicio
                    UnicastRemoteObject.unexportObject(servicioLibros, true);
                }
                // Desexporta el registro
                UnicastRemoteObject.unexportObject(registry, true);
                registry = null;
                stub = null;
                servicioLibros = null;
                servidorIniciado = false;
                lblConexion.setText("Base de datos: No conectada");
                lblConexion.setForeground(Color.RED);
            }
        } catch (Exception e) {
            throw new Exception("Error al detener el servidor: " + e.getMessage());
        }
    }

    // Modificar la parte relevante de configurarEventos()
    private void configurarEventos() {
        btnIniciar.addActionListener(e -> {
            try {
                iniciarServidor();
                // Solo actualizar la UI si el servidor se inició correctamente
                if (servidorIniciado) {
                    btnIniciar.setEnabled(false);
                    btnDetener.setEnabled(true);
                    lblEstado.setText("Estado: Ejecutando");
                    lblEstado.setForeground(new Color(46, 139, 87));
                    agregarLog("Servidor iniciado correctamente");
                }
            } catch (Exception ex) {
                agregarLog("Error al iniciar el servidor: " + ex.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error al iniciar el servidor: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDetener.addActionListener(e -> {
            try {
                detenerServidor();
                btnIniciar.setEnabled(true);
                btnDetener.setEnabled(false);
                lblEstado.setText("Estado: Detenido");
                lblEstado.setForeground(Color.RED);
                agregarLog("Servidor detenido correctamente");
            } catch (Exception ex) {
                agregarLog("Error al detener el servidor: " + ex.getMessage());
                if (!ex.getMessage().contains("no está en ejecución")) {
                    JOptionPane.showMessageDialog(this,
                        "Error al detener el servidor: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void agregarLog(String mensaje) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        areaLogs.append(timestamp + " - " + mensaje + "\n");
        // Auto-scroll al final
        areaLogs.setCaretPosition(areaLogs.getDocument().getLength());
    }
    
    private void configurarVentana() {
        setTitle("Panel de Control - Servidor de Biblioteca");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void inicializarComponentes() {
        // Panel de título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titulo = new JLabel("Control del Servidor de Biblioteca");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        panelTitulo.add(titulo);

        // Panel de control
        JPanel panelControl = new JPanel(new GridBagLayout());
        panelControl.setBorder(BorderFactory.createTitledBorder("Panel de Control"));
        GridBagConstraints gbc = new GridBagConstraints();

        // Botones
        btnIniciar = new JButton("Iniciar Servidor");
        btnDetener = new JButton("Detener Servidor");
        btnIniciar.setPreferredSize(new Dimension(150, 40));
        btnDetener.setPreferredSize(new Dimension(150, 40));
        btnDetener.setEnabled(false);

        // Estilo botones
        btnIniciar.setBackground(new Color(46, 139, 87));
        btnIniciar.setForeground(Color.WHITE);
        btnDetener.setBackground(new Color(178, 34, 34));
        btnDetener.setForeground(Color.WHITE);

        // Estado y conexión
        lblEstado = new JLabel("Estado: Detenido");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 14));
        lblConexion = new JLabel("Base de datos: No conectada");
        lblConexion.setFont(new Font("Arial", Font.PLAIN, 14));

        // Configurar layout
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5,5,5,5);
        panelControl.add(btnIniciar, gbc);

        gbc.gridx = 1;
        panelControl.add(btnDetener, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        panelControl.add(lblEstado, gbc);

        gbc.gridy = 2;
        panelControl.add(lblConexion, gbc);

        // Área de logs
        areaLogs = new JTextArea();
        areaLogs.setEditable(false);
        areaLogs.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLogs = new JScrollPane(areaLogs);
        scrollLogs.setBorder(BorderFactory.createTitledBorder("Registro de Eventos"));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(panelTitulo, BorderLayout.NORTH);
        mainPanel.add(panelControl, BorderLayout.CENTER);
        mainPanel.add(scrollLogs, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazServidor servidor = new InterfazServidor();
            servidor.setLocationRelativeTo(null);
            servidor.setVisible(true);
        });
    }
}