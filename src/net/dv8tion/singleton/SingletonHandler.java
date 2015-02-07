/**
 * Copyright 2015 DV8FromTheWorld (Austin Keener)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.singleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles checking if an Singleton instance is a duplicate.
 * Also handles listening for Duplicates that will be sending messages
 * to the original Singleton instance via the ports that the socket is bound to.
 *
 * @author DV8FromTheWorld (Austin Keener)
 * @version 1.0.0  February 7, 2015
 */
public class SingletonHandler
{
    private static final int MAX_ATTEMPTS = 3;

    private static SingletonHandler _instance = null;
    private Singleton singleton;
    private int port;

    private ServerSocket socket;

    /**
     * This is used to initialize the handler and provide it with the Singleton which it should
     * deal with. The handler runs on its own thread after initialization, so it can monitor the
     * socket that will be bound to the port for messages from duplicates without blocking the main thread.
     *
     * @param singleton
     *             The instance of the Singleton which to track.
     * @param port
     *             The port that the Handler should attempt to bind to.  This is used for inter-communication between duplicates.
     */
    public static void setupHandler(Singleton singleton, int port)
    {
        if (_instance != null)
        {
            throw new IllegalStateException("Another Singleton attempting to register itself from within a currently running Singleton _instance");
        }
        _instance = new SingletonHandler();
        _instance.singleton = singleton;
        _instance.port = port;

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                _instance.checkIfDuplicate(0);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Checks to see if the registered Singleton is a duplicate of another
     * currently running instance.
     *
     * If the registered Singleton is a duplicate, we talk to the Original using the port that we couldn't bind to.
     *
     * If the registered Singleton is not a duplicate, we start listening for future duplicates.
     *
     * @param attempt
     *             The amount of times that we have checked for a duplicate. We will triple check that we are a duplicate.
     */
    @SuppressWarnings("resource")
    private void checkIfDuplicate(int attempt)
    {
        try
        {
            socket = new ServerSocket(port);
            listenForDuplicate();                //Looks like we didn't fail, so this instance is not a duplicate
        }
        catch (IOException e)                    //Oh no! We are a duplicate!
        {
            if (attempt < MAX_ATTEMPTS)          //Try to bind to this port at least 3 times. We do this incase the original was shutting down and 
            {                                    //had yet to release the port that it was bound to.
                try
                {
                    Thread.sleep(100);           //Wait .1 seconds between attempts
                    checkIfDuplicate(++attempt);
                    return;
                }
                catch (InterruptedException e1)
                {
                    e1.printStackTrace();
                }
            }
            String message = singleton.onInstanceIsDuplicate();    //Gets message from duplicate of what to do.
            Socket hostSocket;
            try
            {
                hostSocket = new Socket(InetAddress.getLocalHost(), port);
                OutputStreamWriter write = new OutputStreamWriter(hostSocket.getOutputStream());
                write.write(message);            //Sends message to original
                write.flush();
                hostSocket.close();
                boolean replacingOriginal = singleton.onDuplicateCleanup(message);    //Checks to see if Original was killed in favor of duplicate.
                if (replacingOriginal)
                {
                    Thread.sleep(1000);          //Waits for the Original to stop using port.
                    checkIfDuplicate(0);         //Makes sure original is dead and attempts to bind to port. (resets attempt to 0)
                }
            }
            catch (IOException | InterruptedException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Listens for a connection to be made with the Socket, retrieves the sent message
     * and then informs the Original Singleton that a message has been received from a
     * duplicate.
     */
    private void listenForDuplicate()
    {
        try
        {
            while (true)
            {
                StringBuilder str = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.accept().getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    str.append(line);
                }
                reader.close();
                singleton.onMessageFromDuplicate(str.toString());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
