package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class AggiungiFiglioFragment extends Fragment implements ImageAdapter.OnImageSelectedListener {

    private List<Figlio> figli;
    private MainActivityGenitore mActivity;
    private int selectedImageId = -1;

    // Array contenente gli ID delle risorse delle immagini
    private final Integer[] images = {R.drawable.bambino_1, R.drawable.bambino_2, R.drawable.bambino_3, R.drawable.bambino_4, R.drawable.bambino_5, R.drawable.bambino_6};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivityGenitore) getActivity();

        // Recupero i figli dal bundle passato al fragment
        if (getArguments() != null) {
            figli = getArguments().getParcelableArrayList("figli");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aggiungi_figli_fragment, container, false);

        // Ottieni l'activity contenitore
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Ottieni l'istanza della BottomNavigationView dall'activity
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);

        // Nascondi la BottomNavigationView impostando la visibilità a GONE
        bottomNavigationView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText nomeFiglio = view.findViewById(R.id.nome_figlio);
        EditText cognomeFiglio = view.findViewById(R.id.cognome_figlio);
        EditText codiceFiscaleFiglio = view.findViewById(R.id.codiFiscale_figlio);
        EditText dataNascitaFiglio = view.findViewById(R.id.data_nascita_figlio);

        RecyclerView imageRecyclerView = view.findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        ImageAdapter imageAdapter = new ImageAdapter(images);
        imageAdapter.setOnImageSelectedListener(this); // Imposta il listener
        imageRecyclerView.setAdapter(imageAdapter);

        EditText editTextDate = view.findViewById(R.id.data_nascita_figlio);
        ImageView iconaCalendario = view.findViewById(R.id.imageViewCalendar);
        iconaCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Seleziona una data");
                MaterialDatePicker<Long> materialDatePicker = builder.build();
                materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                    // Converte la data selezionata in un oggetto Calendar
                    Calendar calendarioSelezionato = Calendar.getInstance();
                    calendarioSelezionato.setTimeInMillis(selection);

                    int anno = calendarioSelezionato.get(Calendar.YEAR);
                    int mese = calendarioSelezionato.get(Calendar.MONTH);
                    int giorno = calendarioSelezionato.get(Calendar.DAY_OF_MONTH);

                    // La data selezionata dall'utente
                    String dataSelezionata = giorno + "/" + (mese + 1) + "/" + anno;
                    dataNascitaFiglio.setText(dataSelezionata);
                });
                materialDatePicker.show(requireFragmentManager(), "DATE_PICKER");
            }
        });

        Button confermaAggiungiFiglio = view.findViewById(R.id.conferma_aggiungi_figlio_button);
        confermaAggiungiFiglio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeFiglio.getText().toString().trim();
                String cognome = cognomeFiglio.getText().toString().trim();
                String codiceFiscale = codiceFiscaleFiglio.getText().toString().trim();
                String dataNascita = dataNascitaFiglio.getText().toString().trim();

                if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || dataNascita.isEmpty() || selectedImageId == -1) {
                    Toast.makeText(getContext(), "Inserisci tutti i dati e seleziona un'immagine", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Genera un token univoco a partire dal codice fiscale per ogni figlio
                String token = generateTokenFromString(codiceFiscale);

                // Salvataggio del figlio nel database
                String genitoreUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> figlio = new HashMap<>();
                figlio.put("nome", nome);
                figlio.put("cognome", cognome);
                figlio.put("codiceFiscale", codiceFiscale);
                figlio.put("dataNascita", dataNascita);
                figlio.put("genitore", genitoreUid);
                figlio.put("logopedista", "");
                figlio.put("idAvatar", selectedImageId);
                figlio.put("token", token);
                db.collection("figli")
                        .add(figlio)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    figli.add(new Figlio(nome, cognome, codiceFiscale, "", genitoreUid, dataNascita, selectedImageId, token));
                                    Toast.makeText(mActivity, "Figlio aggiunto con successo", Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                                    navController.navigate(R.id.navigation_home);
                                } else {
                                    Toast.makeText(mActivity, "Registrazione fallita", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Ottieni l'activity contenitore
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Ottieni l'istanza della BottomNavigationView dall'activity
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);

        // Riporta la BottomNavigationView visibile
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    // Metodo dell'interfaccia per la gestione della selezione dell'immagine
    @Override
    public void onImageSelected(int imageId) {
        selectedImageId = imageId - 1;
    }

    // Genera un token univoco a partire dal codice fiscale per ogni figlio
    public static String generateTokenFromString(String codiceFiscale) {
        // Converti la stringa univoca in un array di byte
        byte[] bytes = codiceFiscale.getBytes();

        // Genera un UUID basato sulla stringa univoca
        UUID uuid = UUID.nameUUIDFromBytes(bytes);

        // Converte l'UUID in una stringa
        String token = uuid.toString();

        // Rimuovi eventuali trattini dalla stringa generata
        token = token.replaceAll("-", "");

        return token;
    }
}
