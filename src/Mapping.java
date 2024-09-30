package util;

public class Mapping {
    private String nomClasse;
    private String nomMethode;

    public Mapping(String nomClasse, String nomMethode) {
        this.nomClasse = nomClasse;
        this.nomMethode = nomMethode;
    }

    public String getNomClasse() {
        return nomClasse;
    }

    public String getNomMethode() {
        return nomMethode;
    }
}
