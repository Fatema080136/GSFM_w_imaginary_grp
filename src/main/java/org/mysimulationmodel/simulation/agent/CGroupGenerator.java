package org.mysimulationmodel.simulation.agent;

import org.lightjason.agentspeak.common.CCommon;
import org.mysimulationmodel.simulation.common.CInputFormat;
import org.lightjason.agentspeak.generator.IBaseAgentGenerator;
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


public class CGroupGenerator extends IBaseAgentGenerator<IBaseRoadUser>
{
    /**
     * for fixed start and goal position
     */
    private CopyOnWriteArrayList<Vector2d> m_positions = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Vector2d> m_goalpositions = new CopyOnWriteArrayList<>();
    private static final int m_pixelpermeter = CEnvironment.getpixelpermeter();
    private static final double m_GaussianMean = 0.69*m_pixelpermeter;//1.26*m_pixelpermeter;//meter per second; 0.67: meter per half second
    private static final double m_GaussianStandardDeviation = 0.0;//0.19*m_pixelpermeter;//0.25: meter per half second
    private static final double m_GaussianMeanMaxSpeed = 1.2*m_pixelpermeter;//2.36*m_pixelpermeter;// 3.535: meter per half second
    private static final double m_GaussianStandardDeviationMaxSpeed = 0.1*m_pixelpermeter;//0.435: meter per half second
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
    public CGroupGenerator(@Nonnull final InputStream p_stream, final CEnvironment p_environment ) throws Exception
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
//        m_positions.add( new Vector2d( 63.68339978*m_pixelpermeter,39.22869688*m_pixelpermeter ) );
//        m_positions.add( new Vector2d( 64.57466306*m_pixelpermeter,39.13736299*m_pixelpermeter ) );
//        m_positions.add( new Vector2d( 63.68339978*m_pixelpermeter,41.06579529*m_pixelpermeter ) );
//        m_positions.add( new Vector2d( 65.57466306*m_pixelpermeter,38.02052227*m_pixelpermeter ) );
//        m_positions.add( new Vector2d( 63.91721891*m_pixelpermeter,41.06579529*m_pixelpermeter ) );
//        m_positions.add( new Vector2d( 63.27450905*m_pixelpermeter,30.02052227*m_pixelpermeter ) );
//        m_positions.add( new Vector2d( 49.91721891*m_pixelpermeter,41.06579529*m_pixelpermeter ) );
//        m_positions.add( new Vector2d( 53.27450905*m_pixelpermeter,30.02052227*m_pixelpermeter ) );
//
//        m_goalpositions.add( new Vector2d( 55.49726954*m_pixelpermeter,56.23558352*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 55.87544102*m_pixelpermeter,56.61298879*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 54.72175962*m_pixelpermeter,55.7733178*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 55.34098328*m_pixelpermeter,55.15367526*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 55.82175962*m_pixelpermeter,56.7733178*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 55.62098328*m_pixelpermeter,44.15367526*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 54.72175962*m_pixelpermeter,29.7733178*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 47.34098328*m_pixelpermeter,44.15367526*m_pixelpermeter ) );

//        m_positions.add( new Vector2d( 205,104 ) );
//        m_positions.add( new Vector2d( 200,90) );
//        m_positions.add( new Vector2d( 205,105) );
//        m_positions.add( new Vector2d( 210,105 ) );
//        m_positions.add( new Vector2d( 202,107 ) );
//        m_positions.add( new Vector2d( 200,129 ) );
//        m_positions.add( new Vector2d( 205,135) );
//        m_positions.add( new Vector2d( 200,135 ) );
//        m_positions.add( new Vector2d( 200,140 ) );



        // suhair 30.06.2019
        m_positions.add( new Vector2d( 255,95 ) );
        m_positions.add( new Vector2d( 250,90) );
        m_positions.add( new Vector2d( 243,105) );
//        m_positions.add( new Vector2d( 250,110 ) );
//        m_positions.add( new Vector2d( 255,112 ) );
//        m_positions.add( new Vector2d( 250,100 ) );
        m_positions.add( new Vector2d( 235,135 ) );
        m_positions.add( new Vector2d( 240,130 ) );
        m_positions.add( new Vector2d( 245,140 ) );




