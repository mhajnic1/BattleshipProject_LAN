package brodici;

public class InitBaze {
	public final String driver;
	public final String url;
	public final String user;
	public final String password;

	public InitBaze(String driver, String url, String user, String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
	}
}