package es.unizar.eina.ejerciciosvoley;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EjercicioActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtNombre;
    TextView txtDescripcion;
    TextView txtDuracion;

    EditText txtValoracion;
    Button btnValorar;


    String url = "jdbc:postgresql://%s:%d/%s";

    int idEjercicio;
    String idUsuario;
    double puntuacion;
    int numUsos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ejercicio);
        idEjercicio = getIntent().getIntExtra("idEjercicio", 0);
        idUsuario = getIntent().getStringExtra("idUsuario");
        txtNombre = (TextView)findViewById(R.id.txtNombre);
        txtDescripcion = (TextView)findViewById(R.id.txtDescripcion);
        txtDuracion = (TextView)findViewById(R.id.txtDuracion);
        txtValoracion = (EditText) findViewById(R.id.txtValoracion);
        btnValorar = (Button)findViewById(R.id.btnValorar);
        btnValorar.setOnClickListener(this);
        rellenarDatos(idEjercicio);
    }

    @Override
    public void onClick(View v) {
        if(v instanceof Button){
            String texto = txtValoracion.getText().toString();
            if(texto.length() != 0){
                int puntos = Integer.parseInt(texto);
                if(puntos<0 || puntos >10){
                    Toast.makeText(getApplicationContext(),"Puntuación no válida",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(comprobarEjercicioUsuario(idUsuario, idEjercicio)){ //el usuario ya habia puntuado ese ejercicio
                        rellenarValoracion(idEjercicio, idUsuario,puntos);
                    }
                    else{ //es la primera puntuacion del usuario de ese ejercicio
                        rellenarValoracionNueva(idEjercicio, idUsuario,puntos);
                    }
                    finish();
                }

            }
        }
    }

    private void rellenarValoracion(int idEjercicio,String idUsuario, int puntos){
        Connection connection = null;
        int idComprobar = 0;
        try
        {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/EntrenamientoVoleibol","postgres","root");

            String sentenciaConsultarId = "SELECT * from public.\"Usuario\"" +
                    "WHERE public.\"Usuario\".\"Nombre\" = '" + idUsuario + "'";

            Statement sentencia = connection.createStatement();
            ResultSet resultados = sentencia.executeQuery(sentenciaConsultarId);

            if(resultados.next()) {
                idComprobar = resultados.getInt("Id");
            }

            String sentenciaModificar = "UPDATE public.\"UsuarioEjercicio\" " +
                    " SET \"Puntuacion\" = " + puntos +
                    " WHERE public.\"UsuarioEjercicio\".\"IdEjercicio\" = '" + idEjercicio +
                    "' AND public.\"UsuarioEjercicio\".\"IdUsuario\" = '" + idComprobar + "'";

            String sentenciaUpdate = "UPDATE public.\"Ejercicio\" SET \"NumUsos\" = '"+ (numUsos + 1) +"', \"Puntuacion\" = '" +
                    (puntuacion * numUsos + puntos)/(numUsos + 1) + "' WHERE public.\"Ejercicio\".\"Id\" = " + idEjercicio;

            System.out.println(sentenciaUpdate);
            System.out.println(sentenciaModificar);

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

    private void rellenarValoracionNueva(int idEjercicio,String idUsuario, int puntos){
        Connection connection = null;
        int idComprobar = 0;
        try
        {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/EntrenamientoVoleibol","postgres","root");

            String sentenciaConsultarId = "SELECT * from public.\"Usuario\"" +
                    "WHERE public.\"Usuario\".\"Nombre\" = '" + idUsuario + "'";

            Statement sentencia = connection.createStatement();
            ResultSet resultados = sentencia.executeQuery(sentenciaConsultarId);

            if(resultados.next()) {
                idComprobar = resultados.getInt("Id");
            }

            String sentenciaModificar = "INSERT INTO \"UsuarioEjercicio\" (\"IdUsuario\", \"IdEjercicio\", \"Puntuacion\")"
                    + "VALUES ('" + idComprobar + "', '" + idEjercicio + "', '" + puntos +  "')";

            String sentenciaUpdate = "UPDATE public.\"Ejercicio\" SET \"NumUsos\" = '"+ (numUsos + 1) +"', \"Puntuacion\" = '" +
                    (puntuacion * numUsos + puntos)/(numUsos + 1) + "' WHERE public.\"Ejercicio\".\"Id\" = " + idEjercicio;

            System.out.println(sentenciaModificar);
            sentencia.execute(sentenciaModificar);
            System.out.println(sentenciaUpdate);
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

    private void rellenarDatos(int idEjercicio){
        Connection connection = null;
        try
        {
            Class.forName("org.postgresql.Driver");

            url = String.format(url, ConexionBD.host, ConexionBD.port, ConexionBD.database);

            connection = DriverManager.getConnection(url, ConexionBD.user, ConexionBD.pass);

            String sentenciaConsultar = "select * from public.\"Ejercicio\" where public.\"Ejercicio\".\"Id\" = " + idEjercicio + ";";
            Statement sentencia = connection.createStatement();
            ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);



            if(resultados.next()) { // Cada resultado es un ejercicio
                String id = String.valueOf(resultados.getInt("Id"));
                String nombre = resultados.getString("Nombre");
                String descripcion = resultados.getString("descripcion");;
                int duracion = resultados.getInt("duracion");
                puntuacion = resultados.getDouble("Puntuacion");
                numUsos = resultados.getInt("NumUsos");

                Ejercicio ejercicio = new Ejercicio(id, nombre, descripcion, duracion);
                txtNombre.setText(nombre);
                txtDescripcion.setText(descripcion);
                txtDuracion.setText(duracion + " minutos");
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
        }
    }

    private boolean comprobarEjercicioUsuario(String usuario, int ejercicio){
        boolean yaExiste = false;
        Connection connection = null;
        int idComprobar = 0;

        try
        {
            Class.forName("org.postgresql.Driver");
            url = String.format(url, ConexionBD.host, ConexionBD.port, ConexionBD.database);

            connection = DriverManager.getConnection(url, ConexionBD.user, ConexionBD.pass);

            String sentenciaConsultarId = "SELECT * from public.\"Usuario\"" +
                    "WHERE public.\"Usuario\".\"Nombre\" = '" + usuario + "'";

            Statement sentencia = connection.createStatement();
            ResultSet resultadosComprobar = sentencia.executeQuery(sentenciaConsultarId);

            if(resultadosComprobar.next()) {
                idComprobar = resultadosComprobar.getInt("Id");
            }

            String sentenciaConsultar = "select * from public.\"UsuarioEjercicio\"  where public.\"UsuarioEjercicio\".\"IdUsuario\" = '"
                    + idComprobar + "' AND public.\"UsuarioEjercicio\".\"IdEjercicio\" = '" + ejercicio + "'";

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