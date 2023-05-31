package es.unizar.eina.ejerciciosvoley;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ListaEjerciciosActivity extends AppCompatActivity {

    String url = "jdbc:postgresql://%s:%d/%s";

    ArrayList<String> listEjercicios=new ArrayList<String>();
    ArrayAdapter<String> adapterEjercicios;

    ArrayList<String> listRutinas=new ArrayList<String>();
    ArrayAdapter<String> adapterRutinas;

    ListView lv;
    Spinner sp;

    String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ejercicios);

        url = String.format(url, ConexionBD.host, ConexionBD.port, ConexionBD.database);

        idUsuario = getIntent().getStringExtra("idUsuario");

        //ListView con scroll dentro de ScrollView
        lv = (ListView)findViewById(R.id.listaEjercicios);
        //para que se muestre el scroll:
        lv.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        //Configura Listener.
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String texto = parent.getItemAtPosition(position).toString();
                        int punto = texto.indexOf(".");
                        int idEjercicio = Integer.parseInt(texto.substring(0, punto));
                        // Abre una nueva Activity:
                        Intent myIntent = new Intent(view.getContext(), EjercicioActivity.class);
                        myIntent.putExtra("idEjercicio", idEjercicio);
                        myIntent.putExtra("idUsuario", idUsuario);
                        startActivity(myIntent);

                    }
                }
        );

        adapterEjercicios=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listEjercicios);
        lv.setAdapter(adapterEjercicios);

        mostrarEjercicios(0);

        sp = (Spinner)findViewById(R.id.spRutinas);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String texto = parent.getItemAtPosition(position).toString();
                int punto = texto.indexOf(".");
                int idRutina = 0;
                if(punto != -1){
                    idRutina = Integer.parseInt(texto.substring(0, punto));
                }
                mostrarEjercicios(idRutina);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                mostrarEjercicios(0);
            }

        });

        adapterRutinas=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listRutinas);
        sp.setAdapter(adapterRutinas);

        mostrarRutinas();
    }

    private void mostrarEjercicios(int idRutina){

        Connection connection = null;

        try
        {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(url, ConexionBD.user, ConexionBD.pass);
            // connection = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/EntrenamientoVoleibol","postgres","root");

            String sentenciaConsultar = "select * from public.\"Ejercicio\" ";
            if(idRutina !=0){
                sentenciaConsultar = sentenciaConsultar + " WHERE public.\"Ejercicio\".\"Id\" IN "
                        + " (SELECT \"IdEjercicio\" FROM public.\"RutinaEjercicio\" WHERE public.\"RutinaEjercicio\".\"IdRutina\" = " + idRutina +" )";
            }
            else{
                sentenciaConsultar = sentenciaConsultar + " ORDER BY public.\"Ejercicio\".\"Id\"";
            }
            Statement sentencia = connection.createStatement();

            ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);

            listEjercicios.clear();
            while(resultados.next()) { // Cada resultado es un ejercicio
                String id = String.valueOf(resultados.getInt("Id"));
                String nombre = resultados.getString("Nombre");
                listEjercicios.add(id + ". " + nombre);
            }

            resultados.close();
            sentencia.close();

            adapterEjercicios.notifyDataSetChanged();

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

    private void mostrarRutinas(){
        Connection connection = null;

        try
        {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(url, ConexionBD.user, ConexionBD.pass);

            String sentenciaConsultar = "select * from public.\"Rutina\" ";

            Statement sentencia = connection.createStatement();
            ResultSet resultados = sentencia.executeQuery(sentenciaConsultar);

            listRutinas.clear();
            listRutinas.add("-- Selecciona --");
            while(resultados.next()) {
                String id = String.valueOf(resultados.getInt("Id"));
                String nombre = resultados.getString("Nombre");
                listRutinas.add(id + ". Rutina " + id);
            }

            resultados.close();
            sentencia.close();

            adapterRutinas.notifyDataSetChanged();

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
}