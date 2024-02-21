package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;

public class EserciziAdapter extends RecyclerView.Adapter<EserciziHolderView> {
    Context context;

    List<Esercizio> items;

    private ClickEserciziListener clickEserciziListener;

    // Costruttore che riceve la lista di dati e un'istanza di ClickListener
    public EserciziAdapter(Context context, List<Esercizio> items, ClickEserciziListener listener) {
        this.context = context;
        this.items = items;
        this.clickEserciziListener = listener;
    }

    @NonNull
    @Override
    public EserciziHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EserciziHolderView(LayoutInflater.from(context).inflate(R.layout.singolo_esercizio, parent, false), clickEserciziListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EserciziHolderView holder, int position) {
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

        //holder.textViewTipologiaEserciio.setText(items.get(position).getTipologia());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
