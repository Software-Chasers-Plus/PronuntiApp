package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
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
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.RecordAudio;
import it.uniba.dib.sms232419.pronuntiapp.PermissionManager;

public class EsercizioDenominazioneImmagine extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_CODE_PICK_AUDIO_BUTTON1 = 31;
    private static final int REQUEST_CODE_PICK_AUDIO_BUTTON2 = 32;
    private static final int REQUEST_CODE_PICK_AUDIO_BUTTON3 = 33;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 2;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    boolean isRecording = false;
    boolean isPlaying = false;

    Uri imageUri;
    Uri audioUri1;
    Uri audioUri2;
    Uri audioUri3;

    String ID_immagine;
    String ID_audio1;
    String ID_audio2;
    String ID_audio3;

    String path_img;
    String path_audio1;
    String path_audio2;
    String path_audio3;

    String audioName1;
    String audioName2;
    String audioName3;

    String descrizione_immagine;

    String nome_esercizio;

    private ImageView imageView;

    private static int FILE_DA_CARICARE_NELLO_STORAGE = 4;
    private static int FILE_CARICATO_NELLO_STORAGE = 0;
    private static final int FETCH_TERMINATO = 1;
    private ProgressDialog dialogCaricamentoEsercizio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.denominazione_immagini, container, false);

        // Inizializza le view
        imageView = view.findViewById(R.id.image_view);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
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

        ImageButton record_audio1_button = view.findViewById(R.id.record_audio1_button);
        ImageButton play_audio1_button = view.findViewById(R.id.play_audio1_button);
        // Record to the external cache directory for visibility
        audioName1 = getActivity().getExternalCacheDir().getAbsolutePath();
        audioName1 += "/audiorecord1.mp3";

        ImageButton record_audio2_button = view.findViewById(R.id.record_audio2_button);
        ImageButton play_audio2_button = view.findViewById(R.id.play_audio2_button);
        // Record to the external cache directory for visibility
        audioName2 = getActivity().getExternalCacheDir().getAbsolutePath();
        audioName2 += "/audiorecord2.mp3";

        ImageButton record_audio3_button = view.findViewById(R.id.record_audio3_button);
        ImageButton play_audio3_button = view.findViewById(R.id.play_audio3_button);
        // Record to the external cache directory for visibility
        audioName3 = getActivity().getExternalCacheDir().getAbsolutePath();
        audioName3 += "/audiorecord3.mp3";


        /*
        imageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                // Se il permesso non è stato concesso, richiedilo all'utente
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                selectImage();
            }
        });
        */

        imageView.setOnClickListener(v -> {
            PermissionManager.requestPermissions(EsercizioDenominazioneImmagine.this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, new PermissionManager.PermissionListener() {
                @Override
                public void onPermissionsGranted() {
                    selectImage();
                }

                @Override
                public void onPermissionsDenied() {
                    // Permesso non concesso, mostra un dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Permesso negato")
                            .setMessage("Per favore, fornisci il permesso per accedere alla galleria.")
                            .setPositiveButton("Impostazioni", (dialog, which) -> {
                                // Aprire le impostazioni
                                openAppSettings();
                            })
                            .show();
                }
            });
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

        record_audio1_button.setOnClickListener(v -> {
            PermissionManager.requestPermissions(EsercizioDenominazioneImmagine.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, new PermissionManager.PermissionListener() {
                @Override
                public void onPermissionsGranted() {
                    audioUri1 = recordAudio(record_audio1_button, audioName1);
                    // Ottieni il riferimento all'EditText per il testo dell'audio 1
                    TextView testo_audio1 = requireView().findViewById(R.id.audio1_testo);
                    // Esegui la logica per caricare l'audio e l'immagine
                    testo_audio1.setText(R.string.audio1 + " " + "audioRegistrato1");
                }

                @Override
                public void onPermissionsDenied() {
                    // Permesso non concesso, mostra un dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Permesso negato")
                            .setMessage("Per favore, fornisci il permesso per registrare l'audio.")
                            .setPositiveButton("Impostazioni", (dialog, which) -> {
                                // Aprire le impostazioni
                                openAppSettings();
                            })
                            .show();
                }
            });
        });

        play_audio1_button.setOnClickListener(v -> {
            RecordAudio.onPlay(mStartPlaying , audioName1);
            if (mStartPlaying) {
                play_audio1_button.setImageResource(R.drawable.pause_icon_white_24);
            } else {
                play_audio1_button.setImageResource(R.drawable.baseline_play_arrow_24);
            }
            mStartPlaying = !mStartPlaying;
        });

        //Gestione bottone audio 2
        upload_audio2_button.setOnClickListener(v -> {
            // Crea un Intent per selezionare un file audio
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*"); // Specifica che stai cercando file audio
            startActivityForResult(Intent.createChooser(intent, "Seleziona un file audio"), REQUEST_CODE_PICK_AUDIO_BUTTON2);
        });

        record_audio2_button.setOnClickListener(v -> {
            PermissionManager.requestPermissions(EsercizioDenominazioneImmagine.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, new PermissionManager.PermissionListener() {
                @Override
                public void onPermissionsGranted() {
                    audioUri2 = recordAudio(record_audio2_button, audioName2);
                    // Ottieni il riferimento all'EditText per il testo dell'audio 1
                    TextView testo_audio2 = requireView().findViewById(R.id.audio2_testo);
                    // Esegui la logica per caricare l'audio e l'immagine
                    testo_audio2.setText(R.string.audio2 + " " + "audioRegistrato2");
                }

                @Override
                public void onPermissionsDenied() {
                    // Permesso non concesso, mostra un dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Permesso negato")
                            .setMessage("Per favore, fornisci il permesso per registrare l'audio.")
                            .setPositiveButton("Impostazioni", (dialog, which) -> {
                                // Aprire le impostazioni
                                openAppSettings();
                            })
                            .show();
                }
            });
        });

        play_audio2_button.setOnClickListener(v -> {
            RecordAudio.onPlay(mStartPlaying , audioName2);
            if (mStartPlaying) {
                play_audio2_button.setImageResource(R.drawable.pause_icon_white_24);
            } else {
                play_audio2_button.setImageResource(R.drawable.baseline_play_arrow_24);
            }
            mStartPlaying = !mStartPlaying;
        });

        //Gestione bottone audio 3
        upload_audio3_button.setOnClickListener(v -> {
            // Crea un Intent per selezionare un file audio
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*"); // Specifica che stai cercando file audio
            startActivityForResult(Intent.createChooser(intent, "Seleziona un file audio"), REQUEST_CODE_PICK_AUDIO_BUTTON3);
        });

        record_audio3_button.setOnClickListener(v -> {
            PermissionManager.requestPermissions(EsercizioDenominazioneImmagine.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, new PermissionManager.PermissionListener() {
                @Override
                public void onPermissionsGranted() {
                    audioUri3 = recordAudio(record_audio3_button, audioName3);
                    // Ottieni il riferimento all'EditText per il testo dell'audio 1
                    TextView testo_audio3 = requireView().findViewById(R.id.audio3_testo);
                    // Esegui la logica per caricare l'audio e l'immagine
                    testo_audio3.setText(R.string.audio3 + " " + "audioRegistrato3");
                }

                @Override
                public void onPermissionsDenied() {
                    // Permesso non concesso, mostra un dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Permesso negato")
                            .setMessage("Per favore, fornisci il permesso per registrare l'audio.")
                            .setPositiveButton("Impostazioni", (dialog, which) -> {
                                // Aprire le impostazioni
                                openAppSettings();
                            })
                            .show();
                }
            });
        });

        play_audio3_button.setOnClickListener(v -> {
            RecordAudio.onPlay(mStartPlaying , audioName3);
            if (mStartPlaying) {
                play_audio3_button.setImageResource(R.drawable.pause_icon_white_24);
            } else {
                play_audio3_button.setImageResource(R.drawable.baseline_play_arrow_24);
            }
            mStartPlaying = !mStartPlaying;
        });

        // Gestione bottone conferma
        conferma_button.setOnClickListener(v -> {

            final boolean[] esito = {true};

            // Ottieni il nome dell'esercizio
            nome_esercizio = nome_esercizio_textView.getEditText().getText().toString();

            if(nome_esercizio.isEmpty()) {
                nome_esercizio_textView.setError("Il nome dell'esercizio è obbligatorio");
                return;
            }

            // Ottieni il testo dell'immagine
            descrizione_immagine = editText.getText().toString();

            if(descrizione_immagine.isEmpty()) {
                editText.setError("Inserire la descrizione dell'immagine");
                return;
            }

            //Creazione dei percorsi per Firebase Storage
            path_img = "esercizio1/" + nome_esercizio + descrizione_immagine + ".jpg";
            path_audio1 = "esercizio1/" + nome_esercizio + "_audio1.mp3";
            path_audio2 = "esercizio1/" + nome_esercizio + "_audio2.mp3";
            path_audio3 = "esercizio1/" + nome_esercizio + "_audio3.mp3";

            //modifica angelo
            ArrayList<String> pathFile = new ArrayList<>();
            pathFile.add(path_img);
            pathFile.add(path_audio1);
            pathFile.add(path_audio2);
            pathFile.add(path_audio3);

            ArrayList<Uri> uriFile = new ArrayList<>();
            uriFile.add(imageUri);
            uriFile.add(audioUri1);
            uriFile.add(audioUri2);
            uriFile.add(audioUri3);

            FILE_CARICATO_NELLO_STORAGE = 0;
            FILE_DA_CARICARE_NELLO_STORAGE = 4;
            dialogCaricamentoEsercizio = new ProgressDialog(getContext());
            dialogCaricamentoEsercizio.setMessage("Caricamento in corso...");
            dialogCaricamentoEsercizio.setCancelable(false); // Impedisci all'utente di chiudere la finestra di dialogo
            dialogCaricamentoEsercizio.show();

            caricaFileSulloStorage(pathFile, uriFile);

        });

    }

    // Gestione della risposta alla richiesta di permesso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults,
                new PermissionManager.PermissionListener() {
                    @Override
                    public void onPermissionsGranted() {
                        if (requestCode == REQUEST_IMAGE_PICK) {
                            // Se il permesso è già stato concesso, puoi procedere con la logica per selezionare un'immagine
                            selectImage();
                        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {

                        }
                        Log.d("EsercizioDenominazioneImmagine", "Permissions granted");
                    }

                    @Override
                    public void onPermissionsDenied() {
                        // Permesso non concesso, mostra un dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Permesso negato")
                                .setMessage("Per favore, fornisci il permesso per accedere alla galleria.")
                                .setPositiveButton("Impostazioni", (dialog, which) -> {
                                    // Aprire le impostazioni
                                    openAppSettings();
                                })
                                .show();
                        Log.d("EsercizioDenominazioneImmagine", "Permissions denied");
                    }
                });

    }

    //Metodo per creare l'intent per selezionare un'immagine
    private void selectImage() {
        // Se il permesso è già stato concesso, puoi procedere con la logica per selezionare un'immagine
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
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
                            // Esegui la logica per caricare l'audio e l'immagine
                            testo_audio1.setText(R.string.audio1 + " " + getAudioFileNameFromUri(getActivity(), audioUri));
                            audioUri1 = audioUri;
                            break;
                        case REQUEST_CODE_PICK_AUDIO_BUTTON2:
                            // Ottieni il riferimento all'EditText per il testo dell'audio 2
                            TextView testo_audio2 = requireView().findViewById(R.id.audio2_testo);
                            // Esegui la logica per caricare l'audio e l'immagine
                            testo_audio2.setText(R.string.audio2 +  " "  +getAudioFileNameFromUri(getActivity(), audioUri));
                            audioUri2 = audioUri;
                            break;
                        case REQUEST_CODE_PICK_AUDIO_BUTTON3:
                            // Ottieni il riferimento all'EditText per il testo dell'audio 3
                            TextView testo_audio3 = requireView().findViewById(R.id.audio3_testo);
                            // Esegui la logica per caricare l'audio e l'immagine
                            testo_audio3.setText(R.string.audio3 +  " " + getAudioFileNameFromUri(getActivity(), audioUri));
                            audioUri3 = audioUri;
                            break;
                    }
                }

            }
        }
    }

    // Metodo per aprire le impostazioni dell'applicazione
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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

        // Carica il file su Firebase Storage
        if(fileUri == null) {
            callback.onUploadComplete(true, null);
            return;
        }
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Ottieni l'URI del file caricato
                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Ottieni l'URI del file caricato
                                String downloadUri = uri.toString();
                                Log.d("EsercizioDenominazioneImmagine", "File caricato con successo: " + downloadUri);
                                callback.onUploadComplete(true, downloadUri); // Notifica il chiamante che il caricamento è completato con successo
                            })
                            .addOnFailureListener(e -> {
                                // Gestisci l'errore
                                Log.e("EsercizioDenominazioneImmagine", "Errore nel caricare il file: " + e.getMessage());
                                callback.onUploadComplete(false, null); // Notifica il chiamante che si è verificato un errore durante il caricamento
                            });
                })
                .addOnFailureListener(e -> {
                    // Gestisci l'errore
                    Log.e("EsercizioDenominazioneImmagine", "Errore nel caricare il file: " + e.getMessage());
                    callback.onUploadComplete(false, null); // Notifica il chiamante che si è verificato un errore durante il caricamento
                });
    }

    // Interfaccia per il callback quando il caricamento è completato
    interface OnUploadCompleteListener {
        void onUploadComplete(boolean success, String id_immagine);
    }

    private Uri recordAudio(ImageButton record_audio_button, String audioName) {
        Uri audioUri = null;

        RecordAudio.onRecord(mStartRecording, audioName);
        if (mStartRecording) {
            record_audio_button.setImageResource(R.drawable.stop_icon_24);
            Toasty.success(getContext(), "Registrazione in corso", Toast.LENGTH_SHORT, true).show();
        } else {
            File fileAudio = new File(audioName);
            audioUri = Uri.fromFile(fileAudio);
            record_audio_button.setImageResource(R.drawable.mic_fill0_wght400_grad0_opsz24);
            Toasty.success(getContext(), "Registrazione interrotta", Toast.LENGTH_SHORT, true).show();
        }
        mStartRecording = !mStartRecording;

        return audioUri;
    }


    private void caricaFileSulloStorage(ArrayList<String> pathFile, ArrayList<Uri> uriFile){
        uploadFileToFirebaseStorage(uriFile.get(FILE_CARICATO_NELLO_STORAGE), pathFile.get(FILE_CARICATO_NELLO_STORAGE), (success, id_audio1) -> {
            double progress = (100.0 * FILE_CARICATO_NELLO_STORAGE) / FILE_CARICATO_NELLO_STORAGE;
            dialogCaricamentoEsercizio.setMessage("Caricamento in corso... " + (int) progress + "%");
            if (success) {
                ID_audio1 = id_audio1;

                // Se l'ID dell'audio 1 è nullo, imposta il percorso su null
                if (ID_audio1 == null) {
                    pathFile.add(FILE_CARICATO_NELLO_STORAGE, "null");
                }

                FILE_CARICATO_NELLO_STORAGE++;
            }else {
                FILE_CARICATO_NELLO_STORAGE++;
                pathFile.add(FILE_CARICATO_NELLO_STORAGE, "null");
            }

            if(FILE_CARICATO_NELLO_STORAGE == FILE_DA_CARICARE_NELLO_STORAGE){
                dialogCaricamentoEsercizio.dismiss();
                // Se tutti i file sono stati caricati con successo, mostra un messaggio di successo
                Toasty.success(getContext(), "Esercizio creato con successo", Toasty.LENGTH_LONG, true).show();
                // Creazione di una raccolta su firebase con i dati dell'esercizio
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
                data.put("immagine", pathFile.get(0));
                data.put("descrizioneImmagine", descrizione_immagine);
                data.put("audio1", pathFile.get(1));
                data.put("audio2", pathFile.get(2));
                data.put("audio3", pathFile.get(3));

                // Aggiungi i dati a una nuova raccolta con un ID generato automaticamente
                db.collection("esercizi")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("EsercizioDenominazioneImmagine", "DocumentSnapshot aggiunto con ID: " + documentReference.getId());
                            Toasty.success(getContext(), "Esercizio creato con successo", Toasty.LENGTH_LONG, true).show();
                            // Navigazione alla lista degli esercizi
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
                            navController.navigate(R.id.navigation_esercizi);
                        })
                        .addOnFailureListener(e -> {
                            Log.w("EsercizioDenominazioneImmagine", "Errore durante l'aggiunta del documento", e);
                            Toasty.error(getContext(), "Errore nel creare l'esercizio", Toast.LENGTH_SHORT, true).show();
                            // Navigazione alla lista degli esercizi
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
                            navController.navigate(R.id.navigation_esercizi);
                        });
            }else{
                caricaFileSulloStorage(pathFile, uriFile);
            }
        });
    }
}
