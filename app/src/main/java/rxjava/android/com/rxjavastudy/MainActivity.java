package rxjava.android.com.rxjavastudy;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import rxjava.android.com.rxjavastudy.fragment.FirstExampleFragment;
import rxjava.android.com.rxjavastudy.fragment.NavigationDrawerFragment;
import rxjava.android.com.rxjavastudy.fragment.SecondExampleFragment;
import rxjava.android.com.rxjavastudy.interfaces.NavigationDrawerCallbacks;


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
