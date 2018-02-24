package com.layer.xdk.ui.message.carousel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.layer.xdk.ui.R;
import com.layer.xdk.ui.message.container.MessageConstraintContainer;
import com.layer.xdk.ui.message.model.MessageModel;
import com.layer.xdk.ui.message.view.MessageView;
import com.layer.xdk.ui.message.viewer.MessageViewer;

import java.util.List;

public class CarouselMessageView extends MessageView<CarouselMessageModel> {
    private HorizontalScrollView mScrollView;
    private LinearLayout mLinearLayout;

    private CarouselMessageModel mModel;
    private int mItemVerticalMargins;
    private int mItemHorizontalMargins;

    public CarouselMessageView(Context context) {
        this(context, null, 0);
    }

    public CarouselMessageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CarouselMessageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mItemVerticalMargins = Math.round(context.getResources().getDimension(R.dimen.xdk_ui_carousel_message_item_vertical_margins));
        mItemHorizontalMargins = Math.round(context.getResources().getDimension(R.dimen.xdk_ui_carousel_message_item_horizontal_margins));

        mScrollView = new HorizontalScrollView(context, attrs, defStyleAttr);
        mScrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mLinearLayout = new LinearLayout(context, attrs, defStyleAttr);
        mLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        mScrollView.addView(mLinearLayout);
        addView(mScrollView);
    }

    @Override
    public void setMessageModel(CarouselMessageModel carouselMessageModel) {
        mModel = carouselMessageModel;
        mLinearLayout.removeAllViews();
        List<MessageModel> models = carouselMessageModel.getCarouselItemModels();

        for (int i = 0; i < models.size(); i++) {
            MessageModel model = models.get(i);

            MessageViewer messageViewer = new MessageViewer(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                params.setMargins(0, mItemVerticalMargins, mItemHorizontalMargins, mItemVerticalMargins);
            } else if (i == models.size()-1) {
                params.setMargins(mItemHorizontalMargins, mItemVerticalMargins, 0, mItemVerticalMargins);
            } else {
                params.setMargins(mItemHorizontalMargins, mItemVerticalMargins, mItemHorizontalMargins, mItemVerticalMargins);
            }

            messageViewer.setLayoutParams(params);
            messageViewer.bindModelToView(model);
            mLinearLayout.addView(messageViewer);
        }

    }

    @Override
    public Class<? extends MessageConstraintContainer> getContainerClass() {
        return null;
//        return EmptyMessageContainer.class;
    }
}
