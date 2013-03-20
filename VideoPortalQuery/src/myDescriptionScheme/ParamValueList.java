/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myDescriptionScheme;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import myDescriptionScheme.ObjectFactory;
import myDescriptionScheme.ParamValueListType;
import myDescriptionScheme.ParamValuePairType;

/**
 *
 * @author User
 */
public class ParamValueList {

    public ObjectFactory of;
    public ParamValueListType pvl;

    public ParamValueList(){
        of = new ObjectFactory();
        pvl = of.createParamValueListType();
    }

    public void createParamValuePair(String p, String v){
        ParamValuePairType pvp = of.createParamValuePairType();
        pvp.setParam(p);
        pvp.setValue(v);
        pvl.getParamValuePair().add(pvp);
    }

    public void marshal() {
        try {
            JAXBElement<ParamValueListType> jpvl =
                of.createParamValueList(pvl);
            JAXBContext jc = JAXBContext.newInstance( "myDescriptionScheme" );
            Marshaller m = jc.createMarshaller();
            m.marshal( jpvl, new File("myDescription.xml") );
        } catch( JAXBException jbe ){
            
        }
    }
    
    public ParamValuePairType getParamValuePair(String param){
        
        ParamValuePairType returner = null;
        
       for(ParamValuePairType pvp:pvl.getParamValuePair()){
           
           if(pvp.getParam().equals(param)){
               returner = pvp;
           }
       }
        return returner;
        
    }

}
