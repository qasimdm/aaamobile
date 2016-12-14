package app15.aaamobile.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import app15.aaamobile.R;

/**
 * Created by umyhafzaqa on 2016-11-15.
 */
public class PaymentDialogFragment extends DialogFragment {
    private final String TAG = "PaymentDialogFragment";

    private FragmentTabHost mTabHost;
    private ViewPager viewPager;
    private PaymentPagerAdapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_payment, container);
            getDialog().setTitle("Payment");

            mTabHost = (FragmentTabHost) view.findViewById(R.id.tabHost);
            mTabHost.setup(getActivity(), getChildFragmentManager());

            //Setting Paypal tab
            setupTab(new ImageView(getContext()), "Paypal");
            //Setting Card Payment tab
            mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Bank/Credit Card"), Fragment.class, null);


            adapter = new PaymentPagerAdapter(getChildFragmentManager(), getArguments());
            adapter.setTitles(new String[]{"Paypal", "Card"});

            viewPager = (ViewPager)view.findViewById(R.id.pager);
            viewPager.setAdapter(adapter);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                @Override
                public void onTabChanged(String s) {
                    int i = mTabHost.getCurrentTab();
                    viewPager.setCurrentItem(i);
                }
            });

            return view;
        }

    private void setupTab(final View view, final String tag) {
        View tabview = createTabView(mTabHost.getContext(), tag);
        TabHost.TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {return view;}
        });
        mTabHost.addTab(setContent);

    }
    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_background, null);
        ImageView img_paypal = (ImageView) view.findViewById(R.id.tabBg);
        img_paypal.setImageResource(R.drawable.pp_cc_mark);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
        params.gravity = Gravity.CENTER;
        img_paypal.setLayoutParams(params);

        return view;
    }



        public class PaymentPagerAdapter extends FragmentPagerAdapter {

            Bundle bundle;
            String [] titles;

            public PaymentPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            public PaymentPagerAdapter(FragmentManager fm, Bundle bundle) {
                super(fm);
                this.bundle = bundle;
            }

            @Override
            public Fragment getItem(int num) {
                Fragment fragment;
                if (num == 0){
                    fragment = new PaypalFragment();
                }
                else {
                    fragment = new CardPayment();
                }
                return fragment;
            }


            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            public void setTitles(String[] titles) {
                this.titles = titles;
            }
        }

}
