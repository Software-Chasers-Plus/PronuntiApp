package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class FigliHolderView extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imageViewFiglio; // Campo per l'ImageView per l'avatar del figlio
    TextView textViewNomeFiglio, textViewEtaFiglio, textViewLogopedistaFiglio;
    TextView emailLogopedistaLabelTextView, emailLogopedistaTextView; // Campi per l'email del logopedista

    private ClickFigliListener clickFigliListener;

    // Costruttore che riceve un'istanza di ClickListener
    public FigliHolderView(View itemView) {
        super(itemView);
        // Imposta questo ViewHolder come gestore di clic
        itemView.setOnClickListener(this);

        // Inizializza il campo dell'ImageView per l'avatar del figlio
        imageViewFiglio = itemView.findViewById(R.id.figlio_avatar);

        textViewNomeFiglio = itemView.findViewById(R.id.nome_figlio);
        textViewEtaFiglio = itemView.findViewById(R.id.eta_figlio);
        //textViewLogopedistaFiglio = itemView.findViewById(R.id.logopedista_figlio);

        // Inizializza i campi per l'email del logopedista
        emailLogopedistaLabelTextView = itemView.findViewById(R.id.email_logopedista_label_figlio);
        emailLogopedistaTextView = itemView.findViewById(R.id.email_logopedista_figlio);
    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onClick(View view) {
        // Verifica se il ClickListener è stato assegnato
        if(clickFigliListener != null){
            // Passa la posizione dell'elemento cliccato al ClickListener
            clickFigliListener.onItemClick(getAdapterPosition());
        }
    }
}
