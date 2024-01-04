package it.uniba.dib.sms232419.pronuntiapp.model;

public class Figlio {
    private String nome;
    private String logopedista;

    public Figlio(String nome, String logopedista) {
        this.nome = nome;
        this.logopedista = logopedista;
    }

    public String getNome() {
        return nome;
    }

    public String getLogopedista() {
        return logopedista;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLogopedista(String logopedista) {
        this.logopedista = logopedista;
    }
}
