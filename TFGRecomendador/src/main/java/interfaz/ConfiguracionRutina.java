package interfaz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.SwingConstants;

import util.Rutina;
import util.Usuario;

public class ConfiguracionRutina {

	private JFrame frmSeleccionarCaracteristicas;
	private JTextField txtNumJugadores;
	private JTextField txtEdad;
	private JTextField txtDuracion;
	private JComboBox cbxObjetivo;
	private JComboBox cbxPrioridad;
	private JLabel lblError;
	private Usuario infoUsuario;
	private int tipoRecomendacion;

	/**
	 * Launch the application.
	 */
	/*public static void generarVentana() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfiguracionRutina window = new ConfiguracionRutina();
					window.frmSeleccionarCaracteristicas.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the application.
	 */
	public ConfiguracionRutina(Usuario infoUsuario, int tipoRecomendacion) {
		this.infoUsuario = infoUsuario;
		this.tipoRecomendacion = tipoRecomendacion;
		initialize();
		frmSeleccionarCaracteristicas.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSeleccionarCaracteristicas = new JFrame();
		frmSeleccionarCaracteristicas.setTitle("Seleccionar caracter\u00EDsticas");
		frmSeleccionarCaracteristicas.setBounds(100, 100, 1006, 543);
		frmSeleccionarCaracteristicas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSeleccionarCaracteristicas.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Introduzca las preferencias de su entrenamiento:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel.setBounds(206, 52, 548, 25);
		frmSeleccionarCaracteristicas.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("N\u00BA jugadores:\r ");
		lblNewLabel_1.setBounds(198, 124, 73, 14);
		frmSeleccionarCaracteristicas.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Edad aproximada:");
		lblNewLabel_2.setBounds(509, 124, 108, 14);
		frmSeleccionarCaracteristicas.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Duraci\u00F3n total aproximada:");
		lblNewLabel_3.setBounds(198, 193, 143, 14);
		frmSeleccionarCaracteristicas.getContentPane().add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Objetivo a mejorar:");
		lblNewLabel_4.setBounds(509, 193, 108, 14);
		frmSeleccionarCaracteristicas.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Dar prioridad a:");
		lblNewLabel_5.setBounds(340, 246, 90, 14);
		frmSeleccionarCaracteristicas.getContentPane().add(lblNewLabel_5);
		
		txtNumJugadores = new JTextField();
		txtNumJugadores.setBounds(344, 121, 108, 20);
		frmSeleccionarCaracteristicas.getContentPane().add(txtNumJugadores);
		txtNumJugadores.setColumns(10);
		
		txtEdad = new JTextField();
		txtEdad.setBounds(646, 121, 108, 20);
		frmSeleccionarCaracteristicas.getContentPane().add(txtEdad);
		txtEdad.setColumns(10);
		
		txtDuracion = new JTextField();
		txtDuracion.setBounds(344, 190, 108, 20);
		frmSeleccionarCaracteristicas.getContentPane().add(txtDuracion);
		txtDuracion.setColumns(10);
		
		cbxPrioridad = new JComboBox();
		cbxPrioridad.setModel(new DefaultComboBoxModel(new String[] {"Equilibrado", "T\u00E1ctica", "T\u00E9cnica", "F\u00EDsico"}));
		cbxPrioridad.setBounds(450, 242, 105, 22);
		frmSeleccionarCaracteristicas.getContentPane().add(cbxPrioridad);
		
		cbxObjetivo = new JComboBox();
		cbxObjetivo.setModel(new DefaultComboBoxModel(new String[] {"Equilibrado", "Ataque", "Defensa", "Recepci\u00F3n", "Colocaci\u00F3n", "Remate", "Bloqueo", "Saque"}));
		cbxObjetivo.setBounds(646, 189, 108, 22);
		frmSeleccionarCaracteristicas.getContentPane().add(cbxObjetivo);
		
		JButton btnNewButton = new JButton("Generar entrenamiento");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numJugadores = 0;
				int edad = 0;
				int duracion = 50; //Sesión de duración estándar por defecto
				
				if(!(txtNumJugadores.getText().equals(""))) { //Se ha elegido introducir una preferencia
					numJugadores = Integer.valueOf(txtNumJugadores.getText());
				}
				if(!(txtEdad.getText().equals(""))) { //Se ha elegido introducir una preferencia
					edad = Integer.valueOf(txtEdad.getText());
				}
				if(!(txtDuracion.getText().equals(""))) { //Se ha elegido introducir una preferencia
					duracion = Integer.valueOf(txtDuracion.getText());
				}

				String objetivo = cbxObjetivo.getSelectedItem().toString();
				String prioridad = cbxPrioridad.getSelectedItem().toString();
				
				if (numJugadores < 0) {
				     lblError.setText("Número de jugadores incorrecto");
				}
				else if (edad < 0) {
				     lblError.setText("Edad incorrecta");
				}
				else if (duracion < 0) {
				     lblError.setText("Duración incorrecta");
				}else {
					/*RutinaEjercicios rtEjer = new RutinaEjercicios();
					Rutina rut= new Rutina(numJugadores, edad, duracion, objetivo, prioridad);
					//rtEjer.generarVentana(numJugadores, edad, duracion, objetivo, prioridad);
					rtEjer.generarVentana(rut);*/
					Rutina rut= new Rutina(numJugadores, edad, duracion, objetivo, prioridad);
					RutinaEjercicios rtEjer = new RutinaEjercicios(rut, infoUsuario, tipoRecomendacion);
					frmSeleccionarCaracteristicas.setVisible(false);
				
				}
				
			}
		});
		btnNewButton.setBounds(357, 315, 247, 56);
		frmSeleccionarCaracteristicas.getContentPane().add(btnNewButton);		
		
		lblError = new JLabel("");
		lblError.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblError.setHorizontalAlignment(SwingConstants.CENTER);
		lblError.setForeground(Color.RED);
		lblError.setBounds(198, 88, 529, 14);
		frmSeleccionarCaracteristicas.getContentPane().add(lblError);
	}
}
