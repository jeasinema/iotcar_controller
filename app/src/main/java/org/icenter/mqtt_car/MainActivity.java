package org.icenter.mqtt_car;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    // UI compoments
    private TextView r_val;
    private TextView g_val;
    private TextView b_val;
    private TextView network_status;
    private EditText mqtt_server_addr;
    private EditText motor1_speed;
    private EditText motor2_speed;
    private EditText motor3_speed;
    private EditText motor4_speed;
    private EditText motor5_speed;
    private EditText motor6_speed;
    private Button connect;
    private Button motor1_send;
    private Button motor2_send;
    private Button motor3_send;
    private Button motor4_send;
    private Button motor5_send;
    private Button motor6_send;

    private int motor1 = 0;
    private int motor2 = 0;
    private int motor3 = 0;
    private int motor4 = 0;
    private int motor5 = 0;
    private int motor6 = 0;

    MqttAndroidClient _client;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get UI compoments
        r_val = (TextView) findViewById(R.id.R_val);
        g_val = (TextView) findViewById(R.id.G_val);
        b_val = (TextView) findViewById(R.id.B_val);
        network_status = (TextView) findViewById(R.id.status);
        mqtt_server_addr = (EditText) findViewById(R.id.mqtt_server);
        motor1_speed = (EditText) findViewById(R.id.motor1);
        motor2_speed = (EditText) findViewById(R.id.motor2);
        motor3_speed = (EditText) findViewById(R.id.motor3);
        motor4_speed = (EditText) findViewById(R.id.motor4);
        motor5_speed = (EditText) findViewById(R.id.motor5);
        motor6_speed = (EditText) findViewById(R.id.motor6);
        connect = (Button) findViewById(R.id.connect);
        motor1_send = (Button) findViewById(R.id.button1);
        motor2_send = (Button) findViewById(R.id.button2);
        motor3_send = (Button) findViewById(R.id.button3);
        motor4_send = (Button) findViewById(R.id.button4);
        motor5_send = (Button) findViewById(R.id.button5);
        motor6_send = (Button) findViewById(R.id.button6);

        network_status.setText(R.string.disconnected_status);
        // setup client callback
        _client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                Log.i("[DBG]", "mqtt lost!");
                Toast.makeText(getApplicationContext(), "connect loss!", Toast.LENGTH_SHORT).show();
                network_status.setText(R.string.disconnected_status);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("[DBG]", "Incomming msg!");
                if (topic.equals(getString(R.string.color_topic))) {
                    Pattern p = Pattern.compile("(\\d+)(\\d+)(\\d+)");
                    Matcher m = p.matcher(message.toString());
                    r_val.setText(m.group(1));
                    g_val.setText(m.group(2));
                    b_val.setText(m.group(3));
                    Log.i("[DBG]", "get RBG:" + m.group(1) + " " + m.group(2) + " " + m.group(3));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i("[DBG]", "transport success!");
            }
        });

        // setup button onclick listener
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check input
                String server_uri;
                if (mqtt_server_addr.getText().length() == 0 ||
                        !Misc.check_addr_valid(mqtt_server_addr.getText().toString())) {  // null string input
                    server_uri = "tcp://" + R.string.default_addr + ":" + R.string.default_port;
                    Log.i("[DBG]", "connect to " + server_uri);
                    _client = new MqttAndroidClient(getApplicationContext(), server_uri, getString(R.string.mqtt_client_id));
                } else {
                    server_uri = "tcp://" + mqtt_server_addr.getText().toString() + ":" + R.string.default_port;
                    Log.i("[DBG]", "connect to " + server_uri);
                    _client = new MqttAndroidClient(getApplicationContext(), server_uri, getString(R.string.mqtt_client_id));
                }
                network_status.setText(R.string.connected_status);
                // subscribe colorx
                subscribeToTopic(getString(R.string.color_topic),_client);
            }
        });

        motor1_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Misc.check_speed_valid(Integer.parseInt(motor1_speed.getText().toString()))) {
                    motor1 = Integer.parseInt(motor1_speed.getText().toString());
                    do_motor_ctl(_client);
                }
            }
        });

        motor2_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Misc.check_speed_valid(Integer.parseInt(motor2_speed.getText().toString()))) {
                    motor2 = Integer.parseInt(motor2_speed.getText().toString());
                    do_motor_ctl(_client);
                }
            }
        });

        motor3_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Misc.check_speed_valid(Integer.parseInt(motor3_speed.getText().toString()))) {
                    motor3 = Integer.parseInt(motor3_speed.getText().toString());
                    do_motor_ctl(_client);
                }
            }
        });

        motor4_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Misc.check_speed_valid(Integer.parseInt(motor4_speed.getText().toString()))) {
                    motor4 = Integer.parseInt(motor4_speed.getText().toString());
                    do_motor_ctl(_client);
                }
            }
        });

        motor5_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Misc.check_speed_valid(Integer.parseInt(motor5_speed.getText().toString()))) {
                    motor5 = Integer.parseInt(motor5_speed.getText().toString());
                    do_motor_ctl(_client);
                }
            }
        });

        motor6_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Misc.check_speed_valid(Integer.parseInt(motor6_speed.getText().toString()))) {
                    motor6 = Integer.parseInt(motor6_speed.getText().toString());
                    do_motor_ctl(_client);
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
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
        AppIndex.AppIndexApi.start(client2, viewAction);
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
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }

    // user functions
    public void subscribeToTopic (String topic, MqttAndroidClient client) {
        try {
            if (client == null) {
                Toast.makeText(MainActivity.this, "not connect yet!",Toast.LENGTH_SHORT).show();
                return;
            }
            if (!client.isConnected()) {
                Toast.makeText(MainActivity.this, "not connect yet!",Toast.LENGTH_SHORT).show();
                return;
            }
            client.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Subscribed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Subscribe Failed!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void publishMessage (String topic, String content, MqttAndroidClient client) {

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(content.getBytes());
            if (client == null) {
                Toast.makeText(MainActivity.this, "not connect yet!",Toast.LENGTH_SHORT).show();
                return;
            }
            if (!client.isConnected()) {
                Toast.makeText(MainActivity.this, "not connect yet!",Toast.LENGTH_SHORT).show();
                return;
            }
            client.publish(topic, message);
            Toast.makeText(getApplicationContext(), "publish success!", Toast.LENGTH_SHORT).show();
            Log.i("[DBG]","Message Published");
            if (!client.isConnected()) {
                //addToHistory(client.getBufferedMessageCount() + " messages in buffer.");
                Log.i("[DBG]","add to buffer!");
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void do_motor_ctl(MqttAndroidClient client) {
        String ctl_msg;
        ctl_msg = Integer.toString(motor1) + "+" + Integer.toString(motor2) + "+" +
                Integer.toString(motor3) + "+" + Integer.toString(motor4) + "+" +
                Integer.toString(motor5) + "+" + Integer.toString(motor6) + "+" +
                Integer.toString(motor5) + "+" + Integer.toString(motor6);
        Log.i("[DBG]", "send: " + ctl_msg);
        publishMessage(getString(R.string.motor_topic) ,ctl_msg, client);
    }
}
