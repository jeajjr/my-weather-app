package com.homespotter.weatherinternshipproject.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.homespotter.weatherinternshipproject.R;
import com.homespotter.weatherinternshipproject.data.DataParser;
import com.homespotter.weatherinternshipproject.data.FilesHandler;
import com.homespotter.weatherinternshipproject.data.WeatherClient;

import java.util.ArrayList;

/**
 * Created by José Ernesto on 07/03/2015.
 */
public class DialogFragmentSearchCity extends DialogFragment {
    private static final String TAG = "FragmentMyDialog";

    public static final String ARGS_NAME = "name";
    public static final String ARGS_RATING = "rating";
    public static final String ARGS_WILL_BUY = "buy";

    private DialogFragmentSearchCityResultListener dialogFragmentSearchCityResultListener;

    ArrayList<String> cityList;
    ProgressDialog progressDialog;
    View findCityDialogView;

    private boolean isFirstSearch;

    public DialogFragmentSearchCity() {
        // Required empty public constructor
    }

    public static interface DialogFragmentSearchCityResultListener {
        public abstract void onComplete();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.dialogFragmentSearchCityResultListener = (DialogFragmentSearchCityResultListener) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DialogFragmentSearchCityResultListener");
        }
    }

    public void showDialog() {
        DialogFragmentSearchCity dialog = DialogFragmentSearchCity.newInstance(isFirstSearch);
        dialog.show(getFragmentManager(), "Dialog");
    }

    public static final String ARG_INSTANCE = "instance";
    public static DialogFragmentSearchCity newInstance(boolean firstSearch) {
        DialogFragmentSearchCity fragment = new DialogFragmentSearchCity();

        Bundle args = new Bundle();
        args.putBoolean(ARG_INSTANCE, firstSearch);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isFirstSearch = bundle.getBoolean(ARG_INSTANCE);
        }

        // show progress dialog while data is being fetched
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);

        findCityDialogView = getActivity().getLayoutInflater().inflate(R.layout.layout_city_search_dialog, null);

        final EditText cityName = (EditText) findCityDialogView.findViewById(R.id.editTextDialog);

        final ImageView searchButton = (ImageView) findCityDialogView.findViewById(R.id.imageViewDialogSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityName.getText().toString().length() != 0) {
                    searchCity(cityName.getText().toString());
                }
                else
                    Toast.makeText(getActivity(), getString(R.string.start_dialog_city_empty), Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(findCityDialogView)
                .setTitle(getString(R.string.start_dialog_title));


        if (isFirstSearch) {
            alertDialogBuilder.setMessage(getString(R.string.start_dialog_message));
            alertDialogBuilder.setCancelable(false);
        }
        else {
            alertDialogBuilder.setMessage(getString(R.string.city_search_dialog_message));
            alertDialogBuilder.setCancelable(true);
        }

        return alertDialogBuilder.create();
    }

    public void searchCity(final String city) {
        if (checkInternetAccess()) {
            progressDialog.show();

            new Thread() {
                public void run() {
                    Log.d(TAG, "Searching city: " + city);

                    String data = WeatherClient.getInstance().findCity(city);

                    cityList = DataParser.parseCitySearchResults(data);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            citySearchDone(city);
                        }
                    });

                }
            }.start();
        }
        else
            Toast.makeText(getActivity(), getString(R.string.warning_network_unavailable), Toast.LENGTH_LONG).show();
    }

    private boolean checkInternetAccess() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    private void returnResult() {
        Log.d(TAG, "returnResult");

        dialogFragmentSearchCityResultListener.onComplete();
        this.dismiss();
    }

    private void citySearchDone(final String citySearched) {
        if (cityList == null) {
            Toast.makeText(getActivity(), getString(R.string.warning_error_request), Toast.LENGTH_LONG).show();
        }
        else if (cityList.size() == 0) {
            Toast.makeText(getActivity(), getString(R.string.warning_city_not_found_with_name) + citySearched, Toast.LENGTH_LONG).show();
        }
        else if (cityList.size() == 1) {
            // Save the city and open its weather forecast
            FilesHandler.getInstance().addCityName(getActivity(), cityList.get(0));
            returnResult();
        }
        else {
            TextView listTitle = (TextView) findCityDialogView.findViewById(R.id.textViewDialogListTitle);
            ListView listView = (ListView) findCityDialogView.findViewById(R.id.listViewDialog);

            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, cityList);
            listView.setAdapter(adapter);

            listView.setVisibility(View.VISIBLE);
            listTitle.setVisibility(View.VISIBLE);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Save selected city and open its weather forecast
                    FilesHandler.getInstance().addCityName(getActivity(), cityList.get(position));
                    returnResult();
                }
            });

            findCityDialogView.invalidate();
        }
    }
}