        m_goalpositions.add( new Vector2d( 250,400) );
        m_goalpositions.add( new Vector2d( 253,400) );
        m_goalpositions.add( new Vector2d( 254,400) );
        m_goalpositions.add( new Vector2d( 255,400) );
        m_goalpositions.add( new Vector2d( 258,400) );
        m_goalpositions.add( new Vector2d( 262,400) );
//        m_goalpositions.add( new Vector2d( 250,300) );
//        m_goalpositions.add( new Vector2d( 250,300) );
//        m_goalpositions.add( new Vector2d( 250,300) );

//
//        m_positions.add( new Vector2d( 205,90 ) );
//        m_positions.add( new Vector2d( 200,90) );
//        m_positions.add( new Vector2d( 205,85) );
//
//        m_positions.add( new Vector2d( 210,140 ) );
//        m_positions.add( new Vector2d( 202,143) );
//        m_positions.add( new Vector2d( 200,150 ) );
//
//        m_goalpositions.add( new Vector2d( 200,300) );
//        m_goalpositions.add( new Vector2d( 200,300) );
//        m_goalpositions.add( new Vector2d( 200,300) );
//        m_goalpositions.add( new Vector2d( 200,300) );
//        m_goalpositions.add( new Vector2d( 200,300) );
//        m_goalpositions.add( new Vector2d( 200,300) );
//        m_goalpositions.add( new Vector2d( 200,300) );


//                m_positions.add( new Vector2d( 455,115 ) );
//                m_positions.add( new Vector2d( 450,110) );
//                m_positions.add( new Vector2d( 443,125) );
//                m_positions.add( new Vector2d( 250,110 ) );
//                m_positions.add( new Vector2d( 255,112 ) );
//                m_positions.add( new Vector2d( 250,100 ) );
//                m_positions.add( new Vector2d( 435,155 ) );
//                m_positions.add( new Vector2d( 440,150 ) );
//                m_positions.add( new Vector2d( 445,160 ) );




//                m_goalpositions.add( new Vector2d( 350,400) );
//                m_goalpositions.add( new Vector2d( 353,400) );
//                m_goalpositions.add( new Vector2d( 354,400) );
//                m_goalpositions.add( new Vector2d( 355,400) );
//                m_goalpositions.add( new Vector2d( 358,400) );
//                m_goalpositions.add( new Vector2d( 362,400) );
        //        m_goalpositions.add( new Vector2d( 250,300) );
        //        m_goalpositions.add( new Vector2d( 250,300) );
        //        m_goalpositions.add( new Vector2d( 250,300) );

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

        //l_pedestrian.setSpeed( ( rand.nextGaussian() * m_GaussianStandardDeviation + m_GaussianMean ) * m_environment.getTimestep() );
        l_pedestrian.setSpeed( m_GaussianMean * m_environment.getTimestep() );
        l_pedestrian.setMaxSpeed( m_GaussianMeanMaxSpeed * m_environment.getTimestep() );
        l_pedestrian.setVelocity( l_pedestrian.getSpeed(), l_pedestrian.getGoalposition(), l_pedestrian.getPosition() );

        m_environment.initialset(l_pedestrian);
        // add car to the pedestrian's list
        m_environment.initialPedestrian(l_pedestrian);

        System.out.println( l_pedestrian.getname()+"id"+l_pedestrian.getPosition() +"ped_start_end"+ l_pedestrian.getGoalposition());




        //14may
//        m_environment.groups().parallelStream().forEach(
//                g->g.members().sort((new Comparator<IBaseRoadUser>() {
//                    @Override
//                    public int compare(IBaseRoadUser o1, IBaseRoadUser o2) {
//                        if (o1.getGoalposition().length() == o2.getGoalposition().length())
//                            return 0;
//                        return o1.getGoalposition().length() > o2.getGoalposition().length() ? -1 : 1;
//                    }
//                })));


//        m_environment.getPedestrianinfo().sort(( ( o1, o2 ) -> {
//            if (o1.getPosition().length() == o2.getPosition().length())
//                return 0;
//            return o1.getPosition().length() > o2.getPosition().length() ? -1 : 1;
//        } ));

        return l_pedestrian;

    }
}
