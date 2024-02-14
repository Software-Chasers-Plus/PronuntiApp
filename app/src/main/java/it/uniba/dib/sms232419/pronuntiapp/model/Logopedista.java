package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Logopedista implements Parcelable {

    private String nome;

    private String cognome;

    private String email;

    private String matricola;

    private Boolean abilitazione;

    private String UID;

    public Logopedista(String nome, String cognome, String email, String matricola, Boolean abilitazione, String UID) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.matricola = matricola;
        this.abilitazione = abilitazione;
        this.UID = UID;
    }

    protected Logopedista(Parcel in) {
        nome = in.readString();
        cognome = in.readString();
        email = in.readString();
        matricola = in.readString();
        byte tmpAbilitazione = in.readByte();
        abilitazione = tmpAbilitazione == 0 ? null : tmpAbilitazione == 1;
        UID = in.readString();
    }

    public static final Creator<Logopedista> CREATOR = new Creator<Logopedista>() {
        @Override
        public Logopedista createFromParcel(Parcel in) {
            return new Logopedista(in);
        }

        @Override
        public Logopedista[] newArray(int size) {
            return new Logopedista[size];
        }
    };

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getAbilitazione(){return abilitazione;}

    public String getUID() {
        return UID;
    }

    private void setNome(String nome) {
        this.nome = nome;
    }

    private void setCognome(String cognome) {
        this.cognome = cognome;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(nome);
        parcel.writeString(cognome);
        parcel.writeString(email);
        parcel.writeString(matricola);
        parcel.writeByte((byte) (abilitazione ? 1 : 0));
        parcel.writeString(UID);
    }
}