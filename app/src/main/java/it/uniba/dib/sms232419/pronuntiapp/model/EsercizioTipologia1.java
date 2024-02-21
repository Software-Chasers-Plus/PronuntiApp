package it.uniba.dib.sms232419.pronuntiapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class EsercizioTipologia1 extends Esercizio implements Parcelable {
    private final String immagine;
    private final String descrizione_immagine;
    private final String audio1;
    private final String audio2;
    private final String audio3;

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
    public void eliminaFileDaStorage() {
        // Eliminazione file immagine e audio
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference refImg = storage.getReference().child(immagine);
        refImg.delete();

        if(!audio1.equals("null")){
            StorageReference refAudio1 = storage.getReference().child(audio1);
            refAudio1.delete();
        }

        if(!audio2.equals("null")){
            StorageReference refAudio2 = storage.getReference().child(audio2);
            refAudio2.delete();
        }

        if(!audio3.equals("null")){
            StorageReference refAudio3 = storage.getReference().child(audio3);
            refAudio3.delete();
        }
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

    public boolean correzioneEsercizio(String risposta) {
        int distance = LevenshteinDistance.getDefaultInstance().apply(descrizione_immagine, risposta);
        double similarity = 1 - ((double) distance / Math.max(descrizione_immagine.length(), risposta.length()));

        return similarity > 0.8;
    }
}
