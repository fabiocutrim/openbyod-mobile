package br.edu.ifma.openbyodmobileside.request;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
import br.edu.ifma.openbyodmobileside.adapter.AgendaAdapter;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoAgenda;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoEdicaoContato;
import br.edu.ifma.openbyodmobileside.modelo.Contato;
import br.edu.ifma.openbyodmobileside.modelo.Resource;
import retrofit2.Call;

/**
 * Created by ARTLAN-00 on 20/08/2017.
 */

public class AgendaRequest extends Request implements AdapterInterface {
    private ActivityPrincipal activityPrincipal;
    private List<Contato> contatosList = new ArrayList<Contato>();
    private RecyclerView recyclerView;

    public AgendaRequest(ActivityPrincipal activityPrincipal, RecyclerView recyclerView) {

        Log.i("Requisição", "Agenda");

        this.activityPrincipal = activityPrincipal;
        this.recyclerView = recyclerView;
        progressDialog = new ProgressDialog(this.activityPrincipal);
        progressDialog.setTitle("Carregando a lista de contatos");
        progressDialog.setMessage("Aguarde...");
        progressDialog.setProgressStyle(R.style.ProgressBar);
        progressDialog.show();
    }

    public void listaContatos() {
        efetuaRequisicao("", activityPrincipal);
    }

    private List<Contato> extraiValoresJSON(JSONArray listaDeContatos) throws JSONException {
        for (int i = 0; i < listaDeContatos.length(); i++) {
            JSONObject linha = listaDeContatos.getJSONObject(i);
            String telefone = linha.getString("telefone");
            String nome = linha.getString("nome");
            String email = linha.getString("email");
            String empresa = linha.getString("empresa");
            int idContato = linha.getInt("id");

            Contato contact = new Contato(idContato, nome, telefone, email,
                    empresa);
            contatosList.add(contact);
        }
        return contatosList;
    }

    @Override
    public void onItemClick(int position) {
        final int posicao = position;
        final Contato contato = contatosList.get(posicao);

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activityPrincipal);
        builder.setTitle("O que você deseja fazer?");

        // add a list
        String[] opcoes = {"Efetuar uma chamada", "Editar Contato"};
        builder.setItems(opcoes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int escolha) {
                switch (escolha) {

                    case 0: // Efetuar uma chamada

                        efetuaChamada(contato);

                        break;

                    case 1: // Atualizar Contato

                        carregaFragmentoEdicaoContato(contato);

                        break;
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void efetuaChamada(Contato contato) {
        String permissao = Manifest.permission.CALL_PHONE;
        // Código da requisição "chamada"
        final Integer CHAMADA = 0x2;

        if (ContextCompat.checkSelfPermission(activityPrincipal, permissao) !=
                PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activityPrincipal, permissao)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(activityPrincipal, new String[]{permissao}, CHAMADA);

            } else {
                ActivityCompat.requestPermissions(activityPrincipal, new String[]{permissao}, CHAMADA);
            }
        } else {
            // Call restAPI, method to save the call in the database for further report
            ChamadaRequest request = new ChamadaRequest(activityPrincipal, contato.getTelefone());
            request.salva(contato);
        }
    }

    private void carregaFragmentoEdicaoContato(final Contato contato) {
        activityPrincipal.getSupportActionBar().setTitle("Editar Contato");

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragmento = new FragmentoEdicaoContato();
                Bundle bundle = new Bundle();
                bundle.putInt(FragmentoAgenda.ID_CONTATO, contato.getId());
                bundle.putString(FragmentoAgenda.NOME_CONTATO, contato.getNome());
                bundle.putString(FragmentoAgenda.TELEFONE_CONTATO, contato.getTelefone());
                bundle.putString(FragmentoAgenda.EMAIL_CONTATO, contato.getEmail());
                bundle.putString(FragmentoAgenda.EMPRESA_CONTATO, contato.getEmpresa());
                fragmento.setArguments(bundle);
                FragmentTransaction fragmentTransaction = activityPrincipal.
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragmento);
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
        return apiService.listaContatos(token,
                chaveCriptografada);
    }

    @Override
    void exibeMensagemSucesso() {
        Toast.makeText(activityPrincipal,
                "Você ainda não possui contatos salvos!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    void executaAcaoPosterior(String resposta) {
        try {
            // jsonlist to hold the contacts list that came on the
            JSONArray listaDeContatos = new JSONArray(resposta);
            // extract values from the JSON
            extraiValoresJSON(listaDeContatos);
            // Monta o Adapter
            recyclerView.setAdapter(new AgendaAdapter
                    (contatosList, R.layout.list_item_contato,
                            activityPrincipal, this));
        } catch (JSONException e) {
            Log.e("Exceção JSON", e.getLocalizedMessage());
        }
    }
}
