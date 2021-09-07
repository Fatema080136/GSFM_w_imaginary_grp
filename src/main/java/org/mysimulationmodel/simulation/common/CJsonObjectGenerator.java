package org.mysimulationmodel.simulation.common;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.ArrayList;

public class CJsonObjectGenerator
{
    //private ArrayList<JsonObject> m_JsonArray;
    private ArrayList<CSampleOutput> m_JsonArray;

    public CJsonObjectGenerator()
    {
        m_JsonArray = new ArrayList<CSampleOutput>();
        //m_JsonArray = new ArrayList<JsonObject>();
    }

    public void normalObject( double p_timestep, String p_id, double p_selfX, double p_selfY, double p_speed )
    { m_JsonArray.add( new CSampleOutput( p_timestep, p_id, p_selfX, p_selfY, p_speed ) ); }

    public void check( double p_timestep, String p_id, double p_selfX, double p_selfY )
    {
        JsonObject l_object = Json.createObjectBuilder()
                .add("timestep", p_timestep)
                .add("id", p_id)
                .add("x_axis", p_selfX)
                .add("y_axis", p_selfY )
                .build();

        //m_JsonArray.add( l_object );
    }
    public ArrayList<CSampleOutput> getJsonObject()
    {
        return m_JsonArray;
    }
}
