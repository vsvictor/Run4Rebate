package mk.run4rebate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public class LoginGpActivity extends Activity implements OnClickListener,
		PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener,
		PlusClient.OnAccessRevokedListener {

	private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;

	private static final int REQUEST_CODE_SIGN_IN = 1;
	private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_gp);

		mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				MomentUtil.ACTIONS).build();

		/*
		 * mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
		 * mSignInButton.setOnClickListener(this); mSignOutButton =
		 * findViewById(R.id.sign_out_button);
		 * mSignOutButton.setOnClickListener(this); mRevokeAccessButton =
		 * findViewById(R.id.revoke_access_button);
		 * mRevokeAccessButton.setOnClickListener(this);
		 */

	}

	@Override
	public void onStart() {
		super.onStart();
		mPlusClient.connect();

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		mConnectionResult = result;
		updateButtons(false /* isSignedIn */);
	}

	public void onConnection() {
		int available = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (available != ConnectionResult.SUCCESS) {
			showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
			return;
		}

		try {
			mConnectionResult.startResolutionForResult(this,
					REQUEST_CODE_SIGN_IN);
		} catch (IntentSender.SendIntentException e) {
			// Fetch a new result to start.
			mPlusClient.connect();
		}
	}

	private void updateButtons(boolean isSignedIn) {
		if (isSignedIn) {
			String currentPersonName = mPlusClient.getCurrentPerson()
					.getDisplayName();
			Intent intent = new Intent();
			Person person = mPlusClient.getCurrentPerson();
			int width = getResources().getDimensionPixelOffset(
					R.dimen.avatar_radius) * 2;
			String photo = "https://plus.google.com/s2/photos/profile/"
					+ person.getId() + "?sz=" + width;
			intent.putExtra("user_last_name", person.getName().getFamilyName());
			intent.putExtra("user_first_name", person.getName().getGivenName());
			intent.putExtra("user_photo", photo);
			intent.putExtra("user_email", mPlusClient.getAccountName());
			intent.putExtra("social", "gp");
			setResult(Activity.RESULT_OK, intent);
			finish();
		} else {
			if (mConnectionResult == null) {
				// Disable the sign-in button until onConnectionFailed is called
				// with result.

			} else {
				// Enable the sign-in button since a connection result is
				// available.
				onConnection();

			}

		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
			return super.onCreateDialog(id);
		}

		int available = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (available == ConnectionResult.SUCCESS) {
			return null;
		}
		if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
			return GooglePlayServicesUtil.getErrorDialog(available, this,
					REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
		}
		return new AlertDialog.Builder(this)
				.setMessage(R.string.plus_generic_error).setCancelable(true)
				.create();
	}

	@Override
	public void onConnected(Bundle connectionHint) {

		updateButtons(true /* isSignedIn */);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_SIGN_IN
				|| requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
			if (resultCode == RESULT_OK && !mPlusClient.isConnected()
					&& !mPlusClient.isConnecting()) {
				// This time, connect should succeed.
				mPlusClient.connect();
			}
		}
	}

	@Override
	public void onStop() {
		mPlusClient.disconnect();
		super.onStop();
	}

	@Override
	public void onAccessRevoked(ConnectionResult status) {
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
