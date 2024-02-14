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

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class EsercizioDenominazioneImmagine extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_CODE_PICK_AUDIO_BUTTON1 = 31;
    private static final int REQUEST_CODE_PICK_AUDIO_BUTTON2 = 32;
    private static final int REQUEST_CODE_PICK_AUDIO_BUTTON3 = 33;

    Uri imageUri;
    Uri audioUri1;
    Uri audioUri2;
    Uri audioUri3;

    String ID_immagine;
    String ID_audio1;
    String ID_audio2;
    String ID_audio3;

    String descrizione_immagine;

    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.denominazione_immagini, container, false);

        // Inizializza le view
        imageView = view.findViewById(R.id.image_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageView imageView = view.findViewById(R.id.image_view);
        ImageButton upload_audio1_button = view.findViewById(R.id.upload_audio1_button);
        ImageButton upload_audio2_button = view.findViewById(R.id.upload_audio2_button);
        ImageButton upload_audio3_button = view.findViewById(R.id.upload_audio3_button);
        EditText editText = view.findViewById(R.id.edit_text_esercizio1);
        Button conferma_button = view.findViewById(R.id.crea_esercizio_button);
        TextInputLayout nome_esercizio_textView = view.findViewById(R.id.TextFieldNomeEsercizio1);

        imageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Se il permesso non è stato concesso, richiedilo all'utente
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                // Se il permesso è già stato concesso, puoi procedere con la logica per selezionare un'immagine
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Quando l'EditText perde il focus, controlla se è vuoto e ripristina il suggerimento
                if (editText.getText().toString().isEmpty()) {
                    editText.setHint("Contenuto dell'immagine"); // Imposta il suggerimento desiderato
                }
            } else {
                // Quando l'EditText ottiene il focus, rimuovi il suggerimento
                editText.setHint("");
            }
        });

        //Gestione bottone audio 1
        upload_audio1_button.setOnClickListener(v -> {
            // Crea un Intent per selezionare un file audio
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*"); // Specifica che stai cercando file audio
            startActivityForResult(Intent.createChooser(intent, "Seleziona un file audio"), REQUEST_CODE_PICK_AUDIO_BUTTON1);
        });

        //Gestione bottone audio 2
        upload_audio2_button.setOnClickListener(v -> {
            // Crea un Intent per selezionare un file audio
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*"); // Specifica che stai cercando file audio
            startActivityForResult(Intent.createChooser(intent, "Seleziona un file audio"), REQUEST_CODE_PICK_AUDIO_BUTTON2);
        });

        //Gestione bottone audio 3
        upload_audio3_button.setOnClickListener(v -> {
            // Crea un Intent per selezionare un file audio
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*"); // Specifica che stai cercando file audio
            startActivityForResult(Intent.createChooser(intent, "Seleziona un file audio"), REQUEST_CODE_PICK_AUDIO_BUTTON3);
        });

        //Gestione bottone conferma
        conferma_button.setOnClickListener(v -> {

            final boolean[] esito = {true};

            // Ottieni il testo dell'immagine
            descrizione_immagine = editText.getText().toString();

            // Ottieni il nome dell'esercizio
            String nome_esercizio = nome_esercizio_textView.getEditText().getText().toString();

            //Creazione dei percorsi per Firebase Storage
            String path_img = "esercizio1/" + nome_esercizio + descrizione_immagine + ".jpg";
            String path_audio1 = "esercizio1/" + nome_esercizio + "_audio1.mp3";
            String path_audio2 = "esercizio1/" + nome_esercizio + "_audio2.mp3";
            String path_audio3 = "esercizio1/" + nome_esercizio + "_audio3.mp3";

            // Carica l'immagine su Firebase Storage
            uploadFileToFirebaseStorage(imageUri, path_img, (success, id_img) -> {
                if (success) {
                    ID_immagine = id_img;
                }else {
                    esito[0] = false;
                }
            });

            // Carica l'audio 1 su Firebase Storage
            uploadFileToFirebaseStorage(audioUri1, path_audio1, (success, id_audio1) -> {
                if (success) {
                    ID_audio1 = id_audio1;
                }else {
                    esito[0] = false;
                }
            });

            // Carica l'audio 2 su Firebase Storage
            uploadFileToFirebaseStorage(audioUri2, path_audio2, (success, id_audio2) -> {
                if (success) {
                    ID_audio2 = id_audio2;
                }else {
                    esito[0] = false;
                }
            });

            // Carica l'audio 3 su Firebase Storage
            uploadFileToFirebaseStorage(audioUri3, path_audio3, (success, id_audio3) -> {
                if (success) {
                    ID_audio3 = id_audio3;
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
                    Log.d("EsercizioDenominazioneImmagine", "ID dell'utente attualmente loggato: " + userId);
                } else {
                    Log.d("EsercizioDenominazioneImmagine", "Nessun utente attualmente loggato");
                }

                // Crea un oggetto Map per contenere i dati da inserire nel documento
                Map<String, Object> data = new HashMap<>();
                data.put("tipologia", 1);
                data.put("logopedista", userId);
                data.put("nome", nome_esercizio);
                data.put("immagine", path_img);
                data.put("descrizioneImmagine", descrizione_immagine);
                data.put("audio1", path_audio1);
                data.put("audio2", path_audio2);
                data.put("audio3", path_audio3);

                // Aggiungi i dati a una nuova raccolta con un ID generato automaticamente
                db.collection("esercizi")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("EsercizioDenominazioneImmagine", "DocumentSnapshot aggiunto con ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.w("EsercizioDenominazioneImmagine", "Errore durante l'aggiunta del documento", e);
                        });

            } else {
                // Se c'è stato un errore nel caricare i file, mostra un messaggio di errore
                Toast.makeText(getContext(), "Errore nel creare l'esercizio", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Metodo per gestire il risultato della selezione dell'immagine
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // Ottieni l'URI dell'immagine selezionata
                imageUri = data.getData();
                if (imageUri != null) {
                    // Imposta l'immagine nell'ImageView
                    imageView.setImageURI(imageUri);
                }
            } else if ((requestCode == REQUEST_CODE_PICK_AUDIO_BUTTON1 || requestCode == REQUEST_CODE_PICK_AUDIO_BUTTON2 || requestCode == REQUEST_CODE_PICK_AUDIO_BUTTON3) && data != null) {
                // Ottieni l'URI del file audio selezionato
                Uri audioUri = data.getData();
                if (audioUri != null) {
                    switch (requestCode) {
                        case REQUEST_CODE_PICK_AUDIO_BUTTON1:
                            // Ottieni il riferimento all'EditText per il testo dell'audio 1
                            TextView testo_audio1 = requireView().findViewById(R.id.audio1_testo);
                            String testo_corrente1 = testo_audio1.getText().toString();
                            // Esegui la logica per caricare l'audio e l'immagine
                            testo_audio1.setText(testo_corrente1 + " " + getAudioFileNameFromUri(getActivity(), audioUri));
                            audioUri1 = audioUri;
                            break;
                        case REQUEST_CODE_PICK_AUDIO_BUTTON2:
                            // Ottieni il riferimento all'EditText per il testo dell'audio 2
                            TextView testo_audio2 = requireView().findViewById(R.id.audio2_testo);
                            String testo_corrente2 = testo_audio2.getText().toString();
                            // Esegui la logica per caricare l'audio e l'immagine
                            testo_audio2.setText(testo_corrente2 +  " "  +getAudioFileNameFromUri(getActivity(), audioUri));
                            audioUri2 = audioUri;
                            break;
                        case REQUEST_CODE_PICK_AUDIO_BUTTON3:
                            // Ottieni il riferimento all'EditText per il testo dell'audio 3
                            TextView testo_audio3 = requireView().findViewById(R.id.audio3_testo);
                            String testo_corrente3 = testo_audio3.getText().toString();
                            // Esegui la logica per caricare l'audio e l'immagine
                            testo_audio3.setText(testo_corrente3 +  " " + getAudioFileNameFromUri(getActivity(), audioUri));
                            audioUri3 = audioUri;
                            break;
                    }
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
    private void uploadFileToFirebaseStorage(Uri fileUri, String path, OnUploadCompleteListener callback) {
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
                                Log.d("EsercizioDenominazioneImmagine", "File caricato con successo: " + downloadUri);
                                progressDialog.dismiss();
                                callback.onUploadComplete(true, downloadUri); // Notifica il chiamante che il caricamento è completato con successo
                            })
                            .addOnFailureListener(e -> {
                                // Gestisci l'errore
                                Log.e("EsercizioDenominazioneImmagine", "Errore nel caricare il file: " + e.getMessage());
                                progressDialog.dismiss();
                                callback.onUploadComplete(false, null); // Notifica il chiamante che si è verificato un errore durante il caricamento
                            });
                })
                .addOnFailureListener(e -> {
                    // Gestisci l'errore
                    Log.e("EsercizioDenominazioneImmagine", "Errore nel caricare il file: " + e.getMessage());
                    progressDialog.dismiss();
                    callback.onUploadComplete(false, null); // Notifica il chiamante che si è verificato un errore durante il caricamento
                });
    }

    // Interfaccia per il callback quando il caricamento è completato
    interface OnUploadCompleteListener {
        void onUploadComplete(boolean success, String id_immagine);
    }

}
