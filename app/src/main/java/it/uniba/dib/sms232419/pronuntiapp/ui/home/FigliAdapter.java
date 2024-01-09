package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class FigliAdapter extends RecyclerView.Adapter<FigliHolderView>{

    Context context;

    List<Figlio> items;

    private ClickFigliListener clickFigliListener;

    // Costruttore che riceve la lista di dati e un'istanza di ClickListener
    public FigliAdapter(Context context, List<Figlio> items, ClickFigliListener listener) {
        this.context = context;
        this.items = items;
        this.clickFigliListener = listener;
    }

    @NonNull
    @Override
    public FigliHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FigliHolderView(LayoutInflater.from(context).inflate(R.layout.item_figli_view, parent, false), clickFigliListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FigliHolderView holder, int position) {
        holder.textViewNomeFiglio.setText(items.get(position).getNome());
        holder.textViewLogopedistaFiglio.setText(items.get(position).getEmailLogopedista());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
