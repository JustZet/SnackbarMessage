package com.devmob.snackbarmessage;



import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.devmob.snackbarmessage.databinding.LayoutSnackbarBinding;
import com.google.android.material.behavior.SwipeDismissBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class SnackBarMessage {
    private View view;

    String title;
    String message;
    String action;

    View.OnClickListener actionListener;

    String actionSecond;
    View.OnClickListener actionListenerSecond;

    Type type = Type.SUCCESS;
    int icon = R.drawable.ic_info_circle_outlined;

    int duration = 2000;
    private SnackBarMessage(View view) {
        this.view = view;
    }
    private SnackBarMessage() {
    }

    public static SnackBarMessage from(Activity activity) {
        if (activity == null) return new SnackBarMessage();
        View rootView = activity.findViewById(android.R.id.content);
        if (rootView == null) return new SnackBarMessage();
        return new SnackBarMessage(rootView);
    }
    public static SnackBarMessage from(Fragment fragment) {
        if (fragment == null) return new SnackBarMessage();
        if (fragment.getView() == null || !fragment.isAdded()) return new SnackBarMessage();
        View rootView = fragment.getView();
        if (rootView == null) return new SnackBarMessage();
        return new SnackBarMessage(rootView);
    }

    public static SnackBarMessage from(View view) {
        return new SnackBarMessage(view);
    }


    public void show() {
        if (view == null) return;
        Context context = view.getContext();

        // Use a child view as anchor so Snackbar.make() can find the CoordinatorLayout parent
        View anchorView = view;
        if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
            anchorView = ((ViewGroup) view).getChildAt(0);
        }

        Snackbar snackbar = Snackbar.make(anchorView, "", Snackbar.LENGTH_LONG);
        snackbar.setDuration(duration);
        snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.TRANSPARENT);
        snackbarView.setPadding(0,0,0,120);

        ViewGroup snackbarLayout = (ViewGroup) snackbarView;

        // Remove the original Snackbar content so it doesn't interfere with swipe
        View originalContent = snackbarLayout.getChildAt(0);
        if (originalContent != null) {
            snackbarLayout.removeView(originalContent);
        }

        LayoutSnackbarBinding binding = LayoutSnackbarBinding.inflate(
                LayoutInflater.from(context),
                snackbarLayout,
                false
        );


        int color;
        Drawable icon = AppCompatResources.getDrawable(context, R.drawable.ic_check_circle_outlined);

        switch (type) {
            case INFO:
                icon = AppCompatResources.getDrawable(context, R.drawable.ic_info_circle_outlined);
                color = context.getColor(R.color.baby_blue);
                break;
            case ERROR:
                icon = AppCompatResources.getDrawable(context, R.drawable.ic_error_outlined);
                color = context.getColor(R.color.red_dark);
                break;
            case SUCCESS:
                icon = AppCompatResources.getDrawable(context, R.drawable.ic_check_circle_outlined);
                color = context.getColor(R.color.green_checked);
                break;
            case WARNING:
                icon = AppCompatResources.getDrawable(context, R.drawable.ic_info_circle_outlined);
                color = context.getColor(R.color.orange_dark);
                break;
            default:
                color = Color.BLUE;
        }

        binding.ivIcon.setImageDrawable(icon);

        binding.btnActionSecond.setVisibility(actionSecond != null ? View.VISIBLE : View.GONE);
        binding.btnAction.setVisibility(action != null ? View.VISIBLE : View.GONE);
        binding.btnAction.setText(action);
        binding.btnActionSecond.setText(actionSecond);
        binding.btnAction.setOnClickListener(view -> {
            if (actionListener != null) {
                actionListener.onClick(view);
            }
            snackbar.dismiss();
        });
        binding.btnActionSecond.setOnClickListener(view -> {
            if (actionListenerSecond != null) {
                actionListenerSecond.onClick(view);
            }
            snackbar.dismiss();
        });

        if (title != null) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            int start = sb.length();
            sb.append(title);
            sb.setSpan(new ForegroundColorSpan(color), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append("  ");
            sb.append(message);
            binding.tvMessage.setText(sb);
        } else {
            binding.tvMessage.setText(message);
        }


        binding.lpiLoading.setIndicatorColor(color);
        binding.ivIcon.setColorFilter(color);
        binding.ivDismiss.setColorFilter(color);
        binding.btnAction.setTextColor(color);
        binding.btnActionSecond.setTextColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.cvContainer.setOutlineAmbientShadowColor(color);
            binding.cvContainer.setOutlineSpotShadowColor(color);
        }


        binding.ivDismiss.setOnClickListener(v -> snackbar.dismiss());
        snackbarLayout.addView(binding.getRoot(), 0);


        binding.lpiLoading.setMax(1000);
        binding.lpiLoading.setProgress(0);
        ValueAnimator animator = ValueAnimator.ofInt(0, 1000);
        animator.setDuration(duration * 2);
        animator.addUpdateListener(animation -> {
            binding.lpiLoading.setProgress((int) animation.getAnimatedValue());
        });
        animator.start();

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                animator.cancel();
            }
        });

        snackbar.show();
    }

    public SnackBarMessage setTitle(String title) {
        this.title = title;
        return this;
    }

    public SnackBarMessage setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public SnackBarMessage setActionSecond(String actionSecond, View.OnClickListener actionListenerSecond) {
        this.actionSecond = actionSecond;
        this.actionListenerSecond = actionListenerSecond;
        return this;
    }


    public SnackBarMessage setType(Type type) {
        this.type = type;
        return this;
    }

    public SnackBarMessage setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    public SnackBarMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public SnackBarMessage setAction(String action, View.OnClickListener actionListener) {
        this.action = action;
        this.actionListener = actionListener;
        return this;
    }

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }
}
