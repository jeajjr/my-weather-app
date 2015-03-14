package com.homespotter.weatherinternshipproject.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.homespotter.weatherinternshipproject.R;

public class ActivityAboutApp extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView returnButton = (ImageView) toolbar.findViewById(R.id.imageViewToolboxLeftButton);
        returnButton.setImageResource(R.drawable.return_icon);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((TextView) findViewById(R.id.textViewToolboxTitle)).setText(getString(R.string.about_this_app));

        findViewById(R.id.imageButtonLinkedIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://br.linkedin.com/in/jealmas";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        findViewById(R.id.imageButtonGitHub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/jeajjr/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        setSupportActionBar(toolbar);
    }
}
