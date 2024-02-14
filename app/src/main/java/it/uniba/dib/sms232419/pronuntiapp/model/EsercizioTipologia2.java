package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;

public class EsercizioTipologia2 extends Esercizio {
    private String audio;
    private String trasctrizione_audio;

    public EsercizioTipologia2(String esercizioId, String nome, String logopedista, String tipologia, String audio, String trasctrizione_audio) {
        super(esercizioId, nome, logopedista, tipologia);
        this.audio = audio;
        this.trasctrizione_audio = trasctrizione_audio;
    }

    protected EsercizioTipologia2(Parcel in) {
        super(in);
        audio = in.readString();
        trasctrizione_audio = in.readString();
    }

    public static final Creator<EsercizioTipologia2> CREATOR = new Creator<EsercizioTipologia2>() {
        @Override
        public EsercizioTipologia2 createFromParcel(Parcel in) {
            return new EsercizioTipologia2(in);
        }

        @Override
        public EsercizioTipologia2[] newArray(int size) {
            return new EsercizioTipologia2[size];
        }
    };

    public String getAudio() {
        return audio;
    }

    public String getTrasctrizione_audio() {
        return trasctrizione_audio;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(audio);
        dest.writeString(trasctrizione_audio);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTrascrizione_audio() {
        return trasctrizione_audio;
    }
}
