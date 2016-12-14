package com.akingyin.mylibrary;

import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2016/12/14.
 */

public class LettersKeyBoark implements View.OnClickListener {

    public   View   root;

    public Button  btn_q,btn_w,btn_e,btn_r,btn_t,btn_y,btn_u,btn_i,btn_o,btn_p;
    public Button  btn_a,btn_s,btn_d,btn_f,btn_g,btn_h,btn_j,btn_k,btn_l;
    public Button  btn_z,btn_x,btn_c,btn_v,btn_b,btn_n,btn_m;
    public Button  btn_shownumber,btn_del;

    private    KeyBoarkCall    boarkCall;

    public KeyBoarkCall getBoarkCall() {
        return boarkCall;
    }

    public void setBoarkCall(KeyBoarkCall boarkCall) {
        this.boarkCall = boarkCall;
    }

    public LettersKeyBoark(View root) {
        this.root = root;
        initView();
    }

    public   void   initView(){
        btn_a = findViewById(R.id.btn_a);
        btn_b = findViewById(R.id.btn_b);
        btn_c = findViewById(R.id.btn_c);
        btn_d = findViewById(R.id.btn_d);
        btn_e = findViewById(R.id.btn_e);
        btn_f = findViewById(R.id.btn_f);
        btn_g = findViewById(R.id.btn_g);
        btn_h = findViewById(R.id.btn_h);
        btn_i = findViewById(R.id.btn_i);
        btn_j = findViewById(R.id.btn_j);
        btn_k = findViewById(R.id.btn_k);
        btn_l = findViewById(R.id.btn_l);
        btn_m = findViewById(R.id.btn_m);
        btn_n = findViewById(R.id.btn_n);
        btn_o = findViewById(R.id.btn_o);
        btn_p= findViewById(R.id.btn_p);
        btn_q = findViewById(R.id.btn_q);
        btn_r = findViewById(R.id.btn_r);
        btn_s = findViewById(R.id.btn_s);
        btn_t = findViewById(R.id.btn_t);
        btn_u = findViewById(R.id.btn_u);
        btn_v = findViewById(R.id.btn_v);
        btn_w = findViewById(R.id.btn_w);
        btn_x = findViewById(R.id.btn_x);
        btn_y = findViewById(R.id.btn_y);
        btn_z = findViewById(R.id.btn_z);
        btn_shownumber = findViewById(R.id.btn_shownumber);
        btn_del = findViewById(R.id.btn_del);
        btn_a.setOnClickListener(this);
        btn_b.setOnClickListener(this);
        btn_c.setOnClickListener(this);
        btn_d.setOnClickListener(this);
        btn_e.setOnClickListener(this);
        btn_f.setOnClickListener(this);
        btn_g.setOnClickListener(this);
        btn_h.setOnClickListener(this);
        btn_i.setOnClickListener(this);
        btn_j.setOnClickListener(this);
        btn_k.setOnClickListener(this);
        btn_l.setOnClickListener(this);
        btn_m.setOnClickListener(this);
        btn_n.setOnClickListener(this);
        btn_o.setOnClickListener(this);
        btn_p.setOnClickListener(this);
        btn_q.setOnClickListener(this);
        btn_r.setOnClickListener(this);
        btn_s.setOnClickListener(this);
        btn_t.setOnClickListener(this);
        btn_u.setOnClickListener(this);
        btn_v.setOnClickListener(this);
        btn_w.setOnClickListener(this);
        btn_x.setOnClickListener(this);
        btn_y.setOnClickListener(this);
        btn_z.setOnClickListener(this);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != boarkCall){
                    boarkCall.onDelectLast();
                }
            }
        });
        btn_shownumber.setOnClickListener(new View.OnClickListener() {
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
