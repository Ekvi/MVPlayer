package com.ekvilan.mvplayer.view.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekvilan.mvplayer.R;
import com.ekvilan.mvplayer.utils.FileProvider;

import java.util.List;

public class VideoFoldersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY_VIEW = 10;

    private Context context;
    private LayoutInflater inflater;
    private List<FileProvider.VideoFolder> folders;

    public VideoFoldersAdapter(Context context, List<FileProvider.VideoFolder> folders) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.folders = folders;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == EMPTY_VIEW) {
            return new EmptyViewHolder(inflater.inflate(R.layout.empty_row, parent, false));
        }

        return new VideoFolderViewHolder(
                inflater.inflate(R.layout.video_folder_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof VideoFolderViewHolder) {
            String[] folderName = folders.get(position).getFolderName().split("/");
            String videoCount = folders.get(position).getVideoLinks().size()
                    + " " + context.getResources().getString(R.string.filesCount);

            VideoFolderViewHolder holder = (VideoFolderViewHolder) viewHolder;
            holder.folderName.setText(folderName[folderName.length - 1]);
            holder.filesCount.setText(videoCount);
        }
    }

    @Override
    public int getItemCount() {
        return folders.size() > 0 ? folders.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (folders.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    public class VideoFolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;
        TextView filesCount;

        public VideoFolderViewHolder(View itemView) {
            super(itemView);
            folderName = (TextView) itemView.findViewById(R.id.tvFolderName);
            filesCount = (TextView) itemView.findViewById(R.id.tvFilesCount);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
