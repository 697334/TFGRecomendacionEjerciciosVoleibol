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

public class ConfiguracionRutinaAvanzada {

	private JFrame frmSeleccionarCaracteristicas;
	private JTextField txtDuracion;
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
	public ConfiguracionRutinaAvanzada(Usuario infoUsuario, int tipoRecomendacion) {
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
		
		JLabel lblNewLabel = new JLabel("Introduzca la duración aproximada del entrenamiento:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel.setBounds(208, 117, 581, 25);
		frmSeleccionarCaracteristicas.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_3 = new JLabel("Duraci\u00F3n total aproximada:");
		lblNewLabel_3.setBounds(325, 189, 143, 14);
		frmSeleccionarCaracteristicas.getContentPane().add(lblNewLabel_3);
		
		txtDuracion = new JTextField();
		txtDuracion.setBounds(497, 186, 108, 20);
		frmSeleccionarCaracteristicas.getContentPane().add(txtDuracion);
		txtDuracion.setColumns(10);
		
		JButton btnAtras = new JButton("Atrás");
		btnAtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Entras a la primera ventana de la aplicación:
	         	Inicio vtnInicio = new Inicio(infoUsuario);
				//vtnInicio.generarVentana(infoUsuario);
	         	frmSeleccionarCaracteristicas.setVisible(false);
			}
		});
		btnAtras.setBounds(26, 25, 81, 44);
		frmSeleccionarCaracteristicas.getContentPane().add(btnAtras);
		
		JButton btnNewButton = new JButton("Generar entrenamiento");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int duracion = 50; //Sesión de duración estándar por defecto

				if(!(txtDuracion.getText().equals(""))) { //Se ha elegido introducir una preferencia
					duracion = Integer.valueOf(txtDuracion.getText());
				}

				if (duracion < 0) {
				     lblError.setText("Duración incorrecta");
				}else {

					Rutina rut= new Rutina(duracion);
					RutinaEjercicios rtEjer = new RutinaEjercicios(rut, infoUsuario, tipoRecomendacion);
					frmSeleccionarCaracteristicas.setVisible(false);
				
				}
				
			}
		});
		btnNewButton.setBounds(346, 264, 247, 56);
		frmSeleccionarCaracteristicas.getContentPane().add(btnNewButton);		
		
		lblError = new JLabel("");
		lblError.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblError.setHorizontalAlignment(SwingConstants.CENTER);
		lblError.setForeground(Color.RED);
		lblError.setBounds(198, 88, 529, 14);
		frmSeleccionarCaracteristicas.getContentPane().add(lblError);
	}
}

