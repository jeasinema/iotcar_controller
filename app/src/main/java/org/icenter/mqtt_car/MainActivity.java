package org.icenter.mqtt_car;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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

    MqttAndroidClient client;

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

        // setup client callback
        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

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
                    server_uri = "tcp://"+ R.string.default_addr + ":" + R.string.default_port;
                    Log.i("[DBG]", "connect to " + server_uri);
                    client = new MqttAndroidClient(getApplicationContext(), server_uri, getString(R.string.mqtt_client_id));
                } else {
                    server_uri = "tcp://"+ mqtt_server_addr.getText().toString() + ":" + R.string.default_port;
                    Log.i("[DBG]", "connect to " + server_uri);
                    client = new MqttAndroidClient(getApplicationContext(), server_uri, getString(R.string.mqtt_client_id));
                }
            }
        });

        motor1_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        motor2_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

         motor3_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

         motor4_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

         motor5_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

         motor6_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
