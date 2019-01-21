package com.test.stationalertapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class PresetFragment extends Fragment implements OnRecyclerListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DBOpenHelper helper;
    private SQLiteDatabase db;

    private static final String TABLE_NAME = "stationdb";

    private ArrayList<String> lineData = new ArrayList<>();
    private ArrayList<String> stationData = new ArrayList<>();
    private ArrayList<Integer> alertLineData = new ArrayList<>();


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

//        for (int i = 0; i < 20; i++) {
//            myDataset.add("Data_0" + String.valueOf(i));
//        }

        mAdapter = new RecycleAdapter(getContext(), lineData, stationData, alertLineData, this);
        mRecyclerView.setAdapter(mAdapter);
        readData();
        RecycleClick();
        insertData();
    }

    @Override
    public void onRecyclerClicked(View v, int position) {
        TextView lineText = (TextView) v.findViewById(R.id.preset_line);
        TextView stationText = (TextView) v.findViewById(R.id.preset_station);
        TextView alertText = (TextView) v.findViewById(R.id.preset_alertline);
        Toast.makeText(getActivity(), lineText.getText().toString() + "\n" +
                stationText.getText().toString() + "\n" +
                alertText.getText().toString(), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder
                .setTitle("アラームをセットします")
                .setMessage(lineText.getText().toString() + "\n" + stationText.getText().toString() + "に\n半径" + alertText.getText().toString() + "mでセットします")
                .setPositiveButton("セット", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FancyToast.makeText(getActivity(), "セットしました", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
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
                        lineData.remove(fromPos);
                        stationData.remove(fromPos);
                        alertLineData.remove(fromPos);
                        mAdapter.notifyItemRemoved(fromPos);
                    }
                });
        mIth.attachToRecyclerView(mRecyclerView);
    }

    private void readData() {
        if (helper == null) {
            helper = new DBOpenHelper(getActivity());
        }
        if (db == null) {
            db = helper.getReadableDatabase();
        }

        Cursor cursor = db.query(
                "stationdb",
                new String[] {"line", "stationname", "alertline", "lat", "lng"},
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            lineData.add(cursor.getString(i));
            stationData.add(cursor.getString(i));
            alertLineData.add(cursor.getInt(i));
            cursor.moveToNext();
        }

        cursor.close();
    }

    private void insertData(){

        ContentValues values = new ContentValues();
        values.put("line", "テスト１");
        values.put("stationname", "テスト１駅");
        values.put("alertline", 8);
        values.put("lat", 0);
        values.put("lng", 0);

        db.insert("stationdb", null, values);
        mAdapter.notifyDataSetChanged();
    }
}
