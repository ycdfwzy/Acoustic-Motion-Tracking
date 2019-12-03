package com.example.acoustic_motion_tracking_receiver;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class MapView extends View {
    ArrayList<Double> xs = new ArrayList<>();
    ArrayList<Double> ys = new ArrayList<>();
    Paint paint=new Paint();
    double min_x, min_y, max_x, max_y;
    double width, height;

    public MapView(Context context) {
        super(context);
        init();
    }


    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init()
    {

        min_x = 0;
        min_y = 0;
        max_x = 2;
        max_y = 2;
    }

    public void add_points(double[] coord_x, double[] coord_y)
    {
//        if(coord_x.length!=coord_y.length)
//            return;
        int len = Math.min(coord_x.length, coord_y.length);
        for(int i=0; i<len; i++)
        {
//            min_x = Math.min(coord_x[i], min_x);
//            min_y = Math.min(coord_y[i], min_y);
//            max_x = Math.max(coord_x[i], max_x);
//            max_y = Math.max(coord_y[i], max_y);
            xs.add(coord_x[i]);
            ys.add(coord_y[i]);
        }
        invalidate();
    }

    public void clear_points()
    {
        xs.clear();
        ys.clear();
    }

    public float[] get_draw_coord(int idx)
    {
        float[] draw_pts = new float[2];
        double ori_x = xs.get(idx), ori_y = ys.get(idx);
        draw_pts[0] = (float) (ori_x/(max_x-min_x)*width);
        draw_pts[1] = (float) (ori_y/(max_y-min_y)*height);
//        draw_pts[0]+=width/2;
//        draw_pts[1]+=height/2;
        draw_pts[1] = (float) height-draw_pts[1];
        return draw_pts;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //设置画笔
        width = getWidth();
        height = getHeight();

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(3);
//        canvas.drawLine(0, (float) height/2, (float) width, (float) height/2, paint);
//        canvas.drawLine((float)width/2, 0, (float)width/2, (float) height, paint);
        canvas.drawLine(0, (float) height-1, (float) width, (float) height-1, paint);
        canvas.drawLine(1, 0, 1, (float) height, paint);
        //绘制
        float[] first = new float[1], second;
        for(int i=0; i<xs.size()-1; i++)
        {
            if(i==0)
            {
                first = get_draw_coord(i);
                second = get_draw_coord(i+1);
            }
            else
            {
                second = get_draw_coord(i+1);
            }
            canvas.drawLine(first[0], first[1], second[0], second[1], paint);
            first = second;
        }
    }

}