package org.mysimulationmodel.simulation.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CCSVFileReaderForGA
{
    public static Map<String, String> readDataFromCSV()
    {

        Map<String,String> m_realdata = new HashMap<>();
        Path pathToFile = Paths.get( System.getProperty("user.dir").concat("/pedestrians.csv" ));
        try ( BufferedReader l_br = Files.newBufferedReader( pathToFile, StandardCharsets.US_ASCII ) )
        {
            String l_line = l_br.readLine();
            while ( l_line != null )
            {
                String[] l_attributes = l_line.split(",");
                ArrayList<String> l_temp = new ArrayList<>();
                l_temp.add(l_attributes[4]);
                l_temp.add(l_attributes[5]);

                if(l_attributes.length == 8){
                m_realdata.put( new StringBuffer(l_attributes[3]).append(l_attributes[0]).toString()
                        .replaceAll("\\s+",""), l_attributes[6] );}

                l_line = l_br.readLine();
            }
        }
        catch ( IOException ioe) { ioe.printStackTrace(); }
        return m_realdata;
    }

}
