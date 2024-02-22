package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Figlio implements Parcelable {
    private String Uid;
    private final String nome;
    private final String cognome;
    private final String codiceFiscale;
    private final String dataNascita;
    private final String logopedista;
    private final String emailGenitore;
    private final int idAvatar;
    private final String token;
    private long punteggioGioco;

    public Figlio(String nome, String cognome, String codiceFiscale, String dataNascita, String logopedista, String emailGenitore, int idAvatar, String token, long punteggioGioco) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.dataNascita = dataNascita;
        this.logopedista = logopedista;
        this.emailGenitore = emailGenitore;
        this.idAvatar = idAvatar;
        this.token = token;
        this.punteggioGioco = punteggioGioco;
    }

    protected Figlio(Parcel in) {
        nome = in.readString();
        cognome = in.readString();
        codiceFiscale = in.readString();
        dataNascita = in.readString();
        logopedista = in.readString();
        emailGenitore = in.readString();
        idAvatar = in.readInt();
        token = in.readString();
        punteggioGioco = in.readLong();
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

    public String getDataNascita() {
        return dataNascita;
    }

    public String getLogopedista() {
        return logopedista;
    }

    public String getEmailGenitore() {
        return emailGenitore;
    }

    public int getIdAvatar() {
        return idAvatar;
    }

    public String getToken() {
        return token;
    }

    public long getPunteggioGioco() {
        return punteggioGioco;
    }

    public void setPunteggioGioco(long punteggioGioco) {
        this.punteggioGioco += punteggioGioco;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(cognome);
        dest.writeString(codiceFiscale);
        dest.writeString(dataNascita);
        dest.writeString(logopedista);
        dest.writeString(emailGenitore);
        dest.writeInt(idAvatar);
        dest.writeString(token);
        dest.writeLong(punteggioGioco);
    }
}
