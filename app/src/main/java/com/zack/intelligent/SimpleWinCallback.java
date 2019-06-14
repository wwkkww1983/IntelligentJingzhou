package com.zack.intelligent;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Administrator on 2018/5/24.
 */

public class SimpleWinCallback implements Window.Callback {

    Window.Callback callback;

    public SimpleWinCallback(Window.Callback callback){
        this.callback = callback;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return callback.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return callback.dispatchKeyShortcutEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return callback.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        return callback.dispatchTrackballEvent(event);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return callback.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return callback.dispatchPopulateAccessibilityEvent(event);

    }

    @Override
    public View onCreatePanelView(int featureId) {
        return callback.onCreatePanelView(featureId);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return callback.onCreatePanelMenu(featureId,menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return callback.onPreparePanel(featureId,view,menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return callback.onMenuOpened(featureId,menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return callback.onMenuItemSelected(featureId,item);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        callback.onWindowAttributesChanged(attrs);
    }

    @Override
    public void onContentChanged() {
        callback.onContentChanged();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        callback.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onAttachedToWindow() {
        callback.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        callback.onDetachedFromWindow();
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        callback.onPanelClosed(featureId,menu);
    }

    @Override
    public boolean onSearchRequested() {
        return callback.onSearchRequested();
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        return false;
    }

    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return this.callback.onWindowStartingActionMode(callback);
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return null;
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        callback.onActionModeStarted(mode);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        callback.onActionModeFinished(mode);
    }
}
