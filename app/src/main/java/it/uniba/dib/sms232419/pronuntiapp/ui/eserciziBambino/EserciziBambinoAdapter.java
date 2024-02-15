package it.uniba.dib.sms232419.pronuntiapp.ui.eserciziBambino;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;

public class EserciziBambinoAdapter extends RecyclerView.Adapter<EserciziBambinoHolderView> {
    Context context;

    List<Esercizio> items;

    private ClickEserciziBambinoListener clickEserciziBambinoListener;

    // Costruttore che riceve la lista di dati e un'istanza di ClickListener
    public EserciziBambinoAdapter(Context context, List<Esercizio> items, ClickEserciziBambinoListener listener) {
        this.context = context;
        this.items = items;
        this.clickEserciziBambinoListener = listener;
    }

    @NonNull
    @Override
    public EserciziBambinoHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EserciziBambinoHolderView(LayoutInflater.from(context).inflate(R.layout.singolo_esercizio_cardview, parent, false), clickEserciziBambinoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EserciziBambinoHolderView holder, int position) {
        holder.textViewNomeEsercizio.setText(items.get(position).getNome());
        switch (items.get(position).getTipologia()) {
            case "1":
                holder.textViewTipologiaEserciio.setText(R.string.denominazione_immagini);
                break;
            case "2":
                holder.textViewTipologiaEserciio.setText(R.string.ripetizione_parole);
                break;
            case "3":
                holder.textViewTipologiaEserciio.setText(R.string.riconoscimento_di_coppie);
                break;
        }

        holder.cardView.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
