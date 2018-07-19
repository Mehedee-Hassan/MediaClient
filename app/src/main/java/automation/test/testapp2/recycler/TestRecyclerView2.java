package automation.test.testapp2.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import static java.lang.Math.abs;

/**
 * Created by DELL on 7/19/2018.
 */

public class TestRecyclerView2 extends RecyclerView {

    private static final String TAG = "TestRecyclerView2";
    public TestRecyclerView2(Context context) {
        super(context);
    }

    public TestRecyclerView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestRecyclerView2(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        Log.d(TAG, "onScrolled: "+dx+" "+dy);
        x = dy;
        super.onScrolled(dx, dy);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
//
//
//
//
//        Log.d(TAG, "onInterceptTouchEvent: true  1  "+e.getRawY()+" "+y+" "+e.getRawX()+" diff ="+difY+" "+difX);
//
////        if((x!=0.0 && y!=0.0))
//        {
//
//            if(( difY > difX ) ){
////                super.onTouchEvent(e);
//                Log.d(TAG, "onInterceptTouchEvent: false 3  "+e.getRawY()+" "+y+" "+e.getRawY()+" "+e.getRawX()+" diff ="+difY+" "+difX);
//                return false;
//            }
//
//        }
//
//
//
////        Log.d(TAG, "onInterceptTouchEvent: true 4  "+e.getRawY()+" "+y+" "+e.getY()+" "+e.getRawX()+" diff ="+difY+" "+difX);



        return super.onInterceptTouchEvent(e);

    }




    float x=0.0f ,y=0f,difX = 10.0f,difY=0.0f;

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//
//
//
//        if(MotionEvent.ACTION_DOWN == e.getAction()){
//            x = e.getRawX();
//            y = e.getRawY();
//            Log.d(TAG, "onTouchEvent: 2 ");
//        }
//
//        Log.d(TAG, "onInterceptTouchEvent: **"+e.getRawX()+" "+e.getRawY()+" "+x+" "+y);
//
//        if(MotionEvent.ACTION_UP == e.getAction()){
//
//        }
//        if(MotionEvent.ACTION_MOVE == e.getAction()){
//            difX = abs(x-e.getRawX());
//            difY = abs(y-e.getRawY());
//
//            Log.d(TAG, "onInterceptTouchEvent:2 **"+e.getRawX()+" "+e.getRawY()+" "+difY+" "+difX);
//
//        }
//
//
//        return super.onTouchEvent(e);
//    }
//



}


