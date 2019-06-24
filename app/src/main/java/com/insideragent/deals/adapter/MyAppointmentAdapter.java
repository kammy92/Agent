package com.insideragent.deals.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.insideragent.deals.R;
import com.insideragent.deals.model.MyAppointment;
import com.insideragent.deals.utils.AppConfigTags;
import com.insideragent.deals.utils.AppConfigURL;
import com.insideragent.deals.utils.Constants;
import com.insideragent.deals.utils.NetworkConnection;
import com.insideragent.deals.utils.SetTypeFace;
import com.insideragent.deals.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by actiknow on 9/21/17.
 */

public class MyAppointmentAdapter extends RecyclerView.Adapter<MyAppointmentAdapter.ViewHolder> {
    private Activity activity;
    private List<MyAppointment> myAppointmentList;
    
    public MyAppointmentAdapter(Activity activity, List<MyAppointment> myAppointmentList) {
        this.activity = activity;
        this.myAppointmentList = myAppointmentList;
    }
    
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from (parent.getContext ());
        final View sView = layoutInflater.inflate (R.layout.list_item_appointment, parent, false);
        return new ViewHolder (sView);
    }
    
    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
        final MyAppointment myAppointment = myAppointmentList.get (position);
        holder.tvAddress.setTypeface (SetTypeFace.getTypeface (activity));
        
        holder.tvAddress.setText (myAppointment.getUser_address ());
        holder.tvPropertyAddress.setText (myAppointment.getProperty_address ());
        holder.tvStartDate.setText (myAppointment.getAccess_start_date ());
        holder.tvTime.setText (myAppointment.getTime_frame ());
        holder.tvComment.setText (myAppointment.getUser_comment ());
        switch (myAppointment.getStatus ()) {
            case "0":
                holder.tvStatus.setText ("Pending Access");
                holder.tvCancelRequest.setVisibility (View.GONE);
                holder.tvStatus.setTextColor (activity.getResources ().getColor (R.color.mb_yellow));
                break;

            case "1":
                holder.tvStatus.setText ("Approved Access");
                holder.tvCancelRequest.setVisibility (View.VISIBLE);
                holder.tvCancelRequest.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick (View view) {
                        cancelRequestDialog (myAppointment.getAppointment_id ());
                    }
                });
                holder.tvStatus.setTextColor (activity.getResources ().getColor (R.color.mb_green_dark));
                break;
    
            case "2":
                holder.tvStatus.setText ("Rejected Access");
                holder.tvCancelRequest.setVisibility (View.GONE);
                holder.tvStatus.setTextColor (activity.getResources ().getColor (R.color.mb_red));
                break;
    
            case "3":
                holder.tvStatus.setText ("Access Cancelled By You");
                holder.tvCancelRequest.setVisibility (View.GONE);
                holder.tvStatus.setTextColor (activity.getResources ().getColor (R.color.mb_red));
                break;
    
            case "4":
                holder.tvStatus.setText ("Waiting List");
                holder.tvCancelRequest.setVisibility (View.GONE);
                holder.tvStatus.setTextColor (activity.getResources ().getColor (R.color.mb_red));
                break;
    
            case "5":
                holder.tvStatus.setText ("Access Cancelled By HomeTrust");
                holder.tvCancelRequest.setVisibility (View.GONE);
                holder.tvStatus.setTextColor (activity.getResources ().getColor (R.color.mb_red));
                break;
        }
        
    }
    
    @Override
    public int getItemCount () {
        return myAppointmentList.size ();
    }
    
    private void cancelRequestDialog (final int appointment_id) {
        MaterialDialog dialog = new MaterialDialog.Builder (activity)
                .limitIconToDefaultSize ()
                .content ("Do you want to cancel?")
                .positiveText ("Yes")
                .negativeText ("No")
                .typeface (SetTypeFace.getTypeface (activity), SetTypeFace.getTypeface (activity))
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cancelRequest (appointment_id);
                    }
                }).build ();
        dialog.show ();
    }
    
    private void cancelRequest (final int appointment_id) {
        final ProgressDialog progressDialog = new ProgressDialog (activity);
        if (NetworkConnection.isNetworkAvailable (activity)) {
            Utils.showProgressDialog (progressDialog, activity.getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_CANCEL_APPOINTMENT, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_CANCEL_APPOINTMENT,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        MaterialDialog dialog = new MaterialDialog.Builder (activity)
                                                .limitIconToDefaultSize ()
                                                .content (message)
                                                .positiveText ("OK")
                                                .typeface (SetTypeFace.getTypeface (activity), SetTypeFace.getTypeface (activity))
                                                .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                    @Override
                                                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        activity.finish ();
                                                    }
                                                }).build ();
                                        dialog.show ();
                                    } else {
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.TYPE, AppConfigTags.CANCEL_APPOINTMENT);
                    params.put (AppConfigTags.SCHEDULE_APPOINTMENT_ID, String.valueOf (appointment_id));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        }
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvAddress;
        TextView tvPropertyAddress;
        TextView tvStartDate;
        TextView tvTime;
        TextView tvComment;
        TextView tvStatus;
        TextView tvCancelRequest;
        
        
        public ViewHolder (View itemView) {
            super (itemView);
            tvAddress = (TextView) itemView.findViewById (R.id.tvAddress);
            tvPropertyAddress = (TextView) itemView.findViewById (R.id.tvPropertyAddress);
            tvStartDate = (TextView) itemView.findViewById (R.id.tvStartDate);
            tvTime = (TextView) itemView.findViewById (R.id.tvTime);
            tvComment = (TextView) itemView.findViewById (R.id.tvComment);
            tvStatus = (TextView) itemView.findViewById (R.id.tvStatus);
            tvCancelRequest = (TextView) itemView.findViewById (R.id.tvCancelRequest);
            itemView.setOnClickListener (this);
            
        }
        
        @Override
        public void onClick (View view) {
    
        }
    }
    
    
}
