package com.vmloft.develop.app.match.ui.main.home;

import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.BindView;
import com.avos.avoscloud.AVUser;
import com.vmloft.develop.app.match.R;
import com.vmloft.develop.app.match.base.ACallback;
import com.vmloft.develop.app.match.base.AppActivity;
import com.vmloft.develop.app.match.bean.AMatch;
import com.vmloft.develop.app.match.bean.AUser;
import com.vmloft.develop.app.match.common.AMatchManager;
import com.vmloft.develop.app.match.common.ASignManager;
import com.vmloft.develop.library.tools.utils.VMDimen;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.widget.VMViewGroup;
import com.vmloft.develop.library.tools.widget.toast.VMToast;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by lzan13 on 2019/5/15 23:13
 *
 * 匹配界面
 */
public class MatchActivity extends AppActivity {

    @BindView(R.id.match_anim_view) View mAnimView;
    @BindView(R.id.match_avatar_iv) ImageView mAvatarView;
    @BindView(R.id.match_view_group) VMViewGroup mViewGroup;

    private AUser mUser;
    private AMatch mMatch;
    private List<AMatch> mMatchList = new ArrayList<>();
    // 是否正在执行动画
    private boolean isAnim;
    private int avatarSize;

    @Override
    protected int layoutId() {
        return R.layout.activity_match;
    }

    @Override
    protected void initUI() {
        super.initUI();
        startMatch();
    }

    @Override
    protected void initData() {
        mUser = ASignManager.getInstance().getCurrentUser();

        avatarSize = VMDimen.dp2px(48);
        getMatchData();
    }

    /**
     * 获取匹配数据
     */
    private void getMatchData() {
        AMatchManager.getInstance().getMatchList(new ACallback<List<AMatch>>() {
            @Override
            public void onSuccess(List<AMatch> list) {
                VMLog.d("查询到匹配人员" + list);
                mMatchList.addAll(list);
                setupMatchList();
            }

            @Override
            public void onError(int code, String desc) {
                VMToast.make(mActivity, "好像有问题哎，稍后再来吧").error();
            }
        });
    }

    /**
     * 开始匹配，需要经自己的信息提交到后端
     */
    private void startMatch() {
        if (isAnim) {
            return;
        }
        AMatchManager.getInstance().startMatch(new ACallback<AMatch>() {
            @Override
            public void onSuccess(AMatch match) {
                mMatch = match;
                VMToast.make(mActivity, "提交匹配信息成功").done();
            }

            @Override
            public void onError(int code, String desc) {
                VMToast.make(mActivity, "提交匹配信息失败").error();
            }
        });
        AnimationSet set = (AnimationSet) AnimationUtils.loadAnimation(mActivity, R.anim.home_match_scale_anim);
        mAnimView.startAnimation(set);
        isAnim = true;
    }

    /**
     * 加载匹配数据
     */
    private void setupMatchList() {
        for (AMatch match : mMatchList) {
            ImageView imageView = new ImageView(mActivity);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(avatarSize, avatarSize);
            mViewGroup.addView(imageView, lp);
        }
    }

    @Override
    protected void onDestroy() {
        if (mMatch != null) {
            AMatchManager.getInstance().stopMatch(mMatch);
        }
        if (isAnim) {
            mAnimView.clearAnimation();
        }
        super.onDestroy();
    }
}