package com.example.asif.movies;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by asif on 31-Mar-18.
 */

public class DialogMenu implements View.OnClickListener {
    private Dialog dialog;
    private OnDialogMenuListener listener;
    TextView name,watched , list , wish;

    public DialogMenu(Context context) {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_item);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        name = (TextView) dialog.findViewById(R.id.name);
        watched = (TextView) dialog.findViewById(R.id.watched);
        wish = (TextView) dialog.findViewById(R.id.wish);
        list = (TextView) dialog.findViewById(R.id.list);
        watched.setOnClickListener(this);
        wish.setOnClickListener(this);
        list.setOnClickListener(this);

    }

    public void setListener(OnDialogMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.watched:
                listener.onWatchedPress();
                //dialog.dismiss();
                break;
            case R.id.wish:
                listener.onWishPress();
                break;
            case R.id.list:
                listener.onListPress();
                //dialog.dismiss();
                break;
        }
    }

    public interface OnDialogMenuListener{
        void onWatchedPress();
        void onWishPress();
        void onListPress();
    }
}
