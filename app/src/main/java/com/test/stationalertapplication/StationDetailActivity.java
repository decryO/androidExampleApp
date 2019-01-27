package com.test.stationalertapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class StationDetailActivity extends AppCompatActivity {

    private TextView detailText;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.toolbar);
        detailText = findViewById(R.id.eki_detail);

        toolbar.setTitle("駅情報について");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String str = "このアプリケーションの路線名、駅名、座標等は<b>Heartrails</b> \n (<u><font color='aqua'>http://express.heartrails.com/</u></font>) 様のAPIより取得し使用しております。";
        detailText.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT));

    }

}
