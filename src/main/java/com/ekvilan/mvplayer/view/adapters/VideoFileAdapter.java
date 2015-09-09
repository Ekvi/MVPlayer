package com.ekvilan.mvplayer.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.utils.DurationConverter;

import java.io.File;
import java.util.List;

public class VideoFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY_VIEW = 10;

    private int position;
    private boolean isRecent;

    private Context context;
    private DurationConverter durationConverter;
    private LayoutInflater inflater;
    private List<String> videoLinks;

    public VideoFileAdapter(Context context, List<String> videoLinks, boolean isRecent) {
        inflater = LayoutInflater.from(context);
        durationConverter = new DurationConverter();
        this.context = context;
        this.videoLinks = videoLinks;
        this.isRecent = isRecent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == EMPTY_VIEW) {
            return new EmptyViewHolder(inflater.inflate(R.layout.empty_row, parent, false));
        }

        return new VideoFileViewHolder(
                inflater.inflate(R.layout.video_file_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof VideoFileViewHolder) {
            String[] name = videoLinks.get(position).split("/");

            final VideoFileViewHolder holder = (VideoFileViewHolder) viewHolder;
            holder.fileName.setText(name[name.length - 1]);

            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(videoLinks.get(position),
                    MediaStore.Images.Thumbnails.MINI_KIND);
            holder.imageView.setImageBitmap(thumbnail);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            holder.fileSize.setText(calculateFileSize(videoLinks.get(position)));
            holder.videoLength.setText(durationConverter.convertDuration(getDuration(position)));

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private String calculateFileSize(String uri) {
        long byteLength = new File(uri).length();
        long kByte = byteLength / 1024;
        long mByte = kByte / 1024;
        long divider = kByte % 1024;

        return String.format("%.1f", Double.parseDouble(mByte + "." + divider)) + "M";
    }

    private long getDuration(int position) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoLinks.get(position));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        return Long.parseLong(time);
    }

    @Override
    public int getItemCount() {
        return videoLinks.size() > 0 ? videoLinks.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (videoLinks.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    public class VideoFileViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
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
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(isRecent) {
                menu.add(0, 1, 0, context.getResources().getString(R.string.removeFromRecent));
            }
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
