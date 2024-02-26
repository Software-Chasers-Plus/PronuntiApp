package it.uniba.dib.sms232419.pronuntiapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import firebase.com.protolitewrapper.BuildConfig;
import it.uniba.dib.sms232419.pronuntiapp.EasterEgg.Spacewar;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;

public class LoginFragment extends Fragment {
    private AccessoActivity mActivity;
    private FirebaseAuth auth;
    private EditText loginEmail, loginPassword;

    private final Bundle bundle = new Bundle();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView imageClockPassword;

    private Button loginButton;
    private int counterSoftwareChasers = 0;

    boolean passwordVisible = false;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        loginEmail = view.findViewById(R.id.login_email);
        loginPassword = view.findViewById(R.id.login_password);
        imageClockPassword = view.findViewById(R.id.imageClockPassword);
        Button loginButton = view.findViewById(R.id.login_button);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();
            loginUser(email, password);
        });


        imageClockPassword.setOnClickListener(v -> togglePasswordVisibility());


        ImageView softwareChasers = view.findViewById(R.id.software_chasers_easter_egg);
        softwareChasers.setOnClickListener(v -> {
            counterSoftwareChasers++;
            Toasty.custom(getContext(), "Hai fatto " + counterSoftwareChasers + " click, continua!", R.drawable.goal_aim_achievement_svgrepo_com, R.color.teal_200, Toast.LENGTH_SHORT, false, true).show();
            if (counterSoftwareChasers == 5) {
                Intent intent = new Intent(getContext(), Spacewar.class);
                startActivity(intent);
                counterSoftwareChasers = 0;
            }
        });

        return view;
    }

    // Metodo per la visibilità della password
    private void togglePasswordVisibility() {

        if (!passwordVisible) {
            // Password non visibile
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            passwordVisible = true;

            // Posizionamento del cursore alla fine della stringa
            loginPassword.setSelection(loginPassword.getText().length());

            // Aggiornamento dell'imagine per la visibilità della password
            imageClockPassword.setImageResource(R.drawable.visibility);
        } else {
            // Password visibile
            loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordVisible = false;

            // Posizionamento del cursore alla fine della stringa
            loginPassword.setSelection(loginPassword.getText().length());

            // Aggiornamento dell'imagine per la visibilità della password
            imageClockPassword.setImageResource(R.drawable.invisible);
        }
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
                if (verificaConnessioneInternet()) {
                    loginUser(email, password);
                }
            }
        });

        if(it.uniba.dib.sms232419.pronuntiapp.BuildConfig.IS_GUEST){
            LinearLayout linearLayout = view.findViewById(R.id.layout_ospite);
            linearLayout.setVisibility(View.VISIBLE);
            view.findViewById(R.id.ospite_logopedista).setOnClickListener(v -> {
                auth.signInWithEmailAndPassword("logopedista@gmail.com", "testapp")
                        .addOnSuccessListener(mActivity, authResult -> {
                            checkUserType(authResult.getUser().getUid());
                        })
                        .addOnFailureListener(mActivity, e -> {

                            // Animazione per la validazione della password
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(2)
                                    .playOn(getActivity().findViewById(R.id.textInputLayoutPassword));

                            Toasty.error(mActivity, R.string.login_fallito, Toast.LENGTH_SHORT, true).show();
                        });
            });

            view.findViewById(R.id.ospite_genitore).setOnClickListener(v -> {
                auth.signInWithEmailAndPassword("genitore@gmail.com", "testapp")
                        .addOnSuccessListener(mActivity, authResult -> {
                            checkUserType(authResult.getUser().getUid());
                        })
                        .addOnFailureListener(mActivity, e -> {

                            // Animazione per la validazione della password
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(2)
                                    .playOn(getActivity().findViewById(R.id.textInputLayoutPassword));

                            Toasty.error(mActivity, R.string.login_fallito, Toast.LENGTH_SHORT, true).show();
                        });
            });

        }else{
            Log.d("LoginFragment", "GUEST MODE NON ATTIVO");
        }
    }

    // Metodo per validare l'email
    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            loginEmail.setError(getString(R.string.inserisci_la_tua_email));
            loginEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            // Animazione per la validazione della password
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(3)
                    .playOn(getActivity().findViewById(R.id.textInputLayoutEmail));

            loginEmail.setError(getString(R.string.inserisci_una_email_valida));
            loginEmail.requestFocus();
            return false;
        }
        return true;
    }

    // Metodo per validare la password
    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            loginPassword.setError(getString(R.string.inserisci_la_tua_password));
            loginPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(mActivity, authResult -> {
                    checkUserType(authResult.getUser().getUid());
                })
                .addOnFailureListener(mActivity, e -> {

                    // Animazione per la validazione della password
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(2)
                            .playOn(getActivity().findViewById(R.id.textInputLayoutPassword));

                    Toasty.error(mActivity, R.string.login_fallito, Toast.LENGTH_SHORT, true).show();
                });

    }


    // Metodo per verificare il tipo di utente
    private void checkUserType(String userId) {
        // Verifica se è un genitore
        db.collection(getString(R.string.genitori)).document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            retrieveAppuntamentiForGenitore(userId);
                            retrieveChildren(userId);
                        }
                    } else {
                        // TODO: Gestire il caso in cui la query per verificare che è un genitore non va a buon fine
                    }
                });

        // Verifica se è un logopedista
        db.collection(getString(R.string.logopedisti)).document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Verifica che il campo "abilitazione" sia impostato a true
                            boolean isAbilitato = Boolean.TRUE.equals(document.getBoolean(getString(R.string.abilitazione)));
                            if (isAbilitato) {
                                retrieveAppuntamentiForLogopedista(userId);
                                retrievePatients(userId);
                            } else {
                                Toasty.error(mActivity, R.string.non_ancora_abilitato_come_logopedista, Toast.LENGTH_SHORT).show();
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

                            if (childData.get("logopedista") != null) {
                                childrenList.add(new Figlio(
                                        childData.get("nome").toString(),
                                        childData.get("cognome").toString(),
                                        childData.get("codiceFiscale").toString(),
                                        childData.get("dataNascita").toString(),
                                        childData.get("logopedista").toString(),
                                        userId,
                                        Integer.parseInt(childData.get("idAvatar").toString()),
                                        childData.get("token").toString(),
                                        (long) childData.get("punteggioGioco"),
                                        Integer.parseInt(childData.get("sfondoSelezionato").toString()),
                                        Integer.parseInt(childData.get("personaggioSelezionato").toString())
                                ));
                            } else {
                                childrenList.add(new Figlio(
                                        childData.get("nome").toString(),
                                        childData.get("cognome").toString(),
                                        childData.get("codiceFiscale").toString(),
                                        childData.get("dataNascita").toString(),
                                        "",
                                        userId,
                                        Integer.parseInt(childData.get("idAvatar").toString()),
                                        childData.get("token").toString(),
                                        (long) childData.get("punteggioGioco"),
                                        Integer.parseInt(childData.get("sfondoSelezionato").toString()),
                                        Integer.parseInt(childData.get("personaggioSelezionato").toString())
                                ));
                            }
                        }
                        bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) childrenList);
                        Log.d("LoginFragment", "Figli recuperati: " + childrenList.size());
                        startMainActivityGenitore(MainActivityGenitore.class);

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
                                    patientData.get("dataNascita").toString(),
                                    userId,
                                    patientData.get("genitore").toString(),
                                    Integer.parseInt(patientData.get("idAvatar").toString()),
                                    patientData.get("token").toString(),
                                    (long)patientData.get("punteggioGioco"),
                                    Integer.parseInt(patientData.get("sfondoSelezionato").toString()),
                                    Integer.parseInt(patientData.get("personaggioSelezionato").toString())
                            ));
                        }
                        startMainActivityLogopedista(patientsList, MainActivityLogopedista.class);
                    }
                });
    }

    private void retrieveAppuntamentiForGenitore(String userId) {

        List<Prenotazione> prenotazioni = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("prenotazioni")
                .whereEqualTo("genitore", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> prenotazione = document.getData();


                                if (prenotazione.get("logopedista") != null) {
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            prenotazione.get("logopedista").toString(),
                                            userId,
                                            prenotazione.get("note").toString()
                                    ));
                                } else {
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            "",
                                            userId,
                                            prenotazione.get("note").toString()
                                    ));
                                }
                            }
                            bundle.putParcelableArrayList("prenotazioni", (ArrayList<? extends Parcelable>) prenotazioni);
                        }
                    }
                });
    }

    private void retrieveAppuntamentiForLogopedista(String userId) {

        List<Prenotazione> prenotazioni = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("prenotazioni")
                .whereEqualTo("logopedista", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> prenotazione = document.getData();


                                if (prenotazione.get("genitore") != null) {
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            userId,
                                            prenotazione.get("genitore").toString(),
                                            prenotazione.get("note").toString()
                                    ));
                                } else {
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            userId,
                                            "",
                                            prenotazione.get("note").toString()
                                    ));
                                }
                            }
                            bundle.putParcelableArrayList("prenotazioni", (ArrayList<? extends Parcelable>) prenotazioni);
                        }
                    }
                });
    }


    // Metodo per avviare l'activity principale
    private void startMainActivityGenitore( Class<?> activityClass) {

        Intent intent = new Intent(getContext(), activityClass);
        intent.putExtras(bundle);
        Log.d("LoginFragment","Activity STARTATA");
        startActivity(intent);
        mActivity.finish();
    }

    private void startMainActivityLogopedista(List<Figlio> dataList, Class<?> activityClass) {
        bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) dataList);

        Intent intent = new Intent(getContext(), activityClass);
        intent.putExtras(bundle);

        startActivity(intent);
        mActivity.finish();
    }

    private Boolean verificaConnessioneInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mActivity);

            builder.setTitle("Nessuna connessione ad internet");
            builder.setMessage(R.string.necessara_una_connessione_ad_internet);
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
