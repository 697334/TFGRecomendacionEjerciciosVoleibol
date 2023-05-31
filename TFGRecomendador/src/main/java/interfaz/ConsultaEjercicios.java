package interfaz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import java.awt.BorderLayout;
import javax.swing.table.DefaultTableModel;

import util.Ejercicio;
import util.Usuario;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;


public class ConsultaEjercicios{

	private JFrame frame;
	private JTable table;
	private  List<Ejercicio> listaEjercicios;
	private JTextField txtDur1;
	private JTextField txtDur2;
	private JTextField txtJug1;
	private JTextField txtJug2;
	private JTextField txtEdad1;
	private JTextField txtEdad2;
	private JComboBox cbxObjetivo;
	private JComboBox cbxPrioridad;
	private DefaultTableModel tblModel;
	private Usuario infoUsuario;

	/*public static void generarVentana() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConsultaEjercicios window = new ConsultaEjercicios();
					window.generarTablaEjercicios();
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
	public ConsultaEjercicios(Usuario infoUsuario) {
		this.infoUsuario = infoUsuario;
		initialize();
		generarTablaEjercicios();
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
		
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(96, 28, 248, 277);
		frame.getContentPane().add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"C\u00F3digo", "Nombre", "Descripci\u00F3n"
			}
		));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblModel = (DefaultTableModel)table.getModel();
		
		JButton btnAnyadir = new JButton("Añadir ejercicio");
		btnAnyadir.setBounds(59, 333, 118, 38);
		frame.getContentPane().add(btnAnyadir);
		btnAnyadir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				AnyadirEjercicio anEjer = new AnyadirEjercicio();
				//anEjer.generarVentana();
				//frame.setVisible(false);	
			}
		});
		
		JButton btnEliminar = new JButton("Eliminar ejercicio");
		btnEliminar.setBounds(257, 333, 118, 38);
		frame.getContentPane().add(btnEliminar);
		btnEliminar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				EliminarEjercicio elEjer = new EliminarEjercicio();
				//elEjer.generarVentana();
				//frame.setVisible(false);
			}
		});
		
		JLabel lblBuscador = new JLabel("Buscador:");
		lblBuscador.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblBuscador.setBounds(389, 35, 78, 19);
		frame.getContentPane().add(lblBuscador);
		
		txtDur1 = new JTextField();
		txtDur1.setBounds(485, 83, 33, 20);
		frame.getContentPane().add(txtDur1);
		txtDur1.setColumns(10);
		
		txtDur2 = new JTextField();
		txtDur2.setColumns(10);
		txtDur2.setBounds(544, 83, 33, 20);
		frame.getContentPane().add(txtDur2);
		
		txtJug1 = new JTextField();
		txtJug1.setColumns(10);
		txtJug1.setBounds(485, 114, 33, 20);
		frame.getContentPane().add(txtJug1);
		
		txtJug2 = new JTextField();
		txtJug2.setColumns(10);
		txtJug2.setBounds(544, 114, 33, 20);
		frame.getContentPane().add(txtJug2);
		
		txtEdad1 = new JTextField();
		txtEdad1.setColumns(10);
		txtEdad1.setBounds(485, 145, 33, 20);
		frame.getContentPane().add(txtEdad1);
		
		txtEdad2 = new JTextField();
		txtEdad2.setColumns(10);
		txtEdad2.setBounds(544, 145, 33, 20);
		frame.getContentPane().add(txtEdad2);
		
		cbxPrioridad = new JComboBox();
		cbxPrioridad.setModel(new DefaultComboBoxModel(new String[] {"Equilibrado", "T\u00E1ctica", "T\u00E9cnica", "F\u00EDsico"}));
		cbxPrioridad.setBounds(485, 209, 105, 22);
		frame.getContentPane().add(cbxPrioridad);
		
		cbxObjetivo = new JComboBox();
		cbxObjetivo.setModel(new DefaultComboBoxModel(new String[] {"Equilibrado", "Ataque", "Defensa", "Recepci\u00F3n", "Colocaci\u00F3n", "Remate", "Bloqueo", "Saque"}));
		cbxObjetivo.setBounds(485, 176, 105, 22);
		frame.getContentPane().add(cbxObjetivo);
		
		JLabel lblDur1 = new JLabel("Duración entre");
		lblDur1.setBounds(389, 86, 86, 14);
		frame.getContentPane().add(lblDur1);
		
		JLabel lblY1 = new JLabel("y");
		lblY1.setBounds(528, 86, 16, 14);
		frame.getContentPane().add(lblY1);
		
		JLabel lblY2 = new JLabel("y");
		lblY2.setBounds(528, 117, 16, 14);
		frame.getContentPane().add(lblY2);
		
		JLabel lblY3 = new JLabel("y");
		lblY3.setBounds(528, 148, 16, 14);
		frame.getContentPane().add(lblY3);
		
		JLabel lblDur2 = new JLabel("minutos");
		lblDur2.setBounds(587, 86, 46, 14);
		frame.getContentPane().add(lblDur2);
		
		JLabel lblJug1 = new JLabel("Jugadores entre");
		lblJug1.setBounds(389, 117, 86, 14);
		frame.getContentPane().add(lblJug1);
		
		JLabel lblEdad1 = new JLabel("Edad entre");
		lblEdad1.setBounds(389, 148, 86, 14);
		frame.getContentPane().add(lblEdad1);
		
		JLabel lblEdad2 = new JLabel("años");
		lblEdad2.setBounds(587, 148, 46, 14);
		frame.getContentPane().add(lblEdad2);
		
		JLabel lblDur1_2_1 = new JLabel("Objetivo:");
		lblDur1_2_1.setBounds(389, 180, 86, 14);
		frame.getContentPane().add(lblDur1_2_1);
		
		JLabel lblDur1_2_2 = new JLabel("Prioridad:");
		lblDur1_2_2.setBounds(389, 213, 86, 14);
		frame.getContentPane().add(lblDur1_2_2);
		
		JLabel lblJug2 = new JLabel("personas");
		lblJug2.setBounds(587, 117, 46, 14);
		frame.getContentPane().add(lblJug2);
		
		JButton btnBuscar = new JButton("Filtrar ejercicios");
		btnBuscar.setBounds(433, 254, 118, 23);
		frame.getContentPane().add(btnBuscar);
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numJugadoresMin = 0;
				int numJugadoresMax = 100;
				int edadMin = 0;
				int edadMax = 100;
				int duracionMin = 0;
				int duracionMax = 100;
				
				if(!(txtJug1.getText().equals(""))) { //Se ha elegido introducir una preferencia
					numJugadoresMin = Integer.valueOf(txtJug1.getText());
				}
				if(!(txtJug2.getText().equals(""))) { //Se ha elegido introducir una preferencia
					numJugadoresMax = Integer.valueOf(txtJug2.getText());
				}
				if(!(txtEdad1.getText().equals(""))) { //Se ha elegido introducir una preferencia
					edadMin = Integer.valueOf(txtEdad1.getText());
				}
				if(!(txtEdad2.getText().equals(""))) { //Se ha elegido introducir una preferencia
					edadMax = Integer.valueOf(txtEdad2.getText());
				}
				if(!(txtDur1.getText().equals(""))) { //Se ha elegido introducir una preferencia
					duracionMin = Integer.valueOf(txtDur1.getText());
				}
				if(!(txtDur2.getText().equals(""))) { //Se ha elegido introducir una preferencia
					duracionMax = Integer.valueOf(txtDur2.getText());
				}
				
				String objetivo = cbxObjetivo.getSelectedItem().toString();
				String prioridad = cbxPrioridad.getSelectedItem().toString();
								
				//Realizas la busqueda por requisitos
				generarTablaEjerciciosBusqueda(numJugadoresMin, numJugadoresMax, edadMin, edadMax, duracionMin, duracionMax, objetivo, prioridad);
				
				
			}
		});
		
	}
		
	private void generarTablaEjercicios() {
		
		Connection conexion = null;
		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			String sentenciaConsultar = "SELECT * FROM public.\"Ejercicio\"";
			
			
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);
			
			listaEjercicios= new ArrayList<Ejercicio>();
			
			while(resultados.next()) { // Cada resultado es un ejercicio
				String id = String.valueOf(resultados.getInt("Id"));
				String nombre = resultados.getString("Nombre");
				String descripcion = resultados.getString("Descripcion");
				int numJugMin =resultados.getInt("NumeroJugadoresMinimo");
				int numJugMax =resultados.getInt("NumeroJugadoresMaximo");
				int duracion =resultados.getInt("Duracion");
				int edadMin =resultados.getInt("EdadMinima");
				int edadMax =resultados.getInt("EdadMaxima");
				String objetivo = resultados.getString("Objetivo");
				String tipo = resultados.getString("Tipo");
				double punt = resultados.getDouble("Puntuacion"); //Comprobar double-real
				int numUsos =resultados.getInt("NumUsos");
				String imagen = resultados.getString("ImagenURL");
				
				Ejercicio ejer = new Ejercicio(id, 0.0, nombre, descripcion, edadMin, edadMax, duracion, numJugMin, numJugMax,
						 punt, numUsos, objetivo, tipo, imagen);
				listaEjercicios.add(ejer); //Se va creando la lista con todos los ejercicios de la base,la preferencia no tiene relevancia aqui
				String tbData[] = {id, nombre, descripcion};
				//tblModel = (DefaultTableModel)table.getModel();
				tblModel.addRow(tbData);
			}
			
			//Hacer que al clickar un ejercicio cambie la ventana
			table.addMouseListener(new java.awt.event.MouseAdapter() {
			    @Override
			    public void mouseClicked(java.awt.event.MouseEvent evt) {
			        int row = table.rowAtPoint(evt.getPoint());
			        Ejercicio ejerElegido = listaEjercicios.get(row);

		        	DetalleEjercicio detEjer = new DetalleEjercicio(ejerElegido, infoUsuario);
					/*detEjer.generarVentana(ejerElegido.getId(), ejerElegido.getPreferencia(), ejerElegido.getNombre(), ejerElegido.getDescripcion(), ejerElegido.getEdadMin(), ejerElegido.getEdadMax(), 
							ejerElegido.getDuracion(), ejerElegido.getNumJugadoresMin(), ejerElegido.getNumJugadoresMax(), ejerElegido.getPuntuacion(), ejerElegido.getNumUsos(),
							ejerElegido.getObjetivo(), ejerElegido.getTipo(), ejerElegido.getImagenURL());*/
					//frame.setVisible(false);


			    }
			});
					
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
		
		
	}
	
