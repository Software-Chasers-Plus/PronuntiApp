package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.storage.FirebaseStorage;

public class Esercizio implements Parcelable {
    private String esercizioId;
    private String nome;
    private String logopedista;
    private String tipologia;

    public Esercizio(String esercizioId, String nome, String logopedista, String tipologia) {
        this.esercizioId = esercizioId;
        this.nome = nome;
        this.logopedista = logopedista;
        this.tipologia = tipologia;
    }

    public Esercizio() {
    }

    protected Esercizio(Parcel in) {
        esercizioId = in.readString();
        nome = in.readString();
        logopedista = in.readString();
        tipologia = in.readString();
    }

    public static final Creator<Esercizio> CREATOR = new Creator<Esercizio>() {
        @Override
        public Esercizio createFromParcel(Parcel in) {
            return new Esercizio(in);
        }

        @Override
        public Esercizio[] newArray(int size) {
            return new Esercizio[size];
        }
    };

    public String getNome() {
        return nome;
    }

    public String getLogopedista() {
        return logopedista;
    }

    public String getTipologia() {
        return tipologia;
    }

    public String getEsercizioId() {
        return esercizioId;
    }

    public void eliminaFileDaStorage() {}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(esercizioId);
        dest.writeString(nome);
        dest.writeString(logopedista);
        dest.writeString(tipologia);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
