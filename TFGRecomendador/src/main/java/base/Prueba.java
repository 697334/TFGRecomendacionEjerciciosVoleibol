package base;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Prueba {
	
	// Consulta todos los ejercicios de la base de datos baseTFG.db
	public static void main(String[] args) {
		Connection conexion = null;
		try {
			Class.forName("org.sqlite.JDBC");
			conexion = DriverManager.getConnection("jdbc:sqlite:db\\baseTFG.db");
			System.out.println("Conectado");
			int contadorEjercicios = 0;
			String sentenciaConsultar = "SELECT * FROM Ejercicio";
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);				
			while (resultados.next()) {
				System.out.println("Código = " + resultados.getInt("Id") +
				                   ", Nombre = " + resultados.getString("Nombre") +
				                   ", Descripción = " + resultados.getString("Descripcion"));
				contadorEjercicios++;
			}
			if (contadorEjercicios == 0) {
				System.out.println("No se ha encontrado ningún ejercicio en la base de datos.");
			}
			else {
				System.out.println("Se han consultado " + 
			                       contadorEjercicios + " ejercicios de la base de datos.");
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
