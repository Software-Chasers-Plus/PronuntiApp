package it.uniba.dib.sms232419.pronuntiapp.ui.eserciziBambino;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.ClickFigliListener;

public class EserciziBambinoHolderView extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView textViewNomeEsercizio, textViewTipologiaEserciio;
    MaterialButton dettagliButtonEsercizio;
    MaterialCardView cardView;

    EditText dataEsercizio;
    ImageView iconaCalendario;

    private ClickEserciziBambinoListener clickEserciziBambinoListener;

    // Costruttore che riceve un'istanza di ClickListener
    public EserciziBambinoHolderView(View itemView, ClickEserciziBambinoListener listener) {
        super(itemView);
        // Imposta questo ViewHolder come gestore di clic
        itemView.setOnClickListener(this);
        // Salva il riferimento all'istanza di ClickListener
        this.clickEserciziBambinoListener = listener;

        textViewNomeEsercizio = itemView.findViewById(R.id.nome_esercizio);
        textViewTipologiaEserciio = itemView.findViewById(R.id.tipologia_esercizio);
        dettagliButtonEsercizio = itemView.findViewById(R.id.dettaglio_button);
        cardView = itemView.findViewById(R.id.cardView_eserecizio);
        dataEsercizio = itemView.findViewById(R.id.giorno_esercizio);
        iconaCalendario = itemView.findViewById(R.id.imageViewCalendar);

        dettagliButtonEsercizio.setOnClickListener(v -> {
            if(clickEserciziBambinoListener != null){
                clickEserciziBambinoListener.onDettaglioClick(getAdapterPosition());
            }
        });

        // Set the long click listener
        cardView.setOnLongClickListener(v -> {
            if(clickEserciziBambinoListener != null){
                clickEserciziBambinoListener.onItemLongClick(getAdapterPosition(), cardView);
            }
            return true;
        });

        iconaCalendario.setOnClickListener(v -> {
            if(clickEserciziBambinoListener != null){
                clickEserciziBambinoListener.onCalendarioClick(getAdapterPosition(), dataEsercizio);
            }
        });
    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onClick(View view) {
        // Verifica se il ClickListener è stato assegnato
        if (clickEserciziBambinoListener != null) {
            // Passa la posizione dell'elemento cliccato al ClickListener
            clickEserciziBambinoListener.onItemClick(getAdapterPosition());

        }
    }
}
