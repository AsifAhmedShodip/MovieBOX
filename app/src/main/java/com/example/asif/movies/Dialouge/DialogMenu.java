package com.example.asif.movies.Dialouge;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.asif.movies.R;

/**
 * Created by asif on 31-Mar-18.
 */

public class DialogMenu implements View.OnClickListener {
    private Dialog dialog;
    private OnDialogMenuListener listener;
    TextView reWatched ,  notWatched;

    public DialogMenu(Context context) {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_item);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        reWatched = (TextView) dialog.findViewById(R.id.rewatched);
        notWatched = (TextView) dialog.findViewById(R.id.notWatched);
        reWatched.setOnClickListener(this);
        notWatched.setOnClickListener(this);
    }

    public void setListener(OnDialogMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rewatched:
                listener.onReWatchedPress();
                dialog.dismiss();
                break;
            case R.id.notWatched:
                listener.onNotWatchedPress();
                dialog.dismiss();
                break;
        }
    }

    public interface OnDialogMenuListener{
        void onReWatchedPress();
        void onNotWatchedPress();
    }
}
