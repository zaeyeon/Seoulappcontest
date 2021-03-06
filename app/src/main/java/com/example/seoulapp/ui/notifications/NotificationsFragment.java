package com.example.seoulapp.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;
import com.example.seoulapp.ShopDetaildInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class NotificationsFragment extends Fragment {

    Context mContext = getContext();

    Intent intentSettingShop;

    private NotificationsViewModel notificationsViewModel;

    String clickShopName;
    TextView tvNickname;

    ImageView ivProfile;
    ImageView ivMyItem;
    ImageView ivNews;
    ImageView ivShopAdd;
    ImageView ivShopSetting;
    ImageView ivSettings;
    ListAdapter adapter;

    LinearLayout NoShopPage, YesShopPage;

    static String strEmail;
    ListView m_oListView = null;

    int bookmarkNum;
    int listPosition;

    String TAG = "NotificationsFragment";
    String[] shopName;
    String[] shopProfile;
    String[] shopInfo;

    ArrayList<ItemData> oData = new ArrayList<>();
    ListAdapter oAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        notificationsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        mContext = getContext();

        View v = inflater.inflate(R.layout.fragment_notifications, container, false);

        SharedPreferences auto = this.getActivity().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = auto.getString("inputId", "null");

        new TaskGetBookmark().execute("http://dongdong.com.ngrok.io/getBookmark");
        NoShopPage = v.findViewById(R.id.noShopPage);
        YesShopPage = v.findViewById(R.id.yesShopPage);

        ivProfile = (ImageView)v.findViewById(R.id.ivProfile);
        ivProfile.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            ivProfile.setClipToOutline(true);
        }

        // ivProfile에 현재 사용자 프로필 이미지
        // tvNickname에 현재 사용자 닉네임
        tvNickname = (TextView)v.findViewById(R.id.tvNickname);
        // tvEmail에 현재 사용자 e-mail
        TextView tvEmail = (TextView)v.findViewById(R.id.tvEmail);
        tvEmail.setText(strEmail); // strEmail
        Log.d("NotificationsFragment", "현재 사용자 : " + strEmail);
        // DB에서 현재 사용자의 id값과 일치하는 nickname과 email 추출
        // String(strNickname, strEmail)에 저장


        ivMyItem = (ImageView) v.findViewById(R.id.ivMyItem);
        ivMyItem.setOnClickListener(new goMyItem());
        ivNews = (ImageView) v.findViewById(R.id.ivNews);
        ivNews.setOnClickListener(new goNews());
        ivSettings = (ImageView) v.findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(new goSettings());
        ivShopAdd = (ImageView)v.findViewById(R.id.ivAddShop);
        ivShopAdd.setOnClickListener(new goAddShop());
        ivShopSetting = (ImageView)v.findViewById(R.id.ivShopSetting);
        ivShopSetting.setOnClickListener(new goSettingShop());

        // 와이파이 새로 접속할 때마다 변경
        new JSONTask().execute("http://dongdong.com.ngrok.io/getUserInfo");

        // 즐겨찾기 리스트
//        String[] strBookmark =  {"들락날락", "다래락", "라일락", "라운지오", "워커하우스"};
//        ArrayList<ItemData> oData = new ArrayList<>();
//        for (int i = 0; i < strBookmark.length; i++) {
//            ItemData oItem = new ItemData();
//            oItem.strShopName = strBookmark[i];
//            oData.add(oItem);
//        }

//        ArrayList<ItemData> oData = new ArrayList<>();
//        if (shopName == null) {
//            ItemData oItem = new ItemData();
//            oItem.strShopName = "즐겨 찾는 매장을 등록하세요.";
//            oData.add(oItem);
//        } else {
//            for (int i = 0; i < shopName.length; i++) {
//                ItemData oItem = new ItemData();
//                oItem.strShopName = shopName[i];
//                oData.add(oItem);
//            }
//        }

        m_oListView = (ListView) v.findViewById(R.id.listView);
