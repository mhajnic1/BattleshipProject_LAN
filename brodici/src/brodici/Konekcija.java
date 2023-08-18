package brodici;

import java.sql.Connection;
import java.sql.DriverManager;

public class Konekcija {

	private Connection con;
	private String driver;
	private String url;
	private String user;
	private String password;

	public Konekcija(String driver, String url, String user, String password) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		try {
			Class.forName(driver);
			this.con = DriverManager.getConnection(url, user, password);

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void disconnect() {
		try {
			this.con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public Connection getCon() {
		return con;
	}

	public Connection connect(Connection con) {

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);

		} catch (Exception e) {
			System.out.println(e);
		}
		return con;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
