package it.uniba.dib.sms232419.pronuntiapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nitish.typewriterview.TypeWriterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;


public class SplashActivity extends AppCompatActivity {
    private boolean loggato = false;
    private boolean fecthCompletato = false;
    private final List<Figlio> figli = new ArrayList<>();
    private final List<Prenotazione> prenotazioni=new ArrayList<>();

    private static final long MIN_WAIT_INTERVAL = 1500L;
    private static final long MAX_WAIT_INTERVAL = 6000L;
    private static final int GO_AHEAD_WHAT = 1;
    private static final int FECTH_TERMINATO = 2;
    private long mStartTime;
    private boolean mIsDone;
    private boolean genitore = false;
    private boolean logopedista = false;
    private FirebaseUser currentUser;
    private boolean mainActivityStarted = false;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_AHEAD_WHAT:
                    long elapsedTime = SystemClock.uptimeMillis() - mStartTime;
                    if ((elapsedTime >= MIN_WAIT_INTERVAL && !mIsDone) && fecthCompletato) {
                        mIsDone = true;
                        startMainActivity();
                    }
                    break;
                case FECTH_TERMINATO:
                    if (fecthCompletato) {
                        mIsDone = true;
                        startMainActivity();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Setto l'animazione
        LottieAnimationView lottieAnimationView = findViewById(R.id.animation_splash);
        lottieAnimationView.setAnimation(R.raw.animation_splash);
        lottieAnimationView.playAnimation();

        TypeWriterView typeWriterView = findViewById(R.id.text_pronuntiapp);
        typeWriterView.setCharacterDelay(70);
        typeWriterView.animateText("PronuntiApp");
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        fetchDataFromDataBase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStartTime = SystemClock.uptimeMillis();
        final Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
        mHandler.sendMessageAtTime(goAheadMessage, mStartTime + MAX_WAIT_INTERVAL);
    }

