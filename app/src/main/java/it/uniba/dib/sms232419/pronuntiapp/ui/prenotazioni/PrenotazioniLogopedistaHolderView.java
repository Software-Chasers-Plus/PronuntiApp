package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class PrenotazioniLogopedistaHolderView extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView textViewLogopedistaFiglio, textViewDataPrenotazione, textViewOraPrenotazione,textViewNotePrenotazione;
    ImageView imageViewNonConfermato,imageViewConfermato;

    Button confermaButton;
    private ClickPrenotazioniLogopedistaListener clickPrenotazioniLogopedistaListener;

    // Costruttore che riceve un'istanza di ClickListener
    public PrenotazioniLogopedistaHolderView(View itemView, ClickPrenotazioniLogopedistaListener listener) {
        super(itemView);
        // Imposta questo ViewHolder come gestore di clic
        itemView.setOnClickListener(this);

        this.clickPrenotazioniLogopedistaListener = listener;

        textViewLogopedistaFiglio = itemView.findViewById(R.id.logopedista_prenotazione);
        textViewDataPrenotazione = itemView.findViewById(R.id.data_prenotazione);
        textViewOraPrenotazione = itemView.findViewById(R.id.ora_prenotazione);
        textViewNotePrenotazione = itemView.findViewById(R.id.informazioni_prenotazione);
        imageViewConfermato = itemView.findViewById(R.id.myImageViewConfermato);
        imageViewNonConfermato = itemView.findViewById(R.id.myImageView);
        confermaButton = itemView.findViewById(R.id.conferma_prenotazione_button);
        confermaButton.setOnClickListener(v -> {
            if(clickPrenotazioniLogopedistaListener != null)
                clickPrenotazioniLogopedistaListener.onConfermaClick(getAdapterPosition());

        });

    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onClick(View view) {
        // Verifica se il ClickListener è stato assegnato
        if(clickPrenotazioniLogopedistaListener != null){
            // Passa la posizione dell'elemento cliccato al ClickListener
            clickPrenotazioniLogopedistaListener.onItemClick(getAdapterPosition());
        }
    }
}
