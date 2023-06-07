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


public class ConsultarPuntuaciones{

	private JFrame frame;
	private JTable table;
	private  List<Ejercicio> listaEjercicios;
	private DefaultTableModel tblModel;
	private Usuario infoUsuario;


	/**
	 * Create the application.
	 */
	public ConsultarPuntuaciones(Usuario infoUsuario) {
		this.infoUsuario = infoUsuario;
		initialize();
		generarTablaPuntuaciones();
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
		scrollPane.setBounds(207, 48, 248, 277);
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
		
		JButton btnAtras = new JButton("Atrás");
		btnAtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Entras a la primera ventana de la aplicación:
	         	Inicio vtnInicio = new Inicio(infoUsuario);
				//vtnInicio.generarVentana(infoUsuario);
				frame.setVisible(false);
			}
		});
		btnAtras.setBounds(10, 11, 80, 42);
		frame.getContentPane().add(btnAtras);
		
	}
		
	private void generarTablaPuntuaciones() {
		
		Connection conexion = null;
		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			String sentenciaConsultar =
					"SELECT public.\"Ejercicio\".\"Id\", public.\"Ejercicio\".\"Nombre\", public.\"Ejercicio\".\"Descripcion\", "
					+ "public.\"Ejercicio\".\"NumeroJugadoresMinimo\", public.\"Ejercicio\".\"NumeroJugadoresMaximo\", public.\"Ejercicio\".\"Duracion\", "
					+ "public.\"Ejercicio\".\"EdadMinima\", public.\"Ejercicio\".\"EdadMaxima\", public.\"Ejercicio\".\"Objetivo\", "
					+ "public.\"Ejercicio\".\"Tipo\", public.\"Ejercicio\".\"Puntuacion\" AS \"PUNTUACION_MEDIA\", public.\"Ejercicio\".\"NumUsos\", "
					+ "public.\"Ejercicio\".\"ImagenURL\", public.\"UsuarioEjercicio\".\"Puntuacion\" AS \"PUNTUACION_USUARIO\""
					+ "FROM public.\"UsuarioEjercicio\" INNER JOIN public.\"Ejercicio\" ON public.\"UsuarioEjercicio\".\"IdEjercicio\"=public.\"Ejercicio\".\"Id\""
					+ "WHERE public.\"UsuarioEjercicio\".\"IdUsuario\" = " + infoUsuario.getIdUsuario();
			
			
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
				double punt_media = resultados.getDouble("PUNTUACION_MEDIA");
				double punt_ejercicio = resultados.getDouble("PUNTUACION_USUARIO");
				int numUsos =resultados.getInt("NumUsos");
				String imagen = resultados.getString("ImagenURL");
								
				Ejercicio ejer = new Ejercicio(id, 0.0, nombre, descripcion, edadMin, edadMax, duracion, numJugMin, numJugMax,
						punt_media, numUsos, objetivo, tipo, imagen);
				listaEjercicios.add(ejer); //Se va creando la lista con todos los ejercicios de la base,la preferencia no tiene relevancia aqui
				String tbData[] = {id, nombre, String.valueOf(punt_ejercicio)};
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
