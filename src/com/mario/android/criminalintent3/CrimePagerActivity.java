package com.mario.android.criminalintent3;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class CrimePagerActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.ViewPager);
		setContentView(mViewPager);

		mCrimes = CrimeLab.get(this).getCrimes();

		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

			@Override
			public int getCount() {
				return mCrimes.size();
			}

			@Override
			public Fragment getItem(int pos) {
				Crime crime = mCrimes.get(pos);
				return CrimeFragment.newInstance(crime.getId());
			}
		});

		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						Crime crime = new Crime();
						if (crime.getTitle() != null) {
							new CrimePagerActivity().setTitle(crime.getTitle());
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				});

		UUID crimeId = (UUID) getIntent().getSerializableExtra(
				CrimeFragment.EXTRA_CRIME_ID);
		for (int i = 0; i < mCrimes.size(); i++) {
			if (mCrimes.get(i).getId().equals(crimeId)) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}
	}
}
