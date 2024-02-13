package it.uniba.dib.sms232419.pronuntiapp.model;

public class Esercizio{
    private String nome;
    private String logopedista;
    private String tipologia;

    public Esercizio(String nome, String logopedista, String tipologia) {
        this.nome = nome;
        this.logopedista = logopedista;
        this.tipologia = tipologia;
    }

    public String getNome() {
        return nome;
    }

    public String getLogopedista() {
        return logopedista;
    }

    public String getTipologia() {
        return tipologia;
    }
}
