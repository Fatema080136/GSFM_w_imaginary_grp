package org.mysimulationmodel.simulation.environment;

import org.mysimulationmodel.simulation.agent.*;
import org.mysimulationmodel.simulation.common.CStatic;
import org.mysimulationmodel.simulation.common.CVector;
import org.mysimulationmodel.simulation.common.CWall;
import org.mysimulationmodel.simulation.routechoice.CAstarAlgorithm;
import org.mysimulationmodel.simulation.routechoice.CNode;

//import java.awt.*;

//import javax.swing.JPanel;
import javax.swing.*;
import javax.vecmath.Vector2d;
//import java.awt.geom.Ellipse2D;
//import java.awt.geom.Line2D;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.stream.Collectors;


/**
 * environment class
 * @todo make diferent zone i.e. pedestrian zone shared zone
 * * Created by Fatema on 10/20/2016.
 * extends JPanel
 */

public class CEnvironment extends JPanel implements IEnvironment
{
    /*for genetic algo
    //static Map< String, ArrayList<String>> m_realdata = CCSVFileReaderForGA.readDataFromCSVForSimulation();
     */
    private Graphics2D graphics2d;
    private static final int m_pixelpermeter = 12;
    private static final double m_timestep = 0.25;
    private ArrayList<IBaseRoadUser> m_pedestrian = new ArrayList<>();
    private Map<Integer,List<IBaseRoadUser>> m_pedestrianneedtoinitialize = Collections.synchronizedMap(new HashMap<>());
    private Map<Integer,List<IBaseRoadUser>> m_carneedtoinitialize = Collections.synchronizedMap(new HashMap<>());
    private CopyOnWriteArrayList<IBaseRoadUser> m_agent = new CopyOnWriteArrayList<>();
    private ArrayList<IBaseRoadUser> m_car = new ArrayList<>();
    private List<IBaseRoadUser> m_pedestriangroup = new ArrayList<>();
    private ArrayList<CStatic> m_wall = new ArrayList<>( 4 );
    private ArrayList<CWall> m_walledge = new ArrayList<>( );
    private ArrayList<CWall> m_roadborder = new ArrayList<>( 2 );
    private ArrayList<CWall> m_pedestrianborder = new ArrayList<>( 2 );
    private static final Random m_rand = new Random();
    private int m_width;
    private int m_height;
    private Map<String,Double> m_strategy = new HashMap<>();

    /**
     * list of groups
     */
    private final List<IMainGroup> m_groups = new CopyOnWriteArrayList<>();


    public CEnvironment()
    {
       setFocusable( true );
       //setBackground( Color.getColor("lemon") );
       setBackground( Color.lightGray );
       setDoubleBuffered( true );

//        m_roadborder.add( new CWall ( new Vector2d(0*m_pixelpermeter, 2.5*m_pixelpermeter), new Vector2d( 115*m_pixelpermeter, 66*m_pixelpermeter ), null ) );
//        m_roadborder.add( new CWall ( new Vector2d(0*m_pixelpermeter, 10*m_pixelpermeter), new Vector2d( 115*m_pixelpermeter, 75*m_pixelpermeter ), null ) );
        m_roadborder.add( new CWall ( new Vector2d(0, 3.5*m_pixelpermeter), new Vector2d( 115*m_pixelpermeter, 69.5*m_pixelpermeter ), null ) );
        m_roadborder.add( new CWall ( new Vector2d(0, 7.5*m_pixelpermeter), new Vector2d( 115*m_pixelpermeter, 73.5*m_pixelpermeter ), null ) );
        //m_roadborder.add( new CWall ( new Vector2d(0*m_pixelpermeter, 2*m_pixelpermeter), new Vector2d( 50*m_pixelpermeter, 30*m_pixelpermeter ), null ) );
        //m_roadborder.add( new CWall ( new Vector2d(0*m_pixelpermeter, 10*m_pixelpermeter), new Vector2d( 57*m_pixelpermeter, 37*m_pixelpermeter ), null ) );

        m_width = 70*m_pixelpermeter;//600;//x axis
        m_height =50*m_pixelpermeter; //400;//y axis
    }


    /**
     * add pedestrian to pedestrian's set
     *
     * @param p_agent agent
     *
     */
    public void initialset(IBaseRoadUser p_agent ){ m_agent.add( p_agent ); }
    //change with multimap
    public Map<Integer, List<IBaseRoadUser>> addPedtoInitializeLater()
    {
        return m_pedestrianneedtoinitialize;
    }
    public Map<Integer,List<IBaseRoadUser>> addCartoInitializeLater()
    {
        return m_carneedtoinitialize;
    }
    public void initialCar(IBaseRoadUser p_car ){ m_car.add( p_car ); }
    public void initialPedestrian(IBaseRoadUser p_pedestrian ){ m_pedestrian.add( p_pedestrian ); }

