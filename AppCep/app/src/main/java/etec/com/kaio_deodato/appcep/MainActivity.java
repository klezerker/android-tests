package etec.com.kaio_deodato.appcep;

import android.app.DownloadManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btnBuscar, btnLimpar;
    private EditText edtCEP;
    private TextView txtLogradouro, txtBairro, txtCidade, txtUF;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBuscar = findViewById(R.id.btnBuscar);
        btnLimpar = findViewById(R.id.btnLimpar);
        edtCEP = findViewById(R.id.edtCEP);
        txtLogradouro = findViewById(R.id.txtLogradouro);
        txtBairro = findViewById(R.id.txtBairro);
        txtCidade = findViewById(R.id.txtCidade);
        txtUF = findViewById(R.id.txtUF);
        progressBar = findViewById(R.id.progressBar);

        aplicarMascaraCEP();

        btnBuscar.setOnClickListener(v -> {
            String cep = edtCEP.getText().toString().replace("-", "");
            if (cep.length() != 8) {
                edtCEP.setError("Favor informe um CEP válido");
            } else {
                //salvarUltimoCEP(cep);
                buscarCEP("https://viacep.com.br/ws/" + cep + "/json/");
            }
        });

        btnLimpar.setOnClickListener(v -> limpar());
    }
    //fim do método OnCreate
    //inicio da programação dos métodos

    private void aplicarMascaraCEP() {
        edtCEP.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isUpdating) return;
                // - //D remove qualquer caracter que não seja numero
                String digits = charSequence.toString().replaceAll("\\D", "");
                StringBuilder formatted = new StringBuilder();
                if (digits.length() > 5){
                    formatted.append(digits.substring(0, 5))
                            .append("-")
                            .append(digits.substring(5));
                } else {
                    formatted.append(digits);
                }
                isUpdating = true;
                edtCEP.setText(formatted.toString());
                edtCEP.setSelection(formatted.length());
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void buscarCEP(String url) {
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(
            Request.Method.GET,
            url,
            //quando dar certo
            response -> {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject obj = new JSONObject(response);
                    txtLogradouro.setText("Rua: " + obj.optString("logradouro", "-"));
                    txtBairro.setText("Bairro: " + obj.optString("bairro", "-"));
                    txtCidade.setText("Cidade: " + obj.optString("localidade", "-"));
                    txtUF.setText("UF: " + obj.optString("uf", "-"));
                } catch (JSONException e) {
                    mostrarSnack("CEP inválido");
                }
            },
            //quando dar erro no volley
            error -> {
                progressBar.setVisibility(View.GONE);
                mostrarSnack("Erro de conexão: " + error.getMessage());
            }
        );
        queue.add(request);
    }

    private void limpar(){
        edtCEP.setText("");
        txtLogradouro.setText("Rua: -");
        txtBairro.setText("Bairro: -");
        txtCidade.setText("Cidade: -");
        txtUF.setText("UF: -");
    }

    private void mostrarSnack(String msg){
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }
}