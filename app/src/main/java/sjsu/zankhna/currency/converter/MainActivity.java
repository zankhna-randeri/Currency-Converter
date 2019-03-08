package sjsu.zankhna.currency.converter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String broadcast = "sjsu.zankhna.currency.EXCHANGE_RECEIVER";
    private final String valueReceiver = "sjsu.zankhna.currency.CONVERTER_RECEIVER";

    private String symbol = "";

    private BroadcastReceiver exchangeReceiver;
    private EditText edtAmount;
    private EditText edtCurrency;
    private Button btnEuro;
    private Button btnPound;
    private Button btnINR;
    private Button btnClose;
    private Button btnConvert;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setReceiver();
    }

    private void setReceiver() {
        exchangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = String.valueOf(intent.getFloatExtra("amount", 0f));
                Log.d("Receiver : ", result);
                txtResult.setText("Dollar Amount $" + edtAmount.getText() + " converted to " +
                        result + " " +
                        edtCurrency.getText().toString());
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(valueReceiver);
        registerReceiver(exchangeReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exchangeReceiver != null) {
            unregisterReceiver(exchangeReceiver);
        }
    }

    private void initView() {
        edtAmount = findViewById(R.id.edtAmount);
        edtCurrency = findViewById(R.id.edtTargetCurrency);
        btnClose = findViewById(R.id.btnClose);
        btnConvert = findViewById(R.id.btnConvert);
        btnEuro = findViewById(R.id.btnEuro);
        btnPound = findViewById(R.id.btnPound);
        btnINR = findViewById(R.id.btnINR);
        txtResult = findViewById(R.id.txtResult);

        btnINR.setOnClickListener(this);
        btnPound.setOnClickListener(this);
        btnEuro.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnConvert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                this.finish();
                break;
            case R.id.btnConvert:
                sendBroadcast();
                break;
            case R.id.btnEuro:
                edtCurrency.setText(btnEuro.getText().toString());
                symbol = "EUR";
                break;
            case R.id.btnINR:
                edtCurrency.setText(btnINR.getText().toString());
                symbol = "INR";
                break;
            case R.id.btnPound:
                edtCurrency.setText(btnPound.getText().toString());
                symbol = "GBP";
                break;
        }
    }

    private void sendBroadcast() {
        String amount = edtAmount.getText().toString().trim();
        String currency = edtCurrency.getText().toString().trim();
        if (amount.isEmpty()) {
            showError(getResources().getString(R.string.txt_err_amt));
        } else if (currency.isEmpty()) {
            showError(getResources().getString(R.string.txt_err_currency));
        } else {
            Log.d("broadcast-------", "sendBroadcast invoked");
            Intent currencyExchangeBroadcast = new Intent(broadcast);
            currencyExchangeBroadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            currencyExchangeBroadcast.putExtra("amount",
                    Float.parseFloat(amount));
            currencyExchangeBroadcast.putExtra("currency", currency);
            currencyExchangeBroadcast.putExtra("symbol", symbol);
            if (Build.VERSION.SDK_INT >= 8) {
                currencyExchangeBroadcast.setComponent(null);
            }
            sendBroadcast(currencyExchangeBroadcast);
        }
    }

    private void showError(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
