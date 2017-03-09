package aplicacionChat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import chat.GestionaBotonesFrameChat;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarcoChat extends JFrame implements Runnable{

	public MarcoChat(UsuarioChat uc, Connection conn){
		
		this.usuarioChat= uc;
		
		setTitle(" ----- MyChat ----- ");
		
		setSize(500, 400);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLocationRelativeTo(null);
		
		gvmc= new GestionaVentanaMarcoChat(conn);
		
		this.addWindowListener(gvmc);
		
		gbmc= new GestionaBotonesMarcoChat(this, uc, conn);
		
		crearEstructura();
		
		// INICIANDO EL HILO 2
		Thread miHilo= new Thread(this);
		
		miHilo.start();
		
	}
	
	private void crearEstructura(){
		
		this.setLayout(new BorderLayout());
		
		// panel norte 
		JPanel norte= new JPanel();
		
		lbUsuario= new JLabel("Nick: "+usuarioChat.getNick_usuario());
		lbUsuario.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
		
		norte.add(lbUsuario);
		norte.add(Box.createHorizontalStrut(20));

		// en este componente se veran los usuario conectados
		
		cbConectados= new JComboBox<String>();
		
		norte.add(cbConectados);
		
		// ------------------------------------------------
		
		btnActualizar= new JButton("Act");
		btnActualizar.addActionListener(gbmc);
		
		btnTiempo= new JButton("Tiempo");
		btnTiempo.addActionListener(gbmc);
		
		norte.add(btnActualizar);
		norte.add(btnTiempo);
		
		this.add(norte, BorderLayout.NORTH);
		
		// panel central
		tpPantalla= new JTextArea();
		tpPantalla.setEditable(false);
		tpPantalla.setLineWrap(true);
		
		spMiscroll= new JScrollPane(tpPantalla);
		spMiscroll.setBorder(new EmptyBorder(0, 10, 0, 10));
		
		this.add(spMiscroll, BorderLayout.CENTER);
	
		
		// panel sur
		JPanel sur= new JPanel();
		
		taEscribir= new JTextArea(2, 25);
		taEscribir.setLineWrap(true);
		
		JScrollPane miScroll= new JScrollPane(taEscribir);
		
		sur.add(miScroll);
		
		btnEnviar= new JButton("Enviar");
		btnEnviar.addActionListener(gbmc);
		
		sur.add(btnEnviar);
		
		this.add(sur, BorderLayout.SOUTH);
	
	}
	
	// CLASE INTERNA QUE GESTIONA LOS EVENTOS AL ABRIR Y CERRAR LA VENTANA DEL MarcoChat
	private class GestionaVentanaMarcoChat extends WindowAdapter{
		
		public GestionaVentanaMarcoChat(Connection conn){
			this.link= conn;
		}
		
		// SE EJECUTA CUANDO SE ESTA CERRANDO LA VENTANA
		public void windowClosing(WindowEvent we) {
			
			super.windowClosing(we);
			
			actualizaEstadoUsuarioOffline();
			
			actualizaFechaFinConexion();
		
		}
		
		// SE EJECUTA CUANDO SE ESTA ABRIENDO LA VENTANA
		public void windowOpened(WindowEvent We) {

			super.windowOpened(We);
			
			buscaUsuariosOnline();
			
			
		}
		
		// ACTUALIZA EL USUARIO ACTUAL AL ESTADO OFFLINE
		private void actualizaEstadoUsuarioOffline(){
			
			try{
				
				String query= "UPDATE usuarios SET id_estado= 2 WHERE id= ?";
				
				PreparedStatement ps= link.prepareStatement(query);
				
					ps.setInt(1, usuarioChat.getId_usuario());
				
				ps.executeUpdate();
					
			}catch(Exception e){
				
				JOptionPane.showMessageDialog(MarcoChat.this,"Fallo al actualizar el estado del usuario!");
				
				e.printStackTrace();
			}
			
		}
		
		// ACTUALIZA LA CONEXON EN LA TABLA CONEXIONES COLOCANDO LA FECHA DE FIN
		private void actualizaFechaFinConexion(){
			
			try{
				
				String query= "UPDATE conexiones SET fecha_fin= ? WHERE id= ?";
				
				PreparedStatement ps= link.prepareStatement(query);
				
					ps.setString(1, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				
					ps.setInt(2, usuarioChat.getId_conexiones());
					
				ps.executeUpdate();
					
			}catch(Exception e){
				
				JOptionPane.showMessageDialog(MarcoChat.this, "FALLO AL ACTUALIZAR FECHA DE CONEXION");
				
				e.printStackTrace();
			}
			
		}
		
		// BUSCA LOS USUARIOS QUE ESTAN CONECTADOS O ESTAN ONLINE
		private void buscaUsuariosOnline(){
			
			try{
				
				String query= "SELECT nick FROM usuarios WHERE id <> ? AND id_estado= 1";
				
				PreparedStatement ps= link.prepareStatement(query);
				
					ps.setInt(1, usuarioChat.getId_usuario());
				
				rs= ps.executeQuery();
				
				while(rs.next()){	
					cbConectados.addItem(rs.getString(1));
				}
				
			}catch(Exception e){

				JOptionPane.showMessageDialog(MarcoChat.this, "FALLO AL BUSCAR LOS USUARIOS CONECTADOS!");
				
				e.printStackTrace();		
			}
			
		}
		
		private Connection link;
		
		private ResultSet rs;
		
	} 
	// FIN DE LA CLASE INTERNA
	
	// HILO DE ESCUCHA PARA EL SOCKET DEL CLIENTE
	public void run(){
		
		try{
			
			ServerSocket serverCliente= new ServerSocket(9999);
			
			PaqueteParaEnvio pr;
			
			while(true){
				
				Socket miSocket= serverCliente.accept();
				
				ObjectInputStream datos= new ObjectInputStream(miSocket.getInputStream());
				
				pr= (PaqueteParaEnvio) datos.readObject();
				
				String nick_usuario_d= pr.getNick_usuario_e();
				
				this.setEscribirPantalla("\n"+nick_usuario_d+": -> "+pr.getMensaje());
				
			}
			
		}catch(Exception e){
			
			JOptionPane.showMessageDialog(this, "Fallo al recibir el mensaje!");
			
			e.printStackTrace();
		}

	}
	
	private UsuarioChat usuarioChat;
	
	public JComboBox<String> getCbConectados() {
		return cbConectados;
	}

	public void agregarItemsCbConectados(String t){
		this.cbConectados.addItem(t);
	}

	public void removerItemsCbConectados(){
		this.cbConectados.removeAllItems();
	}
	
	public JTextArea getTpPantalla() {
		return tpPantalla;
	}

	public void setFontPantalla(Font f){
		this.tpPantalla.setFont(f);
	}
	
	public void setEscribirPantalla(String t){
		this.tpPantalla.append(t);;
	}
	
	public JTextArea getTaEscribir() {
		return taEscribir;
	}

	public void setLimpiarEscribir(){
		this.taEscribir.setText("");
	}

	public JButton getBtnEnviar() {
		return btnEnviar;
	}

	public void setBtnEnviar(JButton btnEnviar) {
		this.btnEnviar = btnEnviar;
	}

	public JButton getBtnActualizar() {
		return btnActualizar;
	}

	public void setBtnActualizar(JButton btnActualizar) {
		this.btnActualizar = btnActualizar;
	}

	public JButton getBtnTiempo() {
		return btnTiempo;
	}

	public void setBtnTiempo(JButton btnTiempo) {
		this.btnTiempo = btnTiempo;
	}

	private JLabel lbUsuario;
	
	private JComboBox<String> cbConectados ;
	
	private JTextArea tpPantalla;
	
	private JScrollPane spMiscroll;
	
	private JTextArea taEscribir;
	
	private JButton btnEnviar, btnActualizar, btnTiempo;
	
	private GestionaVentanaMarcoChat gvmc;
	
	private GestionaBotonesMarcoChat gbmc;
	
}
