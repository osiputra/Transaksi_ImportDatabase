package com.aplikasipasardemo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Putra_Osi_PC on 7/7/2017.
 */

public class CustomSwipeAdapter extends PagerAdapter {

    private static final String TAG = "Custom Swipe Adapter";

    private int[] image_resource = {
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3
    };

    private Context context;
    private LayoutInflater layoutInflater;


    public CustomSwipeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return image_resource.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view;
        item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);

        ImageButton imageView = (ImageButton) item_view.findViewById(R.id.image_view);

        imageView.setImageResource(image_resource[position]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0){
                    showToastShort("Banner 1");
                }
                else if (position == 1){
                    showToastShort("Banner 2");
                }
                else{
                    showToastShort("Banner 3");
                }
            }
        });

        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }

    private void showToastLong(String message){
        Toast.makeText(
                context.getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }

    private void showToastShort(String message){
        Toast.makeText(
                context.getApplicationContext(),
                message,
                Toast.LENGTH_LONG).show();
    }
}
