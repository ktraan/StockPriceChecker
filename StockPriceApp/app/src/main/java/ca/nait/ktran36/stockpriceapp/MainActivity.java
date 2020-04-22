package ca.nait.ktran36.stockpriceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @author Kevin Tran
 * @version 1.0.0
 *  This app is made to search a stock ticker symbol, display data accordingly.
 *  It will also feature a historical monthly data chart
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et;
    ArrayList<HashMap<String, String>> stockList;
    ArrayList<HashMap<String, String>> historyList;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > 9) {
            // This allows everything on all threads to prevent usage of the main thread
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        stockList = new ArrayList<>();
        historyList = new ArrayList<>();

        Button button = findViewById(R.id.main_button);
        button.setOnClickListener(this);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setNoDataText("");
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.main_button: {
                EditText et = (EditText) findViewById(R.id.main_edittext_ticker);
                String etText = et.getText().toString();
                if (etText.length() <= 0) {
                    Toast.makeText(this, "Please enter a ticker symbol.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        new GetStock().execute();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
                    }
                }

            }
        }
    }

    private class GetStock extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Retrieving Data...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler handler = new HttpHandler();
            String API_KEY = "Tpk_299216c966614c3bad2ab9ada2d3ba9d";
            et = (EditText) findViewById(R.id.main_edittext_ticker);
            @SuppressLint("WrongThread") String ticker = et.getText().toString();
            String url = "https://sandbox.iexapis.com/stable/stock/" + ticker + "/quote?token=" + API_KEY;
            String jsonString = handler.makeServiceCall(url);
            //    Toast.makeText(MainActivity.this, "Response from URL: " + jsonString, Toast.LENGTH_LONG).show();
            if (jsonString != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    String symbol = jsonObject.getString("symbol");
                    String companyName = jsonObject.getString("companyName");
                    String price = jsonObject.getString("latestPrice");
                    String peRatio = jsonObject.getString("peRatio");
                    if (peRatio == "null") {
                        peRatio = "N/A";
                    }
                    String week52Low = jsonObject.getString("week52Low");
                    String week52High = jsonObject.getString("week52High");
                    HashMap<String, String> stock = new HashMap<>();
                    stock.put("symbol", symbol);
                    stock.put("companyName", companyName);
                    stock.put("latestPrice", price);
                    stock.put("peRatio", peRatio);
                    stock.put("week52Low", week52Low);
                    stock.put("week52High", week52High);
                    stockList.add(stock);

                    //Toast.makeText(MainActivity.this, "symbol + company:" + symbol + companyName, Toast.LENGTH_LONG).show();
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Please Enter a Ticker Symbol",
                                Toast.LENGTH_LONG).show();
                    }
                });

            }
            // "https://sandbox.iexapis.com/stable/stock/MSFT/chart/1m?token=Tpk_299216c966614c3bad2ab9ada2d3ba9d";
            String HISTORY_URL = "https://sandbox.iexapis.com/stable/stock/" + ticker + "/chart/1m?token=" + API_KEY;
            String jsonArrayString = handler.makeServiceCall(HISTORY_URL);
            if (jsonArrayString != null) {
                try {

                    JSONArray historyArray = new JSONArray(jsonArrayString);

                    for (int index = 0; index < historyArray.length(); index++) {
                        JSONObject singleHistory = historyArray.getJSONObject(index);

                        String date = singleHistory.getString("date");
                        String close = singleHistory.getString("close");
                        String high = singleHistory.getString("high");
                        String low = singleHistory.getString("low");
                        String changePercent = singleHistory.getString("changePercent");

                        HashMap<String, String> historyMap = new HashMap<>();
                        historyMap.put("date", date);
                        historyMap.put("close", close);
                        historyMap.put("high", high);
                        historyMap.put("low", low);
                        historyMap.put("changePercent", changePercent);

                        historyList.add(historyMap);

                    }
                } catch (final JSONException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String symbol = stockList.get(0).get("symbol");
            String companyName = stockList.get(0).get("companyName");
            String peRatio = stockList.get(0).get("peRatio");
            String price = stockList.get(0).get("latestPrice");
            String weekLow = stockList.get(0).get("week52Low");
            String weekHigh = stockList.get(0).get("week52High");

            TextView tvSymbol = findViewById(R.id.main_symbol_fetch);
            TextView tvCompanyName = findViewById(R.id.main_company_fetch);
            TextView tvPeRatio = findViewById(R.id.main_peratio_fetch);
            TextView tvPrice = findViewById(R.id.main_price_fetch);
            TextView tvWeekLow = findViewById(R.id.main_weeklow_fetch);
            TextView tvWeekHigh = findViewById(R.id.main_weekhigh_fetch);

            // Format currency
            Locale locale = new Locale("en", "US");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
            BigDecimal formattedPrice = new BigDecimal(price);
            BigDecimal formattedWeekLow = new BigDecimal(weekLow);
            BigDecimal formattedWeekHigh = new BigDecimal(weekHigh);
            currencyFormatter.format(formattedPrice);
            currencyFormatter.format(formattedWeekLow);
            currencyFormatter.format(formattedWeekHigh);

            tvSymbol.setText(symbol);
            tvCompanyName.setText(companyName);
            tvPeRatio.setText(peRatio);
            tvPrice.setText("$" + formattedPrice);
            tvWeekLow.setText("$" + formattedWeekLow);
            tvWeekHigh.setText("$" + formattedWeekHigh);

            // Set stockList to empty to allow a second search
            stockList.clear();
            TextView tv = findViewById(R.id.main_textview_chart_title);
            tv.setText("Monthly Historical Data for " + companyName);
            setMonthlyDataAndRenderChart();

            // Set historyList to empty to be able to re render the chart
            historyList.clear();

        }
    }

    public void setMonthlyDataAndRenderChart() {
        List<Entry> values = new ArrayList<Entry>();
        List<String> strings = new ArrayList<>();
        // Set default chart text to empty

        for (int index = 0; index < historyList.size(); index++) {
            Entry entry = new Entry(index + 1, Float.parseFloat(historyList.get(index).get("close")));
            String a = historyList.get(index).get("date");
            strings.add(a);
            values.add(entry);
        }
        LineDataSet lineDataSet = new LineDataSet(values, "Daily Prices");

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);
        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getDescription().setText("Double click for more info");
        chart.setHighlightPerDragEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // Change the xAxis to bottom
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


    }


}
