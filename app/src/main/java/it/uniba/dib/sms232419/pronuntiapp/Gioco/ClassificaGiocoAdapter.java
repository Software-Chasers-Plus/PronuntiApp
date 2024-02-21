package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class ClassificaGiocoAdapter extends RecyclerView.Adapter<ClasssificaGiocoHolderView> {
    private static final String TAG = "ClassificaGiocoAdapter";

    Context context;
    private final ArrayList<Figlio> bambiniList;
    private final ClickClassificaGiocoListener clickClassificaGiocoListener;

    public ClassificaGiocoAdapter(Context context, ArrayList<Figlio> bambiniList, ClickClassificaGiocoListener listener) {
        this.bambiniList = bambiniList;
        this.clickClassificaGiocoListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ClasssificaGiocoHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClasssificaGiocoHolderView(LayoutInflater.from(parent.getContext()).inflate(R.layout.classifica_bambino_cardview, parent, false), clickClassificaGiocoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClasssificaGiocoHolderView holder, int position) {
        holder.posizioneTextView.setText(String.valueOf(position + 3));
        holder.nomeTextView.setText(bambiniList.get(position).getNome());
        holder.punteggioTextView.setText(String.valueOf(bambiniList.get(position).getPunteggioGioco()));

        // Setta l'immagine del bambino
        int immagineBambino = bambiniList.get(position).getIdAvatar();
        switch (immagineBambino){
            case 1:
                holder.immagineBambinoImageView.setImageResource(R.drawable.bambino_1);
                break;
            case 2:
                holder.immagineBambinoImageView.setImageResource(R.drawable.bambino_2);
                break;
            case 3:
                holder.immagineBambinoImageView.setImageResource(R.drawable.bambino_3);
                break;
            case 4:
                holder.immagineBambinoImageView.setImageResource(R.drawable.bambino_4);
                break;
            case 5:
                holder.immagineBambinoImageView.setImageResource(R.drawable.bambino_5);
                break;
            case 6:
                holder.immagineBambinoImageView.setImageResource(R.drawable.bambino_6);
                break;

            default:
                holder.immagineBambinoImageView.setImageResource(R.drawable.bambino);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bambiniList.size();
    }
}
