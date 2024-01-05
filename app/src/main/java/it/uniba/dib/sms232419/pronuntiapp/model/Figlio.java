package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class Figlio implements Parcelable {
    private String nome;
    private String cognome;
    private String codiceFiscale;

    private Date dataNascita;
    private String emailLogopedista;

    private String emailGenitore;

    public Figlio(String nome, String cognome, String codiceFiscale, String emailLogopedista, String emailGenitore, String dataNascita) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.emailLogopedista = emailLogopedista;
        this.emailGenitore = emailGenitore;
        this.dataNascita = new Date(dataNascita);
    }

    public Date getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

    protected Figlio(Parcel in) {
        nome = in.readString();
        cognome = in.readString();
        codiceFiscale = in.readString();
        emailLogopedista = in.readString();
        emailGenitore = in.readString();
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

    public String getEmailLogopedista() {
        return emailLogopedista;
    }

    public String getEmailGenitore() {
        return emailGenitore;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public void setEmailLogopedista(String emailLogopedista) {
        this.emailLogopedista = emailLogopedista;
    }

    public void setEmailGenitore(String emailGenitore) {
        this.emailGenitore = emailGenitore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(cognome);
        parcel.writeString(codiceFiscale);
        parcel.writeString(emailLogopedista);
        parcel.writeString(emailGenitore);
    }
}
