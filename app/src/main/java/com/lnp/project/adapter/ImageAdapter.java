package com.lnp.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lnp.project.R;

public class ImageAdapter extends BaseAdapter {

    private  int icons[];
    private String letters[];
    private Context context;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, int[] icons, String[] letters ) {
        this.icons = icons;
        this.letters = letters;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return letters.length;
    }

    @Override
    public Object getItem(int position)
    {
        return letters[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent)
    {
        View gridview =convertview;

        if (convertview==null)
        {
            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridview=inflater.inflate(R.layout.main_grid_layout,parent, false);
        }

        ImageView icon=gridview.findViewById(R.id.icon);
        TextView letter=(TextView)gridview.findViewById(R.id.txt);

        icon.setImageResource(icons[position]);
        letter.setText(letters[position]);
        return gridview;
    }
//    private Context mContext;
//
//    // Constructor
//    public ImageAdapter(Context c) {
//        mContext = c;
//    }
//
//    public int getCount() {
//        return mThumbIds.length;
//    }
//
//    public Object getItem(int position) {
//        return null;
//    }
//
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    // create a new ImageView for each item referenced by the Adapter
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageView imageView;
//
//        if (convertView == null) {
//            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 250));
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
//        }
//        else
//        {
//            imageView = (ImageView) convertView;
//        }
//        imageView.setImageResource(mThumbIds[position]);
//        return imageView;
//    }
//
//    // Keep all Images in array
//    public Integer[] mThumbIds = {
//            R.drawable.adminicon, R.drawable.loan, R.drawable.ca, R.drawable.engineer,
//            R.drawable.contactform, R.drawable.viewcontactform, R.drawable.viewcontactform
//    };
}