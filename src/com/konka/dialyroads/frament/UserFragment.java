package com.konka.dialyroads.frament;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konka.dialyroads.R;

public class UserFragment extends Fragment {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	LoginFragment loginFragment;
	RegisterFragment registerFragment;
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.dialoglogin, container, false);
		// List<Fragment> fragments = new ArrayList<Fragment>();
		// loginFragment = new LoginFragment();
		// registerFragment = new RegisterFragment();
		// fragments.add(loginFragment);
		// fragments.add(registerFragment);
		// mSectionsPagerAdapter = new
		// SectionsPagerAdapter(getChildFragmentManager(), fragments);
		//
		// mViewPager = (ViewPager) view.findViewById(R.id.pager);
		//
		// mViewPager.setAdapter(mSectionsPagerAdapter);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		List<Fragment> fragments = new ArrayList<Fragment>();
		loginFragment = new LoginFragment();
		registerFragment = new RegisterFragment();
		fragments.add(loginFragment);
//		fragments.add(registerFragment);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), fragments);

		mViewPager = (ViewPager) view.findViewById(R.id.pager);

		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		List<Fragment> fragments;

		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			// Fragment fragment = new DummySectionFragment();
			// Bundle args = new Bundle();
			// args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position +
			// 1);
			// fragment.setArguments(args);
			System.out.println("fragments.get(position)" + fragments.get(position));
			return fragments.get(position);
			// return new LoginFragment();
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			System.out.println("loginFragment" + loginFragment);
			System.out.println("registerFragment" + registerFragment);
			System.out.println("fragments.get(position)" + fragments.get(position));
			// if (isAdded()) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1);//
			case 1:
				return getString(R.string.title_section2);//
			case 2:
				return getString(R.string.title_section3);//
			}
			// }
			return "dd";
			// return null;
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		view = null;
	}

}
