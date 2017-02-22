package org.icenter.mqtt_car;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;

/**
 * Created by mitsuki on 2016/12/20.
 */
public class MqttClientManager {
    private static final String TAG = "MqttClientManager";

    private class MessageObject {
        String topic;
        String message;

        public MessageObject(String topic, String message) {
            this.topic = topic;
            this.message = message;
        }
    }

    public interface MqttClientListener {

        void onMessageArrived(String topic, MqttMessage message);

        void onDeliveryCompleted(IMqttDeliveryToken token);

        void onDeliveryFailed();

        void onConnected();

        void onConnectionLost();
    }

    private static final String SERVER_URI = "tcp://tdxls-iot.xicp.net:1883";
    private static final String CLIENT_ID = "icenterCar";

    public static final String MOTOR_TOPIC = "/control/iotcar/123";
    public static final String COLOR_TOPIC = "/iot-car/icenter/colorx";

    public static final int MOTOR_MIN_SPEED = -100;
    public static final int MOTOR_MAX_SPEED = 100;

    private static final int MESSAGE_CONNECT = 1001;
    private static final int MESSAGE_PUBLISH = 1002;

    private static MqttClientManager instance;

    private final Handler handler;

    private MqttClient client;

    private Handler callbackHander;
    private MqttClientListener callbackListener;

    public synchronized static MqttClientManager getInstance() {
        if (instance == null) {
            instance = new MqttClientManager();
        }
        return instance;
    }

    public synchronized void setMqttClientListener(Handler callbackHandler, MqttClientListener callbackListener) {
        this.callbackHander = callbackHandler;
        this.callbackListener = callbackListener;
    }

    private MqttClientManager() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_PUBLISH:
                        MessageObject messageObject = (MessageObject) msg.obj;

                        try {
                            client.publish(messageObject.topic, new MqttMessage(messageObject.message.getBytes()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            notifyOnDeliveryFailed();
                        }

                        break;
                    case MESSAGE_CONNECT:

                        handler.removeMessages(MESSAGE_CONNECT);

                        if (isConnected()) {
                            break;
                        }

                        try {
                            client = new MqttClient(SERVER_URI, CLIENT_ID, new MemoryPersistence());
                            client.setCallback(new MqttCallback() {
                                @Override
                                public void connectionLost(Throwable cause) {
                                    cause.printStackTrace();
                                    Log.i(TAG, "connectionLost()");
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            handler.sendEmptyMessageDelayed(MESSAGE_CONNECT, 1000);
                                            notifyOnConnectionLost();
                                        }
                                    });
                                }

                                @Override
                                public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                                    Log.i(TAG, "messageArrived msg = " + message.toString());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyOnMessageArrived(topic, message);
                                        }
                                    });
                                }

                                @Override
                                public void deliveryComplete(final IMqttDeliveryToken token) {
                                    Log.i(TAG, "deliveryComplete()");
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyOnDeliveryCompleted(token);
                                        }
                                    });
                                }
                            });

                            MqttConnectOptions connOpts = new MqttConnectOptions();
                            connOpts.setCleanSession(true);

                            client.connect(connOpts);

                            client.subscribe(MOTOR_TOPIC);
                            client.subscribe(COLOR_TOPIC);

                            notifyOnConnected();

                        } catch (MqttException e) {
                            handler.sendEmptyMessageDelayed(MESSAGE_CONNECT, 1000);
                            e.printStackTrace();
                            notifyOnConnectionLost();
                        }
                        break;
                }
            }
        };

        handler.sendEmptyMessage(MESSAGE_CONNECT);
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public void publishMessage(String topic, List<Integer> numbers) {

        if (numbers == null || numbers.isEmpty()) {
            return;
        }

        String message = "";
        for (int i = 0; i < numbers.size(); i++) {
            message += String.valueOf(numbers.get(i));
            if (i < numbers.size() - 1) {
                message += "+";
            }
        }

        publishMessage(topic, message);
    }

    public void publishMessage(String topic, String message) {
        Log.i(TAG, "publishMessage() topic = " + topic + ", message = " + message);
        handler.obtainMessage(MESSAGE_PUBLISH, new MessageObject(topic, message)).sendToTarget();
    }

    private synchronized void notifyOnConnectionLost() {
        if (callbackHander != null && callbackListener != null) {
            callbackHander.post(new Runnable() {
                @Override
                public void run() {
                    callbackListener.onConnectionLost();
                }
            });
        }
    }

    private synchronized void notifyOnMessageArrived(final String topic, final MqttMessage message) {
        if (callbackHander != null && callbackListener != null) {
            callbackHander.post(new Runnable() {
                @Override
                public void run() {
                    callbackListener.onMessageArrived(topic, message);
                }
            });
        }
    }

    private synchronized void notifyOnDeliveryCompleted(final IMqttDeliveryToken token) {
        if (callbackHander != null && callbackListener != null) {
            callbackHander.post(new Runnable() {
                @Override
                public void run() {
                    callbackListener.onDeliveryCompleted(token);
                }
            });
        }
    }

    private synchronized void notifyOnDeliveryFailed() {
        if (callbackHander != null && callbackListener != null) {
            callbackHander.post(new Runnable() {
                @Override
                public void run() {
                    callbackListener.onDeliveryFailed();
                }
            });
        }
    }

    private synchronized void notifyOnConnected() {
        if (callbackHander != null && callbackListener != null) {
            callbackHander.post(new Runnable() {
                @Override
                public void run() {
                    callbackListener.onConnected();
                }
            });
        }
    }
}

