package rxjava.android.com.rxjavastudy;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import rxjava.android.com.rxjavastudy.chapter3.FirstExampleFragment;
import rxjava.android.com.rxjavastudy.chapter3.SecondExampleFragment;
import rxjava.android.com.rxjavastudy.chapter3.ThirdExampleFragment;
import rxjava.android.com.rxjavastudy.chapter4.DistinctExampleFragment;
import rxjava.android.com.rxjavastudy.chapter4.FilterExampleFragment;
import rxjava.android.com.rxjavastudy.chapter4.TakeExampleFragment;
import rxjava.android.com.rxjavastudy.chapter5.GroupByExampleFragment;
import rxjava.android.com.rxjavastudy.chapter5.MapExampleFragment;
import rxjava.android.com.rxjavastudy.chapter5.ScanExampleFragment;
import rxjava.android.com.rxjavastudy.chapter6.AndThenWhenExampleFragment;
import rxjava.android.com.rxjavastudy.chapter6.CombineLatestExampleFragment;
import rxjava.android.com.rxjavastudy.chapter6.JoinExampleFragment;
import rxjava.android.com.rxjavastudy.chapter6.MergeExampleFragment;
import rxjava.android.com.rxjavastudy.chapter6.ZipExampleFragment;
import rxjava.android.com.rxjavastudy.chapter7.LongTaskFragment;
import rxjava.android.com.rxjavastudy.chapter7.NetworkTaskFragment;
import rxjava.android.com.rxjavastudy.chapter7.SharedPreferencesListFragment;
import rxjava.android.com.rxjavastudy.chapter9.PhoneWallFragment;
import rxjava.android.com.rxjavastudy.chapter9.RxDownloadFragment;
import rxjava.android.com.rxjavastudy.fragment.NavigationDrawerFragment;
import rxjava.android.com.rxjavastudy.interfaces.NavigationDrawerCallbacks;
import rxjava.android.com.rxjavastudy.mvp.LoginFragment;


public class MainActivity extends AppCompatActivity implements NavigationDrawerCallbacks{

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, mDrawerLayout, mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new FirstExampleFragment())
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new SecondExampleFragment())
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ThirdExampleFragment())
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new FilterExampleFragment())
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new TakeExampleFragment())
                        .commit();
                break;
            case 5:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new DistinctExampleFragment())
                        .commit();
                break;
            case 6:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new MapExampleFragment())
                        .commit();
                break;
            case 7:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ScanExampleFragment())
                        .commit();
                break;
            case 8:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new GroupByExampleFragment())
                        .commit();
                break;
            case 9:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new MergeExampleFragment())
                        .commit();
                break;
            case 10:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new ZipExampleFragment())
                        .commit();
                break;
            case 11:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new JoinExampleFragment())
                        .commit();
                break;
            case 12:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new CombineLatestExampleFragment())
                        .commit();
                break;
            case 13:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new AndThenWhenExampleFragment())
                        .commit();
                break;
            case 14:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new SharedPreferencesListFragment())
                        .commit();
                break;
            case 15:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new LongTaskFragment())
                        .commit();
                break;
            case 16:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new NetworkTaskFragment())
                        .commit();
                break;
            case 17:
                break;
            case 18:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new PhoneWallFragment())
                        .commit();
                break;
            case 19:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new RxDownloadFragment())
                        .commit();
                break;
            case 20:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new LoginFragment())
                        .commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
