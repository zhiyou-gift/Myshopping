package com.example.myshopping;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myshopping.database.ShoppingDBHelper;
import com.example.myshopping.enity.GoodsInfo;
import com.example.myshopping.util.ToastUtil;
import java.util.List;

public class ShoppingChannelActivity extends AppCompatActivity implements View.OnClickListener {

    private ShoppingDBHelper mDBHelper;
    private TextView tv_count;
    private GridLayout gl_channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("Myshopping");
        tv_count = findViewById(R.id.tv_count);

        gl_channel = findViewById(R.id.gl_channel);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_cart).setOnClickListener(this);


        mDBHelper = ShoppingDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();

        showGoods();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //查询购物车商品总数
        showCartInfoTotal();
    }

    private void showCartInfoTotal() {
        int counts = mDBHelper.countCartInfo();
        MyApplication.getInstance().goodsCount = counts;
        tv_count.setText(String.valueOf(counts));
    }

    private void showGoods() {
        //商品条目是一个线性布局，设置布局为屏幕的一半
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
        //查询商品数据库的所有商品记录
        List<GoodsInfo> list = mDBHelper.queryAllGoodsInfo();

        //移除下面的所有子图
        gl_channel.removeAllViews();

        for (GoodsInfo info : list){
            //获取布局文件item_goods.xml的视图
            View view = LayoutInflater.from(this).inflate(R.layout.item_goods,null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_price = view.findViewById(R.id.tv_price);
            Button btn_add = view.findViewById(R.id.btn_add);
            //给控件设置值
            iv_thumb.setImageURI(Uri.parse(info.picPath));
            tv_name.setText(info.name);
            tv_price.setText(String.valueOf((int) info.price));
            //添加物品到购物车
            btn_add.setOnClickListener(view1 -> {
                addToCart(info.id,info.name);
            });

            //跳转商品详情页
            iv_thumb.setOnClickListener(view1 -> {
                Intent intent = new Intent(ShoppingChannelActivity.this,ShoppingDetailActivity.class);
                intent.putExtra("goods_id",info.id);
                startActivity(intent);
            });
            // 把商品视图添加到网格布局
            gl_channel.addView(view, params);
        }
    }

    //添加购物车功能
    private void addToCart(int id, String name) {
        mDBHelper.insertCartInfo(id);
        //购物车商品数加1
        int goodscounts = ++MyApplication.getInstance().goodsCount;
        tv_count.setText(String.valueOf(goodscounts));
        ToastUtil.show(this,"已添加"+ name + "至购物车");
    }

    protected void onDestroy(){
        super.onDestroy();
        mDBHelper.closeLink();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back){
            //关闭当前页面
            finish();
        } else if (view.getId() == R.id.iv_cart) {
            //进入购物车页面
            Intent intent = new Intent(this,ShoppingCartActivity.class);
            //设置启动标志，避免多次返回同一界面
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}