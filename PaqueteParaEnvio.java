package aplicacionChat;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class PaqueteParaEnvio implements Serializable{

	public PaqueteParaEnvio(int iue, int iud, String msj, SimpleDateFormat fe){
		
		this.id_usuario_envio= iue;
		
		this.id_usuario_destino= iud;
		
		this.mensaje= msj;
		
		this.fecha_envio= fe;
		
	}
	
	public PaqueteParaEnvio(int iue, int iud, String msj){
		
		this.id_usuario_envio= iue;
		
		this.id_usuario_destino= iud;
		
		this.mensaje= msj;
		
	}
	
	public PaqueteParaEnvio(){
		
	}
	
	
	
	
	public int getId_usuario_envio() {
		return id_usuario_envio;
	}

	public void setId_usuario_envio(int id_usuario_envio) {
		this.id_usuario_envio = id_usuario_envio;
	}

	public int getId_usuario_destino() {
		return id_usuario_destino;
	}

	public void setId_usuario_destino(int id_usuario_destino) {
		this.id_usuario_destino = id_usuario_destino;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public SimpleDateFormat getFecha_envio() {
		return fecha_envio;
	}

	public void setFecha_envio(SimpleDateFormat fecha_envio) {
		this.fecha_envio = fecha_envio;
	}

	public String getNick_usuario_e() {
		return nick_usuario_e;
	}

	public void setNick_usuario_e(String nick_usuario_e) {
		this.nick_usuario_e = nick_usuario_e;
	}


	private int id_usuario_envio;
	
	private int id_usuario_destino;
	
	private String mensaje;
	
	private String nick_usuario_e;
	
	private SimpleDateFormat fecha_envio;
	
}
