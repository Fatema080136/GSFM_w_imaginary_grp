
package org.mysimulationmodel;

import org.mysimulationmodel.simulation.agent.*;
import org.mysimulationmodel.simulation.common.*;
import org.mysimulationmodel.simulation.environment.CEnvironment;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.List;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * errors in ped to car interaction can be solved by fixing pedestrian's speed and ped to car force
 * Multi-agent Simulation
 * @todo create predestrian iteratively, not all at once
 * @bug sometimes number of generated pedestrian is less than p_number
 * @todo for genetic algorithm simulate pedestrian's by giving their position for each cycle
 */
final class CMain
{
    private static final String FILE_HEADER = "scenarioid,time,id,x_axis,y_axis,velocity";

    //private String m_name;
    static
    {
        LogManager.getLogManager().reset();
    }

    private CMain( )
    {
    }

    private static ArrayList<COutputFormat> runSimulation( double p_acceleration, double p_decelerationcartocar,
                                                           double p_decelerationpedtocar, double p_pedlamda,
                                                           double p_repulsefactorpedtoped, double p_pedtopedsigma,
                                                           double p_pedtocarsigma, double p_pedtocarforce,
                                                           double p_cartocardistance, double p_pedtocardistance,
                                                           double p_pedtocaraccelarationdistance, double p_distance,
                                                           double p_collisionchekingfactor, int p_randomization )
    {
        int m_pixelpermeter = CEnvironment.getpixelpermeter();
        CHostAgent l_hostagent;

        File l_ped = new File( System.getProperty( "user.dir" ).concat( "/agent.asl" ) );
        File l_group = new File( System.getProperty( "user.dir" ).concat( "/group.asl" ) );
        File l_car = new File( System.getProperty( "user.dir" ).concat( "/car.asl" ) );
        File l_host = new File( System.getProperty( "user.dir" ).concat( "/host.asl" ) );

        CEnvironment l_env = new CEnvironment();

        JFrame l_frame = new JFrame();
        l_frame.add( l_env );

        l_frame.setSize( l_env.getWidth(), l_env.getHeight() );
        l_frame.setFont( new Font( "System", Font.PLAIN, 14 ) );
        Font f = l_frame.getFont();
        FontMetrics fm = l_frame.getFontMetrics( f );
        int x = fm.stringWidth( "Multiagent-based Simulation" );
        int y = fm.stringWidth( " " );
        int z = l_frame.getWidth() / 2 - ( x / 2 );
        int w = z / y;
        String pad = "";

        pad = String.format( "%" + w + "s", pad );
        l_frame.setTitle( pad + "Multiagent-based Simulation" );
        l_frame.setVisible( true );
        l_frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );

        final String l_name = System.getProperty( "user.dir" ).concat( "/results.csv" );

        int count = 0;
        int maxTries = 3;
        ArrayList<COutputFormat> m_output = new ArrayList<>();

        while ( true )
        {
            try
                    ( final FileInputStream l_pedestrianstream = new FileInputStream( l_ped );
                      final FileInputStream l_groupstream = new FileInputStream( l_group );
                      final FileInputStream l_carstream = new FileInputStream( l_car );
                      final FileInputStream l_hoststream = new FileInputStream( l_host ) )
            {
                Stream.concat( new CPedestrianGenerator( l_pedestrianstream, l_env ).generatemultiple( 0 ),
                               new CCarGenerator( l_carstream, l_env ).generatemultiple( 1 ) )
                      .collect( Collectors.toSet() );

                l_env.initializegroups( 1, 6, new CGroupGenerator( l_groupstream, l_env )
                        .generatemultiple( 6 ).collect( Collectors.toList() ) );

                l_hostagent = new CHostGenarator( l_hoststream, l_env ).generatesingle( 1 );

                break;
            }
            catch ( final Exception l_exception )
            {
                if ( ++count > maxTries )
                    l_exception.printStackTrace();
                else
                    throw new RuntimeException();
            }
        }

        final double[] l_timestep = { 0.5 };//0.5
        CHostAgent finalL_hostagent = l_hostagent;

