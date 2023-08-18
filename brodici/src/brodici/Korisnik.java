package brodici;

import java.sql.Timestamp;

public class Korisnik {

	private Long id_usr;
	private String ip;
	private String nickname;
	private Timestamp active;

	@Override
	public String toString() {
		return nickname.substring(0, nickname.indexOf('.'));
	}

	public Korisnik() {
	};

	public Korisnik(Long id, String ip, String nickname, Timestamp date) {
		this.id_usr = id;
		this.ip = ip;
		this.nickname = nickname;
		this.active = date;
	}
	// new Timestamp(date.getTime());

	public Long getId() {
		return id_usr;
	}

	public void setId(Long id) {
		this.id_usr = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Timestamp getActive() {
		return active;
	}

	public void setActive() {
		this.active = new Timestamp(System.currentTimeMillis());
	}

}
