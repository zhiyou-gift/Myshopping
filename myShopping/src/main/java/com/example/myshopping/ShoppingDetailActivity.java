package com.example.myshopping;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myshopping.database.ShoppingDBHelper;
import com.example.myshopping.enity.GoodsInfo;
import com.example.myshopping.util.ToastUtil;

public class ShoppingDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_title;
    private TextView tv_count;
    private TextView tv_goods_price;
    private TextView tv_goods_desc;
    private ImageView iv_goods_pic;
    private ShoppingDBHelper dbHelper;
    private int mGoodsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_detail);
        tv_title = findViewById(R.id.tv_title);
        tv_count = findViewById(R.id.tv_count);
        tv_goods_price = findViewById(R.id.tv_goods_price);
        tv_goods_desc = findViewById(R.id.tv_goods_desc);
        iv_goods_pic = findViewById(R.id.iv_goods_pic);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.btn_add_cart).setOnClickListener(this);
        findViewById(R.id.iv_cart).setOnClickListener(this);

        tv_count.setText(String.valueOf(MyApplication.getInstance().goodsCount));

        dbHelper = ShoppingDBHelper.getInstance(this);

    }

    protected void onResume(){
        super.onResume();
        showDetail();
    }

    private void showDetail() {
        mGoodsId = getIntent().getIntExtra("goods_id",0);
        if (mGoodsId > 0){
            GoodsInfo info = dbHelper.queryGoodsInfoById(mGoodsId);
            tv_title.setText(info.name);
            tv_goods_desc.setText(info.description);
            tv_goods_price.setText(String.valueOf((int)info.price));
            iv_goods_pic.setImageURI(Uri.parse(info.picPath));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back){
            finish();
        } else if (view.getId() == R.id.iv_cart) {
            Intent intent = new Intent(this,ShoppingCartActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_add_cart) {
            addToCart(mGoodsId);
        }
    }

    private void addToCart(int mGoodsId) {
        dbHelper.insertCartInfo(mGoodsId);
        //购物车商品数加1
        int goodscounts = ++MyApplication.getInstance().goodsCount;
        tv_count.setText(String.valueOf(goodscounts));
        ToastUtil.show(this,"成功添加至购物车");
    }
}