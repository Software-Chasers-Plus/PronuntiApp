package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Figlio implements Parcelable {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String dataNascita;
    private String Logopedista;
    private String emailGenitore;
    private int idAvatar;
    private String token;

    private long punteggioGioco;

    public Figlio(String nome, String cognome, String codiceFiscale, String Logopedista, String emailGenitore, String dataNascita, int idAvatar, String token, long punteggioGioco) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.Logopedista = Logopedista;
        this.emailGenitore = emailGenitore;
        this.dataNascita = dataNascita;
        this.idAvatar = idAvatar;
        this.token = token;
        this.punteggioGioco = punteggioGioco;
    }

    protected Figlio(Parcel in) {
        nome = in.readString();
        cognome = in.readString();
        codiceFiscale = in.readString();
        Logopedista = in.readString();
        emailGenitore = in.readString();
        dataNascita = in.readString();
        idAvatar = in.readInt();
        token = in.readString();
    }

    public static final Creator<Figlio> CREATOR = new Creator<Figlio>() {
        @Override
        public Figlio createFromParcel(Parcel in) {
            return new Figlio(in);
        }

        @Override
        public Figlio[] newArray(int size) {
            return new Figlio[size];
        }
    };

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getLogopedista() {
        return Logopedista;
    }

    public String getEmailGenitore() {
        return emailGenitore;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public int getIdAvatar() {
        return idAvatar;
    }

    public String getToken() {return token;}

    public long getPunteggioGioco() {return punteggioGioco;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(cognome);
        parcel.writeString(codiceFiscale);
        parcel.writeString(Logopedista);
        parcel.writeString(emailGenitore);
        parcel.writeString(dataNascita);
        parcel.writeInt(idAvatar);
        parcel.writeString(token);
        parcel.writeLong(punteggioGioco);
    }
}
