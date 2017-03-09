package aplicacionChat;

import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class LoginChat extends JFrame{

	public LoginChat(){

		
		setTitle(" ----- LOGIN ----- ");
		
		setSize(450, 275);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLocationRelativeTo(null);
		
		setResizable(false);
		
		crearEstructura();
	
		
	}
	
	// Este metodo inicia y coloca los componentes que iran dentro 
	// de la LaminaLogin en el LoginChat
	private void crearEstructura(){
			
		// DECLARACION DE COMPONENTES
			
		lbNick= new JLabel("Nick: ");
		lbPass= new JLabel("Pass: ");
		lbHost= new JLabel("Server: ");
		lbUserHost= new JLabel("User: ");
		lbPassHost= new JLabel("Pass:");
			
		txtNick= new JTextField("Marshal01");
		txtPass= new JPasswordField("010101");
		txtHost= new JTextField("192.168.1.4");
		txtUserHost= new JTextField("ezequiel");
		txtPassHost= new JPasswordField("blue04");
			
		btnIniciarSeccion= new JButton("Iniciar Sección");
		btnComprobarConexion= new JButton("Comprobar Conexion Server");
		
		// CLASE QUE GESTIONA LOS EVENTOS
		
		GestionaBotonesLoginChat gblc= new GestionaBotonesLoginChat(this);
		
		btnIniciarSeccion.addActionListener(gblc);
		
		btnComprobarConexion.addActionListener(gblc);
		
		
		// CREACION DE LOS BOX
			
		Box boxVerticalMain= Box.createVerticalBox();
			
		Box bh1= Box.createHorizontalBox();
		Box bh2= Box.createHorizontalBox();
		Box bh3= Box.createHorizontalBox();
		Box bh4= Box.createHorizontalBox();
			
		// COLOCANDO LOS COMPONENTES
			
		bh1.setBorder(new EmptyBorder(20, 10, 15, 10));
			
		bh1.add(Box.createHorizontalStrut(10));
		bh1.add(lbNick);
		bh1.add(Box.createHorizontalStrut(11));
		bh1.add(txtNick);
		bh1.add(Box.createHorizontalStrut(10));
			
			boxVerticalMain.add(bh1);
				
		bh2.setBorder(new EmptyBorder(15, 10, 15, 10));
				
		bh2.add(Box.createHorizontalStrut(10));
		bh2.add(lbPass);
		bh2.add(Box.createHorizontalStrut(7));
		bh2.add(txtPass);
		bh2.add(Box.createHorizontalStrut(10));
			
			boxVerticalMain.add(bh2);
			
		bh3.setBorder(new EmptyBorder(15, 10, 15, 10));
				
		bh3.add(Box.createHorizontalStrut(10));
		bh3.add(lbHost);
		bh3.add(Box.createHorizontalStrut(10));
		bh3.add(txtHost);
		bh3.add(Box.createHorizontalStrut(5));
		bh3.add(lbUserHost);
		bh3.add(Box.createHorizontalStrut(5));
		bh3.add(txtUserHost);
		bh3.add(Box.createHorizontalStrut(5));
		bh3.add(lbPassHost);
		bh3.add(Box.createHorizontalStrut(5));
		bh3.add(txtPassHost);
		bh3.add(Box.createHorizontalStrut(10));
		
			boxVerticalMain.add(bh3);
				
		bh4.setBorder(new EmptyBorder(15, 5, 10, 10));
				
		bh4.add(Box.createHorizontalStrut(10));
		bh4.add(btnIniciarSeccion);
		bh4.add(Box.createHorizontalStrut(10));
		bh4.add(btnComprobarConexion);
		bh4.add(Box.createHorizontalStrut(10));
				
			boxVerticalMain.add(bh4);

		this.add(boxVerticalMain);
			
	}
	
	
	// METODOS GETTERS
	
	public String getTxtNick(){
		return this.txtNick.getText();
	}
	
	public String getTxtPass(){	
		return new String(this.txtPass.getPassword());	
	}
	
	public String getTxtHost(){
		return this.txtHost.getText();
	}
	
	public String getTxtUserHost(){
		return this.txtUserHost.getText();
	}
	
	public String getTxtPassHost(){
		return new String(this.txtPassHost.getPassword());
	}
	

	private JLabel lbNick, lbPass, lbHost, lbUserHost, lbPassHost;
		
	private JTextField txtNick,txtHost, txtUserHost;
		
	private JPasswordField txtPass, txtPassHost;
	
	private JButton btnIniciarSeccion, btnComprobarConexion;
	
}
