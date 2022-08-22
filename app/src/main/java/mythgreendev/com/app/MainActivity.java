package mythgreendev.com.app;

import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;

import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import mythgreendev.com.app.helper.SqliteHelper;

public class MainActivity extends AppCompatActivity {
    TextView textRevenue, textExpenditure, textBalance;
    ListView listKas;
    SwipeRefreshLayout swipeRefresh;

    String queryKas, queryAmount;
    SqliteHelper sqliteHelper;
    Cursor cursor;

    public static TextView textFilter;
    public static String transactionId, dateFrom, dateTo;
    public static boolean filter;

    ArrayList<HashMap<String, String>> kasFlow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        transactionId = "";
        dateFrom      = "";
        dateTo        = "";
        filter        = false;

        textRevenue     = (TextView) findViewById(R.id.text_revenue);
        textExpenditure = (TextView) findViewById(R.id.text_expenditure);
        textBalance     = (TextView) findViewById(R.id.text_balance);
        listKas         = (ListView) findViewById(R.id.list_kas);
        swipeRefresh    = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        textFilter      = (TextView) findViewById(R.id.text_filter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryKas    = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tanggal FROM transaksi ORDER BY transaksi_id DESC";
                queryAmount = "SELECT SUM(jumlah) AS total, " +
                              "(SELECT SUM(jumlah) FROM transaksi WHERE status='REVENUE') AS revenue, " +
                              "(SELECT sum(jumlah) FROM transaksi WHERE status='EXPENDITURE') AS expenditure " +
                              "FROM transaksi";

                kasAdapter();
            }
        });

        sqliteHelper = new SqliteHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code here
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        queryKas    = "SELECT *, strftime('%d-%m-%Y', tanggal) AS tanggal FROM transaksi ORDER BY tanggal DESC";
        queryAmount = "SELECT SUM(jumlah) AS total, " +
                      "(SELECT SUM(jumlah) FROM transaksi WHERE status='REVENUE') AS revenue, " +
                      "(SELECT sum(jumlah) FROM transaksi WHERE status='EXPENDITURE') AS expenditure " +
                      "FROM transaksi";

        if (filter) {
            queryKas    = "SELECT *, strftime('%d-%m-%Y', tanggal) AS tanggal FROM transaksi " +
                          "WHERE (tanggal >= '" + dateFrom + "') AND (tanggal <= '" + dateTo + "') ORDER BY tanggal ASC";
            queryAmount = "SELECT SUM(jumlah) AS total, " +
                          "(SELECT SUM(jumlah) FROM transaksi WHERE status='REVENUE' AND (tanggal >= '" + dateFrom + "') AND (tanggal <= '" + dateTo + "')), " +
                          "(SELECT sum(jumlah) FROM transaksi WHERE status='EXPENDITURE'AND (tanggal >= '" + dateFrom + "') AND (tanggal <= '" + dateTo + "')) " +
                          "FROM transaksi WHERE (tanggal >= '" + dateFrom + "') AND (tanggal <= '" + dateTo + "')";
        }

        kasAdapter();
    }

    private void kasAdapter() {
        swipeRefresh.setRefreshing(false);
        kasFlow.clear();
        listKas.setAdapter(null);

        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(queryKas, null);
        cursor.moveToFirst();

        for (int i=0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);

            HashMap<String, String> map = new HashMap<>();
            map.put("transaksi_id", cursor.getString(0));
            map.put("status", cursor.getString(1));
            map.put("jumlah", rupiahFormat.format(cursor.getInt(2)));
            map.put("keterangan", cursor.getString(3));
            map.put("tanggal", cursor.getString(5));

            kasFlow.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, kasFlow, R.layout.list_kas,
                new String[]{"transaksi_id", "status", "jumlah", "keterangan", "tanggal"},
                new int[]{R.id.text_transaction_id, R.id.text_status,
                          R.id.text_amount, R.id.text_description, R.id.text_date});

        listKas.setAdapter(simpleAdapter);
        listKas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transactionId = ((TextView) view.findViewById(R.id.text_transaction_id)).getText().toString();
                listMenu();
            }
        });

        kasTotal();
    }

    private void kasTotal() {
        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(queryAmount, null);
        cursor.moveToFirst();
        textRevenue.setText(rupiahFormat.format(cursor.getDouble(1)));
        textExpenditure.setText(rupiahFormat.format(cursor.getDouble(2)));
        textBalance.setText(rupiahFormat.format(cursor.getDouble(1) - cursor.getDouble(2)));

        if (!filter) {
            textFilter.setText("ALL");
        }
        filter = false;
    }

    private void listMenu() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.list_menu);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView textEdit  = (TextView) dialog.findViewById(R.id.text_edit);
        TextView textHapus = (TextView) dialog.findViewById(R.id.text_hapus);

        textEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });

        textHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                hapus();
            }
        });
    }

    private void hapus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure to delete this transaction?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                        database.execSQL("DELETE FROM transaksi WHERE transaksi_id = '" + transactionId +"'");
                        Toast.makeText(MainActivity.this, "Transaction has been deleted",
                                Toast.LENGTH_LONG).show();
                        kasAdapter();
                    }


                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            startActivity(new Intent(MainActivity.this, FilterActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}