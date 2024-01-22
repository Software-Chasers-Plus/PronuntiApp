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

        // Listener per il passaggio al fragment di registrazione
        view.findViewById(R.id.scegli_registrazione).setOnClickListener(v -> {
            mActivity.getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.login_signup_fragment, new SceltaRegistrazioneFragment(), null)
                    .addToBackStack(null)
                    .commit();
        });

        loginEmail = view.findViewById(R.id.login_email);
        loginPassword = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString();
            String password = loginPassword.getText().toString();

            if (isValidEmail(email) && isValidPassword(password)) {
                loginUser(email, password);
            }
        });
    }

    // Metodo per validare l'email
    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            loginEmail.setError("Inserisci la tua email");
            loginEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Inserisci una email valida");
            loginEmail.requestFocus();
            return false;
        }
        return true;
    }

    // Metodo per validare la password
    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            loginPassword.setError("Inserisci la tua password");
            loginPassword.requestFocus();
            return false;
        }
        return true;
    }

    // Metodo per eseguire il login
    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(mActivity, authResult -> {
                    checkUserType(authResult.getUser().getUid());
                })
                .addOnFailureListener(mActivity, e -> {
                    Toast.makeText(mActivity, "Login fallito", Toast.LENGTH_SHORT).show();
                });
    }

    // Metodo per verificare il tipo di utente
    private void checkUserType(String userId) {
        // Verifica se è un genitore
        db.collection("genitori").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            retrieveChildren(userId);
                        }
                    } else {
                        // TODO: Gestire il caso in cui la query per verificare che è un genitore non va a buon fine
                    }
                });

        // Verifica se è un logopedista
        db.collection("logopedisti").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Verifica che il campo "abilitazione" sia impostato a true
                            boolean isAbilitato = document.getBoolean("Abilitazione");
                            if (isAbilitato) {
                                retrievePatients(userId);
                            } else {
                                Toast.makeText(mActivity, "Non sei stato ancora abilitato come logopedista", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // TODO: Gestire il caso in cui la query per verificare che è un logopedista non va a buon fine
                    }
                });

    }

    // Metodo per recuperare i figli di un genitore
    private void retrieveChildren(String userId) {
        List<Figlio> childrenList = new ArrayList<>();

        db.collection("figli").whereEqualTo("genitore", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> childData = document.getData();
                            childrenList.add(new Figlio(
                                    childData.get("nome").toString(),
                                    childData.get("cognome").toString(),
                                    childData.get("codiceFiscale").toString(),
                                    childData.get("logopedista").toString(),
                                    userId,
                                    childData.get("dataNascita").toString()
                            ));
                        }
                        startMainActivity(childrenList, MainActivityGenitore.class);
                    }
                });
    }

    // Metodo per recuperare i pazienti di un logopedista
    private void retrievePatients(String userId) {
        List<Figlio> patientsList = new ArrayList<>();

        db.collection("figli").whereEqualTo("logopedista", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> patientData = document.getData();
                            patientsList.add(new Figlio(
                                    patientData.get("nome").toString(),
                                    patientData.get("cognome").toString(),
                                    patientData.get("codiceFiscale").toString(),
                                    userId,
                                    patientData.get("genitore").toString(),
                                    patientData.get("dataNascita").toString()
                            ));
                        }
                        startMainActivity(patientsList, MainActivityLogopedista.class);
                    }
                });
    }

    // Metodo per avviare l'activity principale
    private void startMainActivity(List<Figlio> dataList, Class<?> activityClass) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) dataList);

        Intent intent = new Intent(getContext(), activityClass);
        intent.putExtras(bundle);

        startActivity(intent);
        mActivity.finish();
    }
}
