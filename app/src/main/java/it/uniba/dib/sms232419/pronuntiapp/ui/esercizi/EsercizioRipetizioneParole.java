package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.R;

public class EsercizioRipetizioneParole extends Fragment {
    private static final int REQUEST_CODE_PICK_AUDIO_BUTTON = 31;
    Uri audioUri;

    String ID_audio;

    String trascrizione_audio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ripetizione_parole, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageButton upload_audio_button = view.findViewById(R.id.upload_audio_button);
        EditText editText = view.findViewById(R.id.edit_text_esercizio2);
        Button conferma_button = view.findViewById(R.id.crea_esercizio2_button);
        TextInputLayout nome_esercizio_textView = view.findViewById(R.id.TextFieldNomeEsercizio2);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Quando l'EditText perde il focus, controlla se è vuoto e ripristina il suggerimento
                if (editText.getText().toString().isEmpty()) {
                    editText.setHint("@string/inserisci_la_sequenza_di_parole_contenuta_nell_audio"); // Imposta il suggerimento desiderato
                }
            } else {
                // Quando l'EditText ottiene il focus, rimuovi il suggerimento
                editText.setHint("");
            }
        });

        //Gestione bottone audio 1
        upload_audio_button.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*"); // Specifica che stai cercando file audio
                startActivityForResult(Intent.createChooser(intent, "Seleziona un file audio"), REQUEST_CODE_PICK_AUDIO_BUTTON);
        });


        //Gestione bottone conferma
        conferma_button.setOnClickListener(v -> {

            final boolean[] esito = {true};

            // Ottieni il testo dell'immagine
            trascrizione_audio = editText.getText().toString();

            // Ottieni il nome dell'esercizio
            String nome_esercizio = nome_esercizio_textView.getEditText().getText().toString();

            //Creazione dei percorsi per Firebase Storage
            String path_audio = "esercizio2/" + nome_esercizio + trascrizione_audio + "_audio.mp3";

            // Carica l'audio su Firebase Storage
            uploadFileToFirebaseStorage(audioUri, path_audio, (success, id_audio) -> {
                if (success) {
                    ID_audio = id_audio;
                }else {
                    esito[0] = false;
                }
            });

            if(esito[0]) {
                // Se tutti i file sono stati caricati con successo, mostra un messaggio di successo
                //Toast.makeText(getContext(), "Esercizio creato con successo", Toast.LENGTH_SHORT).show();
                //Creazione di una raccolta su firebase con i dati dell'esercizio
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();


                String userId = null;
                if (currentUser != null) {
                    userId = currentUser.getUid();
                    Log.d("EsercizioRipetizioneParole", "ID dell'utente attualmente loggato: " + userId);
                } else {
                    Log.d("EsercizioRipetizioneParole", "Nessun utente attualmente loggato");
                }

                // Crea un oggetto Map per contenere i dati da inserire nel documento
                Map<String, Object> data = new HashMap<>();
                data.put("tipologia", 2);
                data.put("logopedista", userId);
                data.put("nome", nome_esercizio);
                data.put("audio", path_audio);
                data.put("trascrizione_audio", trascrizione_audio);

                // Aggiungi i dati a una nuova raccolta con un ID generato automaticamente
                db.collection("esercizi")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("EsercizioRipetizioneParole", "DocumentSnapshot aggiunto con ID: " + documentReference.getId());

                        })
                        .addOnFailureListener(e -> {
                            Log.w("EsercizioRipetizioneParole", "Errore durante l'aggiunta del documento", e);
                        });

                //Navigazione alla lista degli esercizi
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
                navController.navigate(R.id.navigation_esercizi);
            } else {
                // Se c'è stato un errore nel caricare i file, mostra un messaggio di errore
                Toasty.error(getContext(), "Errore nel creare l'esercizio", Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    // Metodo per gestire il risultato della selezione dell'immagine
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_AUDIO_BUTTON && data != null) {
                // Ottieni l'URI del file audio selezionato
                audioUri = data.getData();
                if (audioUri != null) {
                    // Ottieni il riferimento all'EditText per il testo dell'audio 1
                    TextView testo_audio = requireView().findViewById(R.id.audio_esercizio2_testo);
                    String testo_corrente = testo_audio.getText().toString();
                    // Esegui la logica per caricare l'audio e l'immagine
                    testo_audio.setText(testo_corrente + " " + getAudioFileNameFromUri(getActivity(), audioUri));
                }
            }
        }
    }

    public String getAudioFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            ContentResolver contentResolver = context.getContentResolver();
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        fileName = cursor.getString(index);
                    }
                }
            }
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            fileName = new File(uri.getPath()).getName();
        }
        if(fileName != null) {
            return fileName;
        } else {
            return "File non trovato";
        }
    }


    // Metodo per caricare un file su Firebase Storage
    private void uploadFileToFirebaseStorage(Uri fileUri, String path, EsercizioRipetizioneParole.OnUploadCompleteListener callback) {
        // Ottieni un riferimento al Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Crea un riferimento al file su Firebase Storage
        StorageReference fileRef = storageRef.child(path);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Caricamento in corso...");
        progressDialog.setCancelable(false); // Impedisci all'utente di chiudere la finestra di dialogo
        progressDialog.show();


        // Carica il file su Firebase Storage
        if(fileUri == null) {
            progressDialog.dismiss();
            callback.onUploadComplete(true, null);
            return;
        }

        // Carica il file su Firebase Storage
        fileRef.putFile(fileUri)
                .addOnProgressListener(snapshot -> {
                    // Aggiorna la percentuale di completamento
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Caricamento in corso... " + (int) progress + "%");
                })
                .addOnSuccessListener(taskSnapshot -> {
                    // Ottieni l'URI del file caricato
                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Ottieni l'URI del file caricato
                                String downloadUri = uri.toString();
                                Log.d("EsercizioRipetizioneParole", "File caricato con successo: " + downloadUri);
                                progressDialog.dismiss();
                                callback.onUploadComplete(true, downloadUri); // Notifica il chiamante che il caricamento è completato con successo
                            })
                            .addOnFailureListener(e -> {
                                // Gestisci l'errore
                                Log.e("EsercizioRipetizioneParole", "Errore nel caricare il file: " + e.getMessage());
                                progressDialog.dismiss();
                                callback.onUploadComplete(false, null); // Notifica il chiamante che si è verificato un errore durante il caricamento
                            });
                })
                .addOnFailureListener(e -> {
                    // Gestisci l'errore
                    Log.e("EsercizioRipetizioneParole", "Errore nel caricare il file: " + e.getMessage());
                    progressDialog.dismiss();
                    callback.onUploadComplete(false, null); // Notifica il chiamante che si è verificato un errore durante il caricamento
                });
    }

    // Interfaccia per il callback quando il caricamento è completato
    interface OnUploadCompleteListener {
        void onUploadComplete(boolean success, String id_immagine);
    }
}
