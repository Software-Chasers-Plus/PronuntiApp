package it.uniba.dib.sms232419.pronuntiapp.model;

public class EsercizioTipologia3 extends Esercizio{
    private String audio;
    private String immagine1;
    private String immagine2;
    private int immagineCorretta;


    public EsercizioTipologia3(String nome, String logopedista, String tipologia, String audio, String immagine1, String immagine2, int immagineCorretta) {
        super(nome, logopedista, tipologia);
        this.audio = audio;
        this.immagine1 = immagine1;
        this.immagine2 = immagine2;
        this.immagineCorretta = immagineCorretta;
    }
}
