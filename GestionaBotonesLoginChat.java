package aplicacionChat;

import java.awt.event.*;
import java.io.*;

import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTPClient;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GestionaBotonesLoginChat implements ActionListener{

	public GestionaBotonesLoginChat(LoginChat lc){
		
		this.loginChat= lc;
		
		this.ftp= new FTPClient();
		
		this.conexionFTPOk= false;
		
		this.conexionBDOk= false;
		
		this.usuarioChat= new UsuarioChat();
		
	}
	
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getActionCommand().equals("Comprobar Conexion Server")){
			
		// CREAMOS LA CONEXION VIA FTP PARA DESPUES RECUPERAR LOS DATOS DE BD.
		// SI LA CONEXION FTP NO SE A CREADO ANTES.
		if(conexionFTPOk==false){
			
			if(!crearConexionFTP()){
				JOptionPane.showMessageDialog(loginChat, "FALLO AL ESTABLECER CONEXION CON SERVIDOR :(");
			}
			else{
				conexionFTPOk=true;
				JOptionPane.showMessageDialog(loginChat, "CONEXION CON SERVIDOR ESTABLECIDA :)");
				
				if(!recuperarDatosBD()){
					JOptionPane.showMessageDialog(loginChat, "FALLO AL RECUPERAR LOS DATOS DE LA CONEXION :(");
				}
				else{
					// SI TODO CON EL SERVIDOR VA BIEN CREA LA CONEXION CON LA BD.
					
					if(!crearConexionBD()){
						JOptionPane.showMessageDialog(loginChat, "FALLO AL ESTABLECER LA CONEXION CON LA BD,"
								+ " OSEA NO PODRA ENTRAR :(");
					}
					else{
						conexionBDOk= true;
					}
					
				}
			}
			
		}
		else{JOptionPane.showMessageDialog(loginChat, "LA CONEXION FTP YA ESTA CREADA :(");}
		
			
		}
		else if(ae.getActionCommand().equals("Iniciar Sección")){
			
			if(loginChat.getTxtNick().isEmpty() || loginChat.getTxtPass().isEmpty()){
				JOptionPane.showMessageDialog(loginChat, "INSERTE EL NICK Y EL PASS :(");
			}
			else{
				
				if(!conexionFTPOk){
					
					if(!crearConexionFTP()){
						JOptionPane.showMessageDialog(loginChat, "FALLO AL ESTABLECER CONEXION CON SERVIDOR :(");
					}
					else{
						JOptionPane.showMessageDialog(loginChat, "CONEXION CON SERVIDOR ESTABLECIDA :)");
						
						if(!recuperarDatosBD()){
							JOptionPane.showMessageDialog(loginChat, "FALLO AL RECUPERAR LOS DATOS DE LA CONEXION :(");
						}
						else{
							// SI TODO CON EL SERVIDOR BA BIEN CREA LA CONEXION CON LA BD.
							if(!crearConexionBD()){
								JOptionPane.showMessageDialog(loginChat, "FALLO AL ESTABLECER LA CONEXION CON LA BD,"
										+ " OSEA NO PODRA ENTRAR :(");
							}
							else{
								// AHORA VERIFICAR SI EL USUARIO EXISTE
								
								if(!verificarUsuarioBD()){
									JOptionPane.showMessageDialog(loginChat, "USUARIO NO EXISTENTE :(");	
								}
								else{
									
									JOptionPane.showMessageDialog(loginChat, "USUARIO CORRECTO :)");
									// GUARDAMOS LA INFORMACION EN LA TABLA CONEXIONES DE LA BD
								
									guardarYDevolverInfoConexion();
									
									cambiarEstadoUsuario();
									
									loginChat.setVisible(false);
									
									MarcoChat mc= new MarcoChat(usuarioChat, link);
									
									mc.setVisible(true);
									
								}
								
							}
							
						}	
					}
		
				}
				else{
					
					if(!conexionBDOk){
						
						if(!crearConexionBD()){
							JOptionPane.showMessageDialog(loginChat, "FALLO AL ESTABLECER LA CONEXION CON LA BD,"
									+ " OSEA NO PODRA ENTRAR :(");
						}
						else{
							
							// AHORA VERIFICAR SI EL USUARIO EXISTE
							if(!verificarUsuarioBD()){
								JOptionPane.showMessageDialog(loginChat, "USUARIO NO EXISTENTE :(");	
							}
							else{
								
								JOptionPane.showMessageDialog(loginChat, "USUARIO CORRECTO :)");
								// GUARDAMOS LA INFORMACION EN LA TABLA CONEXIONES DE LA BD
							
								guardarYDevolverInfoConexion();
								
								cambiarEstadoUsuario();
								
								loginChat.setVisible(false);
								
								MarcoChat mc= new MarcoChat(usuarioChat, link);
								
								mc.setVisible(true);
							}
							
						}
						
					}
					else{
						
						// AHORA VERIFICAR SI EL USUARIO EXISTE
						
						if(!verificarUsuarioBD()){
							JOptionPane.showMessageDialog(loginChat, "USUARIO NO EXISTENTE :(");	
						}
						else{
							
							JOptionPane.showMessageDialog(loginChat, "USUARIO CORRECTO :)");
							// GUARDAMOS LA INFORMACION EN LA TABLA CONEXIONES DE LA BD
						
							guardarYDevolverInfoConexion();
							
							cambiarEstadoUsuario();
							
							loginChat.setVisible(false);
							
							MarcoChat mc= new MarcoChat(usuarioChat, link);
							
							mc.setVisible(true);
							
						}
						
					}
					
				}
				
			}
			
		}
	
	}
	
	// CREA UNA CONEXION VIA FTP CON EL SERVIDOR PARA DESPUES RECUPERAR LOS DATOS
	private boolean crearConexionFTP(){
		
		String host= loginChat.getTxtHost();
		String user= loginChat.getTxtUserHost();
		String pass= loginChat.getTxtPassHost();
		
		try{
			
			ftp.connect(host);
			
			if(!ftp.login(user, pass))
				return false;
			else
				return true;
			
			
		}catch(Exception e){
			
			return false;
		}
		
	}
	
	// CAMBIA DE DIRECTORIO EN EL SERVIDOR Y BUSCA UNA ARCHIVO DEL CUAL RECUPERAR LOS DATOS DE CONEXION BD
	private boolean recuperarDatosBD(){
		
		try{
			
			ftp.changeWorkingDirectory("/home/ezequiel/config_chat");
			
			InputStream archivo_leer=ftp.retrieveFileStream("datos_config_chat.txt");
			
			boolean final_archivo= false;	
			
			int contadordb=0;
			
			while(!final_archivo){
				
				int byte_entrada= archivo_leer.read();
				
				if(byte_entrada != -1){
					
					char letra= (char)byte_entrada;

					if(letra==';'){
						contadordb++;	
					}else{
						
						if(contadordb==0){
							stringConnectiondb= stringConnectiondb+letra;
						}
						else if(contadordb==1){
							userdb= userdb+letra;
						}
						else if(contadordb==2){
							passdb= passdb+letra;
						}	
					}

				}
				else{final_archivo= true;}
			}
			
			return true;
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(loginChat, e.toString()+"\n"+e.getMessage());
			
			return false;
		}
		
	}
	
	// CREAR UNA CONEXION BD CON LOS DATOS RECUPERADOS DEL SERVIDOR
	private boolean crearConexionBD(){
		
		try{
			
			link= DriverManager.getConnection(stringConnectiondb, userdb, passdb);
			
			return true;
			
		}catch(Exception e){
			return false;
		}
		
	}
	
	// VERIFICA SI EL USUARIO ESTA EN LA BD Y SI NO ESTA EN SECCION
	private boolean verificarUsuarioBD(){
		
		String nick= loginChat.getTxtNick();
		String pass= loginChat.getTxtPass();
		
		try{
			
		String sql= "SELECT id, nick, ip, id_estado FROM usuarios WHERE nick= ? AND pass= ?";	
			
		ps= link.prepareStatement(sql);
			
			ps.setString(1, nick);
			ps.setString(2, pass);
			
			rs= ps.executeQuery();
			
			if(rs.next()){
				
				usuarioChat.setId_usuario(rs.getInt(1));
				usuarioChat.setNick_usuario(rs.getString(2));
				usuarioChat.setIp_usuario(rs.getString(3));
				
				if(rs.getInt(4)==1){
					JOptionPane.showMessageDialog(loginChat, "Hay una session iniciada con ese usuario!");
					return false;
				}else{
					usuarioChat.setId_estado_usuario(rs.getInt(4));
				}
				
				return true;
			}
			
			return false;
			
		}catch(Exception e){
			
			return false;
		}
		
	}
	
	// INSERTA EN LA TABLA CONEXIONES LA FECHA EN QUE ESTE USUARIO SE CONECTO DESPUES DE HABERSE LOGEADO
	private void guardarYDevolverInfoConexion(){
		
		try{
			
			// INSERTA INFO EN LA TABLA CONEXIONES
			ps= link.prepareStatement("INSERT INTO conexiones(fecha_inicio, id_usuario) VALUES (?,?) ");
			
			ps.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			
			ps.setInt(2, usuarioChat.getId_usuario());
			
			ps.executeUpdate();
			
			// OBTENER LA ULTIMA CONEXION QUE SE INSERTO EN LA TABLA CONEXIONES OSEA LA ANTERIOR
			ps= link.prepareStatement("SELECT MAX(id) FROM conexiones");
			
			rs= ps.executeQuery();
			
			if(rs.next()){
				
				usuarioChat.setId_conexiones(rs.getInt(1));
			}
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(loginChat, "FALLO AL GUARDAR INFO O AL BUSCAR LA "
					+ "ULTIMA CONEXION EN LA TABLA CONEXIONES :(");
			
			e.printStackTrace();
		}
		
	}
	
	// CAMBIA EL ESTADO DEL USUARIO DE OFFLINE A ONLINE
	private void cambiarEstadoUsuario(){
		
		try{
				
			ps= link.prepareStatement("UPDATE usuarios SET id_estado= 1 WHERE id= ?");
			
			ps.setInt(1, usuarioChat.getId_usuario());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(loginChat, "FALLO AL ACTUALIZAR EL ESTADO DEL USUARIO :(");
		}
		
	}
	
	
	private String stringConnectiondb="";
	
	private String userdb="";
	
	private String passdb="";
	
	private Connection link;
	
	private ResultSet rs;
	
	private PreparedStatement ps;
	
	private LoginChat loginChat;
	
	private FTPClient ftp;
	
	private UsuarioChat usuarioChat;
	
	private boolean conexionFTPOk, conexionBDOk;
	
}
