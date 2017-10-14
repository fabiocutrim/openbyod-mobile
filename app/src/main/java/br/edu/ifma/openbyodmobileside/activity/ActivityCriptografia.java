package br.edu.ifma.openbyodmobileside.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import java.text.DecimalFormat;

import br.edu.ifma.openbyodmobileside.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Windows on 12/10/2017.
 */

public class ActivityCriptografia extends Activity {
    @Bind(R.id.chaveSimetrica)
    EditText txtChaveSimetrica;
    @Bind(R.id.mensagemOriginal)
    EditText txtMensagemOriginal;
    @Bind(R.id.requisicao)
    EditText txtRequisicao;
    @Bind(R.id.chaveCriptografada)
    EditText txtChaveCriptografada;
    @Bind(R.id.tempoGeracaoChave)
    EditText txtTempoGeracaoChave;
    @Bind(R.id.tempoCriptografiaRequisicao)
    EditText txtTempoCriptografiaRequisicao;
    @Bind(R.id.tempoCriptografiaChave)
    EditText txtTempoCriptografiaChave;
    @Bind(R.id.tempoResposta)
    EditText txtTempoResposta;
    @Bind(R.id.statusCode)
    EditText txtStatusCode;
    @Bind(R.id.tokenEnviado)
    EditText txtTokenEnviado;
    @Bind(R.id.tokenRecebido)
    EditText txtTokenRecebido;
    @Bind(R.id.mensagemResposta)
    EditText txtMensagemResposta;
    @Bind(R.id.mensagemDecriptografada)
    EditText txtMensagemDecriptografada;

    String chaveSimetrica;
    String mensagemOriginal;
    String requisicao;
    String chaveCriptografada;
    String tempoGeracaoChave;
    String tempoCriptografiaRequisicao;
    String tempoCriptografiaChave;
    String tempoResposta;
    String statusCode;
    String tokenEnviado;
    String tokenRecebido;
    String mensagemResposta;
    String mensagemDecriptografada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criptografia);
        ButterKnife.bind(this);

        chaveSimetrica = System.getProperty("chaveSimetrica");
        mensagemOriginal = System.getProperty("mensagemOriginal");
        requisicao = System.getProperty("requisicao");
        chaveCriptografada = System.getProperty("chaveCriptografada");
        tempoGeracaoChave = System.getProperty("tempoGeracaoChaveSimetrica");
        tempoCriptografiaRequisicao = System.getProperty("tempoCriptografiaRequisicao");
        tempoCriptografiaChave = System.getProperty("tempoCriptografiaChave");
        tempoResposta = System.getProperty("tempoRespostaRequisicao");
        statusCode = System.getProperty("statusCode");
        tokenEnviado = System.getProperty("tokenEnviado");
        tokenRecebido = System.getProperty("tokenRecebido");
        mensagemResposta = System.getProperty("mensagemResposta");
        mensagemDecriptografada = System.getProperty("mensagemDecriptografada");

        setaCampos();
    }

    private void setaCampos() {
        double tempoTotalResposta = (Double.parseDouble(tempoResposta))/1000000000;
        double tempoTotalCriptografiaRequisicao = (Double.parseDouble(tempoCriptografiaRequisicao))/1000000000;
        double tempoTotalGeracaoChave = (Double.parseDouble(tempoGeracaoChave))/1000000000;
        double tempoTotalCriptografiaChave = (Double.parseDouble(tempoCriptografiaChave))/1000000000;

        DecimalFormat formatter = new DecimalFormat("0.######");

        txtChaveCriptografada.setText(chaveCriptografada);
        txtChaveSimetrica.setText(chaveSimetrica);
        txtMensagemDecriptografada.setText(mensagemDecriptografada);
        txtMensagemOriginal.setText(mensagemOriginal);
        txtMensagemResposta.setText(mensagemResposta);
        txtRequisicao.setText(requisicao);
        txtStatusCode.setText(statusCode);
        txtTempoCriptografiaChave.setText(formatter.format(tempoTotalCriptografiaChave));
        txtTempoCriptografiaRequisicao.setText(formatter.format(tempoTotalCriptografiaRequisicao));
        txtTempoGeracaoChave.setText(formatter.format(tempoTotalGeracaoChave));
        txtTempoResposta.setText(formatter.format(tempoTotalResposta));
        txtTokenEnviado.setText(tokenEnviado);
        txtTokenRecebido.setText(tokenRecebido);
    }
}
