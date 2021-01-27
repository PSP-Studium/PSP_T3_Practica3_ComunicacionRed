package es.studium.adivina;

import java.io.DataInputStream;
import java.io.DataOutputStream; 
import java.io.IOException; 
import java.net.Socket;


/**
 * Clase servidorHilo
 * @author Alvca
 * @since 2021
 * @version 1.0
 */
public class ServidorHilo extends Thread {

	DataInputStream fentrada; 
	Socket socket; 
	boolean fin = false; 
	boolean jugar = false;
	boolean jugando = true;
	/**
	 * Constructor con par�metro
	 * @param socket Enlace del cliente con el servidor
	 */
	public ServidorHilo (Socket socket) { 
		this.socket = socket; 
		try { 
			fentrada = new DataInputStream(socket.getInputStream()); 
		}
		catch (IOException e) { 
			System.out.println("Error de E/S");
			e.printStackTrace(); 
		} 
	}  
	/**
	 * En el m�todo run() lo primero que hacemos es enviar todos los mensajes actuales al cliente que se acaba de incorporar
	 */
	public void run() throws ArrayIndexOutOfBoundsException 
	{ 
		Servidor.mensaje.setText("N�mero de conexiones actuales: " + Servidor.ACTUALES); 
		Servidor.mensaje3.setText("N�mero: " + Servidor.random);
		String texto = Servidor.textarea.getText(); 
		EnviarMensajes(texto); 
		// Seguidamente, se crea un bucle en el que se recibe lo que el cliente escribe en el chat. 
		// Cuando un cliente finaliza con el bot�n Salir, se env�a un * al servidor del Chat, 
		// entonces se sale del bucle while, ya que termina el proceso del cliente, 
		// de esta manera se controlan las conexiones actuales 
		while(!fin) { 
			String cadena = ""; 
			try { 
				cadena = fentrada.readUTF();
				String[] arrayJuego = cadena.split("> ");
				String nombre = arrayJuego[0];
				String datos = arrayJuego[1];
				if(cadena.trim().equals("*")) 
				{ 
					Servidor.ACTUALES--; 
					Servidor.CONEXIONES--;
					Servidor.mensaje.setText("N�mero de conexiones actuales: " + Servidor.ACTUALES); 
					fin=true;
				} 
				// El texto que el cliente escribe en el chat, 
				// se a�ade al textarea del servidor y se reenv�a a todos los clientes 
				else {
					if ((isNumeric(cadena)==true)&&(jugando==true))
					{
						if(Integer.parseInt(datos) < Servidor.random)
						{
							Servidor.textarea.append("SERVIDOR> " + nombre + " piensa que el n�mero es el " + datos + ", pero el n�mero es MAYOR. \n");
							texto = Servidor.textarea.getText();
							EnviarMensajes(texto);
							Thread.sleep(3000);
						}
						else if((Integer.parseInt(datos) > Servidor.random)&&(jugando==true))
						{
							Servidor.textarea.append("SERVIDOR> " + nombre + " piensa que el n�mero es el " + datos + ", pero el n�mero es MENOR. \n");
							texto = Servidor.textarea.getText();
							EnviarMensajes(texto);
							Thread.sleep(3000);
						}
						else if((Integer.parseInt(datos) == Servidor.random)&&(jugando==true))
						{
							Servidor.textarea.append("SERVIDOR> " + nombre + " piensa que el n�mero es el " + datos + ", y ha ACERTADO! \n" + "SERVIDOR> El ganador ha sido: " + nombre + ". \n" + "SERVIDOR> El juego ha terminado \n"+"SERVIDOR> �Quieres volver a jugar? Para volver a jugar, escribe *JUGAR. \n");	
							texto = Servidor.textarea.getText();
							EnviarMensajes(texto);
							jugando=false;
						}

					}else {
						if (((datos.equals("*JUGAR"))||(datos.equals("*jugar")))&&(jugando==false)) {
							
							Servidor.textarea.append(cadena + "\n"); 
							texto = Servidor.textarea.getText(); 
							EnviarMensajes(texto);
							jugar=true;
							if(jugar==true) {
								Servidor.textarea.append("SERVIDOR>  Empezamos a jugar, escribe un n�mero del 1 al 100. Y suerte. \n");	
								texto = Servidor.textarea.getText();
								EnviarMensajes(texto);
								Servidor.aleatorio();
								Servidor.mensaje3.setText("N�mero: " + Servidor.random);
								jugar=false;
								jugando=true;
								}
						}else if (((datos.equals("*INSTRUCIONES"))||(datos.equals("*instruciones")))&&(jugando==true)){
							Servidor.textarea.append(cadena + "\n"); 
							texto = Servidor.textarea.getText(); 
							EnviarMensajes(texto);
							Servidor.textarea.append("SERVIDOR>  Estamos jugando, para jugar escribe un n�mero del 1 al 100. \n");	
							texto = Servidor.textarea.getText();
							EnviarMensajes(texto);
						}
						else if (((datos.equals("*INSTRUCIONES"))||(datos.equals("*instruciones")))&&(jugando==false)){
							Servidor.textarea.append(cadena + "\n"); 
							texto = Servidor.textarea.getText(); 
							EnviarMensajes(texto);
							Servidor.textarea.append("SERVIDOR>  No estamos jugando, para jugar escribe *JUGAR. \n");	
							texto = Servidor.textarea.getText();
							EnviarMensajes(texto);
						}
						else
						{
							Servidor.textarea.append(cadena + "\n"); 
							texto = Servidor.textarea.getText(); 
							EnviarMensajes(texto);
						}
					}
				} 
			}
			catch (Exception ex) 
			{ 
				fin=true; 
			}
		} 
	} 
	/**
	 * El m�todo EnviarMensajes() env�a el texto del textarea a todos los sockets que est�n en la tabla de sockets, de esta forma todos ven la conversaci�n. El programa abre un stream de salida para escribir el texto en el socket 
	 * @param texto Mensaje a enviar
	 */
	private void EnviarMensajes(String texto) 
	{ 
		for(int i=0;
				i<Servidor.CONEXIONES; i++) 
		{ 
			Socket socket = Servidor.tabla[i]; 
			try { DataOutputStream fsalida = new DataOutputStream(socket.getOutputStream());
			fsalida.writeUTF(texto); 
			} 
			catch (IOException e) { e.printStackTrace(); 
			} 
		} 
	} 
	/**
	 * M�todo para saber si una cadena enviada es un numero
	 * @param cadena Cadena enviada para comprobar si es un n�mero.
	 * @return Devuelve true si es un numero o false si no lo es
	 */
	public static boolean isNumeric(String cadena) {

		boolean resultado;
		//Separamos el usuario del mensaje
		String[] numerico = cadena.split("> ");
		String numero = numerico[1]; // Guardamos el mensaje en una variable para comprobarlo
		try {
			Integer.parseInt(numero);
			resultado = true;
		} catch (NumberFormatException excepcion) {
			resultado = false;
		}
		return resultado;
	}
}
