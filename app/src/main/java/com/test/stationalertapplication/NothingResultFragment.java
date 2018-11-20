package com.test.stationalertapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NothingResultFragment extends Fragment {

    private String title, message;
    private TextView text_title, text_message;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_nothingresult, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        title = bundle.getString("Title");
        message = bundle.getString("Message");

        text_title = view.findViewById(R.id.noResult_Title);
        text_message = view.findViewById(R.id.noResult_Message);

        text_title.setText(title);
        text_message.setText(message);
    }
}
