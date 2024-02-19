package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Prenotazione implements Parcelable {
    private String data;
    private String ora;
    private String logopedista;
    private String genitore;

    private String prenotazioneId;

    private String note;

    private boolean conferma;





    public Prenotazione(String prenotazioneId,String data, String ora, String logopedista, String genitore, String note)
    {
        this.prenotazioneId=prenotazioneId;
        this.ora=ora;
        this.data=data;
        this.logopedista=logopedista;
        this.genitore=genitore;
        this.note=note;
        conferma=false;
    }

    public String getPrenotazioneId() {
        return prenotazioneId;
    }

    public void setPrenotazioneId(String prenotazioneId) {
        this.prenotazioneId = prenotazioneId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLogopedista() {
        return logopedista;
    }

    public void setLogopedista(String logopedista) {
        this.logopedista = logopedista;
    }

    public String getGenitore() {
        return genitore;
    }

    public void setGenitore(String genitore) {
        this.genitore = genitore;
    }

    public boolean isConferma() {
        return conferma;
    }

    public void setConferma(boolean conferma) {
        this.conferma = conferma;
    }

    public String toString()
    {
        return "Logopedista: "+getLogopedista()+"\nData: "+getData()+"\nOra: "+getOra();
    }

    protected Prenotazione(Parcel in) {
        prenotazioneId=in.readString();
        ora = in.readString();
        data = in.readString();
        logopedista = in.readString();
        genitore = in.readString();
        note = in.readString();
        conferma = in.readByte() != 0;
    }

    public static final Creator<Prenotazione> CREATOR = new Creator<Prenotazione>() {
        @Override
        public Prenotazione createFromParcel(Parcel in) {
            return new Prenotazione(in);
        }

        @Override
        public Prenotazione[] newArray(int size) {
            return new Prenotazione[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(prenotazioneId);
        dest.writeString(ora);
        dest.writeString(data);
        dest.writeString(logopedista);
        dest.writeString(genitore);
        dest.writeString(note);
        dest.writeByte((byte) (conferma ? 1 : 0));
    }
}
