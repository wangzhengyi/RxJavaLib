package rxjava.android.com.rxjavastudy.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import rxjava.android.com.rxjavastudy.App;
import rxjava.android.com.rxjavastudy.R;
import rxjava.android.com.rxjavastudy.utils.ImageLoader;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.ViewHolder>{
    private List<String> mPhotos;
    private ImageLoader mImageLoader;

    public PhoneAdapter(List<String> photos) {
        this.mPhotos = photos;
        this.mImageLoader = ImageLoader.getInstance(App.getInstance());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String uri = mPhotos.get(position);
        holder.imageView.setImageResource(R.drawable.ic_launcher);
        mImageLoader.bindImageView(uri, holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPhotos == null ? 0 : mPhotos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.id_photo_img);
        }
    }
}
