package com.leonhuang.xuetangx;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class PostActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		
		final TextView tv = (TextView)findViewById(R.id.post_text_count);
		final EditText textMessage = (EditText)findViewById(R.id.post_text);

		textMessage.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            tv.setText(String.valueOf(textMessage.getText().length()));
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_send:
				send();
        		return true;
        	default:
            	return super.onOptionsItemSelected(item);
		}
	}

	private void send() {
		// TODO Auto-generated method stub
		
	}

}
