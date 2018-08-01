package com.layer.xdk.ui.message.feedback;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.layer.xdk.ui.databinding.XdkUiLargeFeedbackMessageBinding;

/**
 * Shows a feedback message with summary and a comment field. Use the provided ARG_ constants to
 * create a bundle for the arguments.
 */
public class LargeFeedbackMessageFragment extends Fragment {
    public static final String ARG_MESSAGE_PART_ID = "message_part_id";
    public static final String ARG_REQUESTED_RATING = "requested_rating";

    private XdkUiLargeFeedbackMessageBinding mBinding;
    private LargeFeedbackViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(LargeFeedbackViewModel.class);

        if (getArguments() != null) {
            mViewModel.setExtras(getArguments());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mBinding = XdkUiLargeFeedbackMessageBinding.inflate(inflater, container, false);

        mBinding.setViewModel(mViewModel);
        mBinding.feedbackButton.setPerformActionOnClick(false);
        mBinding.feedbackButton.setUpdateStateOnClick(true);
        mBinding.feedbackButton.setAllowClicksWhileDisabled(false);
        mBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.send(mBinding.feedbackButton.getRating(),
                        mBinding.comment.getText().toString());
            }
        });

        return mBinding.getRoot();
    }
}
