package interfaz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import util.Ejercicio;

import javax.swing.JButton;

public class EliminarEjercicio {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	/*public static void generarVentana() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EliminarEjercicio window = new EliminarEjercicio();
					window.borrarEjercicio();
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
	public EliminarEjercicio() {
		initialize();
		borrarEjercicio();
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
		
		JLabel lblNewLabel = new JLabel("¿Está seguro de querer eliminar un ejercicio?");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(126, 60, 378, 77);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblId = new JLabel("Si está seguro, introduzca su código y pulse Borrar:");
		lblId.setBounds(126, 193, 303, 14);
		frame.getContentPane().add(lblId);
		
		JButton btnNewButton_1 = new JButton("Atrás");
		btnNewButton_1.setBounds(34, 33, 89, 23);
		frame.getContentPane().add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
			}
		});
		
		
	}
	
	private void borrarEjercicio() {
		
		textField = new JTextField();
		textField.setBounds(420, 190, 107, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Borrar");
		btnNewButton.setBounds(281, 247, 97, 33);
		frame.getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Connection conexion = null;
				try {
					Class.forName("org.postgresql.Driver");
					conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
					System.out.println("Conectado");
					System.out.println(textField.getText());
					String sentenciaBorrar = "DELETE FROM public.\"RutinaEjercicio\" WHERE public.\"RutinaEjercicio\".\"IdEjercicio\" = " + textField.getText() + "; " +
								"DELETE FROM public.\"UsuarioEjercicio\" WHERE public.\"UsuarioEjercicio\".\"IdEjercicio\" = " + textField.getText() + "; " +
								"DELETE FROM public.\"Ejercicio\" WHERE public.\"Ejercicio\".\"Id\" = " + textField.getText();
					Statement sentencia = conexion.createStatement();
					sentencia.executeUpdate(sentenciaBorrar);
							
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
