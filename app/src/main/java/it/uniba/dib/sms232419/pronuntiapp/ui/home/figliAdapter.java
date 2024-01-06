package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class figliAdapter extends RecyclerView.Adapter<figliHolderView>{

    Context context;

    List<Figlio> items;

    private ClickFigliListener clickFigliListener;

    // Costruttore che riceve la lista di dati e un'istanza di ClickListener
    public figliAdapter(Context context, List<Figlio> items, ClickFigliListener listener) {
        this.context = context;
        this.items = items;
        this.clickFigliListener = listener;
    }

    @NonNull
    @Override
    public figliHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new figliHolderView(LayoutInflater.from(context).inflate(R.layout.item_figli_view, parent, false), clickFigliListener);
    }

    @Override
    public void onBindViewHolder(@NonNull figliHolderView holder, int position) {
        holder.textViewNomeFiglio.setText(items.get(position).getNome());
        holder.textViewLogopedistaFiglio.setText(items.get(position).getEmailLogopedista());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
