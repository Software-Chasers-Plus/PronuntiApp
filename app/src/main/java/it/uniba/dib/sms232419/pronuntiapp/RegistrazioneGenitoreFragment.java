package it.uniba.dib.sms232419.pronuntiapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrazioneGenitoreFragment extends Fragment{

    private FirebaseAuth auth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText signUpNome, signUpCognome, signUpEmail, signUpCodiceFiscale, signUpPassword;

    private Button signUpButton;

    private AccessoActivity mActivity;

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
        return inflater.inflate(R.layout.registrazione_genitore_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        signUpNome = view.findViewById(R.id.registrazione_genitore_nome);
        signUpCognome = view.findViewById(R.id.registrazione_genitore_cognome);
        signUpEmail = view.findViewById(R.id.registrazione_genitore_email);
        signUpCodiceFiscale = view.findViewById(R.id.registrazione_genitore_codiceFiscale);
        signUpPassword = view.findViewById(R.id.registrazione_genitore_password);
        signUpButton = view.findViewById(R.id.regisrazione_genitore_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signUpEmail.getText().toString().trim();
                String password = signUpPassword.getText().toString().trim();

                if(email.isEmpty()){
                    signUpEmail.setError(R.string.inserisci_la_tua_email + "");
                }

                if(password.isEmpty()){
                    signUpPassword.setError(R.string.inserisci_la_tua_password + "");
                }else{
                    // Inserisco l'utente nel database di autenticazione di firebase
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // Registrazione riuscita, aggiorna il profilo utente con nome e cognome
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(signUpNome.getText() + " " + signUpCognome.getText())
                                        .build();
                                auth.getCurrentUser().updateProfile(profileUpdates);

                                //memorizzazione del genitore nel database
                                Map<String, Object> utente = new HashMap<>();
                                utente.put("Nome", signUpNome.getText().toString());
                                utente.put("Cognome", signUpCognome.getText().toString());
                                utente.put("Email", signUpEmail.getText().toString());
                                utente.put("CodiceFiscale", signUpCodiceFiscale.getText().toString());
                                db.collection("genitori").document(auth.getCurrentUser().getUid())
                                        .set(utente).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(mActivity, R.string.registrazione_avvenuta_con_successo, Toast.LENGTH_SHORT).show();
                                                    mActivity.getSupportFragmentManager().beginTransaction()
                                                            .setReorderingAllowed(true)
                                                            .replace(R.id.login_signup_fragment, new LoginFragment(), null)
                                                            .commit();
                                                }else{
                                                    Toast.makeText(mActivity, R.string.registrazione_fallita, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }else{
                                Toast.makeText(mActivity, R.string.registrazione_fallita, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
