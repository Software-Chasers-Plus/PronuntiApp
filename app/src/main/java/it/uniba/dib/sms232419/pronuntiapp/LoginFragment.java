package it.uniba.dib.sms232419.pronuntiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class LoginFragment extends Fragment {
    private AccessoActivity mActivity;

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button loginButton;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AccessoActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        //imposto sul bottone di registrazione il listener per passare al fragment di registrazione
        view.findViewById(R.id.scegli_registrazione).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true) // Imposta che le transazioni di frammenti possono essere riordinate  migliorare le animazioni
                        .replace(R.id.login_signup_fragment, new SceltaRegistrazioneFragment(), null)
                        .addToBackStack(null)//aggiuge il fragment al back stack del fragment manager e permette agli utenti di navigare all'indietro
                        .commit();
            }
        });

        loginEmail = view.findViewById(R.id.login_email);
        loginPassword = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                //controllo che l'email non sia vuota e che sia valida
                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //controllo che la password non sia vuota
                    if(!password.isEmpty()){
                        //faccio il login con email e password
                        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(mActivity, new OnSuccessListener<AuthResult>() {
                            //se il login va a buon fine
                            @Override
                            public void onSuccess(AuthResult authResult) {
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
                                                        //se è un genitore recupero recuperaro i suoi figli
                                                        List<Figlio> figli = new ArrayList<>();

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

                                                                            //creo il bundle da passare all'activity principale
                                                                            Bundle bundle = new Bundle();
                                                                            bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) figli);

                                                                            Log.d("Accessoactivity", "Figli grandezza" + figli.size());
                                                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                                                            intent.putExtras(bundle);

                                                                            //faccio partire l'activity principale
                                                                            startActivity(intent);
                                                                            mActivity.finish();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    //TODO: gestire il caso in cui la query per verificare che è un genitore non va a buon fine
                                                }
                                            }
                                        });

                                db.collection("logopedisti")
                                        .document(auth.getCurrentUser().getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        // Verifica se il campo "Abilitazione" è impostato a true
                                                        boolean abilitazione = document.getBoolean("Abilitazione");
                                                        if (abilitazione) {
                                                            // Il logopedista è abilitato, puoi eseguire le azioni necessarie
                                                            Intent intent = new Intent(getContext(), MainActivityLogopedista.class);

                                                            //faccio partire l'activity principale
                                                            startActivity(intent);
                                                            mActivity.finish();

                                                        } else {
                                                            // Il logopedista non è stato abilitato
                                                            Toast.makeText(mActivity, "Non sei stato ancora abilitato come locopedista", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                } else {
                                                    // TODO: Gestire il caso in cui la query per verificare l'abilitazione non va a buon fine
                                                }
                                            }
                                        });

                            }
                        }).addOnFailureListener(mActivity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mActivity, "Login fallito", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        loginPassword.setError("Inserisci la tua password");
                        loginPassword.requestFocus();
                    }
                }else if(email.isEmpty()){
                    loginEmail.setError("Inserisci la tua email");
                    loginEmail.requestFocus();
                }else{
                    loginEmail.setError("Inserisci una email valida");
                    loginEmail.requestFocus();
                }
            }
        });


    }
}
