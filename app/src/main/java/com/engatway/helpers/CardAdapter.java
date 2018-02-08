package com.engatway.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.engatway.R;
import com.engatway.VerIntrigaActivity;
import com.engatway.classes.Intriga;

import java.util.List;

/**
 * Created by diogobcondeco on 18/01/2018.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    //Imageloader to load image
    // private ImageLoader imageLoader;
    // maybe use context
    private Context context;

    //List to store all superheroes
    List<Intriga> intrigas;

    //Constructor of this class
    public CardAdapter(List<Intriga> intrigas, Context context){
        super();
        //Getting all superheroes
        this.intrigas = intrigas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cartao_intriga, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Getting the particular item from the list
        final Intriga intriga =  intrigas.get(position);

        // Definir onClick para todos os cartao_intriga
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v) {
                Intent i = new Intent(context, VerIntrigaActivity.class);
                i.putExtra("id", intriga.getId_intriga()); // added this
                i.putExtra("id_sujeito", intriga.getId_sujeito()); // added this
                i.putExtra("sujeito", intriga.getSujeito());
                i.putExtra("mensagem", intriga.getMensagem());
                i.putExtra("id_alvo", intriga.getId_alvo()); // added this
                i.putExtra("alvo", intriga.getAlvo());
                i.putExtra("descricao", intriga.getDescricao());

                // context.startActivity(new Intent(context, VerIntriga.class));
                context.startActivity(i);
            }
        });

        //Loading image from url
        // imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        // imageLoader.get(intriga.getSujeito(), ImageLoader.getImageListener(holder.imageView, R.drawable.image, android.R.drawable.ic_dialog_alert));

        //Showing data on the views
        // holder.imageView.setImageUrl(intriga.getSujeito(), imageLoader);
        holder.textViewSujeito.setText(intriga.getSujeito());
        holder.textViewMensagem.setText(intriga.getMensagem());
        holder.textViewAlvo.setText(intriga.getAlvo());
        holder.textViewDescricao.setText(intriga.getDescricao());
        // holder.textViewName.setText(intriga.getName());
        // holder.textViewPublisher.setText(intriga.getPublisher());

    }

    @Override
    public int getItemCount() {
        return intrigas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //Views
        // public NetworkImageView imageView;
        // public TextView textViewName;
        // public TextView textViewPublisher;
        public TextView textViewSujeito;
        public TextView textViewMensagem;
        public TextView textViewAlvo;
        public TextView textViewDescricao;

        //Initializing Views
        public ViewHolder(View itemView) {
            super(itemView);
            // imageView = (NetworkImageView) itemView.findViewById(R.id.imageViewHero);
            // textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            // textViewPublisher = (TextView) itemView.findViewById(R.id.textViewPublisher);
            textViewSujeito = (TextView) itemView.findViewById(R.id.textViewSujeito);
            textViewMensagem = (TextView) itemView.findViewById(R.id.textViewMensagem);
            textViewAlvo = (TextView) itemView.findViewById(R.id.textViewAlvo);
            textViewDescricao = (TextView) itemView.findViewById(R.id.textViewDescricao);
        }
    }
}
