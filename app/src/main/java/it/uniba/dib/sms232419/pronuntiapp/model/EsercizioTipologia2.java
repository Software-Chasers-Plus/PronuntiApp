package it.uniba.dib.sms232419.pronuntiapp.model;

public class EsercizioTipologia2 extends Esercizio{
    private String audio;
    private String trasctrizione_audio;

    public EsercizioTipologia2(String nome, String logopedista, String tipologia, String audio, String trasctrizione_audio) {
        super(nome, logopedista, tipologia);
        this.audio = audio;
        this.trasctrizione_audio = trasctrizione_audio;
    }
}
