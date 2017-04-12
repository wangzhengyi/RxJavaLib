package rxjava.android.com.rxjavastudy.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.bean.AppInfo;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {
    private List<AppInfo> mApplications;
    private int mRowLayout;

    public ApplicationAdapter(List<AppInfo> applications, int rowlayout) {
        this.mApplications = applications;
        this.mRowLayout = rowlayout;
    }

    public void addApplications(List<AppInfo> applications) {
        mApplications.clear();
        mApplications.addAll(applications);
        notifyDataSetChanged();
    }

    public void addApplication(int position, AppInfo appInfo) {
        if (position < 0) {
            position = 0;
        }
        if (position >= mApplications.size()) {
            position = mApplications.size() - 1;
        }
        mApplications.add(position, appInfo);
        notifyItemInserted(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.applications_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AppInfo appInfo = mApplications.get(position);
        holder.name.setText(appInfo.getName());
        final ViewHolder tHolder = holder;
        getBitmap(appInfo.getIcon())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        tHolder.image.setImageBitmap(bitmap);
                    }
                });
    }

    private Observable<Bitmap> getBitmap(final String icon) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(BitmapFactory.decodeFile(icon));
                    subscriber.onCompleted();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mApplications == null? 0 : mApplications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.app_name);
            image = (ImageView) itemView.findViewById(R.id.app_image);
        }
    }
}
