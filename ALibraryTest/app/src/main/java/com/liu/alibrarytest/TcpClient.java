package com.liu.alibrarytest;

import com.liu.lalibrary.log.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by liu on 2018/7/12.
 */

public class TcpClient implements Runnable
{
    public interface TcpClientListener
    {
        public void onTcpClientConnect(boolean isSuccess);
        public void onTcpClientDisconn();
        public void onRecvData(byte[] buffer, int len);
    }

    private Socket client;
    private String listenIP;
    private int listenPort;
    private int timeout;
    private Thread recvThread;
    private TcpClientListener listener;
    private OutputStream out;
    private InputStream in;

    public  boolean connect(String ip, int port, int timeout)
    {
        if (client == null)
        {
            this.timeout = timeout;
            listenIP = ip;
            listenPort = port;
            recvThread = new Thread(this);
            recvThread.start();
            return true;
        }
        return false;
    }

    public void setListener(TcpClientListener listener)
    {
        this.listener = listener;
    }

    public void disconn()
    {
        if (client != null) try
        {
            client.close();
        } catch (IOException e)
        {
        }
    }

    public boolean send(byte[] buffer)
    {
        if (client != null)
        {
            try
            {
                out.write(buffer);
                out.flush();
                return true;
            } catch (IOException e)
            {
            }
        }
        return false;
    }

    @Override
    public void run()
    {
        try
        {
            client = new Socket();
            InetAddress ss = InetAddress.getByName(listenIP);
            client.connect(new InetSocketAddress(listenIP, listenPort), timeout);
        } catch (IOException e)
        {
            LogUtils.LOGE(TcpClient.class, e.getMessage());
            return;
        }
        byte[] buffer = new byte[1024];
        int size;
        do
        {
            try
            {
                out = client.getOutputStream();
                in = client.getInputStream();
                size = in.read(buffer);
                if (size > 0)
                {
                    listener.onRecvData(buffer, size);
                }
            } catch (Exception e)
            {
                LogUtils.LOGE(TcpClient.class, e.getMessage());
                try
                {
                    in.close();
                } catch (IOException e1)
                {
                }
                try
                {
                    client.close();
                } catch (IOException e1)
                {
                }
                listener.onTcpClientDisconn();
                client = null;
                return;
            }
        }while (true);
    }
}
