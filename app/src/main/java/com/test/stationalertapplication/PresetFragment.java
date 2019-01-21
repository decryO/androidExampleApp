package com.test.stationalertapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PresetFragment extends Fragment implements OnRecyclerListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private OnRecyclerListener mListener;

    private ArrayList<String> myDataset = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_preset, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.preset_list);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        for (int i=0; i<20; i++) {
            myDataset.add("Data_0" + String.valueOf(i));
        }

        mAdapter = new RecycleAdapter(getContext(), myDataset, this);
        mRecyclerView.setAdapter(mAdapter);
        RecycleClick();
    }

    @Override
    public void onRecyclerClicked(View v, int position) {
        TextView lineText = (TextView)v.findViewById(R.id.preset_line);
        TextView stationText = (TextView)v.findViewById(R.id.preset_station);
        TextView alertText = (TextView)v.findViewById(R.id.preset_alertline);
        Toast.makeText(getActivity(), lineText.getText().toString()+"\n"+
                stationText.getText().toString()+"\n"+
                alertText.getText().toString(), Toast.LENGTH_SHORT).show();
//        FragmentManager fragmentManager = getFragmentManager();
//        ProgressFragment fragment = new ProgressFragment();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.container, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }

    private void RecycleClick() {
        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        mAdapter.notifyItemMoved(fromPos, toPos);
                        return true;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        myDataset.remove(fromPos);
                        mAdapter.notifyItemRemoved(fromPos);
                    }
                });
        mIth.attachToRecyclerView(mRecyclerView);
    }
}
