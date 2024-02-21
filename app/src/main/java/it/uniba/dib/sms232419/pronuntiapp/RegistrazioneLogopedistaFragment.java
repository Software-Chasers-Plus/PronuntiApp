package it.uniba.dib.sms232419.pronuntiapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RegistrazioneLogopedistaFragment extends Fragment {

    private FirebaseAuth auth;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // signUpAbilitazione serve per impostare il campo a false, una volta che il logopedista invia la
    // richiesta tramite email, se il team conferma i suo certicati, allora tale campo viene impostato
    // a true direttamente da Firebase
    private Boolean signUpAbilitazione, passwordVisible;

    private Button signUpButton;

    private EditText nome, cognome, email, password, ripetiPassword, matricola;

    private ImageView imageClockPassword, imageClockRipetiPassword;

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
        return inflater.inflate(R.layout.registrazione_logopedista_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nome = view.findViewById(R.id.registrazione_logopedista_nome);
        TextInputLayout nomeLayout = view.findViewById(R.id.textInputLayoutNome);
        //gestisce il cambiamento della label una volta che l'EditText ha il focus
        nome.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // L'EditText ha ottenuto il focus
                nomeLayout.setHint(R.string.name);
            }else{
                // L'EditText ha perso il focus
                if(nome.getText().toString().trim().isEmpty()){
                    nomeLayout.setHint(R.string.inserisci_il_tuo_nome);
                }
            }
        });

        cognome = view.findViewById(R.id.registrazione_logopedista_cognome);
        TextInputLayout cognomeLayout = view.findViewById(R.id.textInputLayoutCognome);
        //gestisce il cambiamento della label una volta che l'EditText ha il focus
        cognome.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        // L'EditText ha ottenuto il focus
                        cognomeLayout.setHint(R.string.surname);
                    }else{
                        // L'EditText ha perso il focus
                        if(cognome.getText().toString().trim().isEmpty()){
                            cognomeLayout.setHint(R.string.inserisci_il_tuo_cognome);
                        }
                    }
                });

        email = view.findViewById(R.id.registrazione_logopedista_email);
        TextInputLayout emailLayout = view.findViewById(R.id.textInputLayoutEmail);
        //gestisce il cambiamento della label una volta che l'EditText ha il focus
        email.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // L'EditText ha ottenuto il focus
                emailLayout.setHint(R.string.email);
            }else{
                // L'EditText ha perso il focus
                if(email.getText().toString().trim().isEmpty()){
                    emailLayout.setHint(R.string.inserisci_la_tua_email);
                }
            }
        });

        password = view.findViewById(R.id.registrazione_logopedista_password);
        TextInputLayout passwordLayout = view.findViewById(R.id.textInputLayoutPassword);
        //gestisce il cambiamento della label una volta che l'EditText ha il focus
        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // L'EditText ha ottenuto il focus
                passwordLayout.setHint(R.string.password);
            }else{
                // L'EditText ha perso il focus
                if(password.getText().toString().trim().isEmpty()){
                    passwordLayout.setHint(R.string.inserisci_la_tua_password);
                }
            }
        });

        ripetiPassword = view.findViewById(R.id.registrazione_logopedista_ripeti_password);
        TextInputLayout ripetiPasswordLayout = view.findViewById(R.id.textInputLayoutRipetiPassword);
        //gestisce il cambiamento della label una volta che l'EditText ha il focus
        ripetiPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // L'EditText ha ottenuto il focus
                ripetiPasswordLayout.setHint(R.string.password);
            }else{
                // L'EditText ha perso il focus
                if(ripetiPassword.getText().toString().trim().isEmpty()){
                    ripetiPasswordLayout.setHint(R.string.ripeti_la_tua_password);
                }
            }
        });

        matricola = view.findViewById(R.id.registrazione_logopedista_matricola);
        TextInputLayout matricolaLayout = view.findViewById(R.id.textInputLayoutMatricola);
        //gestisce il cambiamento della label una volta che l'EditText ha il focus
        matricola.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // L'EditText ha ottenuto il focus
                matricolaLayout.setHint(R.string.matricola);
            }else{
                // L'EditText ha perso il focus
                if(matricola.getText().toString().trim().isEmpty()){
                    matricolaLayout.setHint(R.string.inserisci_la_tua_matricola);
                }
            }
        });
        signUpButton = view.findViewById(R.id.regisrazione_logopedista_button);
        signUpAbilitazione = false;
        passwordVisible = false;

        imageClockPassword = view.findViewById(R.id.imageClockPasswordSignupLogopedista);
        imageClockPassword.setOnClickListener(v -> togglePasswordVisibility(password, imageClockPassword));

        imageClockRipetiPassword = view.findViewById(R.id.imageClockRipetiPasswordSignupLogopedista);
        imageClockRipetiPassword.setOnClickListener(v -> togglePasswordVisibility(ripetiPassword, imageClockRipetiPassword));

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeText = nome.getText().toString().trim();
                String cognomeText = cognome.getText().toString().trim();
                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString().trim();
                String ripetiPasswordText = ripetiPassword.getText().toString().trim();
                String matricolaText = matricola.getText().toString().trim();

                if(checkInformazioni(nomeText, cognomeText, emailText, passwordText, ripetiPasswordText, matricolaText) && verificaConnessioneInternet()){
                    signUp(nomeText, cognomeText, emailText, passwordText, matricolaText);
                }
            }
        });

        // Listener per il passaggio al fragment di accesso
        view.findViewById(R.id.scegli_accesso).setOnClickListener(v -> {
            mActivity.getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.login_signup_fragment, new LoginFragment(), null)
                    .addToBackStack(null)
                    .commit();
        });
    }

    //metodo per la visibilità della password
    private void togglePasswordVisibility(EditText loginPassword, ImageView imageClockPassword) {
        if (!passwordVisible) {
            // Password non visibile
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            passwordVisible = true;

            //Posizionamento del cursore alla fine della stringa
            loginPassword.setSelection(loginPassword.getText().length());

            //aggiornamento dell'imagine per la visibilità della password
            imageClockPassword.setImageResource(R.drawable.visibility);
        } else {
            // Password visibile
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordVisible = false;

            //Posizionamento del cursore alla fine della stringa
            loginPassword.setSelection(loginPassword.getText().length());

            //aggiornamento dell'imagine per la visibilità della password
            imageClockPassword.setImageResource(R.drawable.invisible);
        }
    }

    //metodo per verificare la validità delle informazioni inserite
    private Boolean checkInformazioni(String nome, String cognome, String email, String password, String ripetiPassword, String matricola) {
        Boolean check = true;
        if(nome.isEmpty()){
            this.nome.setError(getString(R.string.inserisci_il_tuo_nome));
            this.nome.requestFocus();
            check = false;
        }
        if(cognome.isEmpty()){
            this.cognome.setError(getString(R.string.inserisci_il_tuo_cognome));
            this.cognome.requestFocus();
            check =  false;
        }
        if(email.isEmpty()){
            this.email.setError(getString(R.string.inserisci_la_tua_email));
            this.email.requestFocus();
            check = false;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            this.email.setError(getString(R.string.inserisci_una_email_valida));
            this.email.requestFocus();
            check = false;
        }

        if(password.isEmpty()){
            this.password.setError(getString(R.string.inserisci_la_tua_password));
            this.password.requestFocus();
            check = false;
        }else if(password.length() < 6){
            this.password.setError(getString(R.string.la_tua_password_deve_contenere_almeno_6_caratteri));
            this.password.requestFocus();
            check = false;
        } else if (!password.equals(ripetiPassword)) {
            this.ripetiPassword.setError(getString(R.string.le_password_non_corrispondono));
            this.ripetiPassword.requestFocus();
            check = false;
        }
        //TODO: migliorare il controllo della password

        if(matricola.isEmpty()){
            this.matricola.setError(getString(R.string.inserisci_la_tua_matricola));
            this.matricola.requestFocus();
            check = false;
        }else if(matricola.length() < 5){
            this.matricola.setError("La matricola deve avere 5 caratteri");
            this.matricola.requestFocus();
            check = false;
        }
        //TODO: controllare se la matricola è valida
        return check;
    }

    //metodo per la registrazione del logopedista
    private void signUp(String nome, String cognome, String email, String password, String matricola){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Registrazione riuscita, aggiorna il profilo utente con nome e cognome
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nome + " " + cognome)
                            .build();
                    auth.getCurrentUser().updateProfile(profileUpdates);

                    // prendo la data di oggi
                    Calendar calendar = Calendar.getInstance();
                    String dataRegistrazione = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);

                    //memorizzazione del genitore nel database
                    Map<String, Object> utente = new HashMap<>();
                    utente.put("Nome", nome);
                    utente.put("Cognome", cognome);
                    utente.put("Email", email);
                    utente.put("Matricola", matricola);
                    utente.put("Abilitazione", signUpAbilitazione);
                    utente.put("DataRegistrazione", dataRegistrazione);
                    db.collection("logopedisti").document(auth.getCurrentUser().getUid())
                            .set(utente).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // Creare un nuovo pop up per comincare al logopedista che
                                        // la sua richiesta è stata presa in carico
                                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mActivity);

                                        LayoutInflater inflater = mActivity.getLayoutInflater();
                                        View view = inflater.inflate(R.layout.popup_registrazione_logopedista, null);
                                        builder.setView(view);
                                        AlertDialog dialog = builder.create();

                                        // Impostare un listener per il bottone "Accedi" del pop up
                                        //quando verrà cliccato il logopedista verrà portato alla schermata di accesso
                                        view.findViewById(R.id.bottone_accedi_popup_registrazione_logopedista).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                mActivity.getSupportFragmentManager().beginTransaction()
                                                        .setReorderingAllowed(true)
                                                        .replace(R.id.login_signup_fragment, new LoginFragment(), null)
                                                        .commit();
                                            }
                                        });

                                        // Impostare un listener per quando il dialogo viene cancellato
                                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                mActivity.getSupportFragmentManager().beginTransaction()
                                                        .setReorderingAllowed(true)
                                                        .replace(R.id.login_signup_fragment, new LoginFragment(), null)
                                                        .commit();
                                            }
                                        });

                                        dialog.show();
                                    }else{
                                        Toasty.error(mActivity, R.string.registrazione_fallita, Toast.LENGTH_SHORT, true).show();
                                    }
                                }
                            });
                }else{
                    Toasty.error(mActivity, R.string.registrazione_fallita, Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }
    private Boolean verificaConnessioneInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mActivity);

            builder.setTitle("Nessuna connessione ad internet");
            builder.setMessage(R.string.necessara_una_connessione_ad_internet_registrazione);
            builder.setIcon(R.drawable.baseline_error_outline_24);

            // Aggiungere pulsanti positivi e negativi
            builder.setPositiveButton(R.string.attiva_wi_fi, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            builder.setNeutralButton(R.string.attiva_dati_mobili, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                    if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
            return false;

        }
    }
}
