package brodici;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Baza extends Konekcija {

	private Connection con;

	public Baza(InitBaze init) {
		super(init.driver, init.url, init.user, init.password);
		this.con = super.getCon();
		createUser();
	}

	public void inGame(String string) {
		try {
			Statement stm = con.createStatement();
			String ipString = getMyIp();
			stm.executeQuery("update korisnici set inGame = '" + string + "' where ip ='" + ipString + "'");
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getMyIp() {
		try {
			InetAddress ip;

			ip = InetAddress.getLocalHost();
			String ipString = ip.toString();
			return ipString.substring(ipString.lastIndexOf("/") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}

	}

	public Korisnik dohvatiKorisnika(String ip) {
		Korisnik k = null;
		try {

			Statement stm = con.createStatement();
			String help = "select * from korisnici where ip='" + ip + "'";
			ResultSet rs = stm.executeQuery(help);

			while (rs.next()) {
				k = new Korisnik(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4));
			}
			rs.close();
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
			k = null;
		}
		System.out.println(k);
		return k;

	}

	public String getMyId() {
		String ip = getMyIp();
		String p = null;
		try {
			Statement stm = con.createStatement();
			ResultSet rs;

			rs = stm.executeQuery("select id_usr from korisnici where ip ='" + ip + "'");
			while (rs.next()) {
				p = rs.getString(1);
			}

			rs.close();
			stm.close();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void createUser() {

		try {
			Statement stm = con.createStatement();
			ResultSet rs;
			String ipString = getMyIp();
			rs = stm.executeQuery("select * from korisnici where ip ='" + ipString + "'");

			if (!rs.next()) {
				stm.executeQuery(
						"insert into korisnici (id_usr, nickname, ip,active)\r\n" + " values (korisnik_seq.nextval, '"
								+ System.getProperty("user.name") + "', '" + ipString + "',sysdate)");
			}
			rs.close();
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setActive() {
		try {
			Statement stm = con.createStatement();
			String ipString = getMyIp();
			stm.executeQuery("update korisnici set active=sysdate where ip ='" + ipString + "'");
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<Korisnik> dohvatiKorisnike() {

		List<Korisnik> korisnici = new ArrayList<Korisnik>();

		try {

			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery("select * from korisnici");

			while (rs.next()) {

				Korisnik k = new Korisnik(rs.getLong(1), rs.getString(2), rs.getString(3),
						(rs.getTimestamp(4) != null ? rs.getTimestamp(4) : null));

				korisnici.add(k);
			}
			rs.close();
			stm.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return korisnici;
	}

	public ArrayList<Korisnik> onlineKorisnici() {

		ArrayList<Korisnik> korisnici = new ArrayList<Korisnik>();

		try {

			Statement stm = con.createStatement();
			String p = System.getProperty("user.name");

			ResultSet rs = stm.executeQuery(

					"select * from korisnici where (sysdate-0.000115)<active and nickname != '" + p + "' and ingame='0'");

			while (rs.next()) {

				Korisnik k = new Korisnik(rs.getLong(1), rs.getString(2), rs.getString(3),
						(rs.getTimestamp(4) != null ? rs.getTimestamp(4) : null));

				korisnici.add(k);
			}
			rs.close();
			stm.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return korisnici;
	}

	public String dohvatiProtivnika(String nickname) {
		String k = "";
		try {
			Statement stm = con.createStatement();
			String help = "select ip from korisnici where nickname =" + nickname;

			ResultSet rs = stm.executeQuery(help);
			while (rs.next()) {

				k = new String(rs.getString(1));

			}
			rs.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return k;
	}

	public void updateKorisnikTimestamp(Korisnik korisnik) {

		try {
			String sql;
			sql = "update korisnici set active = ? where id_usr = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			pstmt.setLong(2, korisnik.getId());
			pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

	}

	public void updateKorisnikNickname(Korisnik korisnik) {

		try {
			String sql;
			sql = "update korisnici set nickname = ? where id_usr = ?";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, korisnik.getNickname());
			pstmt.setLong(2, korisnik.getId());
			pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			System.out.println(e);
		}

	}

	public void insertKorisnik(Korisnik korisnik) {
		try {

			String sql;
			sql = "insert into korisnici(ip, nickname, active) values (?,?,?)";

			PreparedStatement stm = con.prepareStatement(sql);

			stm.setString(1, korisnik.getIp());
			stm.setString(2, korisnik.getNickname());
			stm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			stm.execute();

			stm.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public List<Igra> dohvatiIgre() {
		ResultSet rs;
		List<Igra> igre = new ArrayList<Igra>();
		List<Korisnik> korisnici = dohvatiKorisnike();
		try {

			Statement stm = con.createStatement();

			rs = stm.executeQuery("select id_igre, gubitnik, pobjednik, pokusaji1, pokusaji2, trajanje from igre");

			while (rs.next()) {
				Igra igra = new Igra();

				for (Korisnik k : korisnici) {
					if (k.getId() == rs.getLong(2)) {
						igra.setgubitnik(k);
					}

					if (k.getId() == rs.getLong(3)) {

						igra.setPobjednik(k);
					}
				}
				igra.setId_igre(rs.getLong(1));
				igra.setpokusajigubitnik(rs.getInt(4));
				igra.setpokusajipobjednik(rs.getInt(5));
				igra.setTrajanje(rs.getInt(6));
				igre.add(igra);

			}
			rs.close();
			stm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return igre;
	}

	public void insertIgra(Igra igra) {
		PreparedStatement stm;
		try {
			String sql;
			sql = "insert into igre (gubitnik, pobjednik, pokusajigubitnik, pokusajipobjednik, trajanje,id_igre) values (?,?,?,?,?,igre_seq.nextval)";
			stm = con.prepareStatement(sql);
			stm.setLong(1, igra.getgubitnik().getId());
			stm.setLong(2, igra.getPobjednik().getId());
			stm.setInt(3, igra.getpokusajigubitnik());
			stm.setInt(4, igra.getpokusajipobjednik());
			stm.setInt(5, igra.getTrajanje());
			stm.execute();

			stm.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public ArrayList<String> highScore() {

		ArrayList<String> korisnici = new ArrayList<String>();

		try {
			Statement stm = con.createStatement();
			ResultSet rs;
			rs = stm.executeQuery(
					"select nickname, avg(pokusajipobjednik)from igre join korisnici on id_usr=pobjednik group by nickname order by avg(pokusajipobjednik)");
			;
			while (rs.next()) {
				String k = new String(rs.getString(1) + " " + rs.getInt(2));
				korisnici.add(k);
			}
			rs.close();
		} catch (Exception e) {
		}

		return korisnici;

	}

}
