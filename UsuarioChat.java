package aplicacionChat;

public class UsuarioChat {

	public UsuarioChat(){
		
		
	}
	
	public UsuarioChat(int id, String nu, String iu, int ieu, int ic){
		
		this.id_usuario= id;
		
		this.nick_usuario= nu;
		
		this.ip_usuario= iu;
		
		this.id_estado_usuario= ieu;
		
		this.id_conexiones= ic;
		
	}
	
	
	public int getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getNick_usuario() {
		return nick_usuario;
	}

	public void setNick_usuario(String nick_usuario) {
		this.nick_usuario = nick_usuario;
	}

	public String getIp_usuario() {
		return ip_usuario;
	}

	public void setIp_usuario(String ip_usuario) {
		this.ip_usuario = ip_usuario;
	}

	public int getId_estado_usuario() {
		return id_estado_usuario;
	}

	public void setId_estado_usuario(int id_estado_usuario) {
		this.id_estado_usuario = id_estado_usuario;
	}

	public int getId_conexiones() {
		return id_conexiones;
	}

	public void setId_conexiones(int id_conexiones) {
		this.id_conexiones = id_conexiones;
	}




	private int id_usuario;
	
	private String nick_usuario;
	
	private String ip_usuario;
	
	private int id_estado_usuario;
	
	private int id_conexiones;
	
}
