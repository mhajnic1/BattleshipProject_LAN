package brodici;

public class Igra {

	private Korisnik gubitnik;
	private Korisnik pobjednik;
	private Integer pokusajigubitnik;
	private Integer pokusajipobjednik;
	private Integer trajanje;
	private Long id_igre;

	public Long getId_igre() {
		return id_igre;
	}

	public void setId_igre(Long id_igre) {
		this.id_igre = id_igre;
	}

	@Override
	public String toString() {
		return "Igra [gubitnik=[" + gubitnik.toString() + "], pobjednik=[" + pobjednik.toString()
				+ "], pokusajigubitnik=" + pokusajigubitnik + ", pokusajipobjednik=" + pokusajipobjednik + ", trajanje="
				+ trajanje + ", id_igre=" + id_igre + "]";
	}

	public Igra() {
	};

	public Igra(Long id, Korisnik gubitnik, Korisnik igrac2, Korisnik pobjednik, Integer pokusajigubitnik,
			Integer pokusajipobjednik, Integer trajanje) {
		this.gubitnik = gubitnik;
		this.pobjednik = pobjednik;
		this.pokusajigubitnik = pokusajigubitnik;
		this.pokusajipobjednik = pokusajipobjednik;
		this.trajanje = trajanje;
		this.id_igre = id;
	}

	public Igra(Korisnik gubitnik, Korisnik pobjednik, Integer pokusajigubitnik, Integer pokusajipobjednik,
			Integer trajanje) {
		this.gubitnik = gubitnik;
		this.pobjednik = pobjednik;
		this.pokusajigubitnik = pokusajigubitnik;
		this.pokusajipobjednik = pokusajipobjednik;
		this.trajanje = trajanje;

	}

	public Korisnik getgubitnik() {
		return this.gubitnik;
	}

	public void setgubitnik(Korisnik gubitnik) {
		this.gubitnik = gubitnik;
	}

	public Korisnik getPobjednik() {
		return pobjednik;
	}

	public void setPobjednik(Korisnik pobjednik) {
		this.pobjednik = pobjednik;
	}

	public Integer getpokusajigubitnik() {
		return pokusajigubitnik;
	}

	public void setpokusajigubitnik(Integer pokusajigubitnik) {
		this.pokusajigubitnik = pokusajigubitnik;
	}

	public Integer getpokusajipobjednik() {
		return pokusajipobjednik;
	}

	public void setpokusajipobjednik(Integer pokusajipobjednik) {
		this.pokusajipobjednik = pokusajipobjednik;
	}

	public Integer getTrajanje() {
		return trajanje;
	}

	public void setTrajanje(Integer trajanje) {
		this.trajanje = trajanje;
	}

}
