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
import com.ekvilan.mvplayer.utils.FileProvider;

import java.io.File;
import java.util.List;

public class VideoFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY_VIEW = 10;
    public static final int REMOVE_RECENT = 1;
    public static final int REMOVE = 2;
    public static final int RENAME= 3;

    private int position;
    private String filePath;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if(viewHolder instanceof VideoFileViewHolder) {
            final VideoFileViewHolder holder = (VideoFileViewHolder) viewHolder;
            holder.fileName.setText(FileProvider.extractName(videoLinks.get(position)));

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
                    setFilePath(videoLinks.get(position));
                    return false;
                }
            });
        }
    }

    public int getPosition() {
        return position;
    }

    private void setPosition(int position) {
        this.position = position;
    }

    public String getFilePath() {
        return filePath;
    }

    private void setFilePath(String filePath) {
        this.filePath = filePath;
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
            menu.setHeaderTitle(context.getResources().getString(R.string.titleOperations));
            if(isRecent) {
                menu.add(0, REMOVE_RECENT, 0, context.getResources().getString(R.string.removeFromRecent));
            } else {
                menu.add(0, REMOVE, 0, context.getResources().getString(R.string.removeOperation));
                menu.add(0, RENAME, 0, context.getResources().getString(R.string.renameOperation));
            }
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
