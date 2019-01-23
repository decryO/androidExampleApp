package com.test.stationalertapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class PresetFragment extends Fragment implements OnRecyclerListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private int testIDs = 0;
    private Boolean inserted = false;
    private FloatingActionButton floatingActionButton;

    private static final String TABLE_NAME = "stationdb";
    private final int REQUEST_DB_ADD = 1;

    private ArrayList<String> lineData = new ArrayList<>();
    private ArrayList<String> stationData = new ArrayList<>();
    private ArrayList<Integer> alertLineData = new ArrayList<>();
    private ArrayList<String> timeData = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_preset, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.preset_list);

        floatingActionButton = view.findViewById(R.id.fab);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecycleAdapter(getContext(), lineData, stationData, alertLineData, timeData, this);
        mRecyclerView.setAdapter(mAdapter);
        readData();
        RecycleClick();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPresetActivity.class);
                startActivityForResult(intent, REQUEST_DB_ADD);
            }
        });

        mAdapter.notifyDataSetChanged();
//        insertData();
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
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                        final int fromPos = viewHolder.getAdapterPosition();
//                        final int toPos = target.getAdapterPosition();
//                        mAdapter.notifyItemMoved(fromPos, toPos);
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        lineData.remove(fromPos);
                        stationData.remove(fromPos);
                        alertLineData.remove(fromPos);
                        String removeSelect = timeData.get(fromPos);
                        Log.d("#####削除#####"," : "+removeSelect);
                        timeData.remove(fromPos);
                        db.delete(TABLE_NAME, "_id = " + removeSelect, null);
                        mAdapter.notifyItemRemoved(fromPos);
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
                new String[]{"_id", "line", "stationname", "alertline", "lat", "lng"},
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            timeData.add(cursor.getString(0));
            Log.d("###ああああああ##### : ", ""+timeData);
            lineData.add(cursor.getString(1));
            stationData.add(cursor.getString(2));
            alertLineData.add(cursor.getInt(3));
            cursor.moveToNext();
        }

        cursor.close();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();

        if (requestCode == REQUEST_DB_ADD) {
            if (resultCode == RESULT_OK) {
                Cursor c = db.rawQuery("SELECT * from " + TABLE_NAME + " ;", null);
                for (int i = 0; i < c.getColumnCount(); i++) {
                    mAdapter.notifyItemRemoved(i);
                }
                lineData.clear();
                stationData.clear();
                alertLineData.clear();
                timeData.clear();
                readData();
                mAdapter.notifyDataSetChanged();
            } else {

            }
        }
    }
}
