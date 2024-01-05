package it.uniba.dib.sms232419.pronuntiapp.model;

public class Genitore {
    private String nome;

    private String cognome;

    private String email;

    private String CodiceFiscale;

    private String UID;

    public Genitore(String nome, String cognome, String email, String CodiceFiscale, String UID) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.CodiceFiscale = CodiceFiscale;
        this.UID = UID;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getCodiceFiscale() {
        return CodiceFiscale;
    }

    public String getUID() {
        return UID;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCodiceFiscale(String CodiceFiscale) {
        this.CodiceFiscale = CodiceFiscale;
    }
}
