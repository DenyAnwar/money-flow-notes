package mythgreendev.com.app;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import mythgreendev.com.app.helper.CurrentDate;

public class FilterActivity extends AppCompatActivity {
    MainActivity M = new MainActivity();

    EditText editFrom, editTo;
    Button btnSave;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        editFrom = (EditText) findViewById(R.id.edit_from);
        editTo   = (EditText) findViewById(R.id.edit_to);
        btnSave = (Button) findViewById(R.id.btn_save);

        Calendar calendar = Calendar.getInstance();
        final int year  = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day   = calendar.get(Calendar.DAY_OF_MONTH);

        editFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        M.dateFrom = numberFormat.format(year) + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(dayOfMonth);
                        editFrom.setText(numberFormat.format(dayOfMonth) + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(year));
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);
                datePickerDialog.show();
            }
        });

        editTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        M.dateTo = numberFormat.format(year) + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(dayOfMonth);
                        editTo.setText(numberFormat.format(dayOfMonth) + "-" + numberFormat.format(month + 1) + "-" + numberFormat.format(year));
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);
                datePickerDialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (M.dateFrom.equals("") || M.dateTo.equals("")) {
                    Toast.makeText(FilterActivity.this, "Please fill the data correctly",
                            Toast.LENGTH_LONG).show();
                } else {
                    M.filter = true;
                    M.textFilter.setText(editFrom.getText().toString() + " - " + editTo.getText().toString());
                    finish();
                }
            }
        });

        // Set title
        getSupportActionBar().setTitle("Filter");

        // Back icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Click back icon
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}