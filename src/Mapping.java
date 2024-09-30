package org.myspringframework.util;
public class Mapping {
     String nomClasse;
     String nomMethode;

    public Mapping(String nomClasse, String nomMethode) {
        this.nomClasse = nomClasse;
        this.nomMethode = nomMethode;
    }

    public String getNomClasse() {
        return nomClasse;
    }

    public void setNomClasse(String nomClasse) {
        this.nomClasse = nomClasse;
    }

    public String getNomMethode() {
        return nomMethode;
    }

    public void setNomMethode(String nomMethode) {
        this.nomMethode = nomMethode;
    }
}
