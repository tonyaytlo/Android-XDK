package com.layer.xdk.ui.message.adapter2.decoration;


import android.content.Context;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.adapter2.MessageGrouping;

import java.util.EnumSet;

public class SubGroupInnerItemDecoration extends MessageGroupingItemDecoration {

    public SubGroupInnerItemDecoration(Context context) {
        super(context.getResources().getDimensionPixelSize(
                R.dimen.xdk_ui_sub_group_inner_item_decoration_height));
    }

    @Override
    boolean shouldDraw(EnumSet<MessageGrouping> groupings) {
        return groupings.contains(MessageGrouping.SUB_GROUP_MIDDLE)
                || groupings.contains(MessageGrouping.SUB_GROUP_END);
    }
}
