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

public class ActivityQuemSomos extends AppCompatActivity {
    @Bind(R.id.quem_somos)
    JustifiedTextView jtvQuemSomos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quem_somos);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String texto = "O OpenBYOD é um projeto Open-Source desenvolvido" +
                " com o propósito de trazer o advento do BYOD - Bring Your" +
                " Own Device - a empresas de pequeno e médio porte. Objetiva" +
                " incentivar o uso dessa prática cada vez mais presente no " +
                " ambiente corporativo inserida pelo fenômeno da Consumerização" +
                " de TI. \n\nDê adeus aos dipositivos móveis fornecidos pela sua empresa" +
                " e desfrute de um ambiente de trabalho no seu próprio dipositivo." +
                " Consulte seus contatos, efetue ligações, leia e envie e-mails com" +
                " segurança e a garantia de que as informações da empresa e, pricipalmente," +
                " as suas informações estão totalmente protegidas.";
        jtvQuemSomos.setText(texto);
        jtvQuemSomos.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        jtvQuemSomos.setLineSpacing(6);
        jtvQuemSomos.setAlignment(Paint.Align.LEFT);
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
