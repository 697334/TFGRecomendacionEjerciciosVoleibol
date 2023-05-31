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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import util.Ejercicio;
import util.Rutina;
import util.Usuario;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JPasswordField;

public class Login {

	private JFrame frame;
	private JTextField txtUsuario;
	private JLabel lblError;
	private JPasswordField txtPass;
	private Usuario infoUsuario;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		infoUsuario = new Usuario();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 722, 483);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Usuario:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(174, 141, 101, 31);
		frame.getContentPane().add(lblNewLabel);
		
		txtUsuario = new JTextField();
		txtUsuario.setBounds(285, 143, 178, 31);
		frame.getContentPane().add(txtUsuario);
		txtUsuario.setColumns(10);
		
		JLabel lblContrasea = new JLabel("Contraseña:");
		lblContrasea.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblContrasea.setBounds(174, 196, 101, 31);
		frame.getContentPane().add(lblContrasea);
				
		JButton btnEntrar = new JButton("Entrar");
		btnEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			   String usuario = txtUsuario.getText().toString();
               String contrasenya = txtPass.getText().toString();
               if(validar(usuario, contrasenya)){
                   // Entras a la primera ventana de la aplicación:
            	   Inicio vtnInicio = new Inicio(infoUsuario);
   				   //vtnInicio.generarVentana(infoUsuario);
   				   frame.setVisible(false);
               }
               else if(existeUsuario(usuario)){
            	   lblError.setText("Contraseña incorrecta");
               }
               else{
                   //Añadir usuario y contraseña
                   if(crearUsuario(usuario, contrasenya)){
                	   // Entras a la primera ventana de la aplicación:
	            	   Inicio vtnInicio = new Inicio(infoUsuario);
	   				   //vtnInicio.generarVentana(infoUsuario);
	   				   frame.setVisible(false);
                   }
                   else{
                	   	lblError.setText("Error al crear nuevo usuario");
                   }
               }
			}
		});
		btnEntrar.setBounds(268, 255, 129, 37);
		frame.getContentPane().add(btnEntrar);
		
		lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		lblError.setBounds(174, 313, 289, 14);
		frame.getContentPane().add(lblError);
		
		txtPass = new JPasswordField();
		txtPass.setBounds(285, 198, 178, 30);
		frame.getContentPane().add(txtPass);
		
	}
	
	private boolean validar(String usuario, String contrasenya){
        boolean correcto = false;
        Connection conexion = null;

        try
        {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");

            String sentenciaConsultar = "select * from public.\"Usuario\"  WHERE public.\"Usuario\".\"Nombre\" = '"
                    + usuario + "' AND public.\"Usuario\".\"Contrasenya\" = '" + contrasenya + "'";

            Statement sentencia = conexion.createStatement();
            ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);

            if(resultados.next()) {
               correcto = true;
               infoUsuario.setIdUsuario(resultados.getInt("Id"));
               infoUsuario.setTipoUsuario(resultados.getString("Tipo"));
            }
            resultados.close();
            sentencia.close();

        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Error al cargar el conector de SQLite: " + cnfe.getMessage());
            cnfe.printStackTrace();
        }
        catch (SQLException sqle) {
            System.out.println("Error de SQL: " + sqle.getMessage());
            sqle.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
        finally {
            if(conexion != null) {
                try {
                	conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return correcto;
        }
    }

    private boolean existeUsuario(String usuario){
        boolean existe = false;
        Connection conexion = null;

        try
        {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");

            String sentenciaConsultar = "select * from public.\"Usuario\"  WHERE public.\"Usuario\".\"Nombre\" = '"
                    + usuario + "'";

            Statement sentencia = conexion.createStatement();
            ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);

            if(resultados.next()) {
                existe = true;
            }
            resultados.close();
            sentencia.close();

        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Error al cargar el conector de SQLite: " + cnfe.getMessage());
            cnfe.printStackTrace();
        }
        catch (SQLException sqle) {
            System.out.println("Error de SQL: " + sqle.getMessage());
            sqle.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
        finally {
            if(conexion != null) {
                try {
                	conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return existe;
        }
    }

    private boolean crearUsuario(String usuario, String contrasenya){
        boolean insertado = false;
        Connection conexion = null;

        try
        {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");

            String sentenciaAnyadirUsuario = "insert into \"Usuario\" (\"Nombre\", \"Contrasenya\", \"Tipo\") "
                   + " VALUES ('" + usuario + "' , '" + contrasenya + "' , 'entrenador')";

            Statement sentencia = conexion.createStatement();
            
            if(sentencia.executeUpdate(sentenciaAnyadirUsuario) == 1 ) {
                insertado = true;
                //Se obtiene el id del usuario recien creado para guardarlo para futuras operaciones
                String sentenciaObtenerIdUsuario = "SELECT MAX(\"Id\") FROM public.\"Usuario\"";
    			ResultSet resultadoIdUsuario = sentencia.executeQuery(sentenciaObtenerIdUsuario);
    			int idUsuario = 0;
    			while(resultadoIdUsuario.next()) {
    				idUsuario = resultadoIdUsuario.getInt(1);				
    			}
                infoUsuario.setIdUsuario(idUsuario);
                infoUsuario.setTipoUsuario("entrenador");
            }
            		
            sentencia.close();

        }
        catch (ClassNotFoundException cnfe) {
            System.out.println("Error al cargar el conector de SQLite: " + cnfe.getMessage());
            cnfe.printStackTrace();
        }
        catch (SQLException sqle) {
            System.out.println("Error de SQL: " + sqle.getMessage());
            sqle.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
        finally {
            if(conexion != null) {
                try {
                	conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return insertado;
        }
    }
}

