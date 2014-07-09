package org.azurespot.gridimagesearch;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;


public class ImageResultArrayAdapter extends ArrayAdapter<ImageResult> {

    public ImageResultArrayAdapter(Context context, List<ImageResult> images) {
        super(context, R.layout.item_image_result, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageResult imageInfo = this.getItem(position);
        ScaledImageView ivImage;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ivImage = (ScaledImageView) inflater.inflate(R.layout.item_image_result, parent, false);
        } else {
            ivImage = (ScaledImageView) convertView;
            ivImage.getResources().getColor(android.R.color.transparent);
        }
        Picasso.with(getContext()).load(imageInfo.getThumbUrl()).into(ivImage);
        return ivImage;
    }



}
