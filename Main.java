package futbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	//Algunos datos pueden no parecer correctos; lo mas probable es que los datos esten asi en MARCA.COM y no sea un error de este software
	public static int posicion(String parEquipos,String equipos[]){
		int i = 0;
		while(!equipos[i].equals(parEquipos))i++;
		return i++;
	}
	public static void main(String[] args) {
		int jornada = 0;
		int nPartidos = 3;
		try{
			jornada = Integer.parseInt(args[0]);
		}
		catch (Exception e){
			System.out.println("ERROR:Se esperaba al menos un parámetro");
			System.out.println("Uso: ");
			System.out.println("OBLIGATORIO->\t El primer parámetro representa la jornada a consultar(debe ser un numero)");
			System.out.println("OPCIONAL->\t Mostrar los x mejores partidos(por defecto es 3)");
			System.exit(1);
		}
		if(jornada > 38 || jornada < 1){
			System.out.println("ERROR:La jornada especificada no es valida");
			System.out.println("Debe de estar entre 1 y 38");
			System.exit(1);
		}
		try{
			nPartidos = Integer.parseInt(args[1]);
			if(nPartidos < 1 || nPartidos > 10){
				System.out.println("ERROR:Numero de partidos no valido");
				System.out.println("Debe de estar entre 1 y 10");
				System.exit(1);
			}
		}
		catch (Exception e){
			
		}
	    URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    Pattern patronClasificacion = Pattern.compile("<td class=\"equipo\">.*</td>");
	    Pattern patronPartidos = Pattern.compile("<meta itemprop=\\\"name\\\" content=\\\".*\\\">");
	    Pattern patronJornadas = Pattern.compile("<h2 id=\\\"jornada[0-9][0-9]?\\\">Jornada [0-9][0-9]?</h2>");
	    Matcher validador;
	    Date fechaPartido = null;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	    SimpleDateFormat diaPartido = new SimpleDateFormat("dd");
	    SimpleDateFormat horaPartido = new SimpleDateFormat("hh:mm");
	    boolean fechaEsValida = false;
	    String equipos[] = new String[20];
	    String partidos[][] = new String[20][2];
	    String parEquipos[][] = new String[10][2];
	    final String URL_Calendario 	= "http://www.marca.com/futbol/primera/calendario.html?cid=MENUMIGA35903&s_kw=calendario";
	    final String URL_Clasificacion 	= "http://www.marca.com/futbol/primera/clasificacion.html?cid=MENUMIGA35903&s_kw=clasificacion";
	    int posiciones[] = new int[10];
	    int i = 0;
	    int j = 0;
	    int k = 0;
	    int jornadactual = 0;

	    //Descargar clasificacion en este instante
	    System.out.println("Descargando de MARCA.COM...");
	    
	    try {
	        url = new URL(URL_Clasificacion);
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is,"8859_1"));

	        while ((line = br.readLine()) != null) {
	            validador = patronClasificacion.matcher(line);
	            if(validador.matches()){
	            	equipos[i] = new String(line.replaceAll("\\*", ""));
	            	j = 0;
	            	while(equipos[i].charAt(j) != '>')j++;
	            	j++;
	            	k = j;
	            	while(equipos[i].charAt(k) != '<')k++;
	            	equipos[i] = equipos[i].substring(j, k).trim();
	            	//System.out.println(equipos[i]);
	            	i++;
	            }
	        }
	    } catch (MalformedURLException mue) {
	    	System.out.println("URL de la clasificacion mal formada");
	    } catch (IOException ioe) {
	    	System.out.println("ERROR:Exepcion de E/S al intentar procesar la clasificacion");
	    	System.out.println("Puede deberse a una mala conexion");
	    	System.exit(1);
	    }
	    
	    System.out.println("Pagina de clasificacion obtenida correctamente");
	    //Descargar partidos de todo el año
	    
	    i = 0;
	    try {
	        url = new URL(URL_Calendario);
	        is = url.openStream();
	        br = new BufferedReader(new InputStreamReader(is));

	        while ((line = br.readLine()) != null && jornadactual <= jornada) { //Puede fallar la derecha del &&
	        	validador = patronJornadas.matcher(line);
	        	if (validador.matches()){
	        		//Ver que jornada es
	        		j = 0;
	            	while(line.charAt(j) != '>')j++;
	            	j++;
	            	k = j;
	            	while(line.charAt(k) != '<')k++;
	            	jornadactual = Integer.parseInt(line.substring(j, k).replaceAll("Jornada ", ""));
	        	}
	        	if(jornadactual == jornada){
	        		//Es la jornada que necesitamos
	        		validador = patronPartidos.matcher(line);
		            if(validador.matches()){
		            	partidos[i][0] = new String(line);
		            	j = partidos[i][0].length()-1;
		            	while(partidos[i][0].charAt(j) != '"')j--;
		            	j--;
		            	k = j;
		            	while(partidos[i][0].charAt(k) != '"')k--;
		            	//El replaceAll corrige malformaciones del html
		            	partidos[i][0] = partidos[i][0].substring(k+1, j+1).replaceAll("&eacute;", "é").replaceAll("&aacute;", "á");
		            	//Coger cada par por separado
		            	j = 0;
		            	while(partidos[i][0].charAt(j) != 'v' || partidos[i][0].charAt(j+1) != 's')j++;
		            	parEquipos[i][0] = partidos[i][0].substring(0, j-1).trim();
		            	parEquipos[i][1] = partidos[i][0].substring(j+3, partidos[i][0].length()).trim();
		            	//System.out.println(parEquipos[i][0] + " contra " + parEquipos[i][1]);
		            	i++;
		            	//Coger la fecha i guardarla en partidos[i][1]
		            	line = br.readLine();
		            	j = 0;
		            	while(line.charAt(j) != '"')j++;
		            	j++;
		            	k = j;
		            	while(line.charAt(k) != '"')k++;
		            	partidos[i][1] = line.substring(j, k).replace("T", " ");
		            	//System.out.println(partidos[i][1]);
		            }
	        	}
	           
	        }
	    } catch (MalformedURLException mue) {
	    	System.out.println("URL de las jornadas mal formada");
	    } catch (IOException ioe) {
	    	System.out.println("ERROR:Exepcion de E/S al intentar procesar las jornadas");
	    	System.out.println("Puede deberse a una mala conexion");
	    	System.exit(1);
	    }
	    
	    System.out.println("Jornada "+jornada+" obtenida correctamente");
	    
	    //Guardar en posiciones la suma de las posiciones de cada equipo
	    //Las 3 menores sumas corresponden a los 3 partidos con equipos mas altos en la tabla
	    for(int m=0;m<parEquipos.length;m++){
	    	posiciones[m] = posicion(parEquipos[m][0],equipos)+posicion(parEquipos[m][1],equipos);
	    }
	    
	    if(nPartidos == 1)System.out.println("El mejor partido de esta jornada es: ");
	    else System.out.println("Los "+nPartidos+" mejores partidos de esta jornada son: ");
	    
	    //Ver si la fecha es valida
	    //System.out.println(partidos[1][1].substring(partidos[1][1].indexOf(' ')+1, partidos[1][1].length()));
    	if(partidos[1][1].substring(partidos[1][1].indexOf(' ')+1, partidos[1][1].length()).equals("00:00"))System.out.println("MARCA.COM aun no tiene la fecha de los partidos actualizada");
    	else fechaEsValida = true;

	    for(int m=0;m<nPartidos;m++){ //Los nPartidos mejores
	    	i = 999;
	    	j = 0;
	    	for(int a=0;a<posiciones.length;a++){ //Buscar el mejor
	    		if(i > posiciones[a]){
	    			i=posiciones[a];
	    			j = a;
	    		}
	    	}
	    	line = partidos[j+1][1];
	    	try {
				fechaPartido = sdf.parse(line);
			} catch (ParseException e) {
				System.out.println("ERROR:Error al procesar fecha");
				System.exit(1);
			}
	    	System.out.print("-" + parEquipos[j][0] + " vs " + parEquipos[j][1]);
	    	if(fechaEsValida)System.out.println(", el día " + diaPartido.format(fechaPartido) + " a las " + horaPartido.format(fechaPartido));
	    	else System.out.println();
	    	posiciones[j] = 999;
	    }
	    
	}
}
