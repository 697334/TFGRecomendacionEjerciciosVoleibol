package interfaz;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import util.Rutina;
import util.Usuario;

public class Inicio {

	private JFrame frame;
	private Usuario infoUsuario;
	private int tipoRecomendacion = 0; //Con la configuracion se puede cambiar el tipo de recomendacion que se realiza

	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
	public Inicio(Usuario infoUsuario) {
		this.infoUsuario = infoUsuario;
		initialize();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//infoUsuario = new Usuario();
		//System.out.println(infoUsuario.getIdUsuario() + " " + infoUsuario.getTipoUsuario());
		frame = new JFrame();
		frame.setBounds(100, 100, 722, 483);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Recomendador");
		menuBar.add(mnNewMenu);	

		JMenuItem mntmNewMenuItem = new JMenuItem("Comenzar");
		mnNewMenu.add(mntmNewMenuItem);
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(tipoRecomendacion == 0) {
					ConfiguracionRutina cnfRt = new ConfiguracionRutina(infoUsuario, tipoRecomendacion);
					//cnfRt.generarVentana();
					frame.setVisible(false);
				}else {
					ConfiguracionRutinaAvanzada cnfRt = new ConfiguracionRutinaAvanzada(infoUsuario, tipoRecomendacion);
					//cnfRt.generarVentana();
					frame.setVisible(false);
				}							
			
			}
		});
		
		JMenu mnNewMenu_1 = new JMenu("Consultar ejercicios");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Ver ejercicios");
		mnNewMenu_1.add(mntmNewMenuItem_1);
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ConsultaEjercicios cstEjer = new ConsultaEjercicios(infoUsuario);
				//cstEjer.generarVentana();
				frame.setVisible(false);			
			
			}
		});
		
		JMenu mnNewMenu_3 = new JMenu("Puntuaciones");
		menuBar.add(mnNewMenu_3);
		
		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Ver historial de puntuaciones");
		mnNewMenu_3.add(mntmNewMenuItem_6);
		mntmNewMenuItem_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ConsultarPuntuaciones cstEjer = new ConsultarPuntuaciones(infoUsuario);
				//cstEjer.generarVentana();
				frame.setVisible(false);			
			
			}
		});
		
		JMenu mnNewMenu_2 = new JMenu("Configuración");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("TF-IDF");
		mnNewMenu_2.add(mntmNewMenuItem_2);
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				tipoRecomendacion = 1;						
			}
		});
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("TF-IDF avanzado");
		mnNewMenu_2.add(mntmNewMenuItem_3);
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				tipoRecomendacion = 2;						
			}
		});
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("LDA");
		mnNewMenu_2.add(mntmNewMenuItem_4);
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				tipoRecomendacion = 3;						
			}
		});
		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Preferencias");
		mnNewMenu_2.add(mntmNewMenuItem_5);
		mntmNewMenuItem_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				tipoRecomendacion = 0;						
			}
		});		
		frame.getContentPane().setLayout(null);
		
		String texto = "<html><body>¡Bienvenido<br> al sistema recomendador de<br> ejercicios de voleibol!</body></html>";
		JLabel lblNewLabel = new JLabel(texto);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 34));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(117, 127, 508, 176);
		frame.getContentPane().add(lblNewLabel);
		
	}
}
