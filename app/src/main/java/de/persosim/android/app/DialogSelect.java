package de.persosim.android.app;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;



/**
 * This class provides the functionality provided by the "Select Personalization" dialog.
 * @author slutters
 *
 */
public class DialogSelect extends DialogFragment implements OnEditorActionListener {
	
	public static final String LOG_TAG = DialogSelect.class.getName();
	
	private Button buttonOk, buttonCancel, buttonBrowse;
	
	private static Context activityContext;
	
	public interface SelectDialogListener {
        void onFinishEditDialog(String inputText);
    }
	
	private EditText mEditText;
	private Spinner spinner;
	
	public DialogSelect() {
		Log.d(LOG_TAG, "DialogSelect() here");
    }
	
    public DialogSelect(Context newActivityContext) {
        activityContext = newActivityContext;
        Log.d(LOG_TAG, "activity context is: " + newActivityContext);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personalization, container);
        getDialog().setTitle("Settings");
    	
      spinner = (Spinner) view.findViewById(R.id.dialog_personalization_spinner_predefined_id);
		Log.d(LOG_TAG, "spinner is: " + spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activityContext, R.array.std_personalizations, android.R.layout.simple_spinner_item);
		Log.d(LOG_TAG, "adapter is: " + adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		mEditText = (EditText) view.findViewById(R.id.dialog_personalization_edittext_custom_id);
        
        buttonBrowse = (Button) view.findViewById(R.id.dialog_personalization_button_custom_id);
        buttonBrowse.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
               
				Log.d(LOG_TAG, "START showFileSelectDialog()");

				File file = new File(Environment.getExternalStorageDirectory(),
						"myFolder");
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setDataAndType(Uri.fromFile(file), "text/xml");
				
				startActivityForResult(intent,1);

				Log.d(LOG_TAG, "END showFileSelectDialog()");
            }
        });
        
        buttonOk = (Button) view.findViewById(R.id.dialog_personalization_button_ok_id);
        buttonOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	String newPath = mEditText.getText().toString();
            	if((newPath == null) || (newPath.length() == 0)) {
            		int position = spinner.getLastVisiblePosition();
            		
            		if(position == 11) {
            			Log.d(LOG_TAG, "GT Profile selected!");
            			newPath = Constants.DIR_PERSO_NAME + "/ProfileGT.xml";
            		} else {
            			newPath = Constants.DIR_PERSO_NAME + "/Profile" +  String.format("%02d", position) + ".xml";
            		}
            		
            	}
            	
            	Log.d(LOG_TAG, "selected new personalization is: " + newPath);
            	File file = new File(newPath);
            	
            	if(file.exists()) {
            		Log.d(LOG_TAG, "File " + file.getAbsolutePath() + " actually exists");
                	
                	Intent intent = new Intent(CardService.CARDSERVICE_PERSONALIZATION);

            		intent.putExtra(CardService.CARDSERVICE_PERSONALIZATION_PATH, newPath);
            		activityContext.sendBroadcast(intent);
            	} else {
            		Log.w(LOG_TAG, "File " + file.getAbsolutePath() + " does NOT exist");
            	}
            	
                getDialog().dismiss();
            }
        });
        
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
				 if (spinner.getSelectedItemPosition() != 0) {
	                 mEditText.setText("");
	                
				 }
            }
			
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
               // nothing to do
            }
        });
        
        buttonCancel = (Button) view.findViewById(R.id.dialog_personalization_button_cancel_id);
        buttonCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            SelectDialogListener activity = (SelectDialogListener) getActivity();
            activity.onFinishEditDialog(mEditText.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }
    
public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
    if(data != null)
    {
	String temp = data.getData().getPath();
    	temp = temp.substring(temp.lastIndexOf(":") +1);
    	//TODO edit the path, so its possible to have more then 1 sdcard
    	String Fpath = "/storage/sdcard0/" + temp;
        mEditText.setText(Fpath);
       super.onActivityResult(requestCode, resultCode, data);
    }
       spinner.setSelection(0);
    

   }
	
}
