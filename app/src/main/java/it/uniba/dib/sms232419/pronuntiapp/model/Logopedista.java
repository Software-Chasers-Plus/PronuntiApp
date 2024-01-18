package it.uniba.dib.sms232419.pronuntiapp.model;

public class Logopedista {

    private String nome;

    private String cognome;

    private String email;

    private String CodiceFiscale;

    private Boolean abilitazione;

    private String UID;

    public Logopedista(String nome, String cognome, String email, String CodiceFiscale, Boolean abilitazione, String UID) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.CodiceFiscale = CodiceFiscale;
        this.abilitazione = abilitazione;
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

    public Boolean getAbilitazione(){return abilitazione;}

    public String getUID() {
        return UID;
    }

    private void setNome(String nome) {
        this.nome = nome;
    }

    private void setCognome(String cognome) {
        this.cognome = cognome;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    private void setCodiceFiscale(String CodiceFiscale) {
        this.CodiceFiscale = CodiceFiscale;
    }
}