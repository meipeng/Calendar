package com.text.calendartext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 类名：com.text.calendartext
 * 时间：2017/12/6 21:16
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 * @author Liu_xg
 */

public class CalendarView extends LinearLayout {

    private static final String TAG = "CalendarView";
    private TextView tvPrev, tvNext, tvDate;
    private GridView mGridView;
    private String dateFormat;

    private int prevDays;
    /**
     * 获取系统日历
     */
    private Calendar mCalender = Calendar.getInstance();

    private OnNowDateListenter onNowDateListenter;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * 入口函数
     */
    private void initControl(Context context, AttributeSet attrs) {
        bindControl(context, attrs);
        bindEvent();
        renderCalendar();
    }

    /**
     * 渲染日历控件
     */
    @SuppressLint("WrongConstant")
    private void renderCalendar() {
        //当前月份展示
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        tvDate.setText(simpleDateFormat.format(mCalender.getTime()));

        //gridview数据展示
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar) mCalender.clone();

        //设置当前时间为本月的一号
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //今天是星期几，这里是以星期天为一周的第一天
        //calendar.get(Calendar.DAY_OF_WEEK) 的值为1~7之间的整数，1代表周日，7代表周六，其余依次类推
        //减一是判断本月一号之前空几位，举个例子：假如今天是 星期五
        //那么calendar.get(Calendar.DAY_OF_WEEK) = 6，
        // prevDays = 6 - 1 = 5,即一号之前有五个位置是空的。
        prevDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //calendar.add(Calendar.DAY_OF_MONTH, -prevDays)代表上个月的最后 prevDays 天
        calendar.add(Calendar.DATE, -prevDays);

        //一周最多是七天，一个月最多占六行，举个例子
        //假如这个月的一号是在周六并且这个月是三十一天，那么一号就独占一行，那么剩下的三十天就在其他行了
        //就会有 4 * 7 = 28，占据满满的四行，剩余31-1-28=2天独占一行，这样一个月就展示完了，最多占据
        //四行。

        int maxCellCount = getMonthRows() * 7;

        while (cells.size() < maxCellCount) {
            //添加天数
            cells.add(calendar.getTime());
            //当前日期加一后的日期，举个例子，假如今天是2017-12-8，那么
            //calendar.add(Calendar.DAY_OF_MONTH, 1)之后就变成了2017-12-9。
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        mGridView.setAdapter(new CalendarAdapter(getContext(), cells));
        //添加长按事件
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (getOnNowDateListenter() == null) {
                    return false;
                } else {
                    getOnNowDateListenter().onItemLongClick((Date) parent.getItemAtPosition(position));
                    return true;
                }
            }
        });

    }

    /**
     * 取得当月天数
     */
    @SuppressLint("WrongConstant")
    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 计算当前月需要显示几行
     */
    public int getMonthRows() {
        int items = prevDays + getCurrentMonthLastDay();
        int rows = items % 7 == 0 ? items / 7 : (items / 7) + 1;
        if (rows == 4) {
            rows = 5;
        }
        return rows;
    }


    /**
     * 绑定事件
     */
    private void bindEvent() {
        tvPrev.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                //前一个月
                mCalender.add(Calendar.MONTH, -1);
                renderCalendar();
            }
        });
        tvNext.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                //后一个月
                mCalender.add(Calendar.MONTH, 1);
                renderCalendar();
            }
        });


    }

    /**
     * 绑定layout
     */
    private void bindControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_calendar, this);
        tvPrev = findViewById(R.id.tvPrev);
        tvNext = findViewById(R.id.tvNext);
        tvDate = findViewById(R.id.tvDate);
        mGridView = findViewById(R.id.canlender_grid);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        dateFormat = typedArray.getString(R.styleable.CalendarView_dateFormat);
        if (dateFormat == null) {
            dateFormat = "MM yyyy";
        }

        typedArray.recycle();

    }

    private class CalendarAdapter extends ArrayAdapter<Date> {

        LayoutInflater inflater;

        public CalendarAdapter(@NonNull Context context, ArrayList<Date> dates) {
            super(context, R.layout.calender_text_day, dates);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //获取当前数据
            Date date = getItem(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calender_text_day, parent, false);
            }
            //获取天
            int day = 0;
            if (date != null) {
                day = date.getDate();
            }
            ((CircleDateTextView) convertView).setText(String.valueOf(day));

            //获取当前日期
            Date now = new Date();

            //判断是否是同一年、同一月
            boolean isTheSameMonth = false;
            boolean isTheSameYear = false;

            if (date.getMonth() == now.getMonth()) {
                isTheSameMonth = true;

                if (date.getYear() == now.getYear()) {
                    isTheSameYear = true;
                }
            }

            //判断有效月份
            if (isTheSameMonth && isTheSameYear) {
                //同一个月
                ((CircleDateTextView) convertView).setTextColor(Color.parseColor("#333333"));
            } else {
                //不同月
                ((CircleDateTextView) convertView).setTextColor(Color.parseColor("#E3E4E7"));
            }
            //判断是否是当天
            if (now.getDate() == date.getDate() && now.getMonth() == date.getMonth()
                    && now.getYear() == date.getYear()) {
                ((CircleDateTextView) convertView).setTextColor(Color.parseColor("#ff0000"));
                ((CircleDateTextView) convertView).isToday = true;
            }
            return convertView;
        }
    }

    public interface OnNowDateListenter {
        void onItemLongClick(Date date);
    }

    public OnNowDateListenter getOnNowDateListenter() {
        return onNowDateListenter;
    }

    public void setOnNowDateListenter(OnNowDateListenter onNowDateListenter) {
        this.onNowDateListenter = onNowDateListenter;
    }
}
