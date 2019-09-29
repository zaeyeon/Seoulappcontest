package com.example.seoulapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;


public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        //추상화된 view를 실제적으로
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.ReviewToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);




       /* dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);*/

       /* final TextView textView = (TextView) root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        return root;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater Inflater) {
        Inflater.inflate(R.menu._action_bar, menu);
        super.onCreateOptionsMenu(menu, Inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_button:
                Intent intent = new Intent(getActivity(), CreateReviewAct.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}
    /*class cmtMore extends View.OnClickListenr {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), CommentMoreAct.class);
            startActivity(intent);
        }
    }*/





