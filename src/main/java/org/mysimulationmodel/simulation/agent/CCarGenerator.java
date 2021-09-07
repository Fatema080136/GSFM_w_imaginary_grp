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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fatema on 29.01.2018.
 * @todo make one extra force for car which is from the edges of the road: can be either attractive or repulsive:
 * if car goes outside of the edges forces will be the attractive force, if they are inside of the edges then it will get repulsive force
 * from edges so it can avoid touching edges --- the need of this force depends on the topology
 */
public class CCarGenerator extends IBaseAgentGenerator<IBaseRoadUser>
{
    /**
     * for fixed start and goal position
     */
    private CopyOnWriteArrayList<Vector2d> m_positions = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Vector2d> m_goalpositions = new CopyOnWriteArrayList<>();
    private static final int m_pixelpermeter = CEnvironment.getpixelpermeter();
    private Random rand = new Random();
    private final AtomicLong m_counter = new AtomicLong();
    private static final double m_GaussianMeanSpeed =  4*m_pixelpermeter;// 2.25:meter per half second
    private static final double m_GaussianStandardDeviationSpeed = 1.36*m_pixelpermeter;//0.675: meter per half second
    private static final double m_GaussianMeanMaxSpeed =  5.07*m_pixelpermeter;// 3.535: meter per half second
    private static final double m_GaussianStandardDeviationMaxSpeed = 0.87*m_pixelpermeter;//0.435: meter per half second

    /**
     * environment
     */
    private final CEnvironment m_environment;

    /**
     * constructor of the generator
     * @param p_stream ASL code as any stream e.g. FileInputStream
     * @throws Exception Thrown if something goes wrong while generating agents.
     */
    public CCarGenerator( @Nonnull final InputStream p_stream, final CEnvironment p_environment ) throws Exception
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



        m_positions.add( new Vector2d( 110,100 ) );
        m_goalpositions.add(new Vector2d(800, 500));

//        m_positions.add( new Vector2d( 64.36025568*m_pixelpermeter,44.03884639*m_pixelpermeter ) );
//        m_positions.add( new Vector2d( 240*m_pixelpermeter,40*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 22.49473444*m_pixelpermeter,20.25367316*m_pixelpermeter ) );
//        m_goalpositions.add( new Vector2d( 24*m_pixelpermeter,350*m_pixelpermeter ) );
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
        final IBaseRoadUser l_car = new IBaseRoadUser( m_configuration, m_environment,3 ); //1.38//0.278 // max speed 8.33 per second

        //initialize Car's state with fixed start and goal position
        l_car.setPosition( m_positions.remove( 0 ) );
        l_car.setGoalPedestrian( m_goalpositions.remove( 0 ) );

        l_car.setradius( 1*m_pixelpermeter );//5
        l_car.setLengthradius( 1.5*m_pixelpermeter );//10

        l_car.setname( "Car" + m_counter.getAndIncrement() );
        l_car.settype( 2 );
        l_car.setmaxforce( 4.5*m_pixelpermeter*m_environment.getTimestep() );//1//0.09

//        l_car.setSpeed( l_input.m_speed*m_pixelpermeter/2f );
//        l_car.setMaxSpeed( l_input.m_max_speed*m_pixelpermeter/2f );

        l_car.setSpeed( ( rand.nextGaussian() * m_GaussianStandardDeviationSpeed + m_GaussianMeanSpeed )* m_environment.getTimestep() );
        l_car.setMaxSpeed( ( rand.nextGaussian() * m_GaussianStandardDeviationMaxSpeed + m_GaussianMeanMaxSpeed )*m_environment.getTimestep() );
        //System.out.println( l_car.getSpeed()+" initial value "+l_car.getMaxSpeed());
        l_car.setVelocity( l_car.getSpeed(), l_car.getGoalposition(), l_car.getPosition() );

        m_environment.initialset(l_car);
        m_environment.initialCar(l_car);

        System.out.println( l_car.getname()+"id"+l_car.getPosition() +"car_start_end"+ l_car.getGoalposition());

        return l_car;

    }
}

