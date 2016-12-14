package com.akingyin.mylibrary;

import android.view.View;
import android.widget.Button;



/**
 * Created by Administrator on 2016/12/14.
 */

public class NumberKeyBoark  implements View.OnClickListener {

    public View root;


    public Button btn_one,btn_two,btn_three;

    public Button btn_four,btn_five,btn_six;

    public Button btn_seven,btn_eight,btn_nine;

    public  Button btnC,btn_zero,btn_del;

    private    KeyBoarkCall    boarkCall;

    public KeyBoarkCall getBoarkCall() {
        return boarkCall;
    }

    public void setBoarkCall(KeyBoarkCall boarkCall) {
        this.boarkCall = boarkCall;
    }

    public NumberKeyBoark(View root) {
        this.root = root;
        initView();

    }

    public  void  initView(){
        btn_one = findViewById(R.id.btn_one);
        btn_two = findViewById(R.id.btn_two);
        btn_three = findViewById(R.id.btn_three);
        btn_four = findViewById(R.id.btn_four);
        btn_five = findViewById(R.id.btn_five);
        btn_six = findViewById(R.id.btn_six);
        btn_seven = findViewById(R.id.btn_seven);
        btn_eight = findViewById(R.id.btn_eight);
        btn_nine = findViewById(R.id.btn_nine);
        btnC = findViewById(R.id.btnC);
        btn_zero = findViewById(R.id.btn_zero);
        btn_del = findViewById(R.id.btn_del);
        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
        btn_four.setOnClickListener(this);
        btn_five.setOnClickListener(this);
        btn_six.setOnClickListener(this);
        btn_seven.setOnClickListener(this);
        btn_eight.setOnClickListener(this);
        btn_nine.setOnClickListener(this);
        btn_zero.setOnClickListener(this);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != boarkCall){
                    boarkCall.onDelectLast();
                }
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != boarkCall){
                    boarkCall.onChangeKeyBoark();
                }
            }
        });


    }

    public   <T extends View>T   findViewById(int  resid){

        return  (T)root.findViewById(resid);
    }


    @Override
    public void onClick(View view) {
       if(null != boarkCall){
           boarkCall.call(view.getTag().toString());
       }
    }
}