//        ListAdapter oAdapter = new ListAdapter(oData);
//        m_oListView.setAdapter(adapter);

        m_oListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemData item = (ItemData)parent.getItemAtPosition(position);
                clickShopName = item.getStrShopName();

                new TaskGetShopInfo().execute("http://dongdong.com.ngrok.io/getShopInfo");
            }
        });

        // listview 클릭 시 각 매장 페이지로 이동(매장 id를 ShopDetaildInfo에 전달)



        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        // 커스텀디자인 활성화
        actionBar.setDisplayShowCustomEnabled(true);
        // 툴바에 타이틀 비활성화
        actionBar.setDisplayShowTitleEnabled(false);
        // 툴바에 필터 버튼 활성화
        // actionBar.setDisplayHomeAsUpEnabled(true);
        // actionBar.setHomeAsUpIndicator(R.drawable.ic_filter);

        View viewToolbar = getActivity().getLayoutInflater().inflate(R.layout.home_tool_bar, null);
        // ImageButton searchButton = viewToolbar.findViewById(R.id.search_btn);
        toolbar.setBackgroundColor(Color.rgb(38,175,253));
        actionBar.setCustomView(viewToolbar, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.LEFT));


        // return root;
        return v;
    }

    public class JSONTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌


                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String[] userInfo;
            userInfo = result.split("\\|");

            Log.d(TAG, userInfo[0]);

            tvNickname.setText(userInfo[0]);

            if(userInfo[1].equals(""))
            {
                Glide.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/dong-dong-c7d7e.appspot.com/o/images%2FUserProfileImage%2Fblank_profile.png?alt=media&token=90154892-ab2f-4cf4-b21a-f39799468a33").into(ivProfile);
            }
            else {
                Glide.with(getContext()).load(userInfo[1]).into(ivProfile);
                Log.d(TAG, "userInfo[3]:" + userInfo[1]);
            }
            if(userInfo[2].equals("0"))
            {
                YesShopPage.setVisibility(INVISIBLE);
                NoShopPage.setVisibility(VISIBLE);
            }
            else{
                YesShopPage.setVisibility(VISIBLE);
                NoShopPage.setVisibility(INVISIBLE);
            }
        }
    }

    public class TaskGetBookmark extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌


                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            bookmarkNum = Integer.parseInt(result);
            new TaskGetName().execute("http://dongdong.com.ngrok.io/getBMName");
            new TaskGetProfile().execute("http://dongdong.com.ngrok.io/getBMProfile");

        }
    }

    public class TaskGetName extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌


                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("noFavoriteShop")) {

            } else {
                adapter = new ListAdapter();
                m_oListView.setAdapter(adapter);

                String[] favoriteInfo;
                String[] shopName;
                String[] shopProfile;

                favoriteInfo = result.split("&&");
                shopName = favoriteInfo[0].split("\\|");
                shopProfile = favoriteInfo[1].split("\\|");

                for (int i = 0; i < shopName.length; i++) {
                    adapter.addItem(shopName[i], shopProfile[i]);
                }
            }
        }
    }

    public class TaskGetProfile extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌


                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            /*
            shopProfile = result.split("\\|");
            Log.d(TAG, "즐겨찾기 이미지 개수 : " + shopProfile.length);
            if (shopProfile == null) {
                ItemData oItem = new ItemData();
                oItem.strShopImage = "blank_profile.png";
                oData.add(oItem);
            } else {
                for (int i = 0; i < shopProfile.length; i++) {
                    ItemData oItem = new ItemData();
                    Log.d("NotificationsFragment", "상점 이미지 : " + shopProfile[i]);
                    oItem.strShopImage = shopProfile[i];
                    oData.add(oItem);
                }
            }

            oAdapter = new ListAdapter();
            m_oListView.setAdapter(oAdapter);

             */
        }
    }

    public class TaskGetShopInfo extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopName", clickShopName);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌


                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            shopInfo = result.split("\\|");
            Log.d(TAG, "shopinfo : " + shopInfo[0]);
            Intent intent = new Intent(getActivity(), ShopDetaildInfo.class);

            intent.putExtra("profileImage", shopInfo[0]);
            intent.putExtra("name", clickShopName);
            intent.putExtra("building", shopInfo[1]);
            intent.putExtra("floor", shopInfo[2]);
            intent.putExtra("rocation", shopInfo[3]);
            intent.putExtra("category", shopInfo[4]);
            intent.putExtra("style", shopInfo[5]);
            intent.putExtra("intro", shopInfo[6]);
            intent.putExtra("representation1", shopInfo[7]);
            intent.putExtra("representation2", shopInfo[8]);
            intent.putExtra("representation3", shopInfo[9]);

            startActivity(intent);
        }
    }

    class goMyItem implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new TaskGetMyItem().execute("http://dongdong.com.ngrok.io/getMyItem");
        }
    }

    class goNews implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new JSONTaskCurrentUser().execute("http://dongdong.com.ngrok.io/getCurrentUserReview");
        }
    }

    class goAddShop implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentAddShop = new Intent(getActivity(), AddShop.class);
            startActivity(intentAddShop);
        }
    }

    class goSettingShop implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            intentSettingShop = new Intent(getActivity(), ShopDetaildInfo.class);


            new JSONTaskGetMyShop().execute("http://dongdong.com.ngrok.io/getMyShop");

            // startActivity(intentSettingShop);
        }
    }

    class goSettings implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intentSettings);
        }
    }


    public class JSONTaskGetMyShop extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String[] myShopInfo;

            myShopInfo = result.split("\\|");

            for(int i = 0; i < myShopInfo.length; i++)
            {
                Log.d("myShopInfo[i] : ", myShopInfo[i]);
            }

            intentSettingShop.putExtra("name", myShopInfo[5]);
            intentSettingShop.putExtra("building", myShopInfo[6]);
            intentSettingShop.putExtra("floor", myShopInfo[7]);
            intentSettingShop.putExtra("rocation", myShopInfo[8]);
            intentSettingShop.putExtra("category", myShopInfo[10]);
            intentSettingShop.putExtra("style", myShopInfo[9]);
            intentSettingShop.putExtra("intro", myShopInfo[11]);

            startActivity(intentSettingShop);
        }
    }

    public class JSONTaskCurrentUser extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {

                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    //URL url = new URL("http://192.168.25.16:3000/users%22);
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송


                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();
                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result); //undefined라

            Log.d("MyReviewButton : ", result);

            if (result.equals("")) {
                Toast.makeText(mContext, "리뷰가 없습니다", Toast.LENGTH_LONG).show();
            } else {
                Intent intentMyReview = new Intent(getActivity(), MyReviewActivity.class);
                startActivity(intentMyReview);
            }
        }
    }

    public class TaskGetMyItem extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송


                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("")) Toast.makeText(mContext, "관심 상품이 없습니다", Toast.LENGTH_LONG).show();
            else {
                Intent intentMyItem = new Intent(getActivity(), MyItem.class);
                startActivity(intentMyItem);
            }
        }
    }
}