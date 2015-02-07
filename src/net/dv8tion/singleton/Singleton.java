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

/**
 * Interface that provides the functionality of a Singleton.
 * A Singleton is a program that only wants to allow one instance
 * of itself to be running at a given time.
 *
 * After implementing the interface, you will also have to register the Singleton with the SingletonHandler.
 *
 * @author DV8FromTheWorld (Austin Keener)
 * @version 1.0.0  February 7, 2015
 */
public interface Singleton
{
    /**
     * @DuplicateSide
     *
     * Called when the newly created instance of the implementing program already has
     * an instance running.
     *
     * Would be best if in this method, you ask what to do.  Possible options:
     *  1) kill duplicate, unminimize the original.
     *  2) kill duplicate without affecting the original.
     *  3) kill original instance, making the duplicate the new "Original" instance.
     *
     * These are just suggestions.  Personally I don't provide the user an option in most cases,
     * I just use option 1, unminimize the original and kill duplicate.
     *
     * NOTE:  If you plan to kill your duplicate, don't do it here.  If you kill here, your message wont get to
     * the Original instance. Use the onDuplicateCleanup().
     *
     * @return
     * 		A String informing the Original instance as to what it should do.
     */
    public String onInstanceIsDuplicate();

    /**
     * @OriginalSide
     *
     * Called when the duplicate informs the Original what it should do.
     *
     * This is where you would unminimize the original, or kill it to make way for a new instance,
     * or do nothing.
     *
     * @param duplicateMessage
     *             Message sent from the Duplicate to the Original informing it of what action it should take.
     */
    public void onMessageFromDuplicate(String duplicateMessage);

    /**
     * @DuplicateSide
     *
     * This method is called after the Original Instance has been informed of the decided course of action.
     * This would be the best place to clean up resources and kill the duplicate if you planned to do so.
     *
     * @param duplicateMessage
     *             Message sent from the Duplicate to the Original informing it of what action it should take.
     *
     * @return
     *   	True - if the duplicate is replacing the original (killed original) / needs to listen for more duplicates.
     */
    public boolean onDuplicateCleanup(String duplicateMessage);
}
