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

import net.dv8tion.singleton.Singleton;
import net.dv8tion.singleton.SingletonHandler;

/**
 * Provides an example implementation of the Singleton interface and
 * proper setup and registering of the Singleton instance with the SingletonHandler.
 *
 * @author DV8FromTheWorld (Austin Keener)
 * @version 1.0.0  February 7, 2015
 */
public class ExampleSingleton implements Singleton
{
    public static final String UNMINIMIZE = "Unminimize you freak";
    public static final int PORT = 24332;

    /**
     * Simple main method for easy testing.
     *
     * @param args
     *          Command Line Arguments
     */
    public static void main(String[] args)
    {
        new ExampleSingleton();
    }

    /**
     * Simple example constructor showing how to register the Singleton
     * instance with the Handler.
     */
    public ExampleSingleton()
    {
        SingletonHandler.setupHandler(this, PORT);
    }

    @Override
    public String onInstanceIsDuplicate()
    {
        return UNMINIMIZE;
    }

    @Override
    public void onMessageFromDuplicate(String duplicateMessage)
    {
        if (duplicateMessage.equals(UNMINIMIZE))
        {
            System.out.println("I was asked to unminimize.");
        }
    }

    @Override
    public boolean onDuplicateCleanup(String duplicateMessage)
    {
        System.out.println("Sent for unminimize, killing duplicate");
        System.exit(0);
        return false;
    }
}
