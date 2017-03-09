package aplicacionChat;

import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import java.net.*;

public class GestionaBotonesMarcoChat implements ActionListener{

	public GestionaBotonesMarcoChat(MarcoChat mc, UsuarioChat uc, Connection ct){
		
		this.marcoChat= mc;
		
		this.usuarioChat= uc;
		
		this.link= ct;
		
		paqueteEnvio= new PaqueteParaEnvio();
		
	}
	
	// SE EJECUTA AL HACER CLICK EN CUALQUIERA DE LOS BOTONES DEL MarcoFrame
	public void actionPerformed(ActionEvent ae){
		
		if(ae.getSource() == marcoChat.getBtnActualizar()){		
			actualizarUsuariosOnline();
		}
		else if(ae.getSource() == marcoChat.getBtnTiempo()){
			duracionConexionActual();	
		}
		else if(ae.getSource() == marcoChat.getBtnEnviar()){
			
			String texto= marcoChat.getTaEscribir().getText();
			
			if(texto.isEmpty()){
				JOptionPane.showMessageDialog(marcoChat, "Escriba un mensaje para enviar");
			}
			else{
				
				marcoChat.setFontPantalla(new Font(Font.MONOSPACED, Font.PLAIN, 14));

				marcoChat.setEscribirPantalla("\nYo: -> "+texto);

				marcoChat.setLimpiarEscribir();
				
				// ALMACENA EN ESTA INSTANCIA EL USUARIO QUE ENVIO, SU NOMBRE, EL USUARIO DE DESTINO
				// Y EL MENSAJE
				paqueteEnvio.setId_usuario_envio(usuarioChat.getId_usuario());
				paqueteEnvio.setId_usuario_destino(conseguirUsuarioDestino());
				paqueteEnvio.setMensaje(texto.trim());
				paqueteEnvio.setNick_usuario_e(usuarioChat.getNick_usuario());
				
				enviarMensaje();
				
			}
			
		}
		
	}
	
	// ENVIA EL MENSAJE A OTRO USUARIO POR MEDIO DE LA RED
	private void enviarMensaje(){
		
		try{
			
			Socket miSocket= new Socket("192.168.1.50", 9090);
			
			ObjectOutputStream datos= new ObjectOutputStream(miSocket.getOutputStream());
			
			datos.writeObject(paqueteEnvio);
			
			datos.close();
			
			miSocket.close();
			
			System.out.println("se envio");
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(marcoChat, "Fallo al enviar mensaje!");
			
			e.printStackTrace();
		}
		
	}

	
	// ACTUALIZA LOS USUARIOS QUE ESTAN CONECTADOS
	private void actualizarUsuariosOnline(){
		
		marcoChat.removerItemsCbConectados();

		try{
			
			String query= "SELECT nick FROM usuarios WHERE id <> ? AND id_estado= 1";
			
			PreparedStatement ps= link.prepareStatement(query);
			
				ps.setInt(1, usuarioChat.getId_usuario());
			
			rs= ps.executeQuery();
			
			while(rs.next()){	
				marcoChat.agregarItemsCbConectados(rs.getString(1));
			}
	
		}catch(Exception e){
			
			System.out.println("No se encontraron los usuarios!");
			
			e.printStackTrace();
		}
		
	}
	
	// CALCULA QUE TIEMPO A TRANSCURRIDO DESDE QUE EL USUARIO SE CONECTO
	private void duracionConexionActual(){
		
		try{
			
			String query= "SELECT TIMEDIFF(NOW(), fecha_inicio) FROM conexiones WHERE id= ?";
			
			PreparedStatement ps= link.prepareStatement(query);
			
				ps.setInt(1, usuarioChat.getId_conexiones());
			
			rs= ps.executeQuery();
			
			if(rs.next()){
				JOptionPane.showMessageDialog(marcoChat, rs.getString(1));
			}
			
		}catch(Exception e){
			
			System.out.println("No se obtuvo la duracion de la conexion!");
			
			e.printStackTrace();
			
		}
		
	}
	
	// BUSCA EL ID DEL USUARIO AL CUAL SE LE ENVIARA EL MENSAJE
	private int conseguirUsuarioDestino(){
		
		String nick= (String)marcoChat.getCbConectados().getSelectedItem();
		
		try{
			
			String query= "SELECT id FROM usuarios WHERE nick= ?";
			
			PreparedStatement ps= link.prepareStatement(query);
			
				ps.setString(1, nick);
			
			rs= ps.executeQuery();
			
			if(rs.next()){
				
				return rs.getInt(1);
			}
			
		}catch(Exception e){
			
			System.out.println("No se encontro el usuario para envio!");
			
			e.printStackTrace();
			
			return 0;
		}
		
		return 0;
		
	}
	
	
	private MarcoChat marcoChat;
	
	private UsuarioChat usuarioChat;
	
	private Connection link;
	
	private ResultSet rs;
	
	private PaqueteParaEnvio paqueteEnvio;
	 
	
}
