package br.edu.ifma.openbyodmobileside.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.MenuItem;

import br.edu.ifma.openbyodmobileside.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import ir.noghteh.JustifiedTextView;


public class ActivityPoliticaDePrivacidade extends AppCompatActivity {
    @Bind(R.id.politica_privacidade)
    JustifiedTextView jtvPoliticaPrivacidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_de_privacidade);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String texto = "Todas as suas informações pessoais recolhidas, serão usadas para ajudar " +
                " a tornar o uso do aplicativo mais produtivo e agradável possível.\n" +
                " A garantia da confidencialidade dos dados pessoais dos usuários" +
                " é importante para o OpenBYOD.\n\n" +
                " Todas as informações pessoais relativas a quaisquer pessoa envolvida no projeto" +
                " OpenBYOD serão tratadas em concordância com a Lei da " +
                " Proteção de Dados Pessoais de 26 de outubro de 1998 (Lei n.º 67/98).\n\n" +
                " A informação pessoal recolhida pode incluir o seu nome, e-mail, número de " +
                " telefone e/ou telemóvel, endereço, data de nascimento e/ou outros.\n\n" +
                " O uso do OpenBYOD pressupõe a aceitação deste Acordo de privacidade. " +
                " A equipa do OpenBYOD reserva-se ao direito de alterar este acordo sem aviso prévio. " +
                " Deste modo, recomendamos que consulte a nossa política de privacidade com " +
                " regularidade de forma a estar sempre atualizado.";
        jtvPoliticaPrivacidade.setText(texto);
        jtvPoliticaPrivacidade.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        jtvPoliticaPrivacidade.setLineSpacing(6);
        jtvPoliticaPrivacidade.setAlignment(Paint.Align.LEFT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
