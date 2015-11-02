package com.example.kmit.mystartest;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TCPClient {

    public static final String SERVERIP = "192.168.11.254"; //"192.168.173.1"; // "10.1.2.45"; //  your computer IP address
    public static final int SERVERPORT = 8080; //6669;       //;
    private boolean mRun = false;
    private Socket socket;
    OutputStream out;
    DataOutputStream dos;
    InputStream in;
    DataInputStream dis;
    byte[] data;
    private OnMessageReceived mMessageListener = null;

    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public interface OnMessageReceived {
        public void messageReceived(byte[] message);
    }

    public void stopClient() {
        mRun = false;
    }

    public void sendMessage()
    {
        byte[] vav = new byte[11];
        //20 83 B8 ED 02 01 BB 01 01 0A 31
        vav[0] = (byte)0x20;
        vav[1] = (byte)0x83;
        vav[2] = (byte)0xB8;
        vav[3] = (byte)0xED;
        vav[4] = (byte)0x02;
        vav[5] = (byte)0x01;
        vav[6] = (byte)0xBB;
        vav[7] = (byte)0x01;
        vav[8] = (byte)0x01;
        vav[9] = (byte)0x0A;
        vav[10] = (byte)0x31;
        try {
            dos.write(vav);
            Log.e("TCP", "S: Отправило");
        } catch (Exception e) {
            Log.e("TCP", "S: Ошибка отправки сообщения", e);
        }
    }

    public void run() {

        mRun = true;

        try {

            socket = new Socket(SERVERIP, SERVERPORT);
            Log.e("TCP", "S: Покдлючилось");
            try {

                out = socket.getOutputStream();
                dos = new DataOutputStream(out);
                in = socket.getInputStream();
                dis = new DataInputStream(in);
                try {

                    while (mRun)
                    {
                        //int len = dis.readInt(); вычисление длинны
                        data = new byte[11];
                        //if (len > 0) {
                        dis.readFully(data);
                        mMessageListener.messageReceived(data);
                        //}

                    }

                } catch (Exception e) {
                    Log.e("TCP", "S: Ошибка приема сообщения", e);
                }


            } catch (Exception e) {

                Log.e("TCP", "S: Ошибка цикла отправки сообщения", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                Log.e("TCP", "S: Клиент закрыт");

                socket.close();
            }
        } catch (Exception e) {
            Log.e("TCP", "C: Ошибка создания сокета", e);
        }



    }





}
