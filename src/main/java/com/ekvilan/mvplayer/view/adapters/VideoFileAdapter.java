package com.ekvilan.mvplayer.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.utils.FileProvider;

import java.util.List;

public class VideoFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<String> videoLinks;

    public VideoFileAdapter(Context context, List<String> videoLinks) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.videoLinks = videoLinks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoFileViewHolder(
                inflater.inflate(R.layout.video_file_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof VideoFileViewHolder) {
            String[] name = videoLinks.get(position).split("/");

            VideoFileViewHolder holder = (VideoFileViewHolder) viewHolder;
            holder.fileName.setText(name[name.length - 1]);

            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoLinks.get(position),
                    MediaStore.Images.Thumbnails.MINI_KIND);
            holder.imageView.setImageBitmap(thumbnail);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }

    @Override
    public int getItemCount() {
        return videoLinks.size() > 0 ? videoLinks.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class VideoFileViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView fileName;
        TextView fileSize;
        TextView videoLength;

        public VideoFileViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivVideoIcon);
            fileName = (TextView) itemView.findViewById(R.id.tvVideoName);
            fileSize = (TextView) itemView.findViewById(R.id.tvFileSize);
            videoLength = (TextView) itemView.findViewById(R.id.tvVideoLength);
        }
    }
}
