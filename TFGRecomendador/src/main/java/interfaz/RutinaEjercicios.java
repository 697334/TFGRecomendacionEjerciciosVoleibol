package interfaz;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import java.awt.BorderLayout;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.BasicConfigurator;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.AbstractItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.LoglikelihoodSimilarity;

import com.topic.model.GibbsSamplingLDA;

import util.Ejercicio;
import util.Rutina;
import util.TFIDFCalculator;
import util.Usuario;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;


public class RutinaEjercicios{

	private JFrame frame;
	private JTable table;
	private Rutina rut;
	private List<Ejercicio> listaEjerciciosRutina;
	private Usuario infoUsuario;
	private int tipoRecomendacion;
	private List<Double> vectorTfidfUsuario = new ArrayList<Double>();
	private List<List<Double>> vectorTfidfEjercicios = new ArrayList<List<Double>>();


	/**
	 * Create the application.
	 */
	public RutinaEjercicios(Rutina rut, Usuario infoUsuario, int tipoRecomendacion) {
		this.rut = rut;
		this.infoUsuario = infoUsuario;
		this.tipoRecomendacion = tipoRecomendacion;
		initialize();	
		try {
			generarRutina();
			//generarRecomendacionConocimiento();
			//generarRecomendacionLDA();
			//generarRecomendacionTFIDF();
			//generarRecomendacionTFIDFAdvance();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 809, 473);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"C\u00F3digo", "Nombre", "Descripci\u00F3n"
			}
		));
		table.setBounds(108, 69, 565, 264);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		frame.getContentPane().add(table);
		
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(304, 108, 225, 185);
		frame.getContentPane().add(scrollPane);				

	}
	
	private void generarRutina()  throws InterruptedException {
		Connection conexion = null;

		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			//Debes consultar si el usuario tiene suficientes puntuaciones como para generar un perfil
			String sentenciaConsultar = "SELECT * FROM public.\"UsuarioEjercicio\" WHERE public.\"UsuarioEjercicio\".\"IdUsuario\" =" + infoUsuario.getIdUsuario();
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);			
			int conteoEjercicios = 0;
			while(resultados.next()) { // Cada resultado es un ejercicio puntuado por el usuario
				conteoEjercicios++;
			}
			//Se piden al menos 5 puntuaciones introducidas por el usuario
			if(conteoEjercicios < 5) {
				generarRecomendacionConocimiento();
			}else {
				switch (tipoRecomendacion) { 
				    case 1:
				    	generarRecomendacionTFIDF();
				    break;
				    case 2:
				    	generarRecomendacionTFIDFAdvance();
				    break;
				    case 3:
				    	generarRecomendacionLDA();
					break;
				    default:
				    	generarRecomendacionConocimiento();
			  }
			}

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
	
	private void generarRecomendacionConocimiento() throws InterruptedException {
		Connection conexion = null;

		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			String sentenciaConsultar = "SELECT * FROM public.\"Ejercicio\"";
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);
			
			List<Ejercicio> listaEjercicios= new ArrayList<Ejercicio>();
			listaEjerciciosRutina= new ArrayList<Ejercicio>();
					
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
				double punt = resultados.getDouble("Puntuacion");
				int numUsos =resultados.getInt("NumUsos");
				String imagen = resultados.getString("ImagenURL");
				
				double preferencia = 0.0;
				//Condiciones de aumento de preferencia
				if(rut.getNumJugadores() == 0) {
					preferencia = preferencia + 0.7; //Se ha elegido no especificar numJugadores
				}
				if(rut.getEdad() == 0) {
					preferencia = preferencia + 0.7; //Se ha elegido no especificar edad
				}
				if(numJugMin <= rut.getNumJugadores() && numJugMax >= rut.getNumJugadores()) {
					preferencia = preferencia + 1.4;
				}
				if(edadMin <= rut.getEdad() &&edadMax >= rut.getEdad()) {
					preferencia = preferencia + 1.4;
				}
				if(objetivo.equals(rut.getObjetivo())){
					preferencia = preferencia + 1.1;
				}
				if(tipo.equals(rut.getTipo())){
					preferencia = preferencia + 1.1;
				}
				
				
				//Anyadir preferencia teniendo en cuenta puntuacion de la preferencia de los jugadores con el vector de IDs 
				
				Ejercicio ejer = new Ejercicio(id, preferencia, nombre, descripcion, edadMin, edadMax, duracion, numJugMin, numJugMax,
					 punt, numUsos, objetivo, tipo, imagen);
				listaEjercicios.add(ejer); //Se va creando la lista con todos los ejercicios de la base y sus preferencias
				
			}	
			
			Collections.sort(listaEjercicios, new Comparator<Ejercicio>() {
		        @Override
		        public int compare(Ejercicio ejer1, Ejercicio ejer2) {

		        	return Double.compare(ejer1.getPreferencia(), ejer2.getPreferencia());
		        }
		    });
			for (int i = 0; i < listaEjercicios.size(); i++) {
			      System.out.println(listaEjercicios.get(i).getId() + ", " + listaEjercicios.get(i).getPreferencia());
			}
			
			int duracionRestanteRutina = rut.getDuracion();
			int duracionRestanteFisico = duracionRestanteRutina/2;
			
			//Se rellena la tabla con los datos por preferencia
			int i = 1;
			while(duracionRestanteRutina > 0) {
				System.out.println(duracionRestanteRutina);
				Ejercicio ejerObtenido = listaEjercicios.get(listaEjercicios.size()-i); //Obtienes el ejercicio con mayor preferencia
				i++;
				
				if(!(ejerObtenido.getTipo().equals("Físico") && (duracionRestanteFisico - ejerObtenido.getDuracion())< 0)){ //La cantidad de ejercicios fisicos no ha superado el limite y el ejercicio es fisico
										
					listaEjerciciosRutina.add(ejerObtenido); //Se anyade ejercicio elegido a la lista definitiva
					
					duracionRestanteRutina = duracionRestanteRutina - ejerObtenido.getDuracion();
					if(ejerObtenido.getTipo().equals("Físico")) {
						duracionRestanteFisico = duracionRestanteFisico - ejerObtenido.getDuracion();
					}
				}			
			}
			
			//Elimina el ultimo ejercicio por haberse pasado del limite de duracion
			listaEjerciciosRutina.remove(listaEjerciciosRutina.size() - 1);
			
			
			//Añades una nueva rutina a la base y obtienes su id
			String sentenciaAnyadirRutina = "INSERT INTO \"Rutina\" (\"Nombre\") VALUES ('Nombre Rutina')";
			sentencia.executeUpdate(sentenciaAnyadirRutina);
			String sentenciaObtenerIdRutina = "SELECT MAX(\"Id\") FROM public.\"Rutina\"";
			ResultSet resultadoIdRutina = sentencia.executeQuery(sentenciaObtenerIdRutina);
			String idRutina = "";
			while(resultadoIdRutina.next()) {
				idRutina = String.valueOf(resultadoIdRutina.getInt(1));				
			}
			
			for (int j = 0; j < listaEjerciciosRutina.size(); j++) {
				Ejercicio ejerObtenidoRutina = listaEjerciciosRutina.get(j);
				//Insertas la rutina con los ejercicios relacionados
				String sentenciaAnyadirRutinaEjercicio = "INSERT INTO \"RutinaEjercicio\" (\"IdEjercicio\", \"IdRutina\") VALUES ('" +
				ejerObtenidoRutina.getId() + "', '" + idRutina + "')";
				sentencia.executeUpdate(sentenciaAnyadirRutinaEjercicio);
				
				//Muestras los datos clave del ejercicio en la tabla
				String tbData[] = {ejerObtenidoRutina.getId(), ejerObtenidoRutina.getNombre(), ejerObtenidoRutina.getDescripcion()};
				DefaultTableModel tblModel = (DefaultTableModel)table.getModel();
				tblModel.addRow(tbData);
			}

			table.addMouseListener(new java.awt.event.MouseAdapter() {
			    @Override
			    public void mouseClicked(java.awt.event.MouseEvent evt) {
			        int row = table.rowAtPoint(evt.getPoint());
			        Ejercicio ejerElegido = listaEjerciciosRutina.get(row);

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
	
	
	private List<Double> obtenerSimilitudCosenosTfIdf() {
		TFIDFCalculator calculator = new TFIDFCalculator();
		calculator.vectorizarEjercicios();
    	calculator.calcularTfidf();
		
		Connection conexion = null;
		BasicConfigurator.configure();
		List<Double> resultadoSimilitud = new ArrayList<Double>();
		try {
			
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");			
			//String sentenciaConsultarEntrenador = "SELECT * FROM public.\"UsuarioEjercicio\" WHERE public.\"UsuarioEjercicio\".\"IdUsuario\" = " + infoUsuario.getIdUsuario();
			String sentenciaConsultarEntrenador =
					"SELECT public.\"Ejercicio\".\"Id\", public.\"Ejercicio\".\"Descripcion\", public.\"UsuarioEjercicio\".\"Puntuacion\" "
					+ "FROM public.\"UsuarioEjercicio\" INNER JOIN public.\"Ejercicio\" ON public.\"UsuarioEjercicio\".\"IdEjercicio\"=public.\"Ejercicio\".\"Id\""
					+ "WHERE public.\"UsuarioEjercicio\".\"IdUsuario\" = " + infoUsuario.getIdUsuario();
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultarEntrenador);
			
			List<String> stopWords = calculator.crearListaStopWords("src/main/java/util/stop-words.txt");
			List<Double> acumuladorSimilitud = new ArrayList<Double>(); //Guarda el calculo de similitud con cosineSimilarity del total de los ejercicios puntuados
			String descripcionEjercicio;
			double puntuacionUsuario;
			double puntuacionUsuarioMax = 1;
			while(resultados.next()) { // Cada resultado es un Ejercicio del Usuario activo, con su descripcion y la puntuacion dada por el usuario
				puntuacionUsuario = resultados.getDouble("Puntuacion");
				if(puntuacionUsuario > puntuacionUsuarioMax) { //Para normalizar el vector de TfIdf mas adelante
					puntuacionUsuarioMax = puntuacionUsuario;
				}
				descripcionEjercicio = resultados.getString("Descripcion");
				descripcionEjercicio = descripcionEjercicio.replaceAll(",", "");
				descripcionEjercicio = descripcionEjercicio.replaceAll("\\.", "");
				descripcionEjercicio = descripcionEjercicio.replaceAll("\"", "");
				descripcionEjercicio = descripcionEjercicio.replaceAll(":", "");
				//Se calcula el vector con las puntuaciones tfIdf teniendo en cuenta la puntuacion del ejercicio
				
				List<String> listaPalabrasDescripcion = new ArrayList<String>();
				
				for(String s:descripcionEjercicio.split(" ")) { //Anyades cada palabra de una descripcion a tu lista
					listaPalabrasDescripcion.add(s.toLowerCase());					
				}
				//Resta las stopwords y obtienes la lista de las palabras de la descripcion
				listaPalabrasDescripcion.removeAll(stopWords);
				
				List<Double> listaTfIdf = calculator.calcularTfidfConcreto(listaPalabrasDescripcion, puntuacionUsuario);
				
				int i = 0;
				//Vas añadiendo todas las listas de puntuacionTfIdf para obtener la lista perfil de usuario
				if(acumuladorSimilitud.size() == 0) {
					acumuladorSimilitud.addAll(listaTfIdf);
				}else {
					for(double puntTfIdf:listaTfIdf) {
						
						acumuladorSimilitud.set(i, acumuladorSimilitud.get(i) + puntTfIdf);
						i++;
					}
				}
				
			}
			
			int i = 0;
			//Normalizacion de la lista
			for(double puntTfIdf:acumuladorSimilitud) {
				
				acumuladorSimilitud.set(i, puntTfIdf / puntuacionUsuarioMax);
				i++;
			}
			
			resultadoSimilitud = calculator.calcularCosineSimilarityConcreto(acumuladorSimilitud);

			sentencia.close();
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultadoSimilitud;
		
	}
	
	
	private void generarRecomendacionTFIDF()  throws InterruptedException {
		
		List<Double> resultadoSimilitud = obtenerSimilitudCosenosTfIdf();
		
		Connection conexion = null;

		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			String sentenciaConsultar = "SELECT * FROM public.\"Ejercicio\"";
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);
			
			List<Ejercicio> listaEjercicios= new ArrayList<Ejercicio>();
			listaEjerciciosRutina= new ArrayList<Ejercicio>();
			
			int indiceEjer = 0;
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
				double punt = resultados.getDouble("Puntuacion"); 
				int numUsos =resultados.getInt("NumUsos");
				String imagen = resultados.getString("ImagenURL");
				
				double preferencia = resultadoSimilitud.get(indiceEjer); //Tendran mas preferencia los ejercicios con mas similitud al perfil del usuario
				/*if(numUsos == 0) {
					preferencia = preferencia * 2; //Para dar prioridad a los ejercicios nuevos que no han aparecido
				}*/
				Ejercicio ejer = new Ejercicio(id, preferencia, nombre, descripcion, edadMin, edadMax, duracion, numJugMin, numJugMax,
					 punt, numUsos, objetivo, tipo, imagen);
				listaEjercicios.add(ejer); //Se va creando la lista con todos los ejercicios de la base y sus preferencias
				indiceEjer++;
			}	
			
			Collections.sort(listaEjercicios, new Comparator<Ejercicio>() {
		        @Override
		        public int compare(Ejercicio ejer1, Ejercicio ejer2) {

		        	return Double.compare(ejer1.getPreferencia(), ejer2.getPreferencia());
		        }
		    });
			for (int i = 0; i < listaEjercicios.size(); i++) {
			      System.out.println(listaEjercicios.get(i).getId() + ", " + listaEjercicios.get(i).getPreferencia());
			}
			
			int duracionRestanteRutina = rut.getDuracion();
			int duracionRestanteFisico = duracionRestanteRutina/2;
			
			//Se rellena la tabla con los datos por preferencia
			int i = 1;
			while(duracionRestanteRutina > 0) {
				System.out.println(duracionRestanteRutina);
				Ejercicio ejerObtenido = listaEjercicios.get(listaEjercicios.size()-i); //Obtienes el ejercicio con mayor preferencia
				i++;
				
				if(!(ejerObtenido.getTipo().equals("Físico") && (duracionRestanteFisico - ejerObtenido.getDuracion())< 0)){ //La cantidad de ejercicios fisicos no ha superado el limite y el ejercicio es fisico
										
					listaEjerciciosRutina.add(ejerObtenido); //Se anyade ejercicio elegido a la lista definitiva
					
					duracionRestanteRutina = duracionRestanteRutina - ejerObtenido.getDuracion();
					if(ejerObtenido.getTipo().equals("Físico")) {
						duracionRestanteFisico = duracionRestanteFisico - ejerObtenido.getDuracion();
					}
				}			
			}
			
			//Elimina el ultimo ejercicio por haberse pasado del limite de duracion
			listaEjerciciosRutina.remove(listaEjerciciosRutina.size() - 1);
			
			
			//Añades una nueva rutina a la base y obtienes su id
			String sentenciaAnyadirRutina = "INSERT INTO \"Rutina\" (\"Nombre\") VALUES ('Nombre Rutina')";
			sentencia.executeUpdate(sentenciaAnyadirRutina);
			String sentenciaObtenerIdRutina = "SELECT MAX(\"Id\") FROM public.\"Rutina\"";
			ResultSet resultadoIdRutina = sentencia.executeQuery(sentenciaObtenerIdRutina);
			String idRutina = "";
			while(resultadoIdRutina.next()) {
				idRutina = String.valueOf(resultadoIdRutina.getInt(1));				
			}
			
			for (int j = 0; j < listaEjerciciosRutina.size(); j++) {
				Ejercicio ejerObtenidoRutina = listaEjerciciosRutina.get(j);
				//Insertas la rutina con los ejercicios relacionados
				String sentenciaAnyadirRutinaEjercicio = "INSERT INTO \"RutinaEjercicio\" (\"IdEjercicio\", \"IdRutina\") VALUES ('" +
				ejerObtenidoRutina.getId() + "', '" + idRutina + "')";
				sentencia.executeUpdate(sentenciaAnyadirRutinaEjercicio);
				
				//Muestras los datos clave del ejercicio en la tabla
				String tbData[] = {ejerObtenidoRutina.getId(), ejerObtenidoRutina.getNombre(), ejerObtenidoRutina.getDescripcion()};
				DefaultTableModel tblModel = (DefaultTableModel)table.getModel();
				tblModel.addRow(tbData);
			}

			table.addMouseListener(new java.awt.event.MouseAdapter() {
			    @Override
			    public void mouseClicked(java.awt.event.MouseEvent evt) {
			        int row = table.rowAtPoint(evt.getPoint());
			        Ejercicio ejerElegido = listaEjerciciosRutina.get(row);

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
	
	private List<Double> obtenerSimilitudCosenosTfIdfAdvance() {
		TFIDFCalculator calculator = new TFIDFCalculator();
		calculator.vectorizarEjercicios();
    	calculator.calcularTfidfAdvance();
		
		Connection conexion = null;
		BasicConfigurator.configure();
		List<Double> resultadoSimilitud = new ArrayList<Double>();
		try {
			
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");			
			//String sentenciaConsultarEntrenador = "SELECT * FROM public.\"UsuarioEjercicio\" WHERE public.\"UsuarioEjercicio\".\"IdUsuario\" = " + infoUsuario.getIdUsuario();
			String sentenciaConsultarEntrenador =
					"SELECT public.\"Ejercicio\".\"Id\", public.\"Ejercicio\".\"Descripcion\", public.\"UsuarioEjercicio\".\"Puntuacion\" "
					+ "FROM public.\"UsuarioEjercicio\" INNER JOIN public.\"Ejercicio\" ON public.\"UsuarioEjercicio\".\"IdEjercicio\"=public.\"Ejercicio\".\"Id\""
					+ "WHERE public.\"UsuarioEjercicio\".\"IdUsuario\" = " + infoUsuario.getIdUsuario();
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultarEntrenador);
			
			List<String> stopWords = calculator.crearListaStopWords("src/main/java/util/stop-words.txt");
			List<Double> acumuladorSimilitud = new ArrayList<Double>(); //Guarda el calculo de similitud con cosineSimilarity del total de los ejercicios puntuados
			String descripcionEjercicio;
			double puntuacionUsuario;
			double puntuacionUsuarioMax = 1;
			while(resultados.next()) { // Cada resultado es un Ejercicio del Usuario activo, con su descripcion y la puntuacion dada por el usuario
				puntuacionUsuario = resultados.getDouble("Puntuacion");
				if(puntuacionUsuario > puntuacionUsuarioMax) { //Para normalizar el vector de TfIdf mas adelante
					puntuacionUsuarioMax = puntuacionUsuario;
				}
				descripcionEjercicio = resultados.getString("Descripcion");
				descripcionEjercicio = descripcionEjercicio.replaceAll(",", "");
				descripcionEjercicio = descripcionEjercicio.replaceAll("\\.", "");
				descripcionEjercicio = descripcionEjercicio.replaceAll("\"", "");
				descripcionEjercicio = descripcionEjercicio.replaceAll(":", "");
				//Se calcula el vector con las puntuaciones tfIdf teniendo en cuenta la puntuacion del ejercicio
				
				List<String> listaPalabrasDescripcion = new ArrayList<String>();
				
				for(String s:descripcionEjercicio.split(" ")) { //Anyades cada palabra de una descripcion a tu lista
					listaPalabrasDescripcion.add(s.toLowerCase());					
				}
				//Resta las stopwords y obtienes la lista de las palabras de la descripcion
				listaPalabrasDescripcion.removeAll(stopWords);
				
				List<Double> listaTfIdf = calculator.calcularTfidfConcretoAdvance(listaPalabrasDescripcion, puntuacionUsuario);
				
				int i = 0;
				//Vas añadiendo todas las listas de puntuacionTfIdf para obtener la lista perfil de usuario
				if(acumuladorSimilitud.size() == 0) {
					acumuladorSimilitud.addAll(listaTfIdf);
				}else {
					for(double puntTfIdf:listaTfIdf) {
						
						acumuladorSimilitud.set(i, acumuladorSimilitud.get(i) + puntTfIdf);
						i++;
					}
				}
				
			}
			
			int i = 0;
			//Normalizacion de la lista
			for(double puntTfIdf:acumuladorSimilitud) {
				
				acumuladorSimilitud.set(i, puntTfIdf / puntuacionUsuarioMax);
				i++;
			}
			
			resultadoSimilitud = calculator.calcularCosineSimilarityConcreto(acumuladorSimilitud);

			sentencia.close();
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultadoSimilitud;
		
	}
	
	
	private void generarRecomendacionTFIDFAdvance()  throws InterruptedException {
		
		List<Double> resultadoSimilitud = obtenerSimilitudCosenosTfIdfAdvance();
		
		Connection conexion = null;

		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			String sentenciaConsultar = "SELECT * FROM public.\"Ejercicio\"";
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);
			
			List<Ejercicio> listaEjercicios= new ArrayList<Ejercicio>();
			listaEjerciciosRutina= new ArrayList<Ejercicio>();
			
			int indiceEjer = 0;
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
				double punt = resultados.getDouble("Puntuacion"); 
				int numUsos =resultados.getInt("NumUsos");
				String imagen = resultados.getString("ImagenURL");
				
				double preferencia = resultadoSimilitud.get(indiceEjer); //Tendran mas preferencia los ejercicios con mas similitud al perfil del usuario
				/*if(numUsos == 0) {
					preferencia = preferencia * 2; //Para dar prioridad a los ejercicios nuevos que no han aparecido
				}*/
				Ejercicio ejer = new Ejercicio(id, preferencia, nombre, descripcion, edadMin, edadMax, duracion, numJugMin, numJugMax,
					 punt, numUsos, objetivo, tipo, imagen);
				listaEjercicios.add(ejer); //Se va creando la lista con todos los ejercicios de la base y sus preferencias
				indiceEjer++;
			}	
			
			Collections.sort(listaEjercicios, new Comparator<Ejercicio>() {
		        @Override
		        public int compare(Ejercicio ejer1, Ejercicio ejer2) {

		        	return Double.compare(ejer1.getPreferencia(), ejer2.getPreferencia());
		        }
		    });
			for (int i = 0; i < listaEjercicios.size(); i++) {
			      System.out.println(listaEjercicios.get(i).getId() + ", " + listaEjercicios.get(i).getPreferencia());
			}
			
			int duracionRestanteRutina = rut.getDuracion();
			int duracionRestanteFisico = duracionRestanteRutina/2;
			
			//Se rellena la tabla con los datos por preferencia
			int i = 1;
			while(duracionRestanteRutina > 0) {
				System.out.println(duracionRestanteRutina);
				Ejercicio ejerObtenido = listaEjercicios.get(listaEjercicios.size()-i); //Obtienes el ejercicio con mayor preferencia
				i++;
				
				if(!(ejerObtenido.getTipo().equals("Físico") && (duracionRestanteFisico - ejerObtenido.getDuracion())< 0)){ //La cantidad de ejercicios fisicos no ha superado el limite y el ejercicio es fisico
										
					listaEjerciciosRutina.add(ejerObtenido); //Se anyade ejercicio elegido a la lista definitiva
					
					duracionRestanteRutina = duracionRestanteRutina - ejerObtenido.getDuracion();
					if(ejerObtenido.getTipo().equals("Físico")) {
						duracionRestanteFisico = duracionRestanteFisico - ejerObtenido.getDuracion();
					}
				}			
			}
			
			//Elimina el ultimo ejercicio por haberse pasado del limite de duracion
			listaEjerciciosRutina.remove(listaEjerciciosRutina.size() - 1);
			
			
			//Añades una nueva rutina a la base y obtienes su id
			String sentenciaAnyadirRutina = "INSERT INTO \"Rutina\" (\"Nombre\") VALUES ('Nombre Rutina')";
			sentencia.executeUpdate(sentenciaAnyadirRutina);
			String sentenciaObtenerIdRutina = "SELECT MAX(\"Id\") FROM public.\"Rutina\"";
			ResultSet resultadoIdRutina = sentencia.executeQuery(sentenciaObtenerIdRutina);
			String idRutina = "";
			while(resultadoIdRutina.next()) {
				idRutina = String.valueOf(resultadoIdRutina.getInt(1));				
			}
			
			for (int j = 0; j < listaEjerciciosRutina.size(); j++) {
				Ejercicio ejerObtenidoRutina = listaEjerciciosRutina.get(j);
				//Insertas la rutina con los ejercicios relacionados
				String sentenciaAnyadirRutinaEjercicio = "INSERT INTO \"RutinaEjercicio\" (\"IdEjercicio\", \"IdRutina\") VALUES ('" +
				ejerObtenidoRutina.getId() + "', '" + idRutina + "')";
				sentencia.executeUpdate(sentenciaAnyadirRutinaEjercicio);
				
				//Muestras los datos clave del ejercicio en la tabla
				String tbData[] = {ejerObtenidoRutina.getId(), ejerObtenidoRutina.getNombre(), ejerObtenidoRutina.getDescripcion()};
				DefaultTableModel tblModel = (DefaultTableModel)table.getModel();
				tblModel.addRow(tbData);
			}

			table.addMouseListener(new java.awt.event.MouseAdapter() {
			    @Override
			    public void mouseClicked(java.awt.event.MouseEvent evt) {
			        int row = table.rowAtPoint(evt.getPoint());
			        Ejercicio ejerElegido = listaEjerciciosRutina.get(row);

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
	
	private List<Double> obtenerSimilitudCosenosLDA() {
		GibbsSamplingLDA lda = new GibbsSamplingLDA("data/prueba_desc.txt", "UTF-8", 5, 0.1,
				0.01, 5000, 50, "data/salida.txt");
		lda.MCMCSampling();
		
		Connection conexion = null;
		BasicConfigurator.configure();
		List<Double> listaSimilitud = new ArrayList<Double>(); //Guarda el calculo de similitud con cosineSimilarity del total de los ejercicios puntuados
		try {
			
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");			
			String sentenciaConsultarEntrenador =
					"SELECT public.\"Ejercicio\".\"Id\",  public.\"UsuarioEjercicio\".\"Puntuacion\" "
					+ "FROM public.\"UsuarioEjercicio\" INNER JOIN public.\"Ejercicio\" ON public.\"UsuarioEjercicio\".\"IdEjercicio\"=public.\"Ejercicio\".\"Id\""
					+ "WHERE public.\"UsuarioEjercicio\".\"IdUsuario\" = " + infoUsuario.getIdUsuario();
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultarEntrenador);
						
			String descripcionEjercicio;
			int idEjercicio = 0;
			double puntuacionUsuario = 0.0;
			//Te quedas con el ejercicio con mayor puntuacion, el cual compararas con el resto para obtener otros similares
			while(resultados.next()) { // Cada resultado es un Ejercicio del Usuario activo, con la puntuacion dada por el usuario				
				if(puntuacionUsuario < resultados.getDouble("Puntuacion")) {
					idEjercicio = resultados.getInt("Id");
					puntuacionUsuario = resultados.getDouble("Puntuacion");
				}				
			}
			String sentenciaConsultar = "SELECT * FROM public.\"Ejercicio\"";
			//sentencia = conexion.createStatement();
			resultados = sentencia.executeQuery(sentenciaConsultar);
			int filaEjercicio = 0;
			//Buscas el indice del ejercicio con mayor puntuacion, para buscarlo en la lista de puntuaciones LDA
			while(resultados.next()) {
				if(resultados.getInt("Id") == idEjercicio) {
					filaEjercicio = resultados.getRow();
				}
			}
			System.out.println(idEjercicio + "  " + filaEjercicio);
			
			//Ahora comparas con el resto de ejercicios para obtener la CosineSimilarity
			listaSimilitud = lda.calcularCosineSimilarityFila(filaEjercicio);
			System.out.println(listaSimilitud);

			sentencia.close();
						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listaSimilitud;
		
	}
	
private void generarRecomendacionLDA()  throws InterruptedException {
		
		List<Double> resultadoSimilitud = obtenerSimilitudCosenosLDA();
		
		Connection conexion = null;

		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			String sentenciaConsultar = "SELECT * FROM public.\"Ejercicio\"";
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);
			
			List<Ejercicio> listaEjercicios= new ArrayList<Ejercicio>();
			listaEjerciciosRutina= new ArrayList<Ejercicio>();
			
			int indiceEjer = 0;
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
				double punt = resultados.getDouble("Puntuacion"); 
				int numUsos =resultados.getInt("NumUsos");
				String imagen = resultados.getString("ImagenURL");
				
				double preferencia = resultadoSimilitud.get(indiceEjer); //Tendran mas preferencia los ejercicios con mas similitud al perfil del usuario
				/*if(numUsos == 0) {
					preferencia = preferencia * 2; //Para dar prioridad a los ejercicios nuevos que no han aparecido
				}*/
				Ejercicio ejer = new Ejercicio(id, preferencia, nombre, descripcion, edadMin, edadMax, duracion, numJugMin, numJugMax,
					 punt, numUsos, objetivo, tipo, imagen);
				listaEjercicios.add(ejer); //Se va creando la lista con todos los ejercicios de la base y sus preferencias
				indiceEjer++;
			}	
			
			Collections.sort(listaEjercicios, new Comparator<Ejercicio>() {
		        @Override
		        public int compare(Ejercicio ejer1, Ejercicio ejer2) {

		        	return Double.compare(ejer1.getPreferencia(), ejer2.getPreferencia());
		        }
		    });
			for (int i = 0; i < listaEjercicios.size(); i++) {
			      System.out.println(listaEjercicios.get(i).getId() + ", " + listaEjercicios.get(i).getPreferencia());
			}
			
			int duracionRestanteRutina = rut.getDuracion();
			int duracionRestanteFisico = duracionRestanteRutina/2;
			
			//Se rellena la tabla con los datos por preferencia
			int i = 2; //No se recomienda el ejercicio con mayor preferencia, ya que es el mismo ya puntuado
			while(duracionRestanteRutina > 0) {
				System.out.println(duracionRestanteRutina);
				Ejercicio ejerObtenido = listaEjercicios.get(listaEjercicios.size()-i); //Obtienes el ejercicio con mayor preferencia
				i++;
				
				if(!(ejerObtenido.getTipo().equals("Físico") && (duracionRestanteFisico - ejerObtenido.getDuracion())< 0)){ //La cantidad de ejercicios fisicos no ha superado el limite y el ejercicio es fisico
										
					listaEjerciciosRutina.add(ejerObtenido); //Se anyade ejercicio elegido a la lista definitiva
					
					duracionRestanteRutina = duracionRestanteRutina - ejerObtenido.getDuracion();
					if(ejerObtenido.getTipo().equals("Físico")) {
						duracionRestanteFisico = duracionRestanteFisico - ejerObtenido.getDuracion();
					}
				}			
			}
			
			//Elimina el ultimo ejercicio por haberse pasado del limite de duracion
			listaEjerciciosRutina.remove(listaEjerciciosRutina.size() - 1);
			
			
			//Añades una nueva rutina a la base y obtienes su id
			String sentenciaAnyadirRutina = "INSERT INTO \"Rutina\" (\"Nombre\") VALUES ('Nombre Rutina')";
			sentencia.executeUpdate(sentenciaAnyadirRutina);
			String sentenciaObtenerIdRutina = "SELECT MAX(\"Id\") FROM public.\"Rutina\"";
			ResultSet resultadoIdRutina = sentencia.executeQuery(sentenciaObtenerIdRutina);
			String idRutina = "";
			while(resultadoIdRutina.next()) {
				idRutina = String.valueOf(resultadoIdRutina.getInt(1));				
			}
			
			for (int j = 0; j < listaEjerciciosRutina.size(); j++) {
				Ejercicio ejerObtenidoRutina = listaEjerciciosRutina.get(j);
				//Insertas la rutina con los ejercicios relacionados
				String sentenciaAnyadirRutinaEjercicio = "INSERT INTO \"RutinaEjercicio\" (\"IdEjercicio\", \"IdRutina\") VALUES ('" +
				ejerObtenidoRutina.getId() + "', '" + idRutina + "')";
				sentencia.executeUpdate(sentenciaAnyadirRutinaEjercicio);
				
				//Muestras los datos clave del ejercicio en la tabla
				String tbData[] = {ejerObtenidoRutina.getId(), ejerObtenidoRutina.getNombre(), ejerObtenidoRutina.getDescripcion()};
				DefaultTableModel tblModel = (DefaultTableModel)table.getModel();
				tblModel.addRow(tbData);
			}

			table.addMouseListener(new java.awt.event.MouseAdapter() {
			    @Override
			    public void mouseClicked(java.awt.event.MouseEvent evt) {
			        int row = table.rowAtPoint(evt.getPoint());
			        Ejercicio ejerElegido = listaEjerciciosRutina.get(row);

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
