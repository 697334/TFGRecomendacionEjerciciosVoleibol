package interfaz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import util.Ejercicio;
import util.Rutina;
import util.Usuario;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.SwingConstants;

public class DetalleEjercicio {

	private JFrame frame;
	private JTextField txtPuntuacionEntrenador;
	private JLabel lblError;
	private Ejercicio ejercicio;
	private Usuario infoUsuario;

	/**
	 * Launch the application.
	 */
	/*public static void generarVentana(final String id, final double preferencia, final String nombre, final String descripcion, final int edadMin, final int edadMax,
			final int duracion, final int numJugadoresMin, final int numJugadoresMax, final double puntuacion, final int numUsos, final String objetivo, final String tipo, final String imagenURL) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ejercicio ejer = new Ejercicio(id, preferencia, nombre, descripcion, edadMin, edadMax, duracion, numJugadoresMin, numJugadoresMax, puntuacion, numUsos, objetivo, tipo, imagenURL);
					DetalleEjercicio window = new DetalleEjercicio();
					window.verDetalles(ejer);
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
	public DetalleEjercicio(Ejercicio ejercicio, Usuario infoUsuario) {
		this.infoUsuario = infoUsuario;
		this.ejercicio = ejercicio;
		initialize();
		verDetalles();
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 879, 522);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Atr\u00E1s");
		btnNewButton.setBounds(24, 29, 65, 32);
		frame.getContentPane().add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);				
			}
		});
								
	}
	
	private void verDetalles() {
		
		
		//Caracteristicas del ejercicio
		JLabel lblNewLabel = new JLabel(ejercicio.getNombre());
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(166, 80, 394, 32);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel(new ImageIcon(ejercicio.getImagenURL()));
		lblNewLabel_1.setBounds(537, 0, 316, 167);
		frame.getContentPane().add(lblNewLabel_1);
		
		//Meter saltos <br> para las descripciones
		JLabel lblNewLabel_2 = new JLabel(ejercicio.getDescripcion());
		lblNewLabel_2.setBounds(166, 133, 350, 120);
		frame.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Caracter\u00EDsticas del ejercicio:");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_3.setBounds(166, 249, 182, 14);
		frame.getContentPane().add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("Edad: " + ejercicio.getEdadMin() + " - " + ejercicio.getEdadMax());
		lblNewLabel_4.setBounds(166, 274, 128, 30);
		frame.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("N\u00BA de jugadores: "+ ejercicio.getNumJugadoresMin() + " - " + ejercicio.getNumJugadoresMax());
		lblNewLabel_5.setBounds(166, 314, 128, 20);
		frame.getContentPane().add(lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("Duraci\u00F3n: "+ ejercicio.getDuracion() + " minutos");
		lblNewLabel_6.setBounds(166, 352, 128, 14);
		frame.getContentPane().add(lblNewLabel_6);
		
		
		//Puntuaciones
		JLabel lblPuntuacionEntrenador = new JLabel("Puntuación del entrenador (de 0 a 10):");
		lblPuntuacionEntrenador.setBounds(166, 407, 266, 14);
		frame.getContentPane().add(lblPuntuacionEntrenador);
		
		txtPuntuacionEntrenador = new JTextField();
		txtPuntuacionEntrenador.setBounds(442, 404, 86, 20);
		frame.getContentPane().add(txtPuntuacionEntrenador);
		txtPuntuacionEntrenador.setColumns(10);
		
		JButton btnPuntuar = new JButton("Puntuar");
		
		btnPuntuar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*int puntEntrenador;
				System.out.println(txtPuntuacionEntrenador.getText());
				
				if(!(txtPuntuacionEntrenador.getText().equals(""))) { //La puntuacion se ha escrito				
					puntEntrenador = Integer.valueOf(txtPuntuacionEntrenador.getText());
					anyadirPuntuacion(puntEntrenador, ejercicio.getId());
				}*/
				if(txtPuntuacionEntrenador.getText().length() != 0){
	                int puntos = Integer.valueOf(txtPuntuacionEntrenador.getText());
	                if(puntos<0 || puntos >10){
	                	lblError.setText("Puntuación introducida incorrecta");
	                }
	                else{
	                    if(comprobarEjercicioUsuario()){ //el usuario ya habia puntuado ese ejercicio
	                        rellenarValoracion(puntos);
	                    }
	                    else{ //es la primera puntuacion del usuario de ese ejercicio
	                        rellenarValoracionNueva(puntos);
	                    }
	                }

	            }
			}
		});
		btnPuntuar.setBounds(551, 390, 89, 55);
		frame.getContentPane().add(btnPuntuar);
		
		lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		lblError.setBounds(166, 432, 362, 14);
		frame.getContentPane().add(lblError);
		
		JButton btnBorrar = new JButton("<html><body>Borrar<br>puntuación</body></html>");
		btnBorrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				borrarPuntuacion();
			}
		});
		btnBorrar.setBounds(737, 401, 86, 41);
		frame.getContentPane().add(btnBorrar);
		
	}
	
	private void borrarPuntuacion(){
		Connection connection = null;
        try
        {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
            Statement sentencia = connection.createStatement();

            /*String sentenciaConsultar = "SELECT * FROM public.\"UsuarioEjercicio\" " +                   
                    " WHERE public.\"UsuarioEjercicio\".\"IdEjercicio\" = '" + ejercicio.getId() +
                    "' AND public.\"UsuarioEjercicio\".\"IdUsuario\" = '" + infoUsuario.getIdUsuario() + "'";

            System.out.println(sentenciaConsultar);
            ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);
            while(resultados.next()) {
            	
            }*/
            String sentenciaBorrar = "DELETE FROM public.\"UsuarioEjercicio\" WHERE public.\"UsuarioEjercicio\".\"IdEjercicio\" = " + ejercicio.getId()
            		+ " AND  public.\"UsuarioEjercicio\".\"IdUsuario\" = " + infoUsuario.getIdUsuario();
            
            System.out.println(sentenciaBorrar);
            
			sentencia.executeUpdate(sentenciaBorrar);
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
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	/*private void anyadirPuntuacion(double puntuacionNueva,String id) {
		
		Connection conexion = null;
		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");

			Statement sentencia = conexion.createStatement();
			String sentenciaUpdate = "UPDATE public.\"Ejercicio\" " +
                    " SET \"Puntuacion\" = (\"Puntuacion\"*\"NumUsos\" + " + puntuacionNueva + ")/(\"NumUsos\" + 1) ," +
                    " \"NumUsos\" = \"NumUsos\" + 1" +
                    " where public.\"Ejercicio\".\"Id\" = " + id ;
						
			sentencia.executeUpdate(sentenciaUpdate);
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

	}*/
	
	private void rellenarValoracion(int puntos){
        Connection connection = null;
        try
        {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
            Statement sentencia = connection.createStatement();

            String sentenciaModificar = "UPDATE public.\"UsuarioEjercicio\" " +
                    " SET \"Puntuacion\" = " + puntos +
                    " WHERE public.\"UsuarioEjercicio\".\"IdEjercicio\" = '" + ejercicio.getId() +
                    "' AND public.\"UsuarioEjercicio\".\"IdUsuario\" = '" + infoUsuario.getIdUsuario() + "'";

            System.out.println(sentenciaModificar);
            
            String sentenciaUpdate = "UPDATE public.\"Ejercicio\" SET \"NumUsos\" = '"+ (ejercicio.getNumUsos() + 1) +"', \"Puntuacion\" = '" +
                    (ejercicio.getPuntuacion() * ejercicio.getNumUsos() + puntos)/(ejercicio.getNumUsos() + 1) + "' WHERE public.\"Ejercicio\".\"Id\" = " + ejercicio.getId();
        			System.out.println(sentenciaUpdate);
        			
            sentencia.execute(sentenciaModificar);
            sentencia.executeUpdate(sentenciaUpdate);
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
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void rellenarValoracionNueva(int puntos){
        Connection connection = null;
        try
        {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
            Statement sentencia = connection.createStatement();

            String sentenciaModificar = "INSERT INTO public.\"UsuarioEjercicio\" (\"IdUsuario\", \"IdEjercicio\", \"Puntuacion\")"
                    + "VALUES ('" + infoUsuario.getIdUsuario() + "', '" + ejercicio.getId() + "', '" + puntos +  "')";

            System.out.println(sentenciaModificar);
            
            String sentenciaUpdate = "UPDATE public.\"Ejercicio\" SET \"NumUsos\" = '"+ (ejercicio.getNumUsos() + 1) +"', \"Puntuacion\" = '" +
            (ejercicio.getPuntuacion() * ejercicio.getNumUsos() + puntos)/(ejercicio.getNumUsos() + 1) + "' WHERE public.\"Ejercicio\".\"Id\" = " + ejercicio.getId();
			System.out.println(sentenciaUpdate);
			
            sentencia.execute(sentenciaModificar);
            sentencia.executeUpdate(sentenciaUpdate);
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
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean comprobarEjercicioUsuario(){
        boolean yaExiste = false;
        Connection connection = null;

        try
        {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
            Statement sentencia = connection.createStatement();

            String sentenciaConsultar = "select * from public.\"UsuarioEjercicio\"  where public.\"UsuarioEjercicio\".\"IdUsuario\" = '"
                    + infoUsuario.getIdUsuario() + "' AND public.\"UsuarioEjercicio\".\"IdEjercicio\" = '" + ejercicio.getId() + "'";

            ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);

            if(resultados.next()) {
                yaExiste = true;
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
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return yaExiste;
        }
    }
}
