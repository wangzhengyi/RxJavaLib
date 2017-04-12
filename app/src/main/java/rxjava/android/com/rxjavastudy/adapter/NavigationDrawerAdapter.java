package rxjava.android.com.rxjavastudy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.bean.NavigationItem;
import rxjava.android.com.rxjavastudy.interfaces.NavigationDrawerCallbacks;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder>  {
    private LayoutInflater mInflater;
    private List<NavigationItem> mData;
    private NavigationDrawerCallbacks mNavigationDrawerCallbacks;

    private int mTouchedPostion;
    private int mSelectedPosition;

    public NavigationDrawerAdapter(Context context, List<NavigationItem> data) {
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = this.mInflater.inflate(R.layout.drawer_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(mData.get(position).getText());
        holder.textView.setCompoundDrawablesWithIntrinsicBounds(mData.get(position).getDrawable(),
                null, null, null);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNavigationDrawerCallbacks != null) {
                    mNavigationDrawerCallbacks.onNavigationDrawerItemSelected(position);
                }
            }
        });
        holder.textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchPosition(position);
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                        touchPosition(-1);
                        return false;
                    case MotionEvent.ACTION_UP:
                        touchPosition(-1);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        return false;
                }
                return true;
            }
        });
    }

    private void touchPosition(int position) {
        int lastPosition = mTouchedPostion;
        mTouchedPostion = position;
        if (lastPosition >= 0) {
            notifyItemChanged(lastPosition);
        }
        if (position >= 0) {
            notifyItemChanged(position);
        }
    }

    public void selectPosition(int position) {
        int lastPosition = mSelectedPosition;
        if (lastPosition >= 0) {
            notifyItemChanged(lastPosition);
        }
        if (position >= 0) {
            notifyItemChanged(position);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks mNavigationDrawerCallbacks) {
        this.mNavigationDrawerCallbacks = mNavigationDrawerCallbacks;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}
