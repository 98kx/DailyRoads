package com.konka.dialyroads.frament;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konka.dialyroads.R;

public class LocalFileFragment extends Fragment{
	private FragmentTabHost mTabHost;
	private final static String TAG_TAB_1 = "本地图片";
	private final static String TAG_TAB_2 = "本地视频";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.webfileindex, container, false);
		mTabHost = (FragmentTabHost) view.findViewById(R.id.fragmentTabHost);
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabFrameLayout);
		
		mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_2).setIndicator(TAG_TAB_2),
				LocalVideoFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_1).setIndicator(TAG_TAB_1),
				LocalImageFragment.class, null);

		return view;
	}

	   @Override
	    public void onDestroyView() {
	        super.onDestroyView();
	        mTabHost = null;
	    }
}
