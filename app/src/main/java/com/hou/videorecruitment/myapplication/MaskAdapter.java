package com.hou.videorecruitment.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.view.waveprogress.WaveProgressView;

import java.util.ArrayList;

/**
 * Created by hmj on 17/5/8.
 */

class MaskAdapter extends RecyclerView.Adapter<MaskAdapter.MaskViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<MaskBean> mDate;

    public MaskAdapter(@Nullable Context context, @Nullable ArrayList<MaskBean> data) {
        mContext = context;
        mDate = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public MaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_mask, null);
        return new MaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MaskViewHolder holder, int position) {
        MaskBean maskBean = getItem(position);
        if (maskBean == null) return;
        holder.mTitle.setText(maskBean.getName());
        holder.mWaveProgress.setMaskDrawable(mContext, maskBean.getMaskIcon());
        holder.mWaveProgress.setBackgroundColor(maskBean.getBackgroundColor());
    }


    private MaskBean getItem(int position) {
        return mDate.get(position);
    }

    @Override
    public int getItemCount() {
        return mDate == null ? 0 : mDate.size();
    }

    static class MaskViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private WaveProgressView mWaveProgress;

        public MaskViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mWaveProgress = (WaveProgressView) itemView.findViewById(R.id.wave_progress);
        }
    }
}
