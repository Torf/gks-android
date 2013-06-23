package net.thetabx.gksa.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import net.thetabx.gksa.GKSa;
import net.thetabx.gksa.R;
import net.thetabx.gksa.libGKSj.GKS;
import net.thetabx.gksa.libGKSj.http.AsyncListener;
import net.thetabx.gksa.libGKSj.objects.Conversation;
import net.thetabx.gksa.libGKSj.objects.GObject;
import net.thetabx.gksa.libGKSj.objects.GStatus;
import net.thetabx.gksa.libGKSj.objects.PM;

import java.util.List;

/**
 * Created by Zerg on 22/06/13.
 */
public class ConversationActivity extends Activity {
    private GKS gks;
    private Resources res;
    private Context con;
    private String user;
    public final String LOG_TAG = "Conversation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        res = getResources();
        con = getApplicationContext();
        gks = GKSa.getGKSlib();

        String conversationId = getIntent().getStringExtra("conversationId");
        if(conversationId == null) {
            finish();
        }
        user = getIntent().getStringExtra("userStr");
        initActivity(conversationId);
    }

    public void initActivity(String conversationId) {
        gks.fetchConversation(conversationId, new AsyncListener() {
            ProgressDialog initProgressDiag = null;

            @Override
            public void onPreExecute() {
                initProgressDiag = ProgressDialog.show(ConversationActivity.this, "", res.getString(R.string.progress_loading), true, false);
            }

            @Override
            public void onPostExecute(GStatus status, GObject result) {
                //findViewById(R.id.splash_progress).setVisibility(View.INVISIBLE);
                //setContentView(R.layout.activity_welcome);
                if (status == GStatus.OK) {
                    fillActivity((Conversation) result);
                } else {
                    int toastText;
                    switch (status) {
                        case BADKEY:
                            toastText = R.string.toast_badLogin;
                            break;
                        case STATUSCODE:
                            toastText = R.string.toast_serverError;
                            break;
                        default:
                            toastText = R.string.toast_internalError;
                    }
                    Toast.makeText(con, String.format(res.getString(toastText), status.name()), Toast.LENGTH_LONG).show();
                }
                initProgressDiag.dismiss();
            }
        });
    }

    private void fillActivity(Conversation conversation) {
        Log.d(LOG_TAG, "Inflating views");

        TableLayout table = (TableLayout)findViewById(R.id.conversation_table);
        List<PM> PMsList = conversation.getPMs();
        if(PMsList == null) {
            Log.d(LOG_TAG, "No PM");
            return;
        }
        this.setTitle(res.getString(R.string.title_activity_conversation, user));
        for(final PM pm : PMsList) {
            TableRow row;
            if(pm.isMe()) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.pm_outgoing, table, false);
                if (row != null) {
                    ((TextView)row.findViewById(R.id.pm_outgoing_txt_pseudo)).setText(pm.getUser());
                    ((TextView)row.findViewById(R.id.pm_outgoing_txt_text)).setText(pm.getMessage());
                    ((TextView)row.findViewById(R.id.pm_outgoing_txt_time)).setText(pm.getTime());
                }
            }
            else {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.pm_incoming, table, false);
                if (row != null) {
                    ((TextView)row.findViewById(R.id.pm_incoming_txt_pseudo)).setText(pm.getUser());
                    ((TextView)row.findViewById(R.id.pm_incoming_txt_text)).setText(pm.getMessage());
                    ((TextView)row.findViewById(R.id.pm_incoming_txt_time)).setText(pm.getTime());
                }
            }
            if(row != null)
                table.addView(row);
        }
        Log.d(LOG_TAG, "Done");
    }
}