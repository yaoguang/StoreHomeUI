package com.yao.storehomeuidemo.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yao.storehomeuidemo.R
import com.yao.storehomeuidemo.data.GoodsInfo

/**
 * Created by szh on 2017/9/5.
 */

class GoodsAdapter : BaseQuickAdapter<GoodsInfo, BaseViewHolder>(R.layout.item_goods) {
    override fun convert(helper: BaseViewHolder, item: GoodsInfo) {
        helper.setText(R.id.goods_name, item.goodsName)
//        helper.getView<ImageView>(R.id.goods_icon).setImageURI(Uri.parse(item.iconUrl))
    }
}
