package videoportalquery;

/**
 * This manager is used for the querybroker local mode. So the image search
 * is directly invoked instead of sending a SOAP request.
 * 
 * @modified Christian Vilsmaier, Christian.Vilsmaier@me.com
 * 
 */


import de.uop.dimis.air.internalObjects.mpqf.CapabilityType;
import de.uop.dimis.air.internalObjects.mpqf.MpegQueryType;
import de.uop.dimis.air.internalObjects.mpqf.ObjectFactory;
import de.uop.dimis.air.internalObjects.mpqf.ResultItemType;
import de.uop.dimis.air.mpqfManagement.Management;
import de.uop.dimis.air.mpqfManagement.containers.QueryContainer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class NativeRequestManager {

    public static List<MpegQueryType> registeredDBs = new ArrayList<MpegQueryType>();
    public static ObjectFactory of = new ObjectFactory();
    
    public static Result sendRequest(MpegQueryType query)  {
        
        
        Management broker = Management.getInstance();
        System.out.println("send...");

        
        
        List<MpegQueryType> responseList = new ArrayList<MpegQueryType>();

        try {

            QueryContainer searchresult = null;
            searchresult = (QueryContainer) broker.search(query);
            ArrayList<MpegQueryType> mpqList = searchresult.getSubQueryContainer().listSubQueries();
            for (int i = 0; i < mpqList.size(); i++) {
                String key = mpqList.get(i).getMpqfID();
                System.out.println(key);
                List<MpegQueryType> responses = searchresult.getResponses().get(key);

                responseList.addAll(responses);

           }

        } catch (Exception ex) {
            
        }

        System.out.println("overall: " + responseList.size());

        return generateResultSet(responseList);

  
    }

    /**
     * Converts the mpqf query response to a quasia result object.
     * @param response mpqf query response
     * @return a quasia result object to display the results in quasia
     */
    public static Result generateResultSet(List<MpegQueryType> responses) {
        
        Result result = new Result();
        
        int runner = 1;
        int max_size = 0;

        for (MpegQueryType response : responses) {
            if (response.getQuery().getOutput().getResultItem().size() > max_size) {
                max_size = response.getQuery().getOutput().getResultItem().size();
            }
        }

        // fetching the right result item
        for (int j = 0; j < max_size; j++) {

            // runnning through all MPQF responses
            for (int i = 0; i < responses.size(); i++) {

                if (responses.get(i).getQuery().getOutput().getResultItem().size() > j) {

                    ResultItemType mpegResult = responses.get(i).getQuery().getOutput().getResultItem().get(j);
                    
                    ResultItem resultItem = new ResultItem();
                    //resultItem.setRecordNumber("" + runner);
                    resultItem.setTitle(mpegResult.getTextResult().get(0).getValue());
                    resultItem.setImageURL(mpegResult.getMediaResource().get(0).getValue());
                    resultItem.setThumbURL(mpegResult.getThumbnail().get(0).getValue());
                    resultItem.setSource(mpegResult.getMediaResource().get(0).getFromREF());
                    result.getResults().add(resultItem);
                    
                    runner++;
                }
            }
        }
        return result;
    }

    public static void registerDB() throws JAXBException, FileNotFoundException {
        try {
            Management broker = Management.getInstance();
            JAXBContext jc = JAXBContext.newInstance("de.uop.dimis.air.internalObjects.mpqf");
            Unmarshaller u = jc.createUnmarshaller();

            MpegQueryType register = null;
            File registeredServicesDirectory = new File("DBs");
			File[] registeredServices = registeredServicesDirectory.listFiles();

			for (File registeredService : registeredServices) {
				register = (MpegQueryType) ((JAXBElement) u.unmarshal(new FileInputStream(registeredService))).getValue();
                                MpegQueryType registration = broker.getBackendMng().registration(register);
                                register.setMpqfID(registration.getMpqfID());
                                registeredDBs.add(register);

				}

      } catch (JAXBException ex) {
            Logger.getLogger(NativeRequestManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NativeRequestManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        public static void singleRegisterDB(String serviceID, boolean checked) {

            Management broker = Management.getInstance();
            for(MpegQueryType regDB : registeredDBs){
                
                if(regDB.getManagement().getInput().getServiceID().get(0).equals(serviceID)){
                    if(!checked){
                    CapabilityType desiredCapability = regDB.getManagement().getInput().getDesiredCapability();
                        regDB.getManagement().getInput().setDesiredCapability(null);
                        broker.getBackendMng().registration(regDB);
                        regDB.getManagement().getInput().setDesiredCapability(desiredCapability);
                        desiredCapability = null;
                    }else {
                    MpegQueryType registration = broker.getBackendMng().registration(regDB);
                    regDB.setMpqfID(registration.getMpqfID());
                }
  
              }
            }
        
        }
    
}
