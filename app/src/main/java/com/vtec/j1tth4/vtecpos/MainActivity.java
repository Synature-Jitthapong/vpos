package com.vtec.j1tth4.vtecpos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity { // use AppCompatActivity instead ActionBarActivity

    private List<HashMap<String, String>> mProductDepts;
    private List<PagerItem> mPagers;
    private ViewPagerAdapter mPagerAdapter;

    private Toolbar mToolbar;
    private TabLayout mTabs;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initInstances();
    }

    private void initToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void initInstances(){
        mTabs = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        loadProductDept();
        createViewPager();

        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);
        mTabs.setupWithViewPager(mViewPager);
    }

    private void createViewPager(){
        mPagers = new ArrayList<>();
        for (HashMap<String, String> productDept : mProductDepts){
            PagerItem item = new PagerItem(productDept.get("ProductDeptName"),
                    Integer.parseInt(productDept.get("ProductDeptID")));
            mPagers.add(item);
        }
    }

    private void loadProductDept(){
        mProductDepts = new ArrayList<>();

        // init sqlite connection
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from ProductDept " +
                        " where Deleted=0", null);
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                HashMap<String, String> productDept = new HashMap<>();
                productDept.put("ProductDeptID", cursor.getString(cursor.getColumnIndex("ProductDeptID")));
                productDept.put("ProductDeptName", cursor.getString(cursor.getColumnIndex("ProductDeptName")));
                mProductDepts.add(productDept);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_setup_receipt){
            // go to activity ...
            return true;
        }else if(id == R.id.action_add_product){
            return true;
        }else if(id == R.id.action_add_staff){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PagerItem{
        private CharSequence title;
        private int deptId;

        public PagerItem(CharSequence title, int deptId){
            this.title = title;
            this.deptId = deptId;
        }

        public Fragment createFragment(){
            return PagerFragment.newInstance(deptId);
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPagers.get(position).createFragment();
        }

        @Override
        public int getCount() {
            return mPagers != null ? mPagers.size() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPagers.get(position).title;
        }
    }

    public static class PagerFragment extends Fragment{

        private int deptId;

        private List<HashMap<String, String>> products;
        private ProductAdapter adapter;

        private GridView gridView;

        public static PagerFragment newInstance(int deptId){
            PagerFragment f = new PagerFragment();
            Bundle b = new Bundle();
            b.putInt("deptId", deptId);
            f.setArguments(b);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            deptId = getArguments().getInt("deptId");
            products = new ArrayList<>();
            adapter = new ProductAdapter();
            loadProduct();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.pager_fragment, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            gridView = (GridView) view.findViewById(R.id.gridView);
            gridView.setAdapter(adapter);
        }

        private void loadProduct(){
            // init sqlite connection
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery(
                    "select * from Products " +
                            " where ProductDeptID=?",
                    new String[]{
                            String.valueOf(deptId)
                    });
            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast()){
                    HashMap<String, String> product = new HashMap<>();
                    product.put("ProductID", cursor.getString(cursor.getColumnIndex("ProductID")));
                    product.put("ProductName", cursor.getString(cursor.getColumnIndex("ProductName")));
                    products.add(product);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }


        private class ProductAdapter extends BaseAdapter{

            @Override
            public int getCount() {
                return products != null ? products.size() : 0;
            }

            @Override
            public Object getItem(int i) {
                return products.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                ViewHolder holder;
                if(view == null){
                    holder = new ViewHolder();
                    view = getActivity().getLayoutInflater().inflate(R.layout.menu_item, viewGroup, false);
                    holder.productName = (TextView) view.findViewById(R.id.textViewProductName);
                    view.setTag(holder);
                }else{
                    holder = (ViewHolder) view.getTag();
                }
                HashMap<String, String> product = products.get(i);
                holder.productName.setText(product.get("ProductName"));
                return view;
            }

            private class ViewHolder{
                TextView productName;
            }
        }
    }
}
