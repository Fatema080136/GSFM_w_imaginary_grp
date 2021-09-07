package org.mysimulationmodel.simulation.agent;
import org.lightjason.agentspeak.common.CCommon;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
import org.mysimulationmodel.simulation.common.CInputFormat;
import org.mysimulationmodel.simulation.constant.CVariableBuilder;
import org.mysimulationmodel.simulation.environment.CEnvironment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Vector2d;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CPedestrianGenerator extends IBaseAgentGenerator<IBaseRoadUser>
{

    /**
     * for fixed start and goal position
     */
    private CopyOnWriteArrayList<Vector2d> m_positions = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Vector2d> m_goalpositions = new CopyOnWriteArrayList<>();
    private static final int m_pixelpermeter = CEnvironment.getpixelpermeter();
    private static final double m_GaussianMean = 1*m_pixelpermeter;//1.26*m_pixelpermeter;//meter per second; 0.67: meter per half second
    private static final double m_GaussianStandardDeviation = 0.29*m_pixelpermeter;//0.25: meter per half second
    private static final double m_GaussianMeanMaxSpeed = 2*m_pixelpermeter;//2.36*m_pixelpermeter;// 3.535: meter per half second
    private static final double m_GaussianStandardDeviationMaxSpeed = 0.41*m_pixelpermeter;//0.435: meter per half second
    private final AtomicLong m_counter = new AtomicLong();
    private Random rand = new Random();

    /**
     * environment
     */
    private final CEnvironment m_environment;


    /**
     * constructor of the generator
     * @param p_stream ASL code as any stream e.g. FileInputStream
     * @throws Exception Thrown if something goes wrong while generating agents.
     */
    public CPedestrianGenerator(@Nonnull final InputStream p_stream, final CEnvironment p_environment ) throws Exception
    {
        super(
                // input ASL stream
                p_stream,
                // a set with all possible actions for the agent
                Stream.concat(
                    // we use all build-in actions of LightJason
                    CCommon.actionsFromPackage(),
                    // use the actions which are defined inside the agent class
                    CCommon.actionsFromAgentClass( IBaseRoadUser.class )

                // build the set with a collector
                ).collect( Collectors.toSet() ),
                // variable builder
                new CVariableBuilder( p_environment )
        );

        m_environment = p_environment;
        m_positions.add( new Vector2d( 49.91721891*m_pixelpermeter,41.06579529*m_pixelpermeter ) );
        m_positions.add( new Vector2d( 53.27450905*m_pixelpermeter,30.02052227*m_pixelpermeter ) );
        m_positions.add( new Vector2d( 49.91721891*m_pixelpermeter,41.06579529*m_pixelpermeter ) );
        m_positions.add( new Vector2d( 53.27450905*m_pixelpermeter,30.02052227*m_pixelpermeter ) );
        m_positions.add( new Vector2d( 49.91721891*m_pixelpermeter,41.06579529*m_pixelpermeter ) );
        m_positions.add( new Vector2d( 53.27450905*m_pixelpermeter,30.02052227*m_pixelpermeter ) );

        m_goalpositions.add( new Vector2d( 54.72175962*m_pixelpermeter,29.7733178*m_pixelpermeter ) );
        m_goalpositions.add( new Vector2d( 47.34098328*m_pixelpermeter,44.15367526*m_pixelpermeter ) );
        m_goalpositions.add( new Vector2d( 54.72175962*m_pixelpermeter,29.7733178*m_pixelpermeter ) );
        m_goalpositions.add( new Vector2d( 47.34098328*m_pixelpermeter,44.15367526*m_pixelpermeter ) );
        m_goalpositions.add( new Vector2d( 54.72175962*m_pixelpermeter,29.7733178*m_pixelpermeter ) );
        m_goalpositions.add( new Vector2d( 47.34098328*m_pixelpermeter,44.15367526*m_pixelpermeter ) );

    }

    /**
     * generator method of the agent
     * @param p_data any data which can be put from outside to the generator method
     * @return returns an agent
     */
    @Override
    public final IBaseRoadUser generatesingle( @Nullable final Object... p_data )
    {
        // create agent with a reference to the environment
        final IBaseRoadUser l_pedestrian = new IBaseRoadUser( m_configuration, m_environment,1.2*m_pixelpermeter );//0.083

        // initialize pedestrian's state
        l_pedestrian.setPosition( m_positions.remove( 0 ) );
        l_pedestrian.setGoalPedestrian( m_goalpositions.remove( 0 ) );

        l_pedestrian.setradius( 0.75*m_pixelpermeter );// 1.25//.25
        l_pedestrian.setLengthradius(0.75*m_pixelpermeter ); //1.25//.25

        l_pedestrian.setname( "ped"+ m_counter.getAndIncrement() );
        l_pedestrian.settype( 1 );
        l_pedestrian.setmaxforce( 2*m_pixelpermeter* m_environment.getTimestep() );//1//0.09

        l_pedestrian.setSpeed( ( rand.nextGaussian() * m_GaussianStandardDeviation + m_GaussianMean ) * m_environment.getTimestep() );
        l_pedestrian.setMaxSpeed( ( rand.nextGaussian() * m_GaussianStandardDeviationMaxSpeed + m_GaussianMeanMaxSpeed )
                *m_environment.getTimestep() );
        l_pedestrian.setVelocity( l_pedestrian.getSpeed(), l_pedestrian.getGoalposition(), l_pedestrian.getPosition() );

        m_environment.initialset(l_pedestrian);
         // add car to the pedestrian's list
        m_environment.initialPedestrian(l_pedestrian);

        System.out.println( l_pedestrian.getname()+"id"+l_pedestrian.getPosition() +"ped_start_end"+ l_pedestrian.getGoalposition());


        m_environment.getPedestrianinfo().sort((new Comparator<IBaseRoadUser>() {
            @Override
            public int compare(IBaseRoadUser o1, IBaseRoadUser o2) {
                if (o1.getPosition().length() == o2.getPosition().length())
                    return 0;
                return o1.getPosition().length() > o2.getPosition().length() ? -1 : 1;
            }
        }));

        return l_pedestrian;

    }
}
