package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class EsercizioTipologia3 extends Esercizio implements Parcelable{
    private String audio;
    private String immagine1;
    private String immagine2;
    private long immagineCorretta;

    public EsercizioTipologia3(String esercizioId, String nome, String logopedista, String tipologia, String audio, String immagine1, String immagine2, long immagineCorretta) {
        super(esercizioId, nome, logopedista, tipologia);
        this.audio = audio;
        this.immagine1 = immagine1;
        this.immagine2 = immagine2;
        this.immagineCorretta = immagineCorretta;
    }

    protected EsercizioTipologia3(Parcel in) {
        super(in);
        audio = in.readString();
        immagine1 = in.readString();
        immagine2 = in.readString();
        immagineCorretta = in.readLong();
    }

    public static final Creator<EsercizioTipologia3> CREATOR = new Creator<EsercizioTipologia3>() {
        @Override
        public EsercizioTipologia3 createFromParcel(Parcel in) {
            return new EsercizioTipologia3(in);
        }

        @Override
        public EsercizioTipologia3[] newArray(int size) {
            return new EsercizioTipologia3[size];
        }
    };

    public String getAudio() {
        return audio;
    }

    public String getImmagine1() {
        return immagine1;
    }

    public String getImmagine2() {
        return immagine2;
    }

    public long getImmagineCorretta() {
        return immagineCorretta;
    }

    @Override
    public void eliminaFileDaStorage() {
        // Eliminazione file immagine e audio
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference refImg1 = storage.getReference().child(immagine1);
        refImg1.delete();

        StorageReference refImg2 = storage.getReference().child(immagine2);
        refImg2.delete();

        StorageReference refAudio = storage.getReference().child(audio);
        refAudio.delete();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(audio);
        dest.writeString(immagine1);
        dest.writeString(immagine2);
        dest.writeLong(immagineCorretta);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean correzioneEsercizio(int risposta) {
        return risposta == immagineCorretta;
    }
}

