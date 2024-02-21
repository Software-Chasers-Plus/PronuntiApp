package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
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

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.PermissionManager;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.RecordAudio;

public class EsercizioRiconoscimentoCoppie extends Fragment {
    private static final int REQUEST_IMAGE_PICK1 = 11;

    private static final int REQUEST_IMAGE_PICK2 = 12;
    private static final int REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_CODE_PICK_AUDIO_BUTTON = 3;

    Uri image1Uri;

    Uri image2Uri;
    Uri audioUri;

    String ID_immagine1;
    String ID_immagine2;

    String ID_audio;

    private ImageView image1View;
    private ImageView image2View;

    String audioName;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    int riferimento_immagine_audio = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.riconoscimento_coppie, container, false);

        image1View = view.findViewById(R.id.image_view1_esercizio3);
        image2View = view.findViewById(R.id.image_view2_esercizio3);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Record to the external cache directory for visibility
        audioName = getActivity().getExternalCacheDir().getAbsolutePath();
        audioName += "/audiorecord.mp3";

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup_esercizio3);
        Button conferma_button = view.findViewById(R.id.crea_esercizio3_button);
        TextInputLayout nome_esercizio_textView = view.findViewById(R.id.TextFieldNomeEsercizio3);

        ImageButton upload_audio_button = view.findViewById(R.id.upload_audio_button);
        ImageButton record_audio_button = view.findViewById(R.id.record_audio_button);
        ImageButton play_audio_button = view.findViewById(R.id.play_audio_button);

        image1View.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                // Se il permesso non è stato concesso, richiedilo all'utente
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                // Se il permesso è già stato concesso, puoi procedere con la logica per selezionare un'immagine
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK1);
            }
        });

        image2View.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                // Se il permesso non è stato concesso, richiedilo all'utente
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                // Se il permesso è già stato concesso, puoi procedere con la logica per selezionare un'immagine
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK2);
            }
        });



        //Gestione bottone audio 1
        upload_audio_button.setOnClickListener(v -> {
            // Crea un Intent per selezionare un file audio
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*"); // Specifica che stai cercando file audio
            startActivityForResult(Intent.createChooser(intent, "Seleziona un file audio"), REQUEST_CODE_PICK_AUDIO_BUTTON);
        });

        record_audio_button.setOnClickListener(v -> {
            PermissionManager.requestPermissions(EsercizioRiconoscimentoCoppie.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, new PermissionManager.PermissionListener() {
                @Override
                public void onPermissionsGranted() {
                    audioUri = recordAudio(record_audio_button);
                    // Ottieni il riferimento all'EditText per il testo dell'audio 1
                    TextView testo_audio = requireView().findViewById(R.id.audio_esercizio3_testo);
                    // Esegui la logica per caricare l'audio e l'immagine
                    testo_audio.setText(R.string.audio + " " + "audioRegistrato");
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

        play_audio_button.setOnClickListener(v -> {
            RecordAudio.onPlay(mStartPlaying , audioName);
            if (mStartPlaying) {
                play_audio_button.setImageResource(R.drawable.pause_icon_white_24);
            } else {
                play_audio_button.setImageResource(R.drawable.baseline_play_arrow_24);
            }
            mStartPlaying = !mStartPlaying;
        });


        //Selezione immagine corretta
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonImmagine1) {
                riferimento_immagine_audio = 1;
            } else if (checkedId == R.id.radioButtonImmagine2) {
                riferimento_immagine_audio = 2;
            } else {
            }
        });

        //Gestione bottone conferma
        conferma_button.setOnClickListener(v -> {

            final boolean[] esito = {true};

            // Ottieni il nome dell'esercizio
            String nome_esercizio = nome_esercizio_textView.getEditText().getText().toString();
            if(nome_esercizio.isEmpty()) {
                nome_esercizio_textView.setError("Il nome dell'esercizio è obbligatorio");
                return;
            }

            if(riferimento_immagine_audio == 0) {
                Toasty.error(getContext(), "Seleziona quale immagine e' corretta", Toast.LENGTH_SHORT, true).show();
                return;
            }


            //Creazione dei percorsi per Firebase Storage
            String path_img1 = "esercizio3/" + nome_esercizio +"immagine1"+ ".jpg";
            String path_img2 = "esercizio3/" + nome_esercizio + "immagine2" + ".jpg";
            String path_audio = "esercizio3/" + nome_esercizio + "_audio.mp3";

            // Carica l'immagine su Firebase Storage
            uploadFileToFirebaseStorage(image1Uri, path_img1, (success, id_img1) -> {
                if (success) {
                    ID_immagine1 = id_img1;
                }else {
                    esito[0] = false;
                }
            });

            // Carica l'immagine su Firebase Storage
            uploadFileToFirebaseStorage(image2Uri, path_img2, (success, id_img2) -> {
                if (success) {
                    ID_immagine2 = id_img2;
                }else {
                    esito[0] = false;
                }
            });


            // Carica l'audio  su Firebase Storage
            // Carica l'audio su Firebase Storage
            if(audioUri == null) {
                Toasty.error(getContext(), "Seleziona un file audio", Toast.LENGTH_SHORT, true).show();
                return;
            }
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
                    Log.d("EsercizioRiconoscimentoCoppie", "ID dell'utente attualmente loggato: " + userId);
                } else {
                    Log.d("EsercizioRiconoscimentoCoppie", "Nessun utente attualmente loggato");
                }

                // Crea un oggetto Map per contenere i dati da inserire nel documento
                Map<String, Object> data = new HashMap<>();
                data.put("tipologia", 3);
                data.put("logopedista", userId);
                data.put("nome", nome_esercizio);
                data.put("immagine1", path_img1);
                data.put("immagine2", path_img2);
                data.put("audio", path_audio);
                data.put("immagine_corretta", riferimento_immagine_audio);

                // Aggiungi i dati a una nuova raccolta con un ID generato automaticamente
                db.collection("esercizi")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("EsercizioRiconoscimentoCoppie", "DocumentSnapshot aggiunto con ID: " + documentReference.getId());
                            //Navigazione alla lista degli esercizi

                        })
                        .addOnFailureListener(e -> {
                            Log.w("EsercizioRiconoscimentoCoppie", "Errore durante l'aggiunta del documento", e);
                        });

                //NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
                //navController.navigate(R.id.navigation_esercizi);

            } else {
                // Se c'è stato un errore nel caricare i file, mostra un messaggio di errore
                //Toast.makeText(getContext(), "Errore nel creare l'esercizio", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Metodo per gestire il risultato della selezione dell'immagine
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK1 && data != null) {
                // Ottieni l'URI dell'immagine selezionata
                image1Uri = data.getData();
                if (image1Uri != null) {
                    // Imposta l'immagine nell'ImageView
                    image1View.setImageURI(image1Uri);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK2 && data != null) {
                // Ottieni l'URI dell'immagine selezionata
                image2Uri = data.getData();
                if (image2Uri != null) {
                    // Imposta l'immagine nell'ImageView
                    image2View.setImageURI(image2Uri);
                }
            } else if (requestCode == REQUEST_CODE_PICK_AUDIO_BUTTON  && data != null) {
                // Ottieni l'URI del file audio selezionato
                audioUri = data.getData();
                if (audioUri != null) {
                    switch (requestCode) {
                        case REQUEST_CODE_PICK_AUDIO_BUTTON:
                            // Ottieni il riferimento all'EditText per il testo dell'audio 1
                            TextView testo_audio = requireView().findViewById(R.id.audio_esercizio3_testo);
                            String testo_corrente = testo_audio.getText().toString();
                            // Esegui la logica per caricare l'audio e l'immagine
                            testo_audio.setText(testo_corrente + " " + getAudioFileNameFromUri(getActivity(), audioUri));
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
    private void uploadFileToFirebaseStorage(Uri fileUri, String path, EsercizioRiconoscimentoCoppie.OnUploadCompleteListener callback) {
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
                                Log.d("EsercizioRiconoscimentoCoppie", "File caricato con successo: " + downloadUri);
                                progressDialog.dismiss();
                                callback.onUploadComplete(true, downloadUri); // Notifica il chiamante che il caricamento è completato con successo
                            })
                            .addOnFailureListener(e -> {
                                // Gestisci l'errore
                                Log.e("EsercizioRiconoscimentoCoppie", "Errore nel caricare il file: " + e.getMessage());
                                progressDialog.dismiss();
                                callback.onUploadComplete(false, null); // Notifica il chiamante che si è verificato un errore durante il caricamento
                            });
                })
                .addOnFailureListener(e -> {
                    // Gestisci l'errore
                    Log.e("EsercizioRiconoscimentoCoppie", "Errore nel caricare il file: " + e.getMessage());
                    progressDialog.dismiss();
                    callback.onUploadComplete(false, null); // Notifica il chiamante che si è verificato un errore durante il caricamento
                });
    }

    // Interfaccia per il callback quando il caricamento è completato
    interface OnUploadCompleteListener {
        void onUploadComplete(boolean success, String id_immagine);
    }

    private Uri recordAudio(ImageButton record_audio_button) {
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

    // Metodo per aprire le impostazioni dell'applicazione
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}

