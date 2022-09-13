package mythgreendev.com.app;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.math.BigDecimal;

import mythgreendev.com.app.helper.SqliteHelper;

public class AddActivity extends AppCompatActivity {
    RadioGroup radioStatus;
    EditText editAmountMoney, editDescription;
    Button btnSave;

    String status;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        status       = "";
        sqliteHelper = new SqliteHelper(this);

        radioStatus      = (RadioGroup) findViewById(R.id.radio_status);

        editAmountMoney = (EditText) findViewById(R.id.edit_amount_money);
        editAmountMoney.addTextChangedListener(new MoneyTextWatcher(editAmountMoney));

        editDescription  = (EditText) findViewById(R.id.edit_description);
        btnSave          = (Button) findViewById(R.id.btn_save);

        radioStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_revenue:
                        status = "Income";
                        break;
                    case R.id.radio_expenditure:
                        status = "Outcome";
                        break;
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BigDecimal amount = MoneyTextWatcher.parseCurrencyValue(editAmountMoney.getText().toString());
                String description = editDescription.getText().toString();

                if (status.equals("") || amount.equals("") || description.equals("")) {
                    Toast.makeText(AddActivity.this, "Please fill the data correctly",
                            Toast.LENGTH_LONG).show();
                } else {
                    SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                    database.execSQL("INSERT INTO transaksi (status, jumlah, keterangan)" +
                            "VALUES ('" + status + "','" + amount + "','" + description +"')");

                    Toast.makeText(AddActivity.this, "Successfully saved",
                            Toast.LENGTH_LONG).show();
                    finish();
                }

                /*Snackbar.make(view, "amount: " + amount + " description: " + description,
                                Snackbar.LENGTH_LONG).show();*/
            }
        });

        // Set title
        getSupportActionBar().setTitle("Add");
        // Back icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Click back icon
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}