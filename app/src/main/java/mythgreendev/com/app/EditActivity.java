package mythgreendev.com.app;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import mythgreendev.com.app.helper.CurrentDate;
import mythgreendev.com.app.helper.SqliteHelper;

public class EditActivity extends AppCompatActivity {
    MainActivity M = new MainActivity();

    RadioGroup radioStatus;
    RadioButton radioRevenue, radioExpenditure;
    EditText editAmountMoney, editDescription;
    Button btnSave;

    String status, dates;
    SqliteHelper sqliteHelper;
    Cursor cursor;

    EditText editDate;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        status = "";
        dates = ""; // For to save date in sqlite = yyyy-mm-dd

        radioStatus      = (RadioGroup) findViewById(R.id.radio_status);
        radioRevenue      = (RadioButton) findViewById(R.id.radio_revenue);
        radioExpenditure  = (RadioButton) findViewById(R.id.radio_expenditure);
        editAmountMoney = (EditText) findViewById(R.id.edit_amount_money);
        editDescription  = (EditText) findViewById(R.id.edit_description);
        editDate          = (EditText) findViewById(R.id.edit_date);
        btnSave          = (Button) findViewById(R.id.btn_save);

        sqliteHelper = new SqliteHelper(this);
        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery("SELECT *, strftime('%d-%m-%Y', tanggal) AS tanggal FROM transaksi WHERE transaksi_id='"+ M.transactionId +"'", null);
        cursor.moveToFirst();

        status = cursor.getString(1);
        switch (status) {
            case "REVENUE":
                radioRevenue.setChecked(true);
                break;
            case "EXPENDITURE":
                radioExpenditure.setChecked(true);
                break;
        }

        radioStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_revenue:
                        status = "REVENUE";
                        break;
                    case R.id.radio_expenditure:
                        status = "EXPENDITURE";
                        break;
                }
            }
        });

        editAmountMoney.setText(String.valueOf(cursor.getInt(2)));
        editDescription.setText(cursor.getString(3));
        dates = cursor.getString(4);
        editDate.setText(cursor.getString(5));

        Calendar calendar = Calendar.getInstance();
        final int year  = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day   = calendar.get(Calendar.DAY_OF_MONTH);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        dates = numberFormat.format(year) + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(dayOfMonth);
                        editDate.setText(numberFormat.format(dayOfMonth) + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(year));
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);
                datePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount      = editAmountMoney.getText().toString();
                String description = editDescription.getText().toString();

                if (status.equals("") || amount.equals("") || description.equals("")) {
                    Toast.makeText(EditActivity.this, "Please fill the data correctly",
                            Toast.LENGTH_LONG).show();
                } else {
                    database.execSQL("UPDATE transaksi SET status='" + status + "', jumlah='" + amount + "', " +
                            "keterangan='" + description + "', tanggal='" + dates +
                            "' WHERE transaksi_id='" + M.transactionId + "' ");
                    Toast.makeText(EditActivity.this, "Successfully edited",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        // Set title
        getSupportActionBar().setTitle("Edit");
        // Back icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Click back icon
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}