private void generarTablaEjerciciosBusqueda(int numJugadoresMin, int numJugadoresMax, int edadMini, int edadMaxi, int duracionMin, int duracionMax, String objet, String prio) {
		
		Connection conexion = null;
		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			String sentenciaConsultar = "SELECT * FROM public.\"Ejercicio\" WHERE public.\"Ejercicio\".\"NumeroJugadoresMinimo\" >= '" + numJugadoresMin + 
					"' AND public.\"Ejercicio\".\"NumeroJugadoresMaximo\" <= '" + numJugadoresMax +
					"' AND public.\"Ejercicio\".\"Duracion\" >= '" + duracionMin +
					"' AND public.\"Ejercicio\".\"Duracion\" <= '" + duracionMax +
					"' AND public.\"Ejercicio\".\"EdadMinima\" >= '" + edadMini +
					"' AND public.\"Ejercicio\".\"EdadMaxima\" <= '" + edadMaxi + "'";
					//"' AND public.\"Ejercicio\".\"Objetivo\" = '" + objet + //arreglar tildes para que la comparacion de strings funcione
					//"' AND public.\"Ejercicio\".\"Tipo\" = '" + prio + "'";
			
			
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);
			
			listaEjercicios= new ArrayList<Ejercicio>();
			//Borras la lista de ejercicios que haya en el momento
			if (tblModel.getRowCount() > 0) {
			    for (int i = tblModel.getRowCount() - 1; i > -1; i--) {
			    	tblModel.removeRow(i);
			    }
			}
			
			while(resultados.next()) { // Cada resultado es un ejercicio
				String id = String.valueOf(resultados.getInt("Id"));
				System.out.println(id);
				String nombre = resultados.getString("Nombre");
				String descripcion = resultados.getString("Descripcion");
				int numJugMin =resultados.getInt("NumeroJugadoresMinimo");
				int numJugMax =resultados.getInt("NumeroJugadoresMaximo");
				int duracion =resultados.getInt("Duracion");
				int edadMin =resultados.getInt("EdadMinima");
				int edadMax =resultados.getInt("EdadMaxima");
				String objetivo = resultados.getString("Objetivo");
				String tipo = resultados.getString("Tipo");
				double punt = resultados.getDouble("Puntuacion"); //Comprobar double-real
				int numUsos =resultados.getInt("NumUsos");
				String imagen = resultados.getString("ImagenURL");
				
				Ejercicio ejer = new Ejercicio(id, 0.0, nombre, descripcion, edadMin, edadMax, duracion, numJugMin, numJugMax,
						 punt, numUsos, objetivo, tipo, imagen);
				listaEjercicios.add(ejer); //Se va creando la lista con todos los ejercicios de la base, segun la busqueda
				String tbData[] = {id, nombre, descripcion};
				//tblModel = (DefaultTableModel)table.getModel();				
				tblModel.addRow(tbData);
			}
			
			//Hacer que al clickar un ejercicio cambie la ventana
			table.addMouseListener(new java.awt.event.MouseAdapter() {
			    @Override
			    public void mouseClicked(java.awt.event.MouseEvent evt) {
			        int row = table.rowAtPoint(evt.getPoint());
			        Ejercicio ejerElegido = listaEjercicios.get(row);

		        	DetalleEjercicio detEjer = new DetalleEjercicio(ejerElegido, infoUsuario);
					/*detEjer.generarVentana(ejerElegido.getId(), ejerElegido.getPreferencia(), ejerElegido.getNombre(), ejerElegido.getDescripcion(), ejerElegido.getEdadMin(), ejerElegido.getEdadMax(), 
							ejerElegido.getDuracion(), ejerElegido.getNumJugadoresMin(), ejerElegido.getNumJugadoresMax(), ejerElegido.getPuntuacion(), ejerElegido.getNumUsos(),
							ejerElegido.getObjetivo(), ejerElegido.getTipo(), ejerElegido.getImagenURL());*/
					//frame.setVisible(false);


			    }
			});
					
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
		
		
	}
}
