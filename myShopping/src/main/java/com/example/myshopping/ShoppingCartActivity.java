package com.example.myshopping;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myshopping.database.ShoppingDBHelper;
import com.example.myshopping.enity.CartInfo;
import com.example.myshopping.enity.GoodsInfo;
import com.example.myshopping.util.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_count;
    private TextView tv_title;
    private LinearLayout ll_cart;
    private ShoppingDBHelper mDBHelper;
    private List<CartInfo> mCartlist;
    // 声明一个根据商品编号查找商品信息的映射，把商品信息缓存起来，这样不用每一次都去查询数据库
    private Map<Integer, GoodsInfo> mGoodsMap = new HashMap<>();
    private TextView tv_total_price;
    private LinearLayout ll_empty;
    private LinearLayout ll_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_cart);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("购物车");
        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(String.valueOf(MyApplication.getInstance().goodsCount));
        tv_total_price = findViewById(R.id.tv_total_price);

        ll_cart = findViewById(R.id.ll_cart);

        ll_empty = findViewById(R.id.ll_empty);
        ll_content = findViewById(R.id.ll_content);

        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_shopping_channel).setOnClickListener(this);
        findViewById(R.id.btn_settle).setOnClickListener(this);

        mDBHelper = ShoppingDBHelper.getInstance(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //展示购物车中商品列表
        showCart();
    }

    private void showCart() {
        //移除下面所有的子视图
        ll_cart.removeAllViews();
        //查询购物车数据库中的数据
        mCartlist = mDBHelper.queryAllCartInfo();
        if (mCartlist.size() == 0) {
            return;
        }

        for (CartInfo info : mCartlist) {
            // 根据商品编号查询商品数据库中的商品记录
            GoodsInfo goods = mDBHelper.queryGoodsInfoById(info.goodsId);
            mGoodsMap.put(info.goodsId, goods);

            // 获取布局文件item_cart.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.item_cart, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_desc = view.findViewById(R.id.tv_desc);
            TextView tv_count = view.findViewById(R.id.tv_count);
            TextView tv_price = view.findViewById(R.id.tv_price);
            TextView tv_sum = view.findViewById(R.id.tv_sum);

            iv_thumb.setImageURI(Uri.parse(goods.picPath));
            tv_name.setText(goods.name);
            tv_desc.setText(goods.description);
            tv_count.setText(String.valueOf(info.count));
            tv_price.setText(String.valueOf((int) goods.price));
            // 设置商品总价
            tv_sum.setText(String.valueOf((int) (info.count * goods.price)));

            // 给商品行添加长按事件。长按商品行就删除该商品
            view.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCartActivity.this);
                builder.setMessage("是否从购物车删除" + goods.name + "？");
                builder.setPositiveButton("是", (dialog, which) -> {
                    // 移除当前视图
                    ll_cart.removeView(v);
                    // 删除该商品
                    deleteGoods(info);
                });
                builder.setNegativeButton("否", null);
                builder.create().show();
                return true;
            });

            // 给商品行添加点击事件。点击商品行跳到商品的详情页
            view.setOnClickListener(v -> {
                Intent intent = new Intent(ShoppingCartActivity.this, ShoppingDetailActivity.class);
                intent.putExtra("goods_id", goods.id);
                startActivity(intent);
            });

            // 往购物车列表添加该商品行
            ll_cart.addView(view);
        }

        // 重新计算购物车中的商品总金额
        refreshTotalPrice();
    }

    private void deleteGoods(CartInfo info) {
        MyApplication.getInstance().goodsCount -= info.count;
        //从购物车数据库删除商品
        mDBHelper.deleteCartInfoByGoodsId(info.goodsId);
        //从购物车列表删除商品
        CartInfo removed = null;
        for (CartInfo cartInfo : mCartlist) {
            if (cartInfo.goodsId == info.goodsId) {
                removed = cartInfo;
                break;
            }
        }
        mCartlist.remove(removed);
        //显示最新商品数量
        showCount();
        ToastUtil.show(this, "已从购物车中删除" + mGoodsMap.get(info.goodsId).name);
        mGoodsMap.remove(info.goodsId);
        //刷新金额
        refreshTotalPrice();
    }

    private void showCount() {
        tv_count.setText(String.valueOf(MyApplication.getInstance().goodsCount));
        //购物车为空的情况
        if (MyApplication.getInstance().goodsCount == 0) {
            ll_empty.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.GONE);
            ll_cart.removeAllViews();
        }else{
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        }
    }

    private void refreshTotalPrice() {
        int totalPrice = 0;
        for (CartInfo info : mCartlist) {
            GoodsInfo goods = mGoodsMap.get(info.goodsId);
            totalPrice += goods.price * info.count;
        }
        tv_total_price.setText(String.valueOf(totalPrice));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        }else if(view.getId() == R.id.btn_shopping_channel){
            //跳转商品页面
            Intent intent = new Intent(this,ShoppingChannelActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(view.getId() == R.id.btn_clear){
            //清空购物车
            mDBHelper.deleteAllCartInfo();
            MyApplication.getInstance().goodsCount = 0;
            //再次显示购物车商品数量
            showCount();
            ToastUtil.show(this,"已清空购物车");
        } else if (view.getId() == R.id.btn_settle) {
            //结算功能
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("结算商品");
            builder.setMessage("支付功能尚未开发");
            builder.setPositiveButton("明白",null);
            builder.create().show();

            //跳转微信相关逻辑
//            Intent intent = new Intent();
//            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
//            intent.setAction(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setComponent(cmp);
//            startActivity(intent);
        }
    }
}