        final int[] l_t = { 2 };
        IntStream.range( 0, 80 )
                 .forEach( j ->
                           {
                               System.out.println( j );
                               if ( l_env.addPedtoInitializeLater().get( j ) != null )
                               {
                                   l_env.addPedtoInitializeLater().get( j )
                                        .forEach( n ->
                                                  {
                                                      l_env.initialset( n );
                                                      l_env.initialPedestrian( n );
                                                  } );
                               }

                               if ( l_env.addCartoInitializeLater().get( j ) != null )
                               {
                                   l_env.addCartoInitializeLater().get( j )
                                        .forEach( n ->
                                                  {
                                                      l_env.initialset( n );
                                                      l_env.initialCar( n );
                                                  } );
                               }

                               //long startTime = System.nanoTime();
                               if ( ( x & 1 ) == 0 )
                               {
                                   try
                                   {
                                       finalL_hostagent.updateParameter( p_distance * m_pixelpermeter,
                                                                         p_collisionchekingfactor, p_randomization );
                                       finalL_hostagent.call();
                                   }
                                   catch ( Exception e )
                                   {
                                       e.printStackTrace();
                                   }
                               }
                               l_env.repaint();
                               l_env.groups()
                                    .forEach( g ->
                                              {
                                                  try
                                                  {
                                                      g.call();
                                                  }
                                                  catch ( Exception e )
                                                  {
                                                      e.printStackTrace();
                                                  }
                                              } );
                               l_env.getRoadUserinfo().parallelStream()
                                    .forEach( i ->
                                              {
                                                  try
                                                  {
                                                      i.updateParameter( p_acceleration * m_pixelpermeter,
                                                                         p_decelerationcartocar * m_pixelpermeter,
                                                                         p_decelerationpedtocar * m_pixelpermeter,
                                                                         p_pedlamda,
                                                                         p_repulsefactorpedtoped * m_pixelpermeter,
                                                                         p_pedtopedsigma * m_pixelpermeter,
                                                                         p_pedtocarsigma * m_pixelpermeter,
                                                                         p_pedtocarforce * m_pixelpermeter,
                                                                         p_cartocardistance * m_pixelpermeter,
                                                                         p_pedtocardistance * m_pixelpermeter,
                                                                         p_pedtocaraccelarationdistance * m_pixelpermeter );
                                                      i.call();

                                                      //m_output.add( new COutputFormat( p_senarioid,l_timestep[0], i.getname()
                                                      // ,i.getPosition().x, i.getPosition().y, i.getVelocity().length() ) );
                                                      Thread.sleep( 150);
                                                  }
                                                  catch ( final Exception l_exception )
                                                  {
                                                      l_exception.printStackTrace();
                                                      throw new RuntimeException();
                                                  }

                                              } );


                               l_timestep[0] = l_timestep[0] + l_env.getTimestep();//0.5
                               l_t[0]++;
                           } );

        return m_output;
    }

    public static void main( final String[] p_args )
    {
        //to write simulation output
        final String l_name = System.getProperty( "user.dir" ).concat( "/results.csv" );
        ArrayList<ArrayList<COutputFormat>> m_output = new ArrayList<>();
        for ( int i = 1; i <= 1; i++ )//3.4
        {
            m_output.add( CMain.runSimulation(
                    1, 3, 3, 0.2,
                    3.5/*1.4*/, 0.4, 0.2, 10,
                    8, 8, 7, 18.4,
                    7, 2 )
            );//1.35,7 //ped_to_car_distance:(Math.random() * ((13 - 3) + 1)) + 3 //
            // p_distance:14, 18.4
        }//new Random().nextInt(2)
        // 0.2, 1.4, 0.4
        ArrayList<COutputFormat> bla = new ArrayList<>();
        for ( ArrayList<COutputFormat> out : m_output )
        {
            for ( COutputFormat outt : out )
            {
                bla.add( outt );
            }
        }
        CCsvReadWrite.writeCsvFile( l_name, bla, FILE_HEADER );
    }

}