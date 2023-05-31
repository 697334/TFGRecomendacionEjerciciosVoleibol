package base;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class AnyadirEjercicios {
	
	// A�ade un ejercicio
	public static void main(String[] args) {
		Connection conexion = null;
		try {
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			/*conexion = DriverManager.getConnection("jdbc:postgresql://b8zsnovqbew0yjsumrjb-postgresql.services.clever-cloud.com:5432/b8zsnovqbew0yjsumrjb",
					"u0ifv5oeqdkj9sah4qsj","5fUtb5CnkEygwvqyOuPfECQYjkG1Ob");*/
			//postgresql://u0ifv5oeqdkj9sah4qsj:5fUtb5CnkEygwvqyOuPfECQYjkG1Ob@b8zsnovqbew0yjsumrjb-postgresql.services.clever-cloud.com:5432/b8zsnovqbew0yjsumrjb
			System.out.println("Conectado");
			String sentenciaDelete = "DELETE FROM public.\"Ejercicio\"";
			Statement sentencia = conexion.createStatement();
			sentencia.executeUpdate("DELETE FROM public.\"UsuarioEjercicio\"");
			sentencia.executeUpdate("DELETE FROM public.\"RutinaEjercicio\"");
			sentencia.executeUpdate(sentenciaDelete);
			
			File csvFile = new File("db\\ejerciciosCSV.csv");
			BufferedReader br = new BufferedReader(new FileReader(csvFile, StandardCharsets.ISO_8859_1));
			
			String line = "";
			br.readLine();
			
			while((line = br.readLine()) != null) {
				line = line.replaceAll("'", " prima");
				String[] count = line.split(";");
				
				String sentenciaAnyadir = "INSERT INTO \"Ejercicio\" (\"Nombre\", \"Descripcion\", \"EdadMinima\", \"EdadMaxima\", \"Duracion\", \"NumeroJugadoresMinimo\", \"NumeroJugadoresMaximo\", \"Puntuacion\", \"NumUsos\", \"Objetivo\", \"Tipo\", \"ImagenURL\")"
						+ "VALUES ('" + count[1] + "', '" + count[2] + "', '" + count[3] + "', '" + count[4]+ "', '" + count[5] + "', '" + 
						count[6] + "', '" + count[7] + "', '"+ count[8] + "', '" + count[9] + "', '" + count[10] + "', '"
						+ count[11] + "', '" + count[12] + "')";
				
				System.out.println(sentenciaAnyadir);
				sentencia.executeUpdate(sentenciaAnyadir);
				
			}
			
			br.close();
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
		catch (FileNotFoundException e) {
			System.out.println("Error de SQL: " + e.getMessage());
			e.printStackTrace();
		}
		catch (IOException ei) {
			System.out.println("Error de SQL: " + ei.getMessage());
			ei.printStackTrace();
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
