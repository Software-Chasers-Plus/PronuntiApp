package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Scheda implements Parcelable {
    private String nome;
    private String logopedista;
    private String bambino;
    private int numeroEsercizi;
    private String stato;
    private int eserciziCompletati;
    private ArrayList<ArrayList<String>> esercizi;

    public Scheda(String nome, String logopedista, String bambino, ArrayList<ArrayList<String>> esercizi) {
        this.nome = nome;
        this.logopedista = logopedista;
        this.bambino = bambino;
        this.esercizi = esercizi;

        //Numero eseercizi
        this.numeroEsercizi = esercizi.size();

        //Stato
        boolean completata = true;
        int eserciziCompletati = 0;
        for (ArrayList<String> esercizio : esercizi) {
            if (esercizio.get(1).equals("non completato")) {
                completata = false;
            }
            else {
                eserciziCompletati++;
            }
        }

        this.eserciziCompletati = eserciziCompletati;

        if (completata) {
            this.stato = "completata";
        } else {
            this.stato = "non completata";
        }
    }

    protected Scheda(Parcel in) {
        nome = in.readString();
        logopedista = in.readString();
        bambino = in.readString();
        numeroEsercizi = in.readInt();
        stato = in.readString();
        eserciziCompletati = in.readInt();
        esercizi = in.readArrayList(null);
    }

    public static final Creator<Scheda> CREATOR = new Creator<Scheda>() {
        @Override
        public Scheda createFromParcel(Parcel in) {
            return new Scheda(in);
        }

        @Override
        public Scheda[] newArray(int size) {
            return new Scheda[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(logopedista);
        dest.writeString(bambino);
        dest.writeInt(numeroEsercizi);
        dest.writeString(stato);
        dest.writeInt(eserciziCompletati);
        dest.writeList(esercizi);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getNome() {
        return nome;
    }

    public int getNumeroEsercizi() {
        return numeroEsercizi;
    }

    public String getStato() {
        return stato;
    }

    public int getEserciziCompletati() {
        return eserciziCompletati;
    }

    public Object getLogopedista() {
        return logopedista;
    }
}