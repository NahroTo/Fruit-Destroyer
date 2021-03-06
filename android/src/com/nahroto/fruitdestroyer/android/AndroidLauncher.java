package com.nahroto.fruitdestroyer.android;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.nahroto.fruitdestroyer.ActivityController;
import com.nahroto.fruitdestroyer.AdsController;
import com.nahroto.fruitdestroyer.Application;
import com.nahroto.fruitdestroyer.Debug;
import com.nahroto.fruitdestroyer.GiftizButton;
import com.nahroto.fruitdestroyer.Logger;
import com.nahroto.fruitdestroyer.PlayServices;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.giftiz.sdk.GiftizSDK;

public class AndroidLauncher extends AndroidApplication implements AdsController, PlayServices, ActivityController
{
	private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-7980895899775610/2698922485";

	InterstitialAd interstitialAd;

	private GameHelper gameHelper;
	private final static int requestCode = 1;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);

		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
		{
			@Override
			public void onSignInFailed(){ }

			@Override
			public void onSignInSucceeded(){ }
		};

		gameHelper.setup(gameHelperListener);
		AdBuddiz.setPublisherKey("9bfbf1bb-a9e2-4ce5-8dfd-f25455ceb714");
		AdBuddiz.cacheAds(this);
		initialize(new Application(this, this, this, Application.Platform.ANDROID), config);
		setupAds();
	}

	public void setupAds()
	{
		// INTERSTITIAL AD
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);

		AdRequest.Builder builder = new AdRequest.Builder();
		AdRequest ad = builder.build();
		interstitialAd.loadAd(ad);
	}

	@Override
	public void showAd()
	{
		if (AdBuddiz.isReadyToShowAd(this))
		{
			AdBuddiz.showAd(this);
			if (Debug.AD_INFO)
				Logger.log("adbuddiz showed");
		}
		else if (isWifiConnected())
		{
			showGoogleAd();
			if (Debug.AD_INFO)
				Logger.log("admob showed");
		}
		else if (Debug.AD_INFO)
			Logger.log("no ad showed");
	}

	@Override
	public void showGoogleAd() {
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{

				interstitialAd.setAdListener(new AdListener()
				{
					@Override
					public void onAdClosed()
					{
						AdRequest.Builder builder = new AdRequest.Builder();
						AdRequest ad = builder.build();
						interstitialAd.loadAd(ad);
					}
				});
				interstitialAd.show();
			}
		});
	}

	@Override
	public boolean isWifiConnected()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return (ni != null && ni.isConnected());
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		gameHelper.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		gameHelper.onStop();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		GiftizSDK.onResumeMainActivity(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		GiftizSDK.onPauseMainActivity(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void signIn()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					gameHelper.signOut();
				}
			});
		}
		catch (Exception e)
		{
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void rateGame()
	{
		String str = "Your PlayStore Link";
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}

	@Override
	public void unlockAchievement()
	{
		Games.Achievements.unlock(gameHelper.getApiClient(), getString(R.string.achievement_ach1));
	}

	@Override
	public void submitScore(int highScore)
	{
		if (isSignedIn() == true)
		{
			Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_highest_wave_score), highScore);
		}
	}

	@Override
	public void showAchievement()
	{
		if (isSignedIn() == true)
		{
			startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public void showScore()
	{
		if (isSignedIn() == true)
		{
			startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.leaderboard_highest_wave_score)), requestCode);
		}
		else
		{
			signIn();
		}
	}

	@Override
	public boolean isSignedIn()
	{
		return gameHelper.isSignedIn();
	}

	@Override
	public GiftizButton getButtonStatus()
	{
		if (GiftizSDK.Inner.getButtonStatus(this) == null)
			return null;
		switch (GiftizSDK.Inner.getButtonStatus(this))
		{
			case ButtonInvisible:
				return GiftizButton.INVISIBLE;
			case ButtonNaked:
				return GiftizButton.NAKED;
			case ButtonBadge:
				return GiftizButton.BADGE;
			case ButtonWarning:
				return GiftizButton.WARNING;
			default:
				return null;
		}
	}

	@Override
	public void buttonClicked()
	{
		GiftizSDK.Inner.buttonClicked(this);
	}

	@Override
	public void missionCompleted()
	{
		GiftizSDK.missionComplete(this);
	}
}