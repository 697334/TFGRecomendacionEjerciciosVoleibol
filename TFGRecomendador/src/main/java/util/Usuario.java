package util;

public class Usuario {
	
	private int idUsuario;
	private String tipoUsuario;
	
	public Usuario() {		
	}
	
	public Usuario(int idUsuario, String tipoUsuario) {
		this.idUsuario = idUsuario;
		this.tipoUsuario = tipoUsuario;
	}

	public int getIdUsuario() {
		return idUsuario;
	}
	
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	
	public String getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

}
