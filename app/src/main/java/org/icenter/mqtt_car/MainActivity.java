package org.icenter.mqtt_car;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitsuki on 2016/12/20.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private class MotorRowViewHolder {
        private AppCompatCheckBox checkBox;
        private TextView textView;
        private AppCompatEditText editText;
        private AppCompatButton button;

        public MotorRowViewHolder(AppCompatCheckBox checkBox, TextView textView, AppCompatEditText editText, AppCompatButton button) {
            this.checkBox = checkBox;
            this.textView = textView;
            this.editText = editText;
            this.button = button;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient googleApiClient;

    private List<View> motorRootViews = new ArrayList<>();
    private List<MotorRowViewHolder> motorRowViewHolders = new ArrayList<>();

    private View allMotorRootView;
    private MotorRowViewHolder allMotorRowViewHolder;

    private TextView receivedMessageTextView;
    private TextView connectionStatusTextView;
    private TextView sentMessageTextView;

    private AppCompatButton stopButton;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        motorRootViews.add(findViewById(R.id.motor1));
        motorRootViews.add(findViewById(R.id.motor2));
        motorRootViews.add(findViewById(R.id.motor3));
        motorRootViews.add(findViewById(R.id.motor4));
        motorRootViews.add(findViewById(R.id.motor5));
        motorRootViews.add(findViewById(R.id.motor6));

        for (int i = 0; i < motorRootViews.size(); i++) {
            View view = motorRootViews.get(i);

            MotorRowViewHolder holder = new MotorRowViewHolder(
                    (AppCompatCheckBox) view.findViewById(R.id.motor_check_box),
                    (TextView) view.findViewById(R.id.motor_title_text),
                    (AppCompatEditText) view.findViewById(R.id.motor_edit_text),
                    (AppCompatButton) view.findViewById(R.id.motor_publish_button));

            holder.textView.setText(getString(R.string.motor_num, String.valueOf(i + 1)));
            holder.button.setVisibility(View.GONE);

            motorRowViewHolders.add(holder);
        }

        allMotorRootView = findViewById(R.id.motor_all);
        allMotorRowViewHolder = new MotorRowViewHolder(
                (AppCompatCheckBox) allMotorRootView.findViewById(R.id.motor_check_box),
                (TextView) allMotorRootView.findViewById(R.id.motor_title_text),
                (AppCompatEditText) allMotorRootView.findViewById(R.id.motor_edit_text),
                (AppCompatButton) allMotorRootView.findViewById(R.id.motor_publish_button));
        allMotorRowViewHolder.checkBox.setVisibility(View.GONE);
        allMotorRowViewHolder.textView.setText(getString(R.string.motor_all));
        allMotorRowViewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> list = new ArrayList<>();
                for (MotorRowViewHolder holder : motorRowViewHolders) {
                    list.add(parseInt(holder.checkBox.isChecked() ?
                            allMotorRowViewHolder.editText.getText().toString() : holder.editText.getText().toString(),
                            MqttClientManager.MOTOR_MIN_SPEED, MqttClientManager.MOTOR_MAX_SPEED));
                }
                MqttClientManager.getInstance().publishMessage(MqttClientManager.MOTOR_TOPIC, list);
            }
        });

        connectionStatusTextView = (TextView) findViewById(R.id.connection_status);
        receivedMessageTextView = (TextView) findViewById(R.id.received_message);
        sentMessageTextView = (TextView) findViewById(R.id.sent_message);

        stopButton = (AppCompatButton) findViewById(R.id.stop_button);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MqttClientManager.getInstance().publishMessage(MqttClientManager.MOTOR_TOPIC, "0+0+0+0+0+0");
            }
        });

        connectionStatusTextView.setText(MqttClientManager.getInstance().isConnected() ?
                getString(R.string.connected_status) : getString(R.string.disconnected_status));

        MqttClientManager.getInstance().setMqttClientListener(new Handler(), new MqttClientManager.MqttClientListener() {
            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                receivedMessageTextView.setText(getString(R.string.received_message, topic, message.toString()));
            }

            @Override
            public void onDeliveryCompleted(IMqttDeliveryToken token) {
                sentMessageTextView.setText(getString(R.string.sent_message_completed, token.getMessageId()));
            }

            @Override
            public void onDeliveryFailed() {
                sentMessageTextView.setText(getString(R.string.sent_message_failed));
            }

            @Override
            public void onConnected() {
                connectionStatusTextView.setText(getString(R.string.connected_status));
            }

            @Override
            public void onConnectionLost() {
                connectionStatusTextView.setText(getString(R.string.disconnected_status));
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public int parseInt(String string, int min, int max) {
        try {
            int value = Integer.valueOf(string);
            if (value < min) {
                value = min;
            }
            if (value > max) {
                value = max;
            }
            return value;
        } catch (NumberFormatException e) {
            // do nothing
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_help):

                dialog = new AlertDialog.Builder(this).setTitle("帮助").setMessage("自动连接和断线重连, 点击Publish之后会将勾选的马达按照Publish这一行的数值发送, 其他的按照原先的数值发送; 下面的3行状态依次为连接状态、收到的消息、发送的消息; Stop是停止, 也就是发送\"0+0+0+0+0+0\"").setCancelable(true).create();
                dialog.show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://org.icenter.mqtt_car/http/host/path")
        );
        AppIndex.AppIndexApi.start(googleApiClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://org.icenter.mqtt_car/http/host/path")
        );
        AppIndex.AppIndexApi.end(googleApiClient, viewAction);
        googleApiClient.disconnect();
    }
}
