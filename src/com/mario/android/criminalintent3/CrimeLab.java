package com.mario.android.criminalintent3;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CrimeLab {

	private static final String TAG = "CrimeLab";
	private static final String FILENAME = "crimes.json";
	
	private ArrayList<Crime> mCrimes;
	private CriminalIntentJSONSerializer mSerializer;
	
	private static CrimeLab sCrimeLab;
	private Context mAppContext;
	
	private CrimeLab(Context appContext){
		mAppContext = appContext;
//		mCrimes = new ArrayList<Crime>();
		mSerializer = new CriminalIntentJSONSerializer(mAppContext,FILENAME);
		
		try {
			mCrimes = mSerializer.loadCrimes();
		} catch (Exception e) {
			mCrimes = new ArrayList<Crime>();
			Log.e(TAG, "Error loading cries:",e);
		}
	}
	
	public static CrimeLab get(Context c){
		if(sCrimeLab == null){
			sCrimeLab = new CrimeLab(c.getApplicationContext());
		}
		return sCrimeLab;
	}
	
	public void addCrime(Crime c){
		mCrimes.add(c);
	}
	
	public void deleteCrime(Crime c){
		mCrimes.remove(c);
	}
	
	public ArrayList<Crime> getCrimes(){
		return mCrimes;
	}
	
	public Crime getCrime(UUID id){
		for(Crime c : mCrimes){
			if(c.getId().equals(id)){
				return c;
			}
		}
		return null;
	}
	
	public boolean saveCrimes(){
		try {
			mSerializer.saveCrimes(mCrimes);
//			Toast.makeText(mAppContext, "crimes saved to file", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "crimes saved to file");
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error saving crimes:", e);
//			Toast.makeText(mAppContext, "Error saving crimes", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}
