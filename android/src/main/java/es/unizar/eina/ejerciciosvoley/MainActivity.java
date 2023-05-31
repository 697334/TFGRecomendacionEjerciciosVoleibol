package es.unizar.eina.ejerciciosvoley;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MainActivity extends AppCompatActivity {

    String url = "jdbc:postgresql://%s:%d/%s";

    EditText txtUsuario;
    EditText txtPass;
    Button btnEntrar;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
       StrictMode.setThreadPolicy(gfgPolicy);

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtPass = (EditText) findViewById(R.id.txtPass);
        btnEntrar = (Button)findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v)
           {
               String usuario = txtUsuario.getText().toString();
               String contrasenya = txtPass.getText().toString();
               if(validar(usuario, contrasenya)){
                   // Abre una nueva Activity:
                   Intent myIntent = new Intent(v.getContext(), ListaEjerciciosActivity.class);
                   myIntent.putExtra("idUsuario", usuario);
                   startActivity(myIntent);
               }
               else if(existeUsuario(usuario)){
                   Toast.makeText(getApplicationContext(),"Contraseña incorrecta",Toast.LENGTH_SHORT).show();
               }
               else{
                   //Añadir usuario y contraseña
                   if(crearUsuario(usuario, contrasenya)){
                       Toast.makeText(getApplicationContext(),"Se ha creado nuevo usuario",Toast.LENGTH_SHORT).show();
                       // Abre una nueva Activity:
                       Intent myIntent = new Intent(v.getContext(), ListaEjerciciosActivity.class);
                       myIntent.putExtra("idUsuario", usuario);
                       startActivity(myIntent);
                   }
                   else{
                       Toast.makeText(getApplicationContext(),"Error al crear nuevo usuario",Toast.LENGTH_SHORT).show();
                   }
               }
           }
        });
    }

    private boolean validar(String usuario, String contrasenya){
        boolean correcto = false;
        Connection connection = null;

        try
        {
            Class.forName("org.postgresql.Driver");
            url = String.format(url, ConexionBD.host, ConexionBD.port, ConexionBD.database);

            connection = DriverManager.getConnection(url, ConexionBD.user, ConexionBD.pass);

            String sentenciaConsultar = "select * from public.\"Usuario\"  WHERE public.\"Usuario\".\"Nombre\" = '"
                    + usuario + "' AND public.\"Usuario\".\"Contrasenya\" = '" + contrasenya + "'";

            Statement sentencia = connection.createStatement();
            ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);

            if(resultados.next()) {
               correcto = true;
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
            return correcto;
        }
    }

    private boolean existeUsuario(String usuario){
        boolean existe = false;
        Connection connection = null;

        try
        {
            Class.forName("org.postgresql.Driver");
            url = String.format(url, ConexionBD.host, ConexionBD.port, ConexionBD.database);

            connection = DriverManager.getConnection(url, ConexionBD.user, ConexionBD.pass);

            String sentenciaConsultar = "select * from public.\"Usuario\"  WHERE public.\"Usuario\".\"Nombre\" = '"
                    + usuario + "'";

            Statement sentencia = connection.createStatement();
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
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return existe;
        }
    }

    private boolean crearUsuario(String usuario, String contrasenya){
        boolean insertado = false;
        Connection connection = null;

        try
        {
            Class.forName("org.postgresql.Driver");
            url = String.format(url, ConexionBD.host, ConexionBD.port, ConexionBD.database);

            connection = DriverManager.getConnection(url, ConexionBD.user, ConexionBD.pass);

            String sentenciaConsultar = "insert into \"Usuario\" (\"Nombre\", \"Contrasenya\", \"Tipo\") "
                    + " VALUES ('" + usuario + "' , '" + contrasenya + "' , 'jugador')";

            Statement sentencia = connection.createStatement();

            if(sentencia.executeUpdate(sentenciaConsultar) == 1 ) {
                insertado = true;
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
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return insertado;
        }
    }
}