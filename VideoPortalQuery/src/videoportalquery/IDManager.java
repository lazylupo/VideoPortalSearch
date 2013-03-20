/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package videoportalquery;

/**
 *
 * @author zerlot
 */
public class IDManager {
    private static int queryID = 0;
    private static int resourceID = 0;

    public static String getCurrentQueryID(){
        return "vps_" + queryID;
    }
    
    public static void changeQueryID(){
        queryID++;
    }

    public static String getResourceID(){
        resourceID++;
        return "resource_" + resourceID;
    }
}
