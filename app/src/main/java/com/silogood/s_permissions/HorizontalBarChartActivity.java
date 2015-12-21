
package com.silogood.s_permissions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import java.util.ArrayList;

public class HorizontalBarChartActivity extends Fragment implements OnChartValueSelectedListener {

    protected HorizontalBarChart mChart;
    //private SeekBar mSeekBarX, mSeekBarY;
   // private TextView tvX, tvY;

    private int isLoad = 0;

    public HorizontalBarChartActivity() {}


    protected String[] mMonths = new String[] {
            "RECEIVE_BOOT_COMPLETED","WAKE_LOCK","ACCESS_WIFI_STATE","VIBRATE","ACCESS_NETWORK_STATE","CHANGE_WIFI_STATE", "INTERNET","WRITE_CONTACTS","READ_CONTACTS","ACCESS_FINE_LOCATION","ACCESS_COARSE_LOCATION","READ_PHONE_STATE","WRITE_EXTERNAL_STORAGE","RECEIVE_SMS","WRITE_SMS","CALL_PHONE","READ_SNS","SEND_SNS"
    };
    private Typeface tf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (DataSet<?> set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if(mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleHighlightArrow: {
                if (mChart.isDrawHighlightArrowEnabled())
                    mChart.setDrawHighlightArrow(false);
                else
                    mChart.setDrawHighlightArrow(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStartzero: {
                mChart.getAxisLeft().setStartAtZero(!mChart.getAxisLeft().isStartAtZeroEnabled());
                mChart.getAxisRight().setStartAtZero(!mChart.getAxisRight().isStartAtZeroEnabled());
                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {

                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionToggleFilter: {

                Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 25);

                if (!mChart.isFilteringEnabled()) {
                    mChart.enableFiltering(a);
                } else {
                    mChart.disableFiltering();
                }
                mChart.invalidate();
                break;
            }
//            case R.id.actionSave: {
//                if (mChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Saving SUCCESSFUL!",
//                            Toast.LENGTH_SHORT).show();
//                } else
//                    Toast.makeText(getActivity().getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
//                            .show();
//                break;
//            }
        }
        return true;
    }

    /*
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
*/
    private void setData(int count, float range) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            xVals.add(mMonths[i % 18]);}
        yVals1.add(new BarEntry((float) (38.1), 0));
        yVals1.add(new BarEntry((float) (38.7), 1));
        yVals1.add(new BarEntry((float) (38.7), 2));
        yVals1.add(new BarEntry((float) (38.9), 3));
        yVals1.add(new BarEntry((float) (39.7), 4));
        yVals1.add(new BarEntry((float) (40.7), 5));
        yVals1.add(new BarEntry((float) (42.3), 6));
        yVals1.add(new BarEntry((float) (45.5), 7));
        yVals1.add(new BarEntry((float) (46.1), 8));
        yVals1.add(new BarEntry((float) (46.4), 9));
        yVals1.add(new BarEntry((float) (46.5), 10));
        yVals1.add(new BarEntry((float) (46.9), 11));
        yVals1.add(new BarEntry((float) (46.9), 12));
        yVals1.add(new BarEntry((float) (47.3), 13));
        yVals1.add(new BarEntry((float) (49.2), 14));
        yVals1.add(new BarEntry((float) (50.4), 15));
        yVals1.add(new BarEntry((float) (50.4), 16));
        yVals1.add(new BarEntry((float) (54.1), 17));

        BarDataSet set1 = new BarDataSet(yVals1, "Dangerous Permissions_Tab TOP10");

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(tf);

        mChart.setData(data);
    }

    @Override
    public void onResume() {
        super.onResume();
        isLoad = 0;
    }

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if(isLoad == 1) return;
            isLoad = 1;
        if (e == null)
            return;

        Intent intent = new Intent(getActivity(), Permissions_HorizontalBarChart_Detail.class);

        RectF bounds = mChart.getBarBounds((BarEntry) e);
        PointF position = mChart.getPosition(e, mChart.getData().getDataSetByIndex(dataSetIndex)
                .getAxisDependency());
        switch(e.getXIndex()) {
            case 1:
                intent.putExtra("num", "android.permission.RECEIVE_BOOT_COMPLETED");
                break;
            case 2:
                intent.putExtra("num", "android.permission.ACCESS_WIFI_STATE");
                break;
            case 3:
                intent.putExtra("num", "android.permission.VIBRATE");
                break;
            case 4:
                intent.putExtra("num", "android.permission.ACCESS_NETWORK_STATE");
                break;
            case 5:
                intent.putExtra("num", "android.permission.CHANGE_WIFI_STATE");
                break;
            case 6:
                intent.putExtra("num", "android.permission.INTERNET");
                break;
            case 7:
                intent.putExtra("num", "android.permission.WRITE_CONTACTS");
                break;
            case 8:
                intent.putExtra("num", "android.permission.READ_CONTACTS");
                break;
            case 9:
                intent.putExtra("num", "android.permission.ACCESS_FINE_LOCATION");
                break;
            case 10:
                intent.putExtra("num", "android.permission.ACCESS_COARSE_LOCATION");
                break;
            case 11:
                intent.putExtra("num", "android.permission.READ_PHONE_STATE");
                break;
            case 12:
                intent.putExtra("num", "android.permission.WRITE_EXTERNAL_STORAGE");
                break;
            case 13:
                intent.putExtra("num", "android.permission.RECEIVE_SMS");
                break;
            case 15:
                intent.putExtra("num", "android.permission.CALL_PHONE");
                break;
            case 16:
                intent.putExtra("num", "android.permission.READ_SMS");
                break;
            case 17:
                intent.putExtra("num", "android.permission.SEND_SMS");
                break;
            case 18:
                intent.putExtra("num", "android.permission.RECEIVE_BOOT_COMPLETED");
                break;
        }

        startActivity(intent);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_horizontalbarchart, container, false);

        // tvX = (TextView) findViewById(R.id.tvXMax);
        //tvY = (TextView) findViewById(R.id.tvYMax);

        //  mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        // mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        mChart = (HorizontalBarChart) v.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setDoubleTapToZoomEnabled(false);

        mChart.setDescription("Dangerous Permission TOP 18");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
//        mChart.setPinchZoom(true);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        mChart.setDrawGridBackground(false);

        // mChart.setDrawYLabels(false);

        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxisPosition.BOTTOM);
        xl.setTypeface(tf);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setGridLineWidth(0.3f);

        YAxis yl = mChart.getAxisLeft();
        yl.setTypeface(tf);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);
//        yl.setInverted(true);

        YAxis yr = mChart.getAxisRight();
        yr.setTypeface(tf);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
//        yr.setInverted(true);

        setData(18, 50);
        mChart.animateY(2500);


        /* // setting data
        mSeekBarY.setProgress(50);
        mSeekBarX.setProgress(12);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);
        */

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        // mChart.setDrawLegend(false);

        return v;
    }
}
