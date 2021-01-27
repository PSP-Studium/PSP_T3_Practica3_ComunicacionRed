package es.studium.adivina;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Clase Servidor
 * @author Alvca
 * @since 2021
 * @version 1.0
 */
public class Servidor extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	static ServerSocket servidor;
	//Indicamos el puerto de escucha
	static final int PUERTO = 44444;
	static int CONEXIONES = 0;
	static int ACTUALES = 0;
	//Indicamos el maximo de clientes conectados
	static int MAXIMO = 10;
	static int random;
	static JTextField mensaje = new JTextField("");
	static JTextField mensaje2 = new JTextField("");
	static JTextField mensaje3 = new JTextField("");
	private JScrollPane scrollpane1;
	static JTextArea textarea;
	JButton salir = new JButton("Salir");
	static Socket[] tabla = new Socket[MAXIMO];
	/**
	 * Constructor sin par�metros
	 */
	public Servidor()
	{
		// Construimos el entorno gr�fico
		super(" VENTANA DEL SERVIDOR DE CHAT ");
		setLayout(null);
		mensaje.setBounds(10, 10, 400, 30);
		add(mensaje);
		mensaje.setEditable(false);
		mensaje2.setBounds(10, 348, 400, 30);
		add(mensaje2);
		mensaje2.setEditable(false);

		mensaje3.setBounds(420, 40, 100, 30);
		add(mensaje3);
		mensaje3.setEditable(false);

		textarea = new JTextArea();
		scrollpane1 = new JScrollPane(textarea);
		scrollpane1.setBounds(10, 50, 400, 300);
		add(scrollpane1);
		salir.setBounds(420, 10, 100, 30);
		add(salir);
		textarea.setEditable(false);
		// Se ha anulado el cierre de la ventana para que la finalizaci�n
		// del servidor se haga desde el bot�n Salir.
		//Cuando se pulsa el bot�n se cierra el ServerSocket y
		//finaliza la ejecuci�n
		salir.addActionListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/**
	 * main para ejecutar
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		aleatorio();
		//Desde el main se inicia el servidor
		//y las variables y se prepara la pantalla
		servidor = new ServerSocket(PUERTO);
		System.out.println("Servidor iniciado...");
		//Montamos la ventana del servidor y la mostramos
		Servidor pantalla = new Servidor();
		pantalla.setBounds(0, 0, 540, 450);
		pantalla.setVisible(true);
		mensaje.setText("N�mero de conexiones actuales: " + 0);
		mensaje3.setText("N�mero: "+random);
		//Se usa un bucle para controlar el n�mero de conexiones.
		//Dentro del bucle el servidor espera la conexi�n
		//del cliente y cuando se conecta se crea un socket
		while(CONEXIONES < MAXIMO)
		{
			Socket socket;
			try
			{
				socket = servidor.accept();
			}
			catch (SocketException sex)
			{
				//Sale por aqu� si pulsamos el bot�n salir
				break;
			}
			//El socket creado se a�ade a la tabla,
			//se incrementa el n�mero de conexiones
			//y se lanza el hilo para gestionar los mensajes
			//del cliente que se acaba de conectar
			tabla[CONEXIONES] = socket;
			CONEXIONES++;
			ACTUALES++;
			ServidorHilo hilo = new ServidorHilo(socket);
			hilo.start();
		}
		//Si se alcanzan 15 conexiones o se pulsa el bot�n Salir,
		//el programa se sale del bucle.
		//Al pulsar Salir se cierra el ServerSocket
		//lo que provoca una excepci�n (SocketException)
		//en la sentencia accept(), la excepci�n se captura
		//y se ejecuta un break para salir del bucle
		if(!servidor.isClosed())
		{
			try
			{
				mensaje2.setForeground(Color.red);
				mensaje2.setText("M�ximo N� de conexiones establecidas: " + CONEXIONES);
				servidor.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			System.out.println("Servidor finalizado...");
		}
	}
	/**
	 * M�todo para generar el n�mero aleatorio del juego
	 */
	public static void aleatorio() {
		// Obtiene un n�mero aleatorio que ser� el n�mero a adivinar
		random = (int) Math.floor(Math.random() * 100 + 1);
		//System.out.println(random);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==salir)
		{
			try
			{
				servidor.close();
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
			System.exit(0);
		}
	}
}
