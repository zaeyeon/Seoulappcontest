package com.example.seoulapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class QnAListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<QnAListViewItem> QnAListViewItemList = new ArrayList<QnAListViewItem>();

    // QnAListViewAdapter의 생성자
    public QnAListViewAdapter() {

    }

    @Override
    public int getCount() { return QnAListViewItemList.size(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        // "qna_list_item" Layout를 inflate하여 convertView 참조 획득
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.qna_list_item, parent, false);
        }

        TextView QnATitleView = convertView.findViewById(R.id.qnaTitle);
        TextView QnANicknameView = convertView.findViewById(R.id.qnaOwner);
        TextView QnAProductionView = convertView.findViewById(R.id.qnaProduction);
        //TextView QnASizeView = convertView.findViewById(R.id.qnaSize);
        // TextView QnAQuestionView = convertView.findViewById(R.id.qnaQuestion);
        //TextView QnACEOView = convertView.findViewById(R.id.qnaCEO);
        // TextView QnAAnswerView = convertView.findViewById(R.id.qnaAnswer2);
        QnAListViewItem qnaListViewItem = QnAListViewItemList.get(position);
        ImageView QnAAnswerNoView = convertView.findViewById(R.id.answerNo);
        ImageView QnAAnswerYesView = convertView.findViewById(R.id.answerYes);

        Log.d("qnaListViewItem : ", qnaListViewItem.getQnAAnswerExist());
        if(qnaListViewItem.getQnAAnswerExist().equals("0")){
            QnAAnswerNoView.setVisibility(VISIBLE);
            QnAAnswerYesView.setVisibility(INVISIBLE);
        }
        else{
            QnAAnswerYesView.setVisibility(VISIBLE);
            QnAAnswerNoView.setVisibility(INVISIBLE);
        }

        QnATitleView.setText(qnaListViewItem.getQnATitle());
        QnANicknameView.setText(qnaListViewItem.getQnAUserNickname());
        QnAProductionView.setText(qnaListViewItem.getQnAProduction());
        //QnACEOView.setText(qnaListViewItem.getQnACEO());
        return convertView;
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public Object getItem(int position) { return QnAListViewItemList.get(position); }

    // 아이템 추가를 위한 함수
    public void addItem(String title, String production, String nickname, String content, String answer, String shopName, String existance) {

        QnAListViewItem item = new QnAListViewItem();

        item.setQnATitle(title);
        item.setQnAProduction(production);
        item.setQnAUserNickname(nickname);
        item.setQnAContent(content);
        item.setQnAShopName(shopName);
        item.setQnAAnswer(answer);
        item.setQnAAnswerExist(existance);

        QnAListViewItemList.add(item);
    }
}
