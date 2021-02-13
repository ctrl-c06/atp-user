package com.atpuser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atpuser.ContractModels.CovidStats.StatsResponse;
import com.atpuser.Contracts.ICovidStats;
import com.atpuser.Helpers.SharedPref;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CovidStatsActivity extends AppCompatActivity {

    TextView philippinesConfirmedCase, philippinesRecovered, philippinesDeaths;
    TextView worldWideConfirmedCase, worldWideRecovered, worldWideDeaths;

    boolean hasInternet = false;


    Handler handler = new Handler();
    Runnable runnable;
    int delay = 3000;

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    public long daysBetween(Long LAST_FETCH_TIME, Long CURRENT_TIME ) {
        long diffInMillis = CURRENT_TIME - LAST_FETCH_TIME;
        return TimeUnit.MILLISECONDS.toDays(diffInMillis);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_stats);




        long lastFetchedTime = SharedPref.getSharedPreferenceLong(this, "LAST_FETCHED", 0);
        long currentTime = System.currentTimeMillis();



        philippinesConfirmedCase = findViewById(R.id.philippines_confirmed_case);
        philippinesRecovered = findViewById(R.id.philippines_recovered);
        philippinesDeaths = findViewById(R.id.philippines_deaths);

        worldWideConfirmedCase = findViewById(R.id.world_wide_confirmed_case);
        worldWideRecovered = findViewById(R.id.world_wide_recovered);
        worldWideDeaths = findViewById(R.id.world_wide_deaths);

        if(daysBetween(lastFetchedTime, currentTime) >= 1) {
            askUserTofetchData();
        } else { // Not passed by days
            checkIfDataStatsIsPresent();
        }




    }

    private void checkIfDataStatsIsPresent() {
        int checkSum = 0;
        checkSum += SharedPref.getSharedPreferenceLong(this, "WORLDWIDE_CONFIRM", 0);
        checkSum += SharedPref.getSharedPreferenceLong(this, "WORLDWIDE_RECOVERED", 0);
        checkSum += SharedPref.getSharedPreferenceLong(this, "WORLDWIDE_DEATHS", 0);

        checkSum += SharedPref.getSharedPreferenceLong(this, "PHILIPPINES_CONFIRM", 0);
        checkSum += SharedPref.getSharedPreferenceLong(this, "PHILIPPINES_RECOVERED", 0);
        checkSum += SharedPref.getSharedPreferenceLong(this, "PHILIPPINES_DEATHS", 0);
        if(checkSum != 0) {
            philippinesConfirmedCase.setText(String.format("" +
                            "%s",
                    NumberFormat.getInstance().format(SharedPref.getSharedPreferenceLong(this, "PHILIPPINES_CONFIRM", 0)))
            );


            philippinesRecovered.setText(String.format("" +
                            "%s",
                    NumberFormat.getInstance().format(SharedPref.getSharedPreferenceLong(this, "PHILIPPINES_RECOVERED", 0)))
            );

            philippinesDeaths.setText(String.format("" +
                            "%s",
                    NumberFormat.getInstance().format(SharedPref.getSharedPreferenceLong(this, "PHILIPPINES_DEATHS", 0)))
            );

            worldWideConfirmedCase.setText(String.format("" +
                            "%s",
                    NumberFormat.getInstance().format(SharedPref.getSharedPreferenceLong(this, "WORLDWIDE_CONFIRM", 0)))
            );

            worldWideRecovered.setText(String.format("" +
                            "%s",
                    NumberFormat.getInstance().format(SharedPref.getSharedPreferenceLong(this, "WORLDWIDE_RECOVERED", 0)))
            );

            worldWideDeaths.setText(String.format("" +
                            "%s",
                    NumberFormat.getInstance().format(SharedPref.getSharedPreferenceLong(this, "WORLDWIDE_DEATHS", 0)))
            );
        } else {
            askUserTofetchData();
        }
    }

    private void askUserTofetchData() {
        AlertDialog.Builder notificationDialog = new AlertDialog.Builder(CovidStatsActivity.this);
        notificationDialog.setTitle("ATP Notification");
        notificationDialog.setMessage("System detect that your using an active internet connection do you like to get new updates of the COVID-19?");
        notificationDialog.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            // fetch data from api.
            fetchStats();
        });

        notificationDialog.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });

        notificationDialog.show();
    }

    private void fetchStats() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Gathering new update for COVID-19");
        progressDialog.setCancelable(false);
        progressDialog.show();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.stats_base_url))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ICovidStats service = retrofit.create(ICovidStats.class);

        Call<StatsResponse> statsResponseCall = service.getResponse();
        statsResponseCall.enqueue(new Callback<StatsResponse>() {
            @Override
            public void onResponse(Call<StatsResponse> call, Response<StatsResponse> response) {

                SharedPref.setSharedPreferenceLong(getApplicationContext(), "LAST_FETCHED", Calendar.getInstance().getTimeInMillis());

                StatsResponse statsResponse = response.body();

                philippinesConfirmedCase.setText(String.format("" +
                        "%s",
                        NumberFormat.getInstance().format(statsResponse.getCases().getTotal()))
                );


                philippinesRecovered.setText(String.format("" +
                                "%s",
                        NumberFormat.getInstance().format(statsResponse.getCases().getRecovered()))
                );

                philippinesDeaths.setText(String.format("" +
                                "%s",
                        NumberFormat.getInstance().format(statsResponse.getCases().getDeaths()))
                );

                worldWideConfirmedCase.setText(String.format("" +
                                "%s",
                        NumberFormat.getInstance().format(statsResponse.getWorld().getTotal()))
                );

                worldWideRecovered.setText(String.format("" +
                                "%s",
                        NumberFormat.getInstance().format(statsResponse.getWorld().getRecovered()))
                );

                worldWideDeaths.setText(String.format("" +
                                "%s",
                        NumberFormat.getInstance().format(statsResponse.getWorld().getDeaths()))
                );


                 SharedPref.setSharedPreferenceLong(getApplicationContext(), "WORLDWIDE_CONFIRM", statsResponse.getWorld().getTotal());
                 SharedPref.setSharedPreferenceLong(getApplicationContext(), "WORLDWIDE_RECOVERED", statsResponse.getWorld().getRecovered());
                 SharedPref.setSharedPreferenceLong(getApplicationContext(), "WORLDWIDE_DEATHS", statsResponse.getWorld().getDeaths());

                 SharedPref.setSharedPreferenceLong(getApplicationContext(), "PHILIPPINES_CONFIRM", statsResponse.getCases().getTotal());
                 SharedPref.setSharedPreferenceLong(getApplicationContext(), "PHILIPPINES_RECOVERED", statsResponse.getCases().getRecovered());
                 SharedPref.setSharedPreferenceLong(getApplicationContext(), "PHILIPPINES_DEATHS", statsResponse.getCases().getDeaths());

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<StatsResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CovidStatsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    private class runOnBackground extends AsyncTask<Void , Integer , Void> {

        private WeakReference<CovidStatsActivity> activityReference;

        // Only retain a weak reference to the activity
        runOnBackground(CovidStatsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
           hasInternet = internetIsConnected();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(hasInternet) {
                fetchStats();
            }

        }

    }


}