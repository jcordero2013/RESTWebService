package com.rest.full;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.rest.utils.Age;


@Path("/jsonbased")
public class JSONBasedPersona {

	@Path("{input}")
	@GET
	@Produces("application/json")
	public Response reverser(@PathParam("input") String input) throws JSONException {

		// Declaracion de varialbes
		JSONObject jsonObject = new JSONObject();
		String result = "";

		// Se obtienen todos los parametros desde input
		String[] parametros = input.split("&");

		// Se valida que el largo de parametro corresponde a los esperados
		if (parametros.length == 4) {
			// Se obtienen los parametros en el orden esperado
			String run = parametros[0];
			String nombre_completo = parametros[1];
			String fecha_nacimento = parametros[2];
			String sexo = parametros[3];
			
			//Se normaliza el caracter de espacio
			nombre_completo = nombre_completo.replaceAll("%20", " ");
			
			// Se validan los parametros de entrada
			if(	validarRut(run) 
				&& 	validaNombre(nombre_completo) 
				&& 	validarFechaNacimiento(fecha_nacimento)
				&&	validarSexo(sexo)){
				
				//Se calcula la edad de la persona
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			    Date birthDate = new Date();
				try {
					birthDate = sdf.parse(fecha_nacimento.replace("-", "/"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Age age = calcularEdadPersona(birthDate);
				String edad = age.toString();
				
				//Se genera JSON del response
				jsonObject.put("codigo", "0000");
				jsonObject.put("descipcion", "Correcto request and response.");
				jsonObject.put("run", normalizarRut(run));
				jsonObject.put("nombreCompleto", normalizarnombreCompleto(nombre_completo));
				jsonObject.put("fechaNacimento", fecha_nacimento);
				jsonObject.put("sexo", normalizarSexo(sexo));
				jsonObject.put("edad", edad);
				
				
			}else{
				jsonObject.put("codigo", "0002");
				jsonObject.put("descipcion", "Se esperan parametros de entrada con formatos especificos.");
			}
		} else {
			jsonObject.put("codigo", "0001");
			jsonObject.put("descipcion", "No se recibio la cantidad de parametros necesarios, se espera 4 parametros.");
		}
		result = "" + jsonObject;
		return Response.status(200).entity(result).build();
	}
	
	/**
	 * Meotodo para calcular la edad de la persona
	 * con formato Años-Meses-dias
	 * @param fecha_nacimento
	 * @return
	 */
	private static Age calcularEdadPersona(Date birthDate)
	   {
	      int years = 0;
	      int months = 0;
	      int days = 0;
	      //create calendar object for birth day
	      Calendar birthDay = Calendar.getInstance();
	      birthDay.setTimeInMillis(birthDate.getTime());
	      //create calendar object for current day
	      long currentTime = System.currentTimeMillis();
	      Calendar now = Calendar.getInstance();
	      now.setTimeInMillis(currentTime);
	      //Get difference between years
	      years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
	      int currMonth = now.get(Calendar.MONTH) + 1;
	      int birthMonth = birthDay.get(Calendar.MONTH) + 1;
	      //Get difference between months
	      months = currMonth - birthMonth;
	      //if month difference is in negative then reduce years by one and calculate the number of months.
	      if (months < 0)
	      {
	         years--;
	         months = 12 - birthMonth + currMonth;
	         if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
	            months--;
	      } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
	      {
	         years--;
	         months = 11;
	      }
	      //Calculate the days
	      if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
	         days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
	      else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
	      {
	         int today = now.get(Calendar.DAY_OF_MONTH);
	         now.add(Calendar.MONTH, -1);
	         days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
	      } else
	      {
	         days = 0;
	         if (months == 12)
	         {
	            years++;
	            months = 0;
	         }
	      }
	      //Create new Age object 
	      return new Age(days, months, years);
	   }

	/**
	 * Metodo para normalizar el sexo de la Persona
	 * Dejando el parametro siempre en mayuscula
	 * @param sexo
	 * @return String
	 */
	private String normalizarSexo(String sexo) {
		return sexo.toUpperCase();
	}

	/**
	 * Metodo para capitalizar el nombre completo
	 * Ejemplo:
	 *     Input -> juan pablo 
	 *     Output -> Juan Pablo
	 * @param nombre_completo
	 * @return String
	 */
	private String normalizarnombreCompleto(String nombre_completo) {
		return WordUtils.capitalize(nombre_completo);
	}


	/**
	 * Metodo para validar el Sexo de una persona
	 * los paramtros aceptados son M (Masculino) y F (Femenino)
	 * @param sexo
	 * @return
	 */
	private boolean validarSexo(String sexo) {
		boolean validacion = false;
		if( "M".equalsIgnoreCase(sexo) || "F".equalsIgnoreCase(sexo) ){
			validacion = true;
		}
		return validacion;
	}

	/**
	 * Metodo para validar que fecha de nacimiento tenga el formato dd/MM/yyyy
	 * @param fecha_nacimento
	 * @return
	 */
	private boolean validarFechaNacimiento(String fecha_nacimento) {
		boolean validacion = false;
		try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            formatoFecha.setLenient(false);
            formatoFecha.parse(fecha_nacimento);
            validacion = true;
        } catch (ParseException e) {
        }
		return validacion;
	}

	/**
	 * Meotodo para validar un nombre completo de Persona
	 * Esta validacion permite Letras y Numeros
	 * Ejemplo:
	 * 			Juan Pablo 2
	 * @param nombre_completo
	 * @return boolean
	 */
	private boolean validaNombre(String nombre_completo) {
		boolean validacion = true;
		//Se eliminan los espacios para validar que sean solo letras el nombre
		nombre_completo = nombre_completo.replace(" ", "");
		
		for(int i = 0; i < nombre_completo.length(); ++i) {
	        char caracter = nombre_completo.charAt(i);
	 
	        if(!Character.isLetterOrDigit(caracter)) {
	        	validacion = false;
	        }
	    }
		
		return validacion;
	}
	
	/**
	 * Metodo para validar RUT Chileno, 
	 * valida sin importar formato
	 * por lo que puede validar 11.111.111-1 o 111111111 obteniendo el mismo resultado
	 * @param rut
	 * @return boolean
	 */
	public static boolean validarRut(String rut) {
		boolean validacion = false;
		try {
			rut = rut.toUpperCase();
			rut = rut.replace(".", "");
			rut = rut.replace("-", "");
			int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

			char dv = rut.charAt(rut.length() - 1);

			int m = 0, s = 1;
			for (; rutAux != 0; rutAux /= 10) {
				s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
			}
			if (dv == (char) (s != 0 ? s + 47 : 75)) {
				validacion = true;
			}

		} catch (java.lang.NumberFormatException e) {
		} catch (Exception e) {
		}
		return validacion;
	}
	
	/**
	 * Metodo para normalizar los rut 
	 * quitando los punto, guiones 
	 * y dejando cualquier letra como la K siempre como mayuscula
	 * @param rut
	 * @return boolean
	 */
	public static String normalizarRut(String rut){
		rut = rut.toUpperCase();
		rut = rut.replace(".", "");
		rut = rut.replace("-", "");
		return rut;
	}

}