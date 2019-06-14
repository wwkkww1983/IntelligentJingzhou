package com.zack.intelligent.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zack.intelligent.R;
import com.zack.intelligent.bean.PhotoBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 抓拍照片
 */
public class CaptureFragment extends Fragment {
    private static final String TAG = "CaptureFragment";

    Unbinder unbinder;
    @BindView(R.id.capture_grid_view)
    GridView captureGridView;
    //    List<CapturePictureBean> imageList;
    private List<PhotoBean> imageList;
    int index = 0;
    int pageCount = 15;
    @BindView(R.id.btn_pre_page)
    Button btnPrePage;
    @BindView(R.id.tv_cur_page)
    TextView tvCurPage;
    @BindView(R.id.btn_next_page)
    Button btnNextPage;
    @BindView(R.id.btn_delete_photo)
    Button btnDeletePhoto;
    @BindView(R.id.ll_page)
    LinearLayout llPage;
    @BindView(R.id.capture_recycler_view)
    RecyclerView captureRecyclerView;
    //    private CaptureAdapter captureAdapter;
    private GridAdapter gridAdapter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                gridAdapter.notifyDataSetChanged();
                if (imageList.isEmpty()) {
                    btnDeletePhoto.setVisibility(View.INVISIBLE);
                    tvCurPage.setText(index + 1 + "/1");
                } else {
                    if (imageList.size() <= pageCount) {
                        btnNextPage.setVisibility(View.INVISIBLE);
                    } else {
                        btnNextPage.setVisibility(View.VISIBLE);
                    }
                    tvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) imageList.size() / pageCount));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public CaptureFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, container, false);
        unbinder = ButterKnife.bind(this, view);
        imageList = new ArrayList<>();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getCapturePicture();
//            }
//        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                imageList = getImagePathFromSD();
                Collections.reverse(imageList);
                mHandler.sendEmptyMessage(0);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imageList.isEmpty()) {
                            btnDeletePhoto.setVisibility(View.INVISIBLE);
                            tvCurPage.setText(index + 1 + "/1");
                        } else {
                            if (imageList.size() <= pageCount) {
                                btnNextPage.setVisibility(View.INVISIBLE);
                            } else {
                                btnNextPage.setVisibility(View.VISIBLE);
                            }
                            tvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) imageList.size() / pageCount));
                        }
                    }
                });
            }
        }).start();

        btnPrePage.setVisibility(View.INVISIBLE);
        btnNextPage.setVisibility(View.INVISIBLE);

        captureGridView.setNumColumns(5);
        gridAdapter = new GridAdapter();
        captureGridView.setAdapter(gridAdapter);
//        GridLayoutManager glm = new GridLayoutManager(getContext(), 5);
//        captureRecyclerView.setLayoutManager(glm);
//        captureAdapter = new CaptureAdapter();
//        captureRecyclerView.setAdapter(captureAdapter);

        return view;
    }

//    private void getCapturePicture() {
//        HttpClient.getInstance().getCapturePicture(getContext(), new HttpListener<String>() {
//            @Override
//            public void onSucceed(int what, Response<String> response) throws JSONException {
////                Log.i(TAG, "getCapturePicture onSucceed resposne: "+response.get());
//                DataBean dataBean = JSON.parseObject(response.get(), DataBean.class);
//                boolean success = dataBean.isSuccess();
//                Log.i(TAG, "onSucceed success: " + success);
//                if (success) {
//                    String body = dataBean.getBody();
//                    if (!TextUtils.isEmpty(body)) {
//                        List<CapturePictureBean> capturePictureList = JSON.parseArray(body, CapturePictureBean.class);
//                        imageList.addAll(capturePictureList);
//                        captureAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailed(int what, Response<String> response) {
//
//            }
//        });
//    }

    private void nexPager() {
        index++;
        System.out.println(index + "nexPager");
        tvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) imageList.size() / pageCount));
        gridAdapter.notifyDataSetChanged();
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "nexPager index: " + index);
    }

    private void checkButton() {
        if (index <= 0) {
            Log.i(TAG, "checkButton index <=0: ");
            btnPrePage.setVisibility(View.INVISIBLE);
            btnNextPage.setVisibility(View.VISIBLE);
        } else if (imageList.size() - index * pageCount <= pageCount) {    //数据总数减每页数当小于每页可显示的数字时既是最后一页
            Log.i(TAG, "checkButton 2: ");
            btnPrePage.setVisibility(View.VISIBLE);
            btnNextPage.setVisibility(View.INVISIBLE);
        } else {
            Log.i(TAG, "checkButton 3: ");
            btnNextPage.setVisibility(View.VISIBLE);
            btnPrePage.setVisibility(View.VISIBLE);
        }
    }

    private void prePager() {
        index--;
        System.out.println(index + "prePager");
        gridAdapter.notifyDataSetChanged();
        tvCurPage.setText(index + 1 + "/" + (int) Math.ceil((double) imageList.size() / pageCount));
        //隐藏上一个或下一个按钮
        checkButton();
        Log.i(TAG, "prePager index: " + index);
    }

    @OnClick({R.id.btn_pre_page, R.id.btn_next_page, R.id.btn_delete_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_pre_page://上一页
                prePager();
                break;
            case R.id.btn_next_page://下一页
                nexPager();
                break;
            case R.id.btn_delete_photo: //删除图片
//                deletePhoto();
//                if(!imageList.isEmpty()){
//                    for (int i = 0; i < imageList.size(); i++) {
//                        PhotoBean photoBean = imageList.get(i);
//                        String imagePath = photoBean.getImagePath();
//                        String imageName = photoBean.getImageName();
//                        Log.i(TAG, "onCreateView  imagePath: "+imagePath);
//                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//                        String base64Str = Utils.bitmapToBase64Str(bitmap);
//                        Log.i(TAG, "onViewClicked base64Str length: "+base64Str.length());
//                        CapturePictureBean capturePictureBean =new CapturePictureBean();
//                        capturePictureBean.setAddTime(System.currentTimeMillis());
//                        capturePictureBean.setCabId(SharedUtils.getGunCabId());
//                        capturePictureBean.setPhotoFile(base64Str);
//                        capturePictureBean.setPhotoName("capture_picture_"+i);
//                        capturePictureBean.setRoomId(SharedUtils.getRoomId());
//                        String jsonString = JSON.toJSONString(capturePictureBean);
//                        LogUtil.i(TAG, "onViewClicked jsonString: "+jsonString);
//                        postCapturePic(jsonString);
//                    }
//                }
                break;
        }
    }

