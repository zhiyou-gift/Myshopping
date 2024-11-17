package com.example.myshopping.enity;
import com.example.myshopping.R;
import java.util.ArrayList;

public class GoodsInfo {
    public int id;
    // 名称
    public String name;
    // 描述
    public String description;
    // 价格
    public float price;
    // 大图的保存路径
    public String picPath;
    // 大图的资源编号
    public int pic;

    // 声明一个手机商品的名称数组
    private static String[] mNameArray = {
            "iPhone16","iPhone16ProMax", "Mate60", "小米15", "OPPO Find X8", "vivo S18", "荣耀Magic7",
    };
    // 声明一个手机商品的描述数组
    private static String[] mDescArray = {
            "Apple iPhone16 256GB 群青色 5G手机",
            "Apple iPhone16ProMax 256GB 原色钛金属 旗舰手机",
            "华为 HUAWEI Mate60 12GB+256GB 雅丹黑 5G全网通 全面屏手机",
            "小米 Xiaomi15 12GB+256GB 丁香紫 5G手机 智能游戏手机",
            "OPPO Find X8  12GB+256GB 气泡粉 5G智能手机",
            "vivo S18 12GB+256GB 花似锦 5G全网通 5G智能人像拍照AI手机",
            "荣耀Magic7 12GB+256GB 绒黑色 5G芯片 直屏智能手机",
    };
    // 声明一个手机商品的价格数组
    private static float[] mPriceArray = {6999, 9999, 4999, 4499, 4199, 2599,4499};
    // 声明一个手机商品的大图数组
    private static int[] mPicArray = {
            R.drawable.iphone16,R.drawable.iphone16pm, R.drawable.huawei, R.drawable.xiaomi15,
            R.drawable.oppo, R.drawable.vivo, R.drawable.rongyao
    };

    // 获取默认的手机信息列表
    public static ArrayList<GoodsInfo> getDefaultList() {
        ArrayList<GoodsInfo> goodsList = new ArrayList<GoodsInfo>();
        for (int i = 0; i < mNameArray.length; i++) {
            GoodsInfo info = new GoodsInfo();
            info.id = i;
            info.name = mNameArray[i];
            info.description = mDescArray[i];
            info.price = mPriceArray[i];
            info.pic = mPicArray[i];
            goodsList.add(info);
        }
        return goodsList;
    }
}
