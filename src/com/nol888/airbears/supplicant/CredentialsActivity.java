package com.nol888.airbears.supplicant;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.nol888.airbears.supplicant.WLANAuthenticator.AuthenticationResult;


public class CredentialsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credentials);
		
		overridePendingTransition(R.anim.slide_in_up, R.anim.hold);
		
		CredentialStorage cs = CredentialStorage.getInstance(this);
		if (cs.hasCalnetCredentials()) {
			EditText txtUsername = (EditText) findViewById(R.id.txtUsername);
			txtUsername.setText(cs.getCalnetId());
		}

		Button btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveCreds();
			}
		});
		EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
		txtPassword.setImeActionLabel("Update", KeyEvent.KEYCODE_ENTER);
		txtPassword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				saveCreds();
				return true;
			}
		});
    }
    
    @Override
	protected void onPause() {
		super.onPause();

		overridePendingTransition(R.anim.hold, R.anim.slide_out_down);
	}
    
	private void saveCreds() {
		// save/update calnet account
		CredentialStorage cs = CredentialStorage.getInstance(CredentialsActivity.this);
		EditText txtUsername = (EditText) findViewById(R.id.txtUsername);
		EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

		cs.setCalnetId(txtUsername.getText().toString());
		cs.setCalnetPassphrase(txtPassword.getText().toString());

		Toast.makeText(CredentialsActivity.this, "CalNet credentials updated successfully!", Toast.LENGTH_SHORT).show();

		new ValidateCredentialsTask().execute(txtUsername.getText().toString(), txtPassword.getText().toString());

		finish();
	}

	private class ValidateCredentialsTask extends AsyncTask<String, Integer, AuthenticationResult> {

		@Override
		protected AuthenticationResult doInBackground(String... params) {
			String username = params[0], password = params[1];

			return WLANAuthenticator.authenticate(username, password, true);
		}

		@Override
		protected void onPostExecute(AuthenticationResult result) {
			switch(result) {
			case SUCCESS:
				break;
			case FAILURE:
				Toast.makeText(CredentialsActivity.this, "Could not validate credentials. Storing anyway.", Toast.LENGTH_LONG).show();
				break;
			case INVALID_CREDENTIALS:
				Toast.makeText(CredentialsActivity.this, "Your CalNet ID or Passphrase were invalid! Re-enter them or press back to cancel.", Toast.LENGTH_LONG).show();
				Intent i = new Intent(getApplicationContext(), CredentialsActivity.class);
				startActivity(i);
				break;
			}
		}

	}
}
