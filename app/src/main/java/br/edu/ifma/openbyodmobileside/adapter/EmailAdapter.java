package br.edu.ifma.openbyodmobileside.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.modelo.EmailUsuario;

/**
 * Created by Windows on 23/08/2017.
 */

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {
    private List<EmailUsuario> emails;
    private int rowLayout;
    private Context context;
    private static AdapterInterface emailInterface;

    public static class EmailViewHolder extends RecyclerView.ViewHolder {
        LinearLayout emailsLayout;
        TextView assuntoEmail;
        TextView dataEnvio;
        TextView remetenteEmail;

        public EmailViewHolder(View v) {
            super(v);
            emailsLayout = (LinearLayout) v.findViewById(R.id.emails_layout);
            assuntoEmail = (TextView) v.findViewById(R.id.assuntoEmail);
            dataEnvio = (TextView) v.findViewById(R.id.dataEnvio);
            remetenteEmail = (TextView) v.findViewById(R.id.remetenteEmail);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emailInterface.onItemClick(getPosition());
                }
            });
        }
    }

    public EmailAdapter(List<EmailUsuario> emails, int rowLayout,
                         Context context, AdapterInterface emailInterface) {
        this.emails = emails;
        this.rowLayout = rowLayout;
        this.context = context;
        this.emailInterface = emailInterface;
    }

    @Override
    public EmailAdapter.EmailViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new EmailAdapter.EmailViewHolder(view);
    }


    @Override
    public void onBindViewHolder(EmailAdapter.EmailViewHolder holder, final int position) {
        holder.assuntoEmail.setText(emails.get(position).getAssunto());
        holder.dataEnvio.setText(emails.get(position).getData());
        holder.remetenteEmail.setText(emails.get(position).getRemetente());
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }
}