    /*for genetic algo
    public Map< String, ArrayList<String>> getRealData()
    {
        return m_realdata;
    }*/

    public static int getpixelpermeter()
    {
        return m_pixelpermeter;
    }

    public static double getTimestep()
    {
        return m_timestep;
    }


    //-------------------------------------- Suhair Functions ---------------------------------------------

    /**
     * groups construction
     *  @param p_numberofgroups assigned number of groups
     * @param p_groupsize assigned group size
     * @param l_pedgroup
     */
    public void initializegroups(int p_numberofgroups, int p_groupsize, List<IBaseRoadUser> l_pedgroup)//,int p_clustersize)
    {
        int l_numberofgroups;
        int l_groupsize;
        m_pedestriangroup.addAll( l_pedgroup );
        System.out.println( p_groupsize + " group info 1 " + p_numberofgroups + " size " + l_pedgroup.size() );

        // check constraints

        //        if ( p_numberofgroups < 1 )
        //        {
        //            l_numberofgroups = 0;
        //        }
        //if there is only one group

        if ( p_numberofgroups == 1 )
        {
            //if group size bigger than pedestrian size
            //then create a group with the available pedestrains size
            if ( p_groupsize > l_pedgroup.size() )
            {
                l_numberofgroups = 1;
                l_groupsize = l_pedgroup.size();
                System.out.println( "come here 1" );
            }
            else
            {
                //if group size equals pedestrian size ;
                //then create group with the required group size
                l_numberofgroups = 1;
                l_groupsize = p_groupsize;
                System.out.println( "come here 2" );
            }

        }

        //if there are more than one group
        else if ( p_numberofgroups * p_groupsize > l_pedgroup.size() )
        {
            System.out.println( "come here 3" );
            l_groupsize = p_groupsize;
            //l_numberofgroups = m_pedestrian.size() / p_groupsize;
            l_numberofgroups = ( int ) Math.ceil( l_pedgroup.size() / p_groupsize );
        }
        else
        {
            System.out.println( "come here 4" );
            l_numberofgroups = p_numberofgroups;
            l_groupsize = p_groupsize;
        }
        //        System.out.println(l_numberofgroups +" group initialization "+l_groupsize);
        //        System.out.println( p_groupsize + " group info 2 "+ p_numberofgroups );
        // groups construction
        // ToDo: update car
        for ( int i = 0; i < l_numberofgroups; i++ )
        {
            m_groups.add( new CMainGroup( l_groupsize, this, 360 ) );

            for ( int j = 0; j < l_groupsize; j++ )
                m_groups.get( i ).members().add( l_pedgroup.get( i * l_groupsize + j ) );

            //  m_groups.get(i).chooseLeader(m_car.get(0));
            m_groups.get( i ).chooseMainLeader();
            if ( l_groupsize > 1 )
                m_groups.get( i ).findlastmember();

        }

        m_groups.forEach( i -> i.members()
                                .forEach( j ->
                                          {
                                              j.setPedinGroup( 1 );
                                              j.setGroupId( i.id() );

                                          } ) );

        m_groups.forEach( g -> g.goal( g.mainLeader().getGoalposition() ) );
        m_groups.stream().filter( g -> g.size() > 3 ).forEach( IMainGroup::cluster );

        //     m_groups.parallelStream().filter(g -> clusteringprobability(g.size())).forEach(IMainGroup::cluster);

    }

    private boolean clusteringprobability(final double p_size)
    {
        //range of seven nmbers (0-6) starting from the group size
        return m_rand.nextInt(7) + p_size >= 9;
    }

    /**
     * draw groups for visualization
     */
    private void drawgroups(Graphics2D p_graphics2D)
    {
        m_groups.parallelStream()
                .forEach(
                        g ->
                        {
                            if ( g.mode() == EGroupMode.COORDINATING && g.clusters().size() > 1 /*&& clusteringprobability(g.size())*/ )
                                g.clusters().forEach(c -> c.draw(p_graphics2D));
                            else
                                g.draw(p_graphics2D);
                        });
    }

    public List<IMainGroup> groups()
    {
        return m_groups;
    }

    //------------------------ end Suhair functions -----------------------------------------------------

    // get methods
    public int getWidth() { return m_width; }

