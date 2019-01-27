package com.test.stationalertapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shashank.sony.fancytoastlib.FancyToast;

public class StopServiceFragment extends Fragment {

    private Button StopButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_stop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MainActivity mainActivity = new MainActivity();
        StopButton = view.findViewById(R.id.stopbutton);
        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.clearStopFragment();
                Intent intent = new Intent(getActivity(), GetLocationService.class);
                getActivity().stopService(intent);
                FancyToast.makeText(getActivity(), "アラームを停止しました", FancyToast.LENGTH_LONG, FancyToast.INFO, true).show();
            }
        });
    }
}
