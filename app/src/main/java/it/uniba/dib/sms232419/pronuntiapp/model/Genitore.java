package it.uniba.dib.sms232419.pronuntiapp.model;

public class Genitore {
    private String nome;

    private String cognome;

    private String email;

    private String UID;

    public Genitore(String nome, String cognome, String email, String UID) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
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
}
