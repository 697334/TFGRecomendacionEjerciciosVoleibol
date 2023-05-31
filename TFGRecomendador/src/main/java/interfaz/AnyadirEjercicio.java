package interfaz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTextField;

public class AnyadirEjercicio {

	private JFrame frame;
	private JTextField txtNombre;
	private JTextField txtDescripcion;
	private JTextField txtDuracion;
	private JTextField txtNumJugMin;
	private JTextField txtNumJugMax;
	private JTextField txtEdadMin;
	private JTextField txtEdadMax;
	private JComboBox cbxObjetivo;
	private JComboBox cbxPrioridad;
	private JTextField txtImagenURL;

	/**
	 * Launch the application.
	 */
	/*public static void generarVentana() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AnyadirEjercicio window = new AnyadirEjercicio();
					window.realizarAdicion();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the application.
	 */
	public AnyadirEjercicio() {
		initialize();
		realizarAdicion();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 698, 451);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Atrás");
		btnNewButton.setBounds(10, 28, 89, 23);
		frame.getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
			}
		});
		
		JLabel lblNewLabel = new JLabel("<html><body>Para introducir un ejercicio, introduzca <br>sus características y pulse Aceptar:</body></html>");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(171, 22, 366, 48);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(105, 117, 75, 14);
		frame.getContentPane().add(lblNombre);
		
		txtNombre = new JTextField();
		txtNombre.setBounds(190, 114, 89, 20);
		frame.getContentPane().add(txtNombre);
		txtNombre.setColumns(10);
		
		JLabel lblDescripcion = new JLabel("Descripción:");
		lblDescripcion.setBounds(105, 162, 75, 14);
		frame.getContentPane().add(lblDescripcion);
		
		txtDescripcion = new JTextField();
		txtDescripcion.setBounds(190, 159, 89, 20);
		frame.getContentPane().add(txtDescripcion);
		txtDescripcion.setColumns(10);
		
		JLabel lblDuracion = new JLabel("Duración:");
		lblDuracion.setBounds(105, 212, 75, 14);
		frame.getContentPane().add(lblDuracion);
		
		txtDuracion = new JTextField();
		txtDuracion.setBounds(190, 209, 89, 20);
		frame.getContentPane().add(txtDuracion);
		txtDuracion.setColumns(10);
		
		JLabel lblNumJugMin = new JLabel("Nº jugadores mínimos:");
		lblNumJugMin.setBounds(301, 117, 140, 14);
		frame.getContentPane().add(lblNumJugMin);
		
		txtNumJugMin = new JTextField();
		txtNumJugMin.setBounds(484, 114, 86, 20);
		frame.getContentPane().add(txtNumJugMin);
		txtNumJugMin.setColumns(10);
		
		JLabel lblNumJugMax = new JLabel("Nº jugadores máximos:");
		lblNumJugMax.setBounds(301, 162, 140, 14);
		frame.getContentPane().add(lblNumJugMax);
		
		txtNumJugMax = new JTextField();
		txtNumJugMax.setBounds(484, 159, 86, 20);
		frame.getContentPane().add(txtNumJugMax);
		txtNumJugMax.setColumns(10);
		
		JLabel lblEdadMin = new JLabel("Edad mínima recomendada:");
		lblEdadMin.setBounds(301, 212, 173, 14);
		frame.getContentPane().add(lblEdadMin);
		
		txtEdadMin = new JTextField();
		txtEdadMin.setBounds(484, 209, 86, 20);
		frame.getContentPane().add(txtEdadMin);
		txtEdadMin.setColumns(10);
		
		JLabel lblEdadMax = new JLabel("Edad máxima recomendada:");
		lblEdadMax.setBounds(301, 263, 173, 14);
		frame.getContentPane().add(lblEdadMax);
		
		txtEdadMax = new JTextField();
		txtEdadMax.setBounds(484, 260, 86, 20);
		frame.getContentPane().add(txtEdadMax);
		txtEdadMax.setColumns(10);
		
		cbxPrioridad = new JComboBox();
		cbxPrioridad.setModel(new DefaultComboBoxModel(new String[] {"Equilibrado", "T\u00E1ctica", "T\u00E9cnica", "F\u00EDsico"}));
		cbxPrioridad.setBounds(481, 308, 89, 22);
		frame.getContentPane().add(cbxPrioridad);
		
		cbxObjetivo = new JComboBox();
		cbxObjetivo.setModel(new DefaultComboBoxModel(new String[] {"Equilibrado", "Ataque", "Defensa", "Recepci\u00F3n", "Colocaci\u00F3n", "Remate", "Bloqueo", "Saque"}));
		cbxObjetivo.setBounds(190, 308, 89, 22);
		frame.getContentPane().add(cbxObjetivo);
		
		JLabel lblObjetivo = new JLabel("Objetivo:");
		lblObjetivo.setBounds(105, 312, 75, 14);
		frame.getContentPane().add(lblObjetivo);
		
		JLabel lblTipo = new JLabel("Dar prioridad a:");
		lblTipo.setBounds(301, 312, 140, 14);
		frame.getContentPane().add(lblTipo);
		
		JLabel lblImagenURL = new JLabel("Imagen URL*:");
		lblImagenURL.setBounds(105, 263, 78, 14);
		frame.getContentPane().add(lblImagenURL);
		
		txtImagenURL = new JTextField();
		txtImagenURL.setBounds(193, 260, 86, 20);
		frame.getContentPane().add(txtImagenURL);
		txtImagenURL.setColumns(10);
	}
	
	private void realizarAdicion() {
		
		JButton btnNewButton_1 = new JButton("Aceptar");
		btnNewButton_1.setBounds(266, 352, 101, 38);
		frame.getContentPane().add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Connection conexion = null;
				try {
					Class.forName("org.postgresql.Driver");
					conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
					System.out.println("Conectado");
					//System.out.println(textField.getText());
					String sentenciaAnyadir;
					
					if(txtImagenURL.getText().equals("")) { //No hay imagen asociada
						sentenciaAnyadir = "INSERT INTO \"Ejercicio\" (\"Nombre\", \"Descripcion\", \"EdadMinima\", \"EdadMaxima\", \"Duracion\", \"NumeroJugadoresMinimo\", \"NumeroJugadoresMaximo\", \"Puntuacion\", \"NumUsos\", \"Objetivo\", \"Tipo\")"
								+ "VALUES ('" + txtNombre.getText() + "', '" + txtDescripcion.getText() + "', '" + txtEdadMin.getText() + "', '" + txtEdadMax.getText() + "', '" + txtDuracion.getText() + "', '" + 
								txtNumJugMin.getText() + "', '" + txtNumJugMax.getText() + "', '0.0', '0', '" + cbxObjetivo.getSelectedItem().toString() + "', '"
								+ cbxPrioridad.getSelectedItem().toString() + "')";
						
					}else {
						sentenciaAnyadir = "INSERT INTO \"Ejercicio\" (\"Nombre\", \"Descripcion\", \"EdadMinima\", \"EdadMaxima\", \"Duracion\", \"NumeroJugadoresMinimo\", \"NumeroJugadoresMaximo\", \"Puntuacion\", \"NumUsos\", \"Objetivo\", \"Tipo\")"
								+ "VALUES ('" + txtNombre.getText() + "', '" + txtDescripcion.getText() + "', '" + txtEdadMin.getText() + "', '" + txtEdadMax.getText() + "', '" + txtDuracion.getText() + "', '" + 
								txtNumJugMin.getText() + "', '" + txtNumJugMax.getText() + "', '0.0', '0', '" + cbxObjetivo.getSelectedItem().toString() + "', '"
								+ cbxPrioridad.getSelectedItem().toString() + "', '" + txtImagenURL.getText() + "')";
					}
									
					System.out.println(sentenciaAnyadir);
					Statement sentencia = conexion.createStatement();
					sentencia.executeUpdate(sentenciaAnyadir);
							
					sentencia.close();
				} 
				catch (ClassNotFoundException cnfe) {
					System.out.println("Error al cargar el conector de Postgresql: " + cnfe.getMessage());
					cnfe.printStackTrace();
				} 
				catch (SQLException sqle) {
					System.out.println("Error de SQL: " + sqle.getMessage());
					sqle.printStackTrace();
				}
				finally {
					try {
						if (conexion != null) {
							conexion.close();
						}
					} 
					catch (SQLException sqle) {
						System.out.println("Error al cerrar la base de datos: " + sqle.getMessage());
						sqle.printStackTrace();
					}
				}

				frame.setVisible(false);
			}
		});
	}

}
