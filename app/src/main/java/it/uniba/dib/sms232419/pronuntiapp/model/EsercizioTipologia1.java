package it.uniba.dib.sms232419.pronuntiapp.model;

public class EsercizioTipologia1 extends Esercizio{
    private String immagine;
    private String descrizione_immagine;
    private String audio1;
    private String audio2;
    private String audio3;

    public EsercizioTipologia1(String nome, String logopedista, String tipologia, String immagine, String descrizione_immagine, String audio1, String audio2, String audio3) {
        super(nome, logopedista, tipologia);
        this.immagine = immagine;
        this.descrizione_immagine = descrizione_immagine;
        this.audio1 = audio1;
        this.audio2 = audio2;
        this.audio3 = audio3;
    }
}
