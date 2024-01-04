package it.uniba.dib.sms232419.pronuntiapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginFragment extends Fragment {
    private accessoActivity mActivity;

    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;

    private Button loginButton;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (accessoActivity) context;
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
                        .replace(R.id.login_signup_fragment, new sceltaRegistrazioneFragment(), null)
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
                            //se il login è andato a buon fine faccio partire l'activity principale
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(mActivity, "Login effettuato", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(mActivity, MainActivity.class));
                                mActivity.finish();
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
