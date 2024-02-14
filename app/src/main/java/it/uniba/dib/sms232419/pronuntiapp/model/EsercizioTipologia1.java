package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;

public class EsercizioTipologia1 extends Esercizio {
    private String immagine;
    private String descrizione_immagine;
    private String audio1;
    private String audio2;
    private String audio3;

    public EsercizioTipologia1(String esercizioId, String nome, String logopedista, String tipologia, String immagine, String descrizione_immagine, String audio1, String audio2, String audio3) {
        super(esercizioId, nome, logopedista, tipologia);
        this.immagine = immagine;
        this.descrizione_immagine = descrizione_immagine;
        this.audio1 = audio1;
        this.audio2 = audio2;
        this.audio3 = audio3;
    }

    protected EsercizioTipologia1(Parcel in) {
        super(in);
        immagine = in.readString();
        descrizione_immagine = in.readString();
        audio1 = in.readString();
        audio2 = in.readString();
        audio3 = in.readString();
    }

    public static final Creator<EsercizioTipologia1> CREATOR = new Creator<EsercizioTipologia1>() {
        @Override
        public EsercizioTipologia1 createFromParcel(Parcel in) {
            return new EsercizioTipologia1(in);
        }

        @Override
        public EsercizioTipologia1[] newArray(int size) {
            return new EsercizioTipologia1[size];
        }
    };

    public String getImmagine() {
        return immagine;
    }

    public String getDescrizione_immagine() {
        return descrizione_immagine;
    }

    public String getAudio1() {
        return audio1;
    }

    public String getAudio2() {
        return audio2;
    }

    public String getAudio3() {
        return audio3;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(immagine);
        dest.writeString(descrizione_immagine);
        dest.writeString(audio1);
        dest.writeString(audio2);
        dest.writeString(audio3);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
