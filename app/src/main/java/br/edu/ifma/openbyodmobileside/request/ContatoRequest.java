package br.edu.ifma.openbyodmobileside.request;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoAgenda;
import br.edu.ifma.openbyodmobileside.modelo.Contato;
import br.edu.ifma.openbyodmobileside.modelo.Resource;
import retrofit2.Call;

/**
 * Created by Windows on 21/08/2017.
 */

public class ContatoRequest extends Request {
    private ActivityPrincipal activityPrincipal;

    public ContatoRequest(ActivityPrincipal activityPrincipal) {

        Log.i("Requisição", "Contato");

        this.activityPrincipal = activityPrincipal;
        progressDialog = new ProgressDialog(this.activityPrincipal);
        progressDialog.setTitle("Adicionando o contato");
        progressDialog.setMessage("Aguarde...");
        progressDialog.setProgressStyle(R.style.ProgressBar);
        progressDialog.show();
    }

    public void adicionaContato(Contato contato) {
        efetuaRequisicao(contato.toString(), activityPrincipal);
    }

    @Override
    Call getRequest(Resource resource, String token, String chaveCriptografada) {
        return apiService.insereContato(resource,
                token, chaveCriptografada);
    }

    @Override
    void exibeMensagemSucesso() {
        Toast.makeText(activityPrincipal,
                "Contato adicionado com sucesso!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    void executaAcaoPosterior(String mensagemDecriptografada) {
        // Carrega a lista de contatos atualizada
        // activityPrincipal.carregaFragmento("Agenda", new FragmentoAgenda());
    }
}