    private void fetchDataFromDataBase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            checkIfGenitore(auth);
            checkIfLogopedista(auth);
        } else {
            // Utente non loggato
            loggato = false;
            fecthCompletato = true;
            startMainActivity(); // Chiamare startMainActivity anche se non è loggato
        }
    }

    private void checkIfGenitore(final FirebaseAuth auth) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("genitori")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                genitore = true;
                                retrieveFigliForGenitore(auth);
                                retrieveAppuntamentiForGenitore(auth);

                            } else {
                                fecthCompletato = true;
                            }
                        } else {
                            // TODO: Gestire il caso in cui la query per verificare che è un genitore non va a buon fine
                        }
                    }
                });

    }

    private void retrieveFigliForGenitore(final FirebaseAuth auth) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("figli")
                .whereEqualTo("genitore", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> nuovoFiglio = document.getData();

                                if(nuovoFiglio.get("logopedista") != null){
                                    figli.add(new Figlio(
                                            nuovoFiglio.get("nome").toString(),
                                            nuovoFiglio.get("cognome").toString(),
                                            nuovoFiglio.get("codiceFiscale").toString(),
                                            nuovoFiglio.get("dataNascita").toString(),
                                            nuovoFiglio.get("logopedista").toString(),
                                            currentUser.getUid(),
                                            Integer.parseInt(nuovoFiglio.get("idAvatar").toString()),
                                            nuovoFiglio.get("token").toString(),
                                            (long)nuovoFiglio.get("punteggioGioco"),
                                            Integer.parseInt(nuovoFiglio.get("sfondoSelezionato").toString()),
                                            Integer.parseInt(nuovoFiglio.get("personaggioSelezionato").toString())
                                    ));
                                }else{
                                    figli.add(new Figlio(
                                            nuovoFiglio.get("nome").toString(),
                                            nuovoFiglio.get("cognome").toString(),
                                            nuovoFiglio.get("codiceFiscale").toString(),
                                            nuovoFiglio.get("dataNascita").toString(),
                                            "",
                                            currentUser.getUid(),
                                            Integer.parseInt(nuovoFiglio.get("idAvatar").toString()),
                                            nuovoFiglio.get("token").toString(),
                                            (long)nuovoFiglio.get("punteggioGioco"),
                                            Integer.parseInt(nuovoFiglio.get("sfondoSelezionato").toString()),
                                            Integer.parseInt(nuovoFiglio.get("personaggioSelezionato").toString())
                                    ));
                                }

                            }
                            loggato = true;
                            fecthCompletato = true;
                            mHandler.sendEmptyMessage(FECTH_TERMINATO);
                        }
                    }
                });
    }


    private void checkIfLogopedista(final FirebaseAuth auth) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("logopedisti")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Verifica che il campo "abilitazione" sia impostato a true
                                boolean isAbilitato = document.getBoolean("Abilitazione");
                                if (isAbilitato) {
                                    retrievePazientiForLogopedista(auth);
                                    retrieveAppuntamentiForLogopedista(auth);
                                } else {
                                    fecthCompletato = true;
                                }
                            } else {
                                fecthCompletato = true;
                            }
                        } else {
                            // TODO: Gestire il caso in cui la query per verificare che è un logopedista non va a buon fine
                        }
                    }
                });
    }

    private void retrievePazientiForLogopedista(final FirebaseAuth auth) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("figli")
                .whereEqualTo("logopedista", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> nuovoFiglio = document.getData();
                                // Aggiungi la conversione dell'ID avatar da String a int
                                figli.add(new Figlio(
                                        nuovoFiglio.get("nome").toString(),
                                        nuovoFiglio.get("cognome").toString(),
                                        nuovoFiglio.get("codiceFiscale").toString(),
                                        nuovoFiglio.get("dataNascita").toString(),
                                        currentUser.getUid(),
                                        nuovoFiglio.get("genitore").toString(),
                                        Integer.parseInt(nuovoFiglio.get("idAvatar").toString()), // Conversione da String a int
                                        nuovoFiglio.get("token").toString(),
                                        (long)nuovoFiglio.get("punteggioGioco"),
                                        Integer.parseInt(nuovoFiglio.get("sfondoSelezionato").toString()),
                                        Integer.parseInt(nuovoFiglio.get("personaggioSelezionato").toString())
                                ));
                            }
                            logopedista = true;
                            loggato = true;
                            fecthCompletato = true;
                            mHandler.sendEmptyMessage(FECTH_TERMINATO);
                        }
                    }
                });
    }

    private void retrieveAppuntamentiForGenitore(final FirebaseAuth auth)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("prenotazioni")
                .whereEqualTo("genitore", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> prenotazione = document.getData();

                                if(prenotazione.get("logopedista") != null){
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            prenotazione.get("logopedista").toString(),
                                            currentUser.getUid(),
                                            prenotazione.get("note").toString()
                                    ));
                                }else{
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                           "",
                                            currentUser.getUid(),
                                            prenotazione.get("note").toString()
                                    ));
                                }

                            }
                            loggato = true;
                            fecthCompletato = true;
                            mHandler.sendEmptyMessage(FECTH_TERMINATO);
                        }
                    }
                });
    }

    private void retrieveAppuntamentiForLogopedista(final FirebaseAuth auth)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("prenotazioni")
                .whereEqualTo("logopedista", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> prenotazione = document.getData();

                                if(prenotazione.get("genitore") != null){
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            currentUser.getUid(),
                                            prenotazione.get("logopedista").toString(),
                                            prenotazione.get("note").toString()
                                    ));
                                }else{
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            currentUser.getUid(),
                                            "",
                                            prenotazione.get("note").toString()
                                    ));
                                }

                            }
                            loggato = true;
                            fecthCompletato = true;
                            mHandler.sendEmptyMessage(FECTH_TERMINATO);
                        }
                    }
                });
    }


    private void startMainActivity() {
        // Se il metodo è già stato chiamato, esce per evitare chiamate multiple
        if (mainActivityStarted) {
            return;
        }

        mainActivityStarted = true;

        // Se l'utente è loggato come genitore
        if (loggato && genitore) {
            // Creazione del bundle e lancio dell'activity MainActivityGenitore
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) figli);
            bundle.putParcelableArrayList("prenotazioni",(ArrayList<? extends Parcelable>) prenotazioni);
            Intent intent = new Intent(this, MainActivityGenitore.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        // Se l'utente è loggato come logopedista
        else if (loggato && logopedista) {
            // Creazione del bundle e lancio dell'activity MainActivityLogopedista
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) figli);
            bundle.putParcelableArrayList("prenotazioni",(ArrayList<? extends Parcelable>) prenotazioni);
            Intent intent = new Intent(this, MainActivityLogopedista.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        // Se l'utente non è né genitore né logopedista
        else {
            // Lancio dell'activity AccessoActivity
            startActivity(new Intent(this, AccessoActivity.class));
            finish();
        }
    }

}
