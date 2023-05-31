package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class TFIDFCalculator {
	
	
	private List<List<String>> totalDescripciones = new ArrayList<List<String>>();
    private List<String> totalPalabras = new ArrayList<String>();
    private List<String> palabrasSeleccionadas = new ArrayList<String>();
    private List<List<Double>> vectorTfidf = new ArrayList<List<Double>>();
    
    //Devuelve la lista de listas con las puntuaciones tf-idf de cada palabra de cada ejercicio
    public List<List<Double>> getVectorTfidf() {
		return vectorTfidf;
	}

	/**
     * @param doc  list of strings
     * @param term String represents a term
     * @return term frequency of term in document
     */
    public double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public double idf(List<List<String>> docs, String term) {
        double n = 0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        if(n != 0) {
        	return Math.log(docs.size() / n);
        }else {
        	return 0;
        }
        
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);

    }

    // Guarda una lista de listas de palabras en un fichero de texto.
    public static void guardar(List<List<String>> listaLineas, String nombreFichero) 
    throws IOException {
    	BufferedWriter bw = null;
    	try {
    		bw = new BufferedWriter(new FileWriter(nombreFichero));
    		for (List<String> linea : listaLineas) {
    			for (String palabra : linea) {
    				bw.write(palabra + " ");
    			}
    			bw.newLine();
    		}
    	}
    	finally {
    		if (bw != null) {
    			bw.close();
    		}
    	}
    }
    
    public List<String> crearListaStopWords(String file) throws IOException {
    	List<String> stopWords = new ArrayList<String>();
    	FileReader fr=new FileReader(file);
        BufferedReader br= new BufferedReader(fr);
        String palabraLeida;
        while ((palabraLeida = br.readLine()) != null){ //Se construye la lista de stop-words
        	stopWords.add(palabraLeida);
        }
        fr.close();
        br.close();
        return stopWords;
    }
    
    public void crearListaPalabrasSeleccionadas(String file) throws IOException {
    	FileReader fr=new FileReader(file);
        BufferedReader br= new BufferedReader(fr);
        String palabraLeida;
        while ((palabraLeida = br.readLine()) != null){ //Se construye la lista de stop-words
        	palabrasSeleccionadas.add(palabraLeida);
        }
        fr.close();
        br.close();
    }
    
    public void vectorizarEjercicios() {
    	Connection conexion = null;
		try {
			
			Class.forName("org.postgresql.Driver");
			conexion = DriverManager.getConnection("jdbc:postgresql://localhost:5432/EntrenamientoVoleibol","postgres","root");
			System.out.println("Conectado");
			String sentenciaConsultarEntrenador = "SELECT * FROM public.\"Ejercicio\"";
			Statement sentencia = conexion.createStatement();
			ResultSet resultados = sentencia.executeQuery(sentenciaConsultarEntrenador);
			
			List<String> stopWords = crearListaStopWords("src/main/java/util/stop-words.txt");
			crearListaPalabrasSeleccionadas("src/main/java/util/important-words.txt");
			String descripcionAux;
			while(resultados.next()) { // Cada resultado es un ejercicio
				descripcionAux = resultados.getString("Descripcion");
				descripcionAux = descripcionAux.replaceAll(",", "");
				descripcionAux = descripcionAux.replaceAll("\\.", "");
				descripcionAux = descripcionAux.replaceAll("\"", "");
				descripcionAux = descripcionAux.replaceAll(":", "");
				//eliminar mas caracteres si hace falta
				
				List<String> listaAux = new ArrayList<String>();
				
				for(String s:descripcionAux.split(" ")) { //Anyades cada palabra de una descripcion a tu lista
					listaAux.add(s.toLowerCase());					
				}
				//Resta las stopwords
				listaAux.removeAll(stopWords);
				for(String palabra : listaAux) { //Crea el vector con todas las palabras sin duplicar
                	if(!totalPalabras.contains(palabra)){
                		totalPalabras.add(palabra);
                	}
                }
				totalDescripciones.add(listaAux);				
			}
			//check final
			System.out.println(totalDescripciones);
			
			// guardar a fichero txt para la comparacion con LDA
			guardar(totalDescripciones, "data/prueba_desc.txt");
			
			sentencia.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public void calcularTfidf() {
    	
        double tfidf; //term frequency inverse document frequency        
        for (List<String> descripcion : totalDescripciones) 
        {
            List<Double> tfidfvectors = new ArrayList<Double>();
            for (String palabra : totalPalabras) {
                tfidf = tfIdf(descripcion, totalDescripciones, palabra);
                tfidfvectors.add(tfidf);
            }
            vectorTfidf.add(tfidfvectors);  //storing document vectors;            
        }
    }
    
    public void calcularTfidfAdvance() {
    	
        double tfidf; //term frequency inverse document frequency        
        for (List<String> descripcion : totalDescripciones) 
        {
            List<Double> tfidfvectors = new ArrayList<Double>();
            for (String palabra : palabrasSeleccionadas) {
                tfidf = tfIdf(descripcion, totalDescripciones, palabra);
                tfidfvectors.add(tfidf);
            }
            vectorTfidf.add(tfidfvectors);  //storing document vectors;            
        }
    }
    
    public List<Double> calcularTfidfConcreto(List<String> listaPalabrasDescripcion, double puntuacion) {
    	
        double tfidf; //term frequency inverse document frequency
        List<Double> listaTfIdf = new ArrayList<Double>();
        
        for (String palabra : totalPalabras) {
            tfidf = tfIdf(listaPalabrasDescripcion, totalDescripciones, palabra) * puntuacion;
            listaTfIdf.add(tfidf);
        }           

        return listaTfIdf;
    }
    
    public List<Double> calcularTfidfConcretoAdvance(List<String> listaPalabrasDescripcion, double puntuacion) {
    	
        double tfidf; //term frequency inverse document frequency
        List<Double> listaTfIdf = new ArrayList<Double>();
        
        for (String palabra : palabrasSeleccionadas) {
            tfidf = tfIdf(listaPalabrasDescripcion, totalDescripciones, palabra) * puntuacion;
            listaTfIdf.add(tfidf);
        }           

        return listaTfIdf;
    }
    
    public double cosineSimilarity(List<Double> docVector1, List<Double> docVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;
        double cosineSimilarity = 0.0;

        for (int i = 0; i < docVector1.size(); i++) {//docVector1 and docVector2 must be of same length
        
            dotProduct += docVector1.get(i) * docVector2.get(i);  //a.b
            magnitude1 += Math.pow(docVector1.get(i), 2);  //(a^2)
            magnitude2 += Math.pow(docVector2.get(i), 2); //(b^2)
        }

        magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
        magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)

        if (magnitude1 != 0.0 | magnitude2 != 0.0) {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        } 
        else{
            return 0.0;
        }
        return cosineSimilarity;
    }
  
    
    public void calcularCosineSimilarity() {
    	double resultadoComparacion = 0.0;
        for (int i = 0; i < vectorTfidf.size(); i++) {
            for (int j = 0; j < vectorTfidf.size(); j++) {
	            if(i!=j) {
	            	resultadoComparacion = cosineSimilarity(vectorTfidf.get(i),  vectorTfidf.get(j));
	            	
	            	System.out.println("between " + i + " and " + j + "  =  "+ resultadoComparacion);
	            }	                
            }
        }
    }
    
    public List<Double> calcularCosineSimilarityConcreto(List<Double> puntuacionTfIdf) {
    	double resultadoComparacion = 0.0;
    	List<Double> listaSimilitud = new ArrayList<Double>();

        for (int i = 0; i < vectorTfidf.size(); i++) {
        	resultadoComparacion = cosineSimilarity(puntuacionTfIdf,  vectorTfidf.get(i));
        	listaSimilitud.add(resultadoComparacion);
        }
        
        return listaSimilitud;
    }
    
    /*public List<String> calcularCosineSimilarityConcreto(List<String> vectorTfIdf, List<String> acumulador) {
    	double resultadoComparacion = 0.0;
        for (int i = 0; i < vectorTfidf.size(); i++) {
            for (int j = 0; j < vectorTfidf.size(); j++) {
	            if(i!=j) {
	            	resultadoComparacion = cosineSimilarity(vectorTfidf.get(i),  vectorTfidf.get(j));
	            	
	            	System.out.println("between " + i + " and " + j + "  =  "+ resultadoComparacion);
	            }	                
            }
        }
        return acumulador;
    }*/
    
    public static void main(String[] args) {

    	TFIDFCalculator calculator = new TFIDFCalculator();
    	calculator.vectorizarEjercicios();
    	calculator.calcularTfidf();
    	calculator.calcularCosineSimilarity();

    }


}