//    private void postCapturePic(String jsonString) {
//        HttpClient.getInstance().postCapturePicture(getContext(), jsonString, new HttpListener<String>() {
//            @Override
//            public void onSucceed(int what, Response<String> response) throws JSONException {
//                Log.i(TAG, "postCapturePic onSucceed  response: " + response.get());
//            }
//
//            @Override
//            public void onFailed(int what, Response<String> response) {
//                Log.i(TAG, "onFailed error: " + response.getException().getMessage());
//            }
//        });
//    }

//    public class CaptureAdapter extends RecyclerView.Adapter<CaptureAdapter.CaptureViewHolder> {
//
//        @Override
//        public CaptureAdapter.CaptureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(
//                    R.layout.capture_recycler_list_item, parent, false);
//            return new CaptureViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(CaptureAdapter.CaptureViewHolder holder, int position) {
//            ViewGroup.LayoutParams layoutParams = holder.captureIvItemImage.getLayoutParams();
//            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//            holder.captureIvItemImage.setAdjustViewBounds(true);
//            int pos = position + index * pageCount;
//            CapturePictureBean capturePictureBean = imageList.get(pos);
//            String photoFile = capturePictureBean.getPhotoFile();
//            long addTime = capturePictureBean.getAddTime();
//            String time = Utils.longTime2String(addTime);
//            Bitmap bitmap = Utils.base64ToBitmap(photoFile);
//            holder.captureIvItemImage.setImageBitmap(bitmap);
//            holder.captureTvItemName.setText(time);
//        }
//
//        @Override
//        public int getItemCount() {
//            int current = index * pageCount;
//            return imageList.size() - current < pageCount ? imageList.size() - current : pageCount;
//        }
//
//        class CaptureViewHolder extends RecyclerView.ViewHolder {
//            @BindView(R.id.capture_iv_item_image)
//            ImageView captureIvItemImage;
//            @BindView(R.id.capture_tv_item_name)
//            TextView captureTvItemName;
//
//            CaptureViewHolder(View view) {
//                super(view);
//                ButterKnife.bind(this, view);
//            }
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 从sd卡获取图片资源
     *
     * @return
     */
    public static List<PhotoBean> getImagePathFromSD() {
        // 图片列表
        List<PhotoBean> imageList = new ArrayList<PhotoBean>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator
                + "Capture";
//        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator
//                + "Camera";
        Log.i(TAG, "getImagePathFromSD filePath: " + filePath);
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        if(files !=null && files.length !=0){
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
                    String name = file.getName();
//                Log.i("MAIN", "getImagePathFromSD name: "+name);
                    PhotoBean photoBean = new PhotoBean();
                    photoBean.setImagePath(file.getPath());
                    file.getName();
                    long lastmodified = file.lastModified();
//                Log.i("main", "getImagePathFromSD lastmodified : "+lastmodified);
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    String newDate = simpleDateFormat.format(new Date(lastmodified));
                    Log.i("main", "getImagePathFromSD date: " + newDate);
                    photoBean.setImageName(newDate);
                    imageList.add(photoBean);
                }
            }
        }
        // 返回得到的图片列表
        return imageList;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    private static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int current = index * pageCount;
            return imageList.size() - current < pageCount ? imageList.size() - current : pageCount;
        }

        @Override
        public Object getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.capture_recycler_list_item, parent, false);
                holder = new ViewHolder(convertView);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ViewGroup.LayoutParams layoutParams = holder.captureIvItemImage.getLayoutParams();
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            holder.captureIvItemImage.setAdjustViewBounds(true);
            int pos = position + index * pageCount;
            PhotoBean photoBean = imageList.get(pos);
            Bitmap bitmap = BitmapFactory.decodeFile(photoBean.getImagePath());
            holder.captureIvItemImage.setImageBitmap(bitmap);
            holder.captureTvItemName.setText(photoBean.getImageName());

            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.capture_iv_item_image)
            ImageView captureIvItemImage;
            @BindView(R.id.capture_tv_item_name)
            TextView captureTvItemName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
                view.setTag(this);
            }
        }
    }
}
