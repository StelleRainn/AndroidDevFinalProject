package com.example.myreader;

import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

public class SalesRecordActivity extends AppCompatActivity {
    private ListView salesRecordListView;
    private SalesRecordAdapter adapter;
    private List<SalesRecord> salesRecords;
    private String username;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_record);

        username = getIntent().getStringExtra("username");
        if (username == null) {
            Log.e("SalesRecordActivity", "Username is null");
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        salesRecordListView = findViewById(R.id.salesRecordListView);
        emptyView = findViewById(R.id.emptyView);

        salesRecordListView.setEmptyView(emptyView);

        loadSalesRecords();

        // 添加滚动监听，控制下拉刷新
        salesRecordListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = firstVisibleItem == 0 && view.getChildAt(0) != null && view.getChildAt(0).getTop() == 0;
                swipeRefreshLayout.setEnabled(enable);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadSalesRecords();
        });
    }

    private void loadSalesRecords() {
        try {
            salesRecords = DatabaseHelper.getInstance(getApplicationContext()).getSalesRecords(username);
            Log.d("SalesRecordActivity", "Loaded " + (salesRecords != null ? salesRecords.size() : 0) + " sales records for " + username);
            if (salesRecords == null || salesRecords.isEmpty()) {
                emptyView.setText("暂无出售记录");
            }
            if (adapter == null) {
                adapter = new SalesRecordAdapter(this, salesRecords);
                salesRecordListView.setAdapter(adapter);
            } else {
                adapter.clear();
                if (salesRecords != null) {
                    adapter.addAll(salesRecords);
                }
                adapter.notifyDataSetChanged();
            }
            Toast.makeText(SalesRecordActivity.this, "已刷新出售记录", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("SalesRecordActivity", "Error loading sales records: " + e.getMessage());
            Toast.makeText(this, "加载出售记录失败", Toast.LENGTH_SHORT).show();
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}