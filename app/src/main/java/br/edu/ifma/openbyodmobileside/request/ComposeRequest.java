package br.edu.ifma.openbyodmobileside.request;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoInbox;
import br.edu.ifma.openbyodmobileside.modelo.EmailUsuario;
import br.edu.ifma.openbyodmobileside.modelo.Resource;
import retrofit2.Call;

/**
 * Created by Windows on 24/08/2017.
 */

public class ComposeRequest extends Request {
    private ActivityPrincipal activityPrincipal;

    public ComposeRequest(ActivityPrincipal activityPrincipal) {

        Log.i("Requisição", "Compose");

        this.activityPrincipal = activityPrincipal;
        progressDialog = new ProgressDialog(this.activityPrincipal);
        progressDialog.setTitle("Enviando o e-mail");
        progressDialog.setMessage("Aguarde...");
        progressDialog.setProgressStyle(R.style.ProgressBar);
        progressDialog.show();
    }

    public void envia(EmailUsuario emailUsuario) {
        // Envia um e-mail
        efetuaRequisicao(emailUsuario.toString(), this.activityPrincipal);
    }

    @Override
    Call getRequest(Resource resource, String token, String chaveCriptografada) {
        return apiService.enviaEmail(resource,
                token, chaveCriptografada);
    }

    @Override
    void exibeMensagemSucesso() {
        Toast.makeText(activityPrincipal,
                "E-mail enviado com sucesso!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    void executaAcaoPosterior(String mensagemDecriptografada) {
        // Carrega a caixa de entrada atualizada
        // activityPrincipal.carregaFragmento("E-mail", new FragmentoInbox());
    }
}
