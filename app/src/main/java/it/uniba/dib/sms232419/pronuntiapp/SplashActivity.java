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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;



public class SplashActivity extends AppCompatActivity {
    private boolean loggato = false;
    private boolean fecthCompletato = false;
    private List<Figlio> figli = new ArrayList<>();

    private static final long MIN_WAIT_INTERVAL = 1500L;
    private static final long MAX_WAIT_INTERVAL = 3000L;
    private static final int GO_AHEAD_WHAT = 1;
    private static final int FECTH_TERMINATO = 2;
    private long mStartTime;
    private boolean mIsDone;
    private boolean genitore = false;
    private boolean logopedista = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_AHEAD_WHAT:
                    long elapsedTime = SystemClock.uptimeMillis() -
                            mStartTime;
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
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        fetchDataFromdataBase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStartTime = SystemClock.uptimeMillis();
        final Message goAheadMessage =
                mHandler.obtainMessage(GO_AHEAD_WHAT);
        mHandler.sendMessageAtTime(goAheadMessage, mStartTime +
                MAX_WAIT_INTERVAL);
    }


    private void fetchDataFromdataBase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //verifico se l'utente è già loggato
        if (auth.getCurrentUser() != null) {
            //lancio la query per verificare se l'utente è un genitore
            db.collection("genitori")
                    .document(auth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    genitore = true;
                                    //se è un genitore recupero recuperaro i suoi figli

                                    //lancio la query per recuperare i figli del genitore
                                    db.collection("figli")
                                            .whereEqualTo("genitore", auth.getCurrentUser().getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        //se i figli vengono trovati li passo all'activity principale e la faccio partire
                                                        Log.d("Accessoactivity", "Figli trovati con query");
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Map<String, Object> nuovoFiglio = document.getData();
                                                            figli.add(new Figlio(nuovoFiglio.get("nome").toString(), nuovoFiglio.get("cognome").toString(),
                                                                    nuovoFiglio.get("codiceFiscale").toString(), nuovoFiglio.get("logopedista").toString(),
                                                                    FirebaseAuth.getInstance().getCurrentUser().getUid(), nuovoFiglio.get("dataNascita").toString()));
                                                        }
                                                        loggato = true;
                                                        fecthCompletato = true;
                                                        final Message goAheadMessage =
                                                                mHandler.obtainMessage(FECTH_TERMINATO);
                                                        mHandler.sendMessage(goAheadMessage);
                                                    }
                                                }
                                            });
                                } else {
                                    //l'utente è un logopedista
                                    loggato = true;
                                    fecthCompletato = true;
                                    final Message goAheadMessage = mHandler.obtainMessage(FECTH_TERMINATO);
                                    mHandler.sendMessage(goAheadMessage);
                                }
                            } else {
                                //TODO: gestire il caso in cui la query per verificare che è un genitore non va a buon fine
                            }
                        }
                    });

            //lancio la query per verificare se l'utente è un logopedista
            db.collection("logopedisti")
                    .document(auth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    logopedista = true;
                                    loggato = true;
                                    fecthCompletato = true;
                                    final Message goAheadMessage =
                                            mHandler.obtainMessage(FECTH_TERMINATO);
                                    mHandler.sendMessage(goAheadMessage);
                                } else {

                                }
                            } else {
                                //TODO: gestire il caso in cui la query per verificare che è un genitore non va a buon fine
                            }
                        }
                    });
        } else {
            loggato = false;
            fecthCompletato = true;
        }
    }

    private void startMainActivity() {
        if (loggato == true && genitore == true) {
            //creo il bundle da passare all'activity principale
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) figli);

            Log.d("Accessoactivity", "Figli grandezza" + figli.size());
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(bundle);

            //faccio partire l'activity principale
            startActivity(intent);
            finish();
        } else if (loggato == true && logopedista == true) {

            Intent intent = new Intent(this, MainActivityLogopedista.class);

            //faccio partire l'activity principale
            startActivity(intent);
            finish();
        } else {
            startActivity(new Intent(this, AccessoActivity.class));
            finish();
        }
    }
}