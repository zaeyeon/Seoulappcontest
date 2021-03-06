package com.example.seoulapp.ui.dashboard;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;

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
import java.util.List;


public class CustomViewFrag extends ListFragment {

    ListView lv;
    TextView tv_comment;
    ImageView User_img;
    ImageView onclick_heart;
    ImageView profile_img;
    TextView Commentmore;

    ReviewAdapter adapter;

    String strEmail;
    String[] User_Image;
    String[] User_Id;
    String[] User_Content;
    String[] Review_StoreName;
    String[] Review_Number;
    String[] Like;
    String[] allView;
    String[] emails;
    String[] ReviewProfile;
    ScrollView Sv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//추상화된 view를 실제적으로
        View getItemId = inflater.inflate(R.layout.review_listview_itemview, container, false);
        SharedPreferences auto = this.getActivity().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = auto.getString("inputId", "null"); //이메일

        tv_comment = getItemId.findViewById(R.id.comment);
        User_img = (ImageView) getItemId.findViewById(R.id.User_img);
        onclick_heart = getItemId.findViewById(R.id.love_filed);
        profile_img = getItemId.findViewById(R.id.User_profilePicture);
        Commentmore = getItemId.findViewById(R.id.watching_comment);


        ActivityManager manager = (ActivityManager) getContext().getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = manager.getRunningTasks(1);
        ActivityManager.RunningTaskInfo info = list.get(0);
        Log.d("CustomViewFrag", "최상위 액티비티 : " + info.topActivity.getClassName());
        if (info.topActivity.getClassName().equals("com.example.seoulapp.ui.notifications.MyReviewActivity")) {
            new JSONTaskCurrentUser().execute("http://dongdong.com.ngrok.io/getCurrentUserReview");
        } else {
            new JSONTaskUserReview().execute("http://dongdong.com.ngrok.io/getReview");

        }

/* dashboardViewModel =
ViewModelProviders.of(this).get(DashboardViewModel.class);*/

// lv = inflater.inflate(R.layout.fragment_dashboard, container, false).findViewById(R.id.dashboard_layout);

// adapter.addItem((ContextCompat.getDrawable(getActivity(), R.drawable.icon_heart)), ContextCompat.getDrawable(getActivity(), R.drawable.ic_dashboard_black_24dp), "sterning", "aaaaa");
// adapter.addItem((ContextCompat.getDrawable(getActivity(), R.drawable.ic_notifications_black_24dp)), ContextCompat.getDrawable(getActivity(), R.drawable.icon_picture), "sonder", "bbbb");
// adapter.addItem((ContextCompat.getDrawable(getActivity(), R.drawable.test_beach)), ContextCompat.getDrawable(getActivity(), R.drawable.star), "sonder", "bbbb");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //리뷰 불러오기
    public class JSONTaskUserReview extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
//JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
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
        protected void onPostExecute(String result) { //result 값은 2임.
            super.onPostExecute(result); //undefined라고 떠

            Log.d("CustomView", "UserProfile 성공");
            ReviewAdapter adapter = new ReviewAdapter();
            setListAdapter(adapter);

            allView = result.split("#&#"); //result = allView

// Review_Number = allView[0].split("\\|");
            User_Image = allView[0].split("\\|"); //유효 |
            User_Id = allView[1].split("\\|");
            User_Content = allView[2].split("\\|");
            Review_StoreName = allView[3].split("\\|");
            Like = allView[4].split("\\|");
            Review_Number = allView[5].split("\\|");
            ReviewProfile = allView[6].split("\\|");
            emails = allView[7].split("\\|");

//하나,둘 이렇게 되어있을 거거든요.

            for (int i = 0; i < User_Id.length; i++) {

                int a = User_Id.length - 1;
//String _number = Review_Number[a-i];
                String UserImg = "" + User_Image[a - i];
                String UserId = User_Id[a - i];
                String UserContent = User_Content[a - i];
                String StoreName = Review_StoreName[a - i];
                String _Like = Like[a - i];
                String Number = Review_Number[a - i];
                String Review_profile = ReviewProfile[a - i];
                String email = emails[a - i];

                Log.d("CustomViewFrag", "_Like = " + _Like);

                int like = Integer.parseInt(_Like);
                int Numbering = Integer.parseInt(Number);
                adapter.addItem(UserImg, UserId, UserContent, StoreName, like, Numbering, Review_profile, email);
            }
//comment_Id랑 comment를 commentAct로 넘겨야합니당..
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
        protected void onPostExecute(String result) { //result 값은 2임.
            super.onPostExecute(result); //undefined라

            Log.d("CustomView", "UserProfile 성공");
            ReviewAdapter adapter = new ReviewAdapter();
            setListAdapter(adapter);

            allView = result.split("\\/"); //result = allView

// Review_Number = allView[0].split("\\|");
            User_Image = allView[0].split("\\|"); //유효 |
            User_Id = allView[1].split("\\|");
            User_Content = allView[2].split("\\|");
            Review_StoreName = allView[3].split("\\|");
            Like = allView[4].split("\\|");
            Review_Number = allView[5].split("\\|");
            ReviewProfile = allView[6].split("\\|");
            emails = allView[7].split("\\|");

//하나,둘 이렇게 되어있을 거거든요.

            for (int i = 0; i < User_Id.length; i++) {

                int a = User_Id.length - 1;
//String _number = Review_Number[a-i];
                String UserImg = User_Image[a - i];
                String UserId = User_Id[a - i];
                String UserContent = User_Content[a - i];
                String StoreName = Review_StoreName[a - i];
                String _Like = Like[a - i];
                String Number = Review_Number[a - i];
                String Review_profile = ReviewProfile[a - i];
                String email = emails[a - i];

                Log.d("CustomViewFrag", "_Like = " + _Like);

                int like = Integer.parseInt(_Like);
                int Numbering = Integer.parseInt(Number);
                adapter.addItem(UserImg, UserId, UserContent, StoreName, like, Numbering, Review_profile, email);
            }
//comment_Id랑 comment를 commentAct로 넘겨야합니당..
        }
    }
}