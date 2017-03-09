package aplicacionChat;

import javax.swing.*;
import org.apache.commons.net.ftp.FTPClient;
import java.awt.*;
import java.io.*;
import java.net.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AplicacionServidor extends JFrame implements Runnable{
	
	public static void main(String[] args) {
		
		new AplicacionServidor().setVisible(true);
		
	}

	public AplicacionServidor(){
		
		setTitle(" ----- SERVIDOR -----");
		
		setSize(300, 300);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLocationRelativeTo(null);
		
		setLayout(new BorderLayout());
		
		// ---------------------------------
		
		miarea= new JTextArea();
		miarea.setLineWrap(true);
		
		JScrollPane miScroll= new JScrollPane(miarea);
		
		add(miScroll, BorderLayout.CENTER);
		
		
		// BUSCANDO LOS DATOS DE CONEXION BD
		
		if(!crearConexionFTPServer()){	
			JOptionPane.showMessageDialog(this, "Fallo al crear la conexion ftp por lo tanto todo fallo!");
		}
		else{
			
			recuperarDatosBD();
			
			// CREANDO LA CONEXION BD
			
			try{
				
				link= DriverManager.getConnection(driverdb, userdb, passdb);
				
			}catch(Exception e){
				
				System.out.println("Fallo al crear la conexion BD.");
				
				e.printStackTrace();
			}

			// ----------------------
			
		}
		
		Thread miHilo= new Thread(this);
		
		miHilo.start();
		
	}
	
	// HILO DE EJECUCION
	public void run(){
		
		try{
			
			ServerSocket server= new ServerSocket(9090);
			
			PaqueteParaEnvio pr;
			
			while(true){
				
				Socket miSocket= server.accept();
				
				ObjectInputStream datosRecibidos= new ObjectInputStream(miSocket.getInputStream()); 
				
				pr= (PaqueteParaEnvio) datosRecibidos.readObject();
				
				
				miarea.append(pr.getId_usuario_envio()+"\t"+pr.getMensaje()+"\t"+pr.getId_usuario_destino()+"\n");

				// BUSCA LA IP DEL USUARIO DE DESTINO
				

				if(pr.getMensaje().isEmpty() || pr.getMensaje()==null){
					
					JOptionPane.showMessageDialog(this, "NO se envio");
					
				}
				else{
					
					// GUARDA EN LA TABLA MENSAJES UN NUEVO REGISTRO
					guardarMensajeUsuarios(pr.getId_usuario_envio(), pr.getId_usuario_destino(), pr.getMensaje());
					
					
					// REENVIA LA INFORMACION AL USUARIO ENVIADO
					String ip_destino= buscarIpUsuarioDestino(pr.getId_usuario_destino());
					
					System.out.println("enviado a "+ip_destino);

					Socket reenvio= new Socket(ip_destino, 9999);
					
					ObjectOutputStream datosReenvio= new ObjectOutputStream(reenvio.getOutputStream());
						
					datosReenvio.writeObject(pr);
					
				}

			}		
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(this, e.getMessage());
			
			e.printStackTrace();
			
		}
		

	}

	// CREAR LA CONEXION FTP PARA RECUPERAR LOS DATOS DEL SERVIDOR
	private boolean crearConexionFTPServer(){
		
		ftp= new FTPClient();
	
		try {
				
			ftp.connect("192.168.1.4");
				
			if(ftp.login("ezequiel", "greenblue"))
				return true;
			else
				return false;
			
		} catch (IOException e) {
			return false;
		}	
		
	}
	
	// RECUPERA LOS DATOS DE CONEXION DESDE EL SERVIDOR
		private void recuperarDatosBD(){
			
			driverdb="";
			userdb="";
			passdb="";
			
			try {
				
				ftp.changeWorkingDirectory("/home/ezequiel/config_chat");
				
				InputStream datos_leer= ftp.retrieveFileStream("datos_config_chat.txt");
				
				boolean final_archivo= false;	
				
				int contadordb=0;
				
				while(!final_archivo){
					
					int byte_entrada= datos_leer.read();
					
					if(byte_entrada != -1){
						
						char letra= (char)byte_entrada;

						if(letra==';'){contadordb++;}else{
							if(contadordb==0){driverdb= driverdb+letra;}
							else if(contadordb==1){userdb= userdb+letra;}
							else if(contadordb==2){passdb= passdb+letra;}
						}
					}
					else{final_archivo= true;}	
				}
				
			} catch (IOException e) {
				System.out.println("Hubo un error al recuperar los datos del servidor");
				e.printStackTrace();
			}
		
		}
	
	private String buscarIpUsuarioDestino(int id_destino){
		
		try{
			
			String query= "SELECT ip FROM usuarios WHERE id = ?";
			
			PreparedStatement ps= link.prepareStatement(query);
			
				ps.setInt(1, id_destino);
			
			rs= ps.executeQuery();	
				
			if(rs.next()){
				
				return rs.getString(1);
			}
			else{
				
				return null;
			}
			
		}catch(Exception e){
			
			System.out.println("Hubo un error al recuperar el usuario de destino!");
			e.printStackTrace();
			
			return null;
		}
		
	}
	
	// GUARDA EN LA TABLA MENSAJE EL MENSAJE, EL USUARIO QUE ENVIO Y EL USUARIO DE DESTINO
	private void guardarMensajeUsuarios(int id_usuario_e, int id_usuario_d, String texto){
		
		try{
			
			String query= "INSERT INTO mensajes (mensaje, fecha, id_usuario_envio, id_usuario_destino)"
					+ " VALUES (?,?,?,?)";
			
			PreparedStatement ps= link.prepareStatement(query);
			
				ps.setString(1, texto);
				ps.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				ps.setInt(3, id_usuario_e);
				ps.setInt(4, id_usuario_d);
			
			ps.executeUpdate();
				
		}catch(Exception e){
			
			System.out.println("Hubo un error al guardar el mensaje!");
			e.printStackTrace();
			
		}
		
	}
	
	
	private String driverdb, userdb, passdb;

	private FTPClient ftp;
	
	private Connection link;
	
	private ResultSet rs;
	
	private JTextArea miarea;
	
}
