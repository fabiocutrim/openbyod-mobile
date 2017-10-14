package br.edu.ifma.openbyodmobileside.request;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.modelo.Contato;
import br.edu.ifma.openbyodmobileside.modelo.Resource;
import retrofit2.Call;

/**
 * Created by Windows on 21/08/2017.
 */

public class ChamadaRequest extends Request {
    private ActivityPrincipal activityPrincipal;
    private String telefone;

    public ChamadaRequest(ActivityPrincipal acticityPrincipal, String telefone) {

        Log.i("Requisição", "Chamada");

        this.activityPrincipal = acticityPrincipal;
        this.telefone = telefone;
        progressDialog = new ProgressDialog(this.activityPrincipal);
        progressDialog.setTitle("Salvando a chamada");
        progressDialog.setMessage("Aguarde...");
        progressDialog.setProgressStyle(R.style.ProgressBar);
        progressDialog.show();
    }

    private int formataNumeroTelefone(String numero) {
        String numeroSemPrimeiroParentese = numero.replace("(", "");
        String numeroSemSegundoParentese = numeroSemPrimeiroParentese.replace(")", "");
        String numeroInteiro = numeroSemSegundoParentese.replace("-", "");
        int telefone = Integer.parseInt(numeroInteiro.substring(2, 10));
        return telefone;
    }

    public void salva(Contato contato) {
        efetuaRequisicao(contato.toString(), activityPrincipal);
    }

    @Override
    Call getRequest(Resource resource, String token, String chaveCriptografada) {
        // Executa a comunicação com o servidor
        return apiService.salvaChamada(resource,
                token, chaveCriptografada);
    }

    @Override
    void exibeMensagemSucesso() {
        Toast.makeText(activityPrincipal, "Chamada salva com sucesso!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    void executaAcaoPosterior(String mensagemDecriptografada) {
        try {
            //Call to the number using the default dial application
            Intent intent = new Intent(Intent.ACTION_CALL);
            // here it will be getting the previously specified number based on the index
            intent.setData(Uri.parse("tel:" + formataNumeroTelefone(telefone)));
            activityPrincipal.startActivity(intent);
        } catch (SecurityException e) {
            Log.e("Erro na chamada", e.getLocalizedMessage());
        }
    }
}
