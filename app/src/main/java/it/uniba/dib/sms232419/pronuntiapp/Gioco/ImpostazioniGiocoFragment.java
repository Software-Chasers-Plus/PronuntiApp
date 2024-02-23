package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import static java.lang.Math.abs;

import android.Manifest;
import android.animation.AnimatorSet;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class ImpostazioniGiocoFragment extends Fragment{

    private AudioManager audioManager;
    private SeekBar volumeSeekBar;
    private ImageView audioImageView;
    private boolean isAudioOn = true;
    private GiocoActivity giocoActivity;
    int personaggioSelezionatoInt;
    int previousVolume;

    ContentObserver volumeObserver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inizializza l'AudioManager
        audioManager = (AudioManager) requireActivity().getSystemService(requireActivity().AUDIO_SERVICE);
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        giocoActivity = (GiocoActivity) getActivity();
        volumeObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange);
                // Ottieni il volume corrente dal ContentResolver
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volumeSeekBar.setProgress(volume);
                // Fai qualcosa con il volume
                Log.d("ImpostazioniGiocoFragment", "Volume: " + volume);
            }
        };
        // Registra il ContentObserver per rilevare i cambiamenti del volume e aggiornare la SeekBar
        requireActivity().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, volumeObserver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_impostazioni_gioco, container, false);

        volumeSeekBar = view.findViewById(R.id.seekBar);

        // Imposta il massimo volume e il volume attuale
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Imposta il volume del flusso audio STREAM_MUSIC
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

                // Aggiorna la SeekBar
                seekBar.setProgress(progress);

                // Aggiorna lo stato dell'audio e l'immagine corrispondente
                isAudioOn = (progress > 0);
                audioImageView.setImageResource(isAudioOn ? R.drawable.audio_on : R.drawable.audio_off);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        audioImageView = view.findViewById(R.id.audio);
        audioImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioOn) {
                    // Spegni l'audio
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    volumeSeekBar.setProgress(0);
                } else {
                    // Accendi l'audio al volume precedente
                    int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    volumeSeekBar.setProgress(currentVolume);
                }

                // Inverti lo stato dell'audio
                isAudioOn = !isAudioOn;

                audioImageView.setImageResource(isAudioOn ? R.drawable.audio_on : R.drawable.audio_off);
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Richiedi il permesso MODIFY_AUDIO_SETTINGS se non è già stato concesso
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 0);
        }

        RecyclerView recyclerView = view.findViewById(R.id.carousel_recycler_view);
        ArrayList<Integer> arrayList = new ArrayList<>();

        arrayList.add(R.drawable.deserto);
        arrayList.add(R.drawable.antartide);
        arrayList.add(R.drawable.giungla);

        // Crea e imposta l'adapter per il RecyclerView
        sfondoAdapter adapter = new sfondoAdapter(getActivity(), arrayList);
        recyclerView.setAdapter(adapter);

        TextView sfondoSelezionato = view.findViewById(R.id.sfondo_selezionato);
        RelativeLayout fragmentLayout = view.findViewById(R.id.impostazioni_gioco);
        CardView cardView = view.findViewById(R.id.cardView);
        CardView cardViewPersonaggio = view.findViewById(R.id.cardView_personaggio);
        TextView personaggioSelezionato = view.findViewById(R.id.personaggio_selezionato);
        FloatingActionButton fab_successivo = view.findViewById(R.id.bottone_personaggio_successivo);
        FloatingActionButton fab_precedente = view.findViewById(R.id.bottone_personaggio_precedente);

        //listener che si attiva quando l'utente seleziona un'immagine modifica lo sfondo del gioco e il tema delle impostazioni
        adapter.setOnItemClickListener(new sfondoAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageView imageView, Integer path) {
                switch (path) {
                    case 0:
                        fragmentLayout.setBackgroundResource(R.drawable.deserto);
                        sfondoSelezionato.setText("Deserto");
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.secondaryDeserto));
                        cardViewPersonaggio.setBackgroundTintList(getResources().getColorStateList(R.color.primaryDeserto));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryDeserto));
                        personaggioSelezionato.setTextColor(getResources().getColor(R.color.secondaryDeserto));
                        fab_precedente.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryDeserto));
                        fab_successivo.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryDeserto));
                        giocoActivity.sfondoSelezionato = 0;
                        break;
                    case 1:
                        fragmentLayout.setBackgroundResource(R.drawable.antartide);
                        sfondoSelezionato.setText("Antartide");
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.secondaryAntartide));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryAntartide));
                        cardViewPersonaggio.setBackgroundTintList(getResources().getColorStateList(R.color.primaryAntartide));
                        personaggioSelezionato.setTextColor(getResources().getColor(R.color.secondaryAntartide));
                        fab_precedente.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryAntartide));
                        fab_successivo.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryAntartide));
                        giocoActivity.sfondoSelezionato = 1;
                        break;
                    case 2:
                        fragmentLayout.setBackgroundResource(R.drawable.giungla);
                        sfondoSelezionato.setText("Giungla");
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.secondaryGiungla));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryGiungla));
                        cardViewPersonaggio.setBackgroundTintList(getResources().getColorStateList(R.color.primaryGiungla));
                        personaggioSelezionato.setTextColor(getResources().getColor(R.color.secondaryGiungla));
                        fab_precedente.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryGiungla));
                        fab_successivo.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryGiungla));
                        giocoActivity.sfondoSelezionato = 2;
                        break;
                }
            }
        });

        // Inizializza la lista di personaggi del gioco
        ArrayList<Integer> personaggiGioco = new ArrayList<>();
        personaggiGioco.add(R.drawable.pinguino);
        personaggiGioco.add(R.drawable.serpente);
        personaggiGioco.add(R.drawable.husky);
        personaggiGioco.add(R.drawable.leone);

        int[] moneteRichieste = {0, 150 , 300, 450};
        personaggioSelezionatoInt = giocoActivity.personaggioSelezionato;


        ImageView personaggio = view.findViewById(R.id.personaggio_selezionato_immagine);
        // Imposta l'immagine del personaggio selezionato
        personaggio.setImageDrawable(getResources().getDrawable(personaggiGioco.get(giocoActivity.personaggioSelezionato)));
        // Imposta il nome del personaggio selezionato
        switch (giocoActivity.personaggioSelezionato) {
            case 0:
                personaggioSelezionato.setText(R.string.pinguino);
                break;
            case 1:
                personaggioSelezionato.setText(R.string.serpente);
                break;
            case 2:
                personaggioSelezionato.setText(R.string.husky);
                break;
            case 3:
                personaggioSelezionato.setText(R.string.leone);
                break;
        }

        ImageView lucchetto = view.findViewById(R.id.lock_icon_selezione_personaggio);
        lucchetto.setVisibility(View.GONE);

        // Oggetto Animation per lo scorrimento in entrata da sinistra verso il centro
        Animation enterSlideLeft = AnimationUtils.loadAnimation(getContext(), R.anim.enter_slide_left);
        // Ogggetto Animation per lo scorrimento in uscita dal centro verso destra
        Animation exitSlideRight = AnimationUtils.loadAnimation(getContext(), R.anim.exit_slide_right);
        // Imposta un listener per l'animazione che si atttiva quando l'utente passa al personaggio successivo
        exitSlideRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //cambio immagine del personaggio
                if(personaggioSelezionatoInt != personaggiGioco.size() - 1) {
                    if(giocoActivity.figlio.getPunteggioGioco() >= moneteRichieste[personaggioSelezionatoInt + 1]) {
                        //modifica personaggio selezionato e cambia immagine
                        personaggioSelezionatoInt++;
                        giocoActivity.personaggioSelezionato = personaggioSelezionatoInt;
                        personaggio.setImageDrawable(getResources().getDrawable(personaggiGioco.get(giocoActivity.personaggioSelezionato)));
                        lucchetto.setVisibility(View.GONE);
                    }else{
                        //cambio solo immagine senza modificare il personaggio selezionato
                        personaggioSelezionatoInt++;
                        personaggio.setImageDrawable(getResources().getDrawable(personaggiGioco.get(personaggioSelezionatoInt)));
                        lucchetto.setVisibility(View.VISIBLE);
                    }
                } else {
                    giocoActivity.personaggioSelezionato = 0;
                    personaggioSelezionatoInt = 0;
                    personaggio.setImageDrawable(getResources().getDrawable(personaggiGioco.get(giocoActivity.personaggioSelezionato)));
                    lucchetto.setVisibility(View.GONE);
                }
                //avvio animazione di entrata
                personaggio.startAnimation(enterSlideLeft);
                switch (giocoActivity.personaggioSelezionato) {
                    case 0:
                        personaggioSelezionato.setText(R.string.pinguino);
                        break;
                    case 1:
                        personaggioSelezionato.setText(R.string.serpente);
                        break;
                    case 2:
                        personaggioSelezionato.setText(R.string.husky);
                        break;
                    case 3:
                        personaggioSelezionato.setText(R.string.leone);
                        break;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // Oggetto Animation per lo scorrimento in entrata da destra verso il centro
        Animation enterSlideRight = AnimationUtils.loadAnimation(getContext(), R.anim.enter_slide_right);
        // Oggetto Animation per lo scorrimento in sucita dal centro verso sinistra
        Animation slideLeft = AnimationUtils.loadAnimation(getContext(), R.anim.exit_slide_left);
        // Imposta un listener per l'animazione che si attiva quando l'utente passa al personaggio precedente
        slideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //cambio immagine del personaggio
                if(personaggioSelezionatoInt == 0) {
                    if (giocoActivity.figlio.getPunteggioGioco() >= moneteRichieste[personaggiGioco.size() - 1]) {
                        //modifica personaggio selezionato e cambia immagine
                        personaggioSelezionatoInt = personaggiGioco.size() - 1;
                        giocoActivity.personaggioSelezionato = personaggioSelezionatoInt;
                        personaggio.setImageDrawable(getResources().getDrawable(personaggiGioco.get(giocoActivity.personaggioSelezionato)));
                        lucchetto.setVisibility(View.GONE);
                    } else {
                        //cambio solo immagine senza modificare il personaggio selezionato
                        personaggioSelezionatoInt = personaggiGioco.size() - 1;
                        personaggio.setImageDrawable(getResources().getDrawable(personaggiGioco.get(personaggioSelezionatoInt)));
                        lucchetto.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (giocoActivity.figlio.getPunteggioGioco() >= moneteRichieste[personaggioSelezionatoInt - 1]) {
                        //modifica personaggio selezionato e cambia immagine
                        personaggioSelezionatoInt--;
                        giocoActivity.personaggioSelezionato = personaggioSelezionatoInt;
                        personaggio.setImageDrawable(getResources().getDrawable(personaggiGioco.get(giocoActivity.personaggioSelezionato)));
                        lucchetto.setVisibility(View.GONE);
                    } else {
                        //cambio solo immagine senza modificare il personaggio selezionato
                        personaggioSelezionatoInt--;
                        personaggio.setImageDrawable(getResources().getDrawable(personaggiGioco.get(personaggioSelezionatoInt)));
                        lucchetto.setVisibility(View.VISIBLE);
                    }
                }
                //avvio animazione di entrata
                personaggio.startAnimation(enterSlideRight);
                switch (giocoActivity.personaggioSelezionato) {
                    case 0:
                        personaggioSelezionato.setText(R.string.pinguino);
                        break;
                    case 1:
                        personaggioSelezionato.setText(R.string.serpente);
                        break;
                    case 2:
                        personaggioSelezionato.setText(R.string.husky);
                        break;
                    case 3:
                        personaggioSelezionato.setText(R.string.leone);
                        break;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fab_successivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personaggio.startAnimation(exitSlideRight);
            }
        });

        fab_precedente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personaggio.startAnimation(slideLeft);
            }
        });

        // Imposta il deserto come layout di default selezionato
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Scrolla il RecyclerView alla posizione dello sfondo selezionato
                recyclerView.scrollToPosition(giocoActivity.sfondoSelezionato);

                // Imposta il colore del testo e del tema in base allo sfondo selezionato
                switch (giocoActivity.sfondoSelezionato){
                    case 0:
                        fragmentLayout.setBackgroundResource(R.drawable.deserto);
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.primaryDeserto));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryDeserto));
                        cardViewPersonaggio.setBackgroundTintList(getResources().getColorStateList(R.color.primaryDeserto));
                        sfondoSelezionato.setText(R.string.deserto);
                        personaggioSelezionato.setTextColor(getResources().getColor(R.color.secondaryDeserto));
                        fab_precedente.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryDeserto));
                        fab_successivo.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryDeserto));
                        break;
                    case 1:
                        fragmentLayout.setBackgroundResource(R.drawable.antartide);
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.primaryAntartide));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryAntartide));
                        cardViewPersonaggio.setBackgroundTintList(getResources().getColorStateList(R.color.primaryAntartide));
                        sfondoSelezionato.setText(R.string.antartide);
                        personaggioSelezionato.setTextColor(getResources().getColor(R.color.secondaryAntartide));
                        fab_precedente.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryAntartide));
                        fab_successivo.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryAntartide));
                        break;
                    case 2:
                        fragmentLayout.setBackgroundResource(R.drawable.giungla);
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.primaryGiungla));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryGiungla));
                        cardViewPersonaggio.setBackgroundTintList(getResources().getColorStateList(R.color.primaryGiungla));
                        sfondoSelezionato.setText(R.string.giungla);
                        personaggioSelezionato.setTextColor(getResources().getColor(R.color.secondaryGiungla));
                        fab_precedente.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryGiungla));
                        fab_successivo.setBackgroundTintList(getResources().getColorStateList(R.color.secondaryGiungla));
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        volumeSeekBar = null;
        audioImageView = null;
        requireActivity().getContentResolver().unregisterContentObserver(volumeObserver);
    }
}
