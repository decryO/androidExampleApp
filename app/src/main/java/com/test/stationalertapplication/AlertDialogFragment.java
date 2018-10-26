package com.test.stationalertapplication;

import android.os.Bundle;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import static android.support.v4.os.LocaleListCompat.create;

public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setTitle("到着しました").setMessage("停止を押すとアラームを停止します").setPositiveButton("停止", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = "Stop";
                AlertDialogActivity callingActivity = (AlertDialogActivity) getActivity();
                callingActivity.onReturnValue(text);

                dismiss();
            }
        }).create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setCancelable(false);
//        builder.setTitle("到着しました");
//        builder.setMessage("停止を押すとアラームを停止します\n再設定を押すとアプリを起動します");
//        builder.setPositiveButton("停止", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String text = "Stop";
//                AlertDialogActivity callingActivity = (AlertDialogActivity) getActivity();
//                callingActivity.onReturnValue(text);
//            }
//        });
//        builder.setNegativeButton("再設定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String text = "return";
//                AlertDialogActivity callingActivity = (AlertDialogActivity) getActivity();
//                callingActivity.onReturnValue(text);
//            }
//        });
//
//        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().finish();
    }

}
