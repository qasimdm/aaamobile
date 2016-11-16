package app15.aaamobile.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import app15.aaamobile.R;

/**
 * Created by umyhafzaqa on 2016-11-15.
 */
public class PaymentDialogFragment extends DialogFragment {
    final String TAG = "PaymentDialogFragment";

    private FragmentTabHost mTabHost;
    private ViewPager viewPager;
    private PaymentPagerAdapter adapter;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.dialog_payment, container);
            getDialog().setTitle("Payment");

            mTabHost = (FragmentTabHost) view.findViewById(R.id.tabHost);
            mTabHost.setup(getActivity(), getChildFragmentManager());
            //mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Paypal"), Fragment.class, null);
            /*TabHost.TabSpec tabPaypal = mTabHost.newTabSpec("tab1");
            tabPaypal.setIndicator("Paypal",getResources().getDrawable(R.drawable.ic_delete_24dp));
            Intent intentPaypal = new Intent(getContext(), PaymentDialogFragment.class);
            tabPaypal.setContent(intentPaypal);
            mTabHost.addTab(tabPaypal);
            mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.ic_delete_24dp);*/

            setupTab(new ImageButton(getContext()), "Paypal");
            //mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Paypal", getResources().getDrawable(android.R.drawable.arrow_down_float)));
            mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Bank/Credit Card"), Fragment.class, null);


            adapter = new PaymentPagerAdapter(getChildFragmentManager(), getArguments());
            adapter.setTitles(new String[]{"Paypal", "Card"});

            viewPager = (ViewPager)view.findViewById(R.id.pager);
            viewPager.setAdapter(adapter);

            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i2) {
                }

                @Override
                public void onPageSelected(int i) {
                    mTabHost.setCurrentTab(i);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

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
        ImageButton tv = (ImageButton) view.findViewById(R.id.tabBg);
        tv.setImageResource(R.drawable.pp_cc_mark);
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
                Fragment fragment = new AboutFragment();
                /*Bundle args = new Bundle();
                args.putSerializable("voters",bundle.getSerializable( num == 0 ? "pros" : "cons"));
                fragment.setArguments(args);*/
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

        /*public static class VotersListFragment extends ListFragment {

            List<String> voters = new ArrayList<>();

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_about, container, false);
                return view;
            }

            @Override
            public void onActivityCreated(Bundle savedInstanceState) {

                super.onActivityCreated(savedInstanceState);
               // voters = (ArrayList) getArguments().getSerializable("voters");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, voters);
                setListAdapter(adapter);

                getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });

            }

        }*/


}
