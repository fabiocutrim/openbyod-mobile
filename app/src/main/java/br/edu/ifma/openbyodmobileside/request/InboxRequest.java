package br.edu.ifma.openbyodmobileside.request;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.adapter.AdapterInterface;
import br.edu.ifma.openbyodmobileside.adapter.EmailAdapter;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoInbox;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoVisualizacaoEmail;
import br.edu.ifma.openbyodmobileside.modelo.EmailUsuario;
import br.edu.ifma.openbyodmobileside.modelo.Resource;
import retrofit2.Call;

/**
 * Created by Windows on 22/08/2017.
 */

public class InboxRequest extends Request implements AdapterInterface {
    private ActivityPrincipal activityPrincipal;
    private List<EmailUsuario> emailsList = new ArrayList<EmailUsuario>();
    private RecyclerView recyclerView;

    public InboxRequest(ActivityPrincipal activityPrincipal, RecyclerView recyclerView) {

        Log.i("Requisição", "Inbox");

        this.activityPrincipal = activityPrincipal;
        this.recyclerView = recyclerView;
        progressDialog = new ProgressDialog(this.activityPrincipal);
        progressDialog.setTitle("Carregando a caixa de entrada");
        progressDialog.setMessage("Aguarde...");
        progressDialog.setProgressStyle(R.style.ProgressBar);
        progressDialog.show();
    }

    public void getCaixaDeEntrada() {
        efetuaRequisicao("", activityPrincipal);
    }

    private List<EmailUsuario> extraiEmailsJSON(JSONArray jsonListaDeEmails)
            throws JSONException {
        for (int i = 0; i < jsonListaDeEmails.length(); i++) {
            JSONObject linha = jsonListaDeEmails.getJSONObject(i);
            String remetente = linha.getString("remetente");
            String assunto = linha.getString("assunto");
            String corpo = linha.getString("conteudo");
            String destinatario = linha.getString("destinatario");
            String data = linha.getString("data");

            EmailUsuario email = new EmailUsuario(destinatario, remetente, data,
                    assunto, corpo);
            emailsList.add(email);
        }
        return emailsList;
    }

    @Override
    public void onItemClick(int position) {
        final EmailUsuario email = emailsList.get(position);
        activityPrincipal.getSupportActionBar().setTitle("E-mail");

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragmentoVisualizacaoEmail = new FragmentoVisualizacaoEmail();
                Bundle bundle = new Bundle();
                bundle.putString(FragmentoInbox.EMAIL_REMETENTE, email.getRemetente());
                bundle.putString(FragmentoInbox.EMAIL_DESTINATARIO, email.getDestinatario());
                bundle.putString(FragmentoInbox.EMAIL_ASSUNTO, email.getAssunto());
                bundle.putString(FragmentoInbox.EMAIL_CONTEUDO, email.getConteudo());
                bundle.putString(FragmentoInbox.EMAIL_DATA, email.getData());
                fragmentoVisualizacaoEmail.setArguments(bundle);
                FragmentTransaction fragmentTransaction = activityPrincipal.
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragmentoVisualizacaoEmail);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            Handler mHandler = new Handler();
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        DrawerLayout drawer = (DrawerLayout) activityPrincipal.findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
    }

    @Override
    Call getRequest(Resource resource, String token, String chaveCriptografada) {
        // Executa a comunicação com o servidor
        return apiService.getCaixaDeEntrada(token,
                chaveCriptografada);
    }

    @Override
    void exibeMensagemSucesso() {
        Toast.makeText(activityPrincipal,
                "Sua caixa de entrada está vazia!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    void executaAcaoPosterior(String resposta) {
        try {
        // jsonlist to hold the contacts list that came on the
        JSONArray jsonListaDeEmails = new JSONArray(resposta);
        // extract values from the JSON
        extraiEmailsJSON(jsonListaDeEmails);
        // populates the list
        recyclerView.setAdapter(new EmailAdapter
                (emailsList, R.layout.list_item_email,
                        activityPrincipal, this));
        } catch (JSONException e) {
            Log.e("Exceção JSON", e.getLocalizedMessage());
        }
    }
}