    public int getHeight() { return m_height; }
    /**
     * paint all elements
     **/
    public void paint( Graphics g )
    {
        super.paint( g );
        graphics2d = ( Graphics2D ) g;
//        int[] x = new int[]{0,115*m_pixelpermeter, 0, 115*m_pixelpermeter};
//        int[] y = new int[]{(int)2.5*m_pixelpermeter,66*m_pixelpermeter,10*m_pixelpermeter,75*m_pixelpermeter};;
//        g.drawPolygon (x, y, 4);

        drawborder( Color.gray );
        //drawroad();
        try {
            drawPedestrian();
            drawgroups(graphics2d);
            drawCar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    public void setStrategy( String p_id, double p_strategy ) { m_strategy.putIfAbsent(p_id,p_strategy); }

    private void drawborder( Color color )
    {
        graphics2d.setColor( color ) ;
        graphics2d.setStroke(new BasicStroke(42f));
        m_roadborder.forEach(
                i->
                    graphics2d.draw( new Line2D.Float((float)i.getPoint1().x, (float)i.getPoint1().y,
                            (float)i.getPoint2().x, (float)i.getPoint2().y) ) );
    }


    /**
     * draw each pedestrian
     */
    private void drawPedestrian()
    {
        graphics2d.setColor( Color.black ) ;

        m_pedestrian
                .stream().filter( i -> !m_pedestriangroup.contains(i))
                .forEach( i -> {
                    Ellipse2D.Double shape = new Ellipse2D.Double( i.getPosition().getX(), i.getPosition().getY(), 0.75*m_pixelpermeter, 0.75*m_pixelpermeter );//2.5//8
                    graphics2d.fill( shape );

                });
    }

    /**
     * draw each car
     */
    private void drawCar()
    {
        graphics2d.setColor( Color.BLUE ) ;
        m_car
                .forEach( i -> {
                    // graphics2d.fillRect( (int)i.getPosition().x, (int)i.getPosition().y, 14, 14 );
                    // or
                    AffineTransform rat = graphics2d.getTransform();
                    graphics2d.rotate( Math.toRadians(207),i.getPosition().x, i.getPosition().y );
                    //.rotate( Math.toRadians(205+CVector.angle(i.getPosition(), i.getGoalposition())),i.getPosition().x, i.getPosition().y );
                    graphics2d.fillRect( (int)(i.getPosition().x -1.5*m_pixelpermeter ), (int)(i.getPosition().y -1*m_pixelpermeter), (int)3*m_pixelpermeter, (int)2*m_pixelpermeter );//20,10//16, 8
                    graphics2d.setTransform(rat);
                    graphics2d.setColor( Color.GREEN ) ;
                });

    }

    /**
     * draw each car
     */
    private void drawroad()
    {
        graphics2d.setColor( Color.BLUE ) ;

                    AffineTransform rat = graphics2d.getTransform();
                    graphics2d.rotate( Math.toRadians(35),(int)58*m_pixelpermeter, (int)35*m_pixelpermeter );

                    graphics2d.fillRect( 0, (int)2.5*m_pixelpermeter, (int)7*m_pixelpermeter, (int)110*m_pixelpermeter );//20,10//16, 8
                    graphics2d.setTransform(rat);

    }

    /**
     * get the list of pedestrian with their information
     * @return a list of pedestrian information
     **/
    public ArrayList<IBaseRoadUser> getPedestrianinfo()
    {
        return m_pedestrian;
    }

    /**
     * get the list of car with their information
     * @return a list of car information
     **/
    public ArrayList<IBaseRoadUser> getCarinfo()
    {
        return m_car;
    }

    /**
     * get the list of pedestrian with their information
     * @return a list of agent
     **/
    public CopyOnWriteArrayList<IBaseRoadUser> getRoadUserinfo()
    {
        return m_agent;
    }


    /**
     * get the list of walls with their information
     * @return a list of wall information
     **/
    public ArrayList<CStatic> getWall()
    {
        return m_wall;
    }


    /**
     * get the list of walls with their information
     * @return a list of wall information
     **/
    public ArrayList<CWall> getWallinfo()
    {
        return m_walledge;
    }

    /**
     * check if the line segment between two points intersect with any wall edge and
     * the intersection point is not the start or end of point of the wall edge
     * @return true or false
     **/
    public Boolean check( Vector2d p_point1, CNode p_node1, Vector2d p_point2, CNode p_node2 )
    {
        int l_count = 0;
        for ( CWall l_walledge : m_walledge )
        {
            if ( CVector.segmentIntersect( p_point1, p_point2, l_walledge.getPoint1(),
                    l_walledge.getPoint2() ) )
            {
                l_count++;
            }
        }
        return l_count <= 2 || ( l_count == 4 && !Objects.equals( p_node1.getPolygonName(), p_node2.getPolygonName() ) );

    }

    /**
     * calculate route
     *
     * @param p_currentposition start position
     * @param p_targetposition target position
     * @return list of Vector2d points as path
     */
    public final List<Vector2d> route( final Vector2d p_currentposition, final Vector2d p_targetposition )
    {
        return new CAstarAlgorithm(this).route( m_walledge, p_currentposition, p_targetposition );
    }

    @Override
    public IEnvironment call() throws Exception
    {
        return this;
    }
}
