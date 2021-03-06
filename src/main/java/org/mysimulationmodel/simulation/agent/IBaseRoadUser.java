package org.mysimulationmodel.simulation.agent;

import org.lightjason.agentspeak.action.binding.IAgentAction;
import org.lightjason.agentspeak.action.binding.IAgentActionFilter;
import org.lightjason.agentspeak.action.binding.IAgentActionName;
import org.lightjason.agentspeak.agent.*;
import org.lightjason.agentspeak.configuration.IAgentConfiguration;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
import org.mysimulationmodel.simulation.common.CForce;
import org.mysimulationmodel.simulation.common.CVector;
import org.mysimulationmodel.simulation.environment.CEnvironment;
//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.Gdx;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Collectors;


/**
 * Created by Fatema on 28.01.2018.
 * Basic class for modeling individual agent's behaviours
 * @todo convert environment into grid environment, so I can get walls which lies
 * @todo probability of pedestrian stopping at crossing should be done based on real data
 * on FOV and deal with problem when angle between 2 cars is  < 10 || > 350 and near to wall,
 * so which direction to deviate for pedestrian also to deviate with direction left or right
 * @todo route calculation process should be more efficient
 * @todo add pedestrian is in group or not and NoSC
 * @todo fix the social forces for cars
 * @todo road user's size and speed or velocity should be consistent according the pixel meter relationship
 */

@IAgentAction
public class IBaseRoadUser extends IBaseAgent<IBaseRoadUser>
{
    private static final long serialVersionUID = -2111543876806742109L;
    private UUID m_groupid;
    private Vector2d m_position;
    private Vector2d m_goal;
    private Vector2d m_velocity ;
    private double m_speed;
    private CEnvironment m_env;
    private double m_maxspeed;
    private double m_lengthRadious;
    private String m_name;
    private int m_type; // ped 1 nd car 2
    private double m_maxspeedfactor;
    private double m_maxforce;
    private double m_radius;
    private double m_groupId;
    private double m_carfollowingActive = 0;
    private double m_carfollowed = 0;
    private double m_pedinGroup = 0;
    private double m_moodcount;//number of give way
    private static final int m_pixelpermeter = CEnvironment.getpixelpermeter();
    private static double m_distanceStillFollowCurrentDeviateStrategy;
    private static int m_numberofgoalpoint;
    private static double m_pedtocardistance;
    private static double m_cartocardistance;
    private static double m_acceleration;
    private static double m_pedacceleration;
    private static double m_decelerationcartocar;
    private static double m_decelerationpedtocar;
    //newly added
    private static double m_deceleration;
    private static double m_distancetocompitativeuser;
    private static IBaseRoadUser m_carfollowingcar;
    private Vector2d m_desiredVelocity;
    private double m_angle;

    //from force class
    private static double m_pedlamda;
    private static double m_repulsefactorpedtoped;
    private static double m_repulsefactorpedtocar;
    private static double m_pedtopedsigma;
    private static double m_pedtocaraccelarationdistance;
    private static double m_pedtocarsigma;

    private int m_NoSc;
    private int m_usercreated;
    private Vector2d m_previousposition;
    private Vector2d m_movingDirection;
    // all players with whom this player is currently playing or already played
    private List<IBaseRoadUser> m_competitiveuser;

    // all deviated players with whom this player is currently playing or already played
    private List<IBaseRoadUser> m_competitivedeviateuser;

    // all players with whom this player is currently playing
    private List<IBaseRoadUser> l_currentlyplayingwith = new ArrayList<>();
    private IBaseRoadUser m_currentlyovertaking;

    // current behavior 3 means agent is moving freely, no extra forces are applied, but
    // if current behavior is 1 then any of the forces, namely accelerate, decelerate
    // or deviate, is applied on the agent
    private int m_currentbehavior = 3;

    //this variable is 0 if this car is in his lane and 1 if in another lane
    private int m_inopposite_lane = 0;

    /**
     * route
     */
    private List<Vector2d> m_route = new ArrayList<>();

    IBaseRoadUser( @Nonnull final IAgentConfiguration<IBaseRoadUser> p_configuration, final CEnvironment p_env,
                   final double p_maxspeedfactor )
    {
        super( p_configuration );
        m_env = p_env;
        m_maxspeedfactor = p_maxspeedfactor;
        m_competitiveuser = new ArrayList<>();
        m_competitivedeviateuser = new ArrayList<>();
        m_moodcount = 0;
    }


    /**
     * set values to the variable
     **/
    void setSpeed( final double p_speed )
    {
        this.m_speed = p_speed;
        //m_maxspeed = p_speed * m_maxspeedfactor;
    }

    /**659-
     * set values to the variable
     **/
    private void setCarFollowing( final double p_carfollowing )
    {
        this.m_carfollowingActive = p_carfollowing;
    }

    private void setCarFollowed( final double p_carfollowed )
    {
        this.m_carfollowed = p_carfollowed;
    }

    public void setBehavior(final int p_behavior)
    {
        this.m_currentbehavior = p_behavior;
    }

    void setVelocity(final double p_speed, final Vector2d p_goal, final Vector2d p_position )
    {
        m_velocity = CVector.scale( p_speed, CVector.direction( p_goal, p_position ) );
    }

    public void setM_deceleration( final double p_decelerationrate )
    {
        m_deceleration = p_decelerationrate;
    }
    public double getM_deceleration()
    {
        return m_deceleration;
    }

    public void setM_distancetocompitativeuser( final double p_distance )
    {
        m_distancetocompitativeuser = p_distance;
    }
    public double getM_distancetocompitativeuser()
    {
        return m_distancetocompitativeuser;
    }

    public void setGoalPedestrian(final Vector2d p_goalposition )
    {
        this.m_goal = p_goalposition;
    }

    void setGoalPedestrian(final double p_x, final double p_y )
    {
        this.m_goal = new Vector2d( p_x, p_y );
    }

    void setPosition( final double p_x, final double p_y )
    {
        this.m_position = new Vector2d( p_x, p_y );
    }

    private void setposX( final double p_posX )
    {
        this.m_position.x = p_posX;
    }

    private void setposY( final double p_posY )
    {
        this.m_position.y = p_posY;
    }
    public void setMaxSpeed( double p_maxspeed )
    {
        m_maxspeed = p_maxspeed;
    }

    void setradius(final double p_radius)
    {
        this.m_radius = p_radius;
    }

    void setname(final String p_name)
    {
        this.m_name = p_name;
    }

    void setmaxforce(final double p_maxforce)
    {
        this.m_maxforce = p_maxforce;
    }

    void settype( final int p_type )
    {
        this.m_type = p_type;
    }

    public void setStrategyFollowDistance( final double p_distance )
    {
        this.m_distanceStillFollowCurrentDeviateStrategy = p_distance;
    }

    void setPosition( final Vector2d p_position )
    {
        this.m_position = p_position;
    }

    void setmovingdirection( final Vector2d p_start, final Vector2d p_end )
    {
        this.m_movingDirection = CVector.direction( p_end, p_start );;
    }

    private void setCurrentlyOvertakingCar( final IBaseRoadUser p_currentlyovertaking )
    { m_currentlyovertaking = p_currentlyovertaking;}


    /**
     * get values of the variable
     **/

    Vector2d getmovingdirection()
    {
        return this.m_movingDirection;
    }

    public Vector2d getGoalposition() {
        return m_goal;
    }

    public Vector2d getPosition() {
        return m_position;
    }

    public Vector2d getVelocity() {
        return m_velocity;
    }


    public IBaseRoadUser getCarFollowingCar() {
        return m_carfollowingcar;
    }

    public IBaseRoadUser setCarFollowingCar(IBaseRoadUser p_test) {
        m_carfollowingcar = p_test;
        return this;
    }

    public double getSpeed() {
        return m_speed;
    }

    public double getAccelaration() {
        return m_acceleration;
    }

    public double getMaxSpeed() {
        return m_maxspeed;
    }

    public double getCarfollowingActive() {
        return m_carfollowingActive;
    }

    public double getCarfollowed() {
        return m_carfollowed;
    }

    public double getPedinGroup() {
        return m_pedinGroup;
    }

    public double getMood() {
        return m_moodcount;
    }

    public double getMoodCount() { return m_maxspeed; }

    public List<IBaseRoadUser> getCurrentlyPlayingWith() { return  l_currentlyplayingwith; }

    public int getCurrentBehavior() { return m_currentbehavior; }

    IBaseRoadUser getCurrentlyOvertakingCar(){ return m_currentlyovertaking;}

    int getLane() { return m_inopposite_lane; }

    public String getname() {
        return m_name;
    }

    public int getType() {
        return m_type;
    }

    public List<Vector2d> getRoute() {
        return m_route;
    }

    public double getnumberofgoalpoints()
    {
        return m_numberofgoalpoint;
    }

    public double getM_distanceStillFollowCurrentDeviateStrategy()
    {
        return m_distanceStillFollowCurrentDeviateStrategy;
    }

    public double getM_radius()
    {
        return m_radius;
    }

    public void setLengthradius( double p_radious )
    {
        m_lengthRadious = p_radious;
    }

    public void setM_numberofgoalpoints( int p_number )
    {
        m_numberofgoalpoint = p_number;
    }
    public double getLengthradius()
    {
        return m_lengthRadious;
    }

    void setNoSC(int p_NoSC)
    {
        m_NoSc = p_NoSC;
    }
    public int getNoSC()
    {
        return m_NoSc;
    }

    List<IBaseRoadUser> get_competitiveUserList()
    {
        return m_competitiveuser;
    }

    List<IBaseRoadUser> getDeviateList()
    {
        return m_competitivedeviateuser;
    }

    @Override
    public IBaseRoadUser call() throws Exception
    {
        // run default cycle
        return super.call();
    }

    //update variables
    public IBaseRoadUser updateParameter(double p_acceleration, double p_decelerationcartocar, double p_decelerationpedtocar,
                                         double p_pedlamda, double p_repulsefactorpedtoped, double p_pedtopedsigma, double p_pedtocarsigma,
                                         double p_pedtocarforce, double p_cartocardistance, double p_pedtocardistance, double p_pedtocaraccelarationdistance )
    {
        m_acceleration = 0.30*m_pixelpermeter;
        //m_pedacceleration = 0.05*m_pixelpermeter;

        m_decelerationcartocar = p_decelerationcartocar;
        m_decelerationpedtocar = p_decelerationpedtocar;
        m_pedlamda = p_pedlamda;
        m_repulsefactorpedtoped = p_repulsefactorpedtoped;
        m_pedtopedsigma = p_pedtopedsigma;
        m_pedtocarsigma = p_pedtocarsigma;
        m_repulsefactorpedtocar = p_pedtocarforce;
        m_cartocardistance = p_cartocardistance;
        m_pedtocardistance = p_pedtocardistance;
        m_pedtocaraccelarationdistance = p_pedtocaraccelarationdistance;
        return this;
    }

    /**
     * route calculation and add landmarks at the beginning
     */
    @IAgentActionFilter
    @IAgentActionName( name = "route/set/calculate" )
    protected final void routecalculation()
    {
        m_route.add( this.getGoalposition() );
        this.m_goal = m_route.remove(0 );
        this.setM_numberofgoalpoints(m_route.size());
    }

    /**
     * calculate acceleration value based on all applied force
     * this function can be different for type to type
     * @return acceleration value
     **/
    private void accelaration( double p_speedfactor )
    {
        Vector2d l_desiredVelocity = CVector.scale( this.m_maxspeed, CVector.direction( this.getGoalposition(), this.getPosition() ) );
        m_speed = p_speedfactor;
        if (this.getType() == 1)
        {
            Vector2d l_repulsetoOthers = new Vector2d( 0, 0 );
            Vector2d l_groupforce = new Vector2d( 0, 0 );
            Vector2d l_totalforce;


            if( !this.getCurrentlyPlayingWith().isEmpty() )
            {
                //System.out.println("hellooooooooooooooo");
                /*double l_angle1 = CForce.getViewAngle( CVector.direction(this.m_goal, this.m_position).x,
                        CVector.direction(this.m_goal, this.m_position).y,
                        (this.getCurrentlyPlayingWith().get(0).getPosition().x - this.m_position.x),
                        (this.getCurrentlyPlayingWith().get(0).getPosition().y - this.m_position.y));*/

                if (this.m_currentbehavior != 0  && !this.getCurrentlyPlayingWith().get(0).getCurrentlyPlayingWith().contains(this) &&
                        !CForce.collisionCheckingfordeviate(this,this.getCurrentlyPlayingWith().get(0)))
                    //&& !(l_angle1 <= 90 || l_angle1 >= 270)
                    //
                {
                    this.beliefbase().remove(CLiteral.from("force", CRawTerm.from(1.0)));
                    this.beliefbase().remove(CLiteral.from("force", CRawTerm.from(2.0)));
                    this.beliefbase().remove(CLiteral.from("force", CRawTerm.from(0.0)));
                    this.beliefbase().add(CLiteral.from("force", CRawTerm.from(3.0)));

                    this.getCurrentlyPlayingWith().clear();
                    this.trigger(
                            CTrigger.from(
                                    ITrigger.EType.ADDGOAL,
                                    CLiteral.from( "update/detour/belief" ) ) );
                    //this.cancelDetour();
                }
            }

            for ( int i = 0; i < m_env.getRoadUserinfo().size(); i++ )
            {
                if ( !m_env.getRoadUserinfo().get(i).equals(this) )
                {
                    l_repulsetoOthers = CVector.add( l_repulsetoOthers, CForce.repulseotherPed(this, m_env.getRoadUserinfo().get(i),
                            m_pedlamda, m_repulsefactorpedtoped, m_pedtopedsigma, m_pedtocarsigma, m_repulsefactorpedtocar ) );
                }
            }

            /*
             * groups interaction to each other
             * if any group finds other group in his direction of travel in his FOV
             * the direction of that agent is deviated in the same direction
             */
            //suhair - slowing down the velocity in order to stop gradually  instead of bouncing on the leader
            // Check the distance to detect whether the character is inside the slowing area

            IPedestrianGroup l_group = group();
            if ( l_group != null )
            {
                //if(m_speed == 0) return;


                //new suhair
                if ( !this.equals(l_group.mainLeader()) )//&& l_group.zone() == EZone.SAFE )
                {
                    l_groupforce = CForce.groupforce(l_group, this);
                }

//                double l_distance = CVector.distance(this.getPosition(), this.getGoalposition());
//                // slowing radius is taken as the sum of:
//                //      5m pedestrian radius
//                //      4m graphical pedestrian size (used in visualization)
//                double l_slowingRadius = 3 * m_pixelpermeter;
//
//                if (l_distance < l_slowingRadius && !this.equals(l_group.mainLeader())) {
//                    double ss = (l_distance) / l_slowingRadius;
//                    if (ss > 0.2) {
//                        //       System.out.println( "before " + this.m_speed );
//                        this.setSpeed(ss * m_speed);
//                    } else
//                        this.setSpeed(0);
//                    // System.out.println( "slowing speed " + getSpeed() );
//                } else
//                    this.m_speed = p_speedfactor;



                //problem-the total force is not taking the group force into account
                l_totalforce = CVector.add( l_repulsetoOthers,l_groupforce);
                System.out.println( "total force" + l_totalforce );
                System.out.println( "repulsive force" + l_repulsetoOthers );
                System.out.println( "group force" + l_groupforce );

                Vector2d l_velocity =
                        CVector.truncate(
                                CVector.add(CForce.drivingForce(l_desiredVelocity, this.m_velocity,0.5), l_totalforce),
                                2*m_pixelpermeter
                        );
                //System.out.println(l_totalforce+ " "+ l_groupforce+" hellooooooooooooooo group "+l_velocity+ " "+m_speed);
                this.m_velocity = CVector.scale(m_speed, CVector.normalize(CVector.add(this.m_velocity, l_velocity)));
                this.m_position = CVector.add(m_position, m_velocity);
            }
            else
            { //System.out.println( "orrrrrr group came here");
//                m_speed = this.m_velocity.length();
                this.m_velocity = CVector.scale( p_speedfactor, CVector.normalize( CVector.add( this.m_velocity,
                    CVector.add( CForce.drivingForce( l_desiredVelocity, this.m_velocity ), l_repulsetoOthers ) ) ) );
                this.m_position = CVector.add( m_position, m_velocity );
            }
            return;
        }//till here

        if(this.getType() == 2)
        {
            /*
             * car following behavior
             * if any agent finds other competitive agent in his direction of travel with degree 10 or 350 and
             * the direction of that competitive agent is in the same direction ( approximately )
             * as ego (current) agent, ego agent follows the competitive agent
             */
            double l_distance = 14*m_pixelpermeter;
            double finalL_distance = 14*m_pixelpermeter;
            List<IBaseRoadUser> l_viewedcarlist = m_env.getCarinfo().stream()
                    .filter(j -> !j.equals(this) && finalL_distance >= CVector.distance(this.getPosition(), j.getPosition()))
                    .filter(i ->
                    {
                        double l_angle = CForce.getViewAngle(CVector.direction(this.getGoalposition(), this.getPosition()).x,
                                CVector.direction(this.getGoalposition(), this.getPosition()).y,
                                (i.getPosition().x - this.m_position.x), (i.getPosition().y - this.m_position.y));
                        return l_angle < 10 || l_angle > 350;
                    })
                    .collect(Collectors.toList());

            IBaseRoadUser l_test = null;

            // to get the nearest competitive road user
            if (l_viewedcarlist.size() != 0)

                for (IBaseRoadUser aL_viewedcarlist : l_viewedcarlist)
                {
                    if (CVector.distance(this.getPosition(), aL_viewedcarlist.getPosition()) <= l_distance) {
                        l_test = aL_viewedcarlist;
                        l_distance = CVector.distance(this.getPosition(), aL_viewedcarlist.getPosition());
                    }
                }

            if (l_test != null && this.getCurrentlyOvertakingCar() != l_test)
            {
                if (l_test.getPosition().equals(l_test.getGoalposition()))
                {
                    this.overtaking(l_test);
                    return;
                }
                if ( CVector.direction( this.getGoalposition(), this.getPosition() ).dot(CVector.direction(l_test.getGoalposition(), l_test.getPosition())) > 0.95)
                {
                    this.carFollowing(l_test);
                    return;
                }
            }
            m_speed = this.m_velocity.length();
            this.m_velocity = CVector.scale(p_speedfactor, CVector.normalize(CVector.add(this.m_velocity,
                    CForce.drivingForce(l_desiredVelocity, this.m_velocity))));
            this.m_position = CVector.add(m_position, m_velocity);
        }

    }

    /**
     * find user group
     *
     * @return group reference if user is member, else return null
     */
    public IPedestrianGroup group()
    {
        for ( IMainGroup g : m_env.groups() )
            if ( g.ismember( this ) )
            {
                if ( g.mode() == EGroupMode.COORDINATING )
                {
                    for ( ICluster c : g.clusters() )
                        if ( c.ismember( this ) )
                            return c;
                }

                return g;
            }

        return null;
    }

    public IPedestrianGroup maingroup()
    {
        for ( IMainGroup g : m_env.groups() )
            if ( g.ismember( this ) )
                return g;

        return null;
    }

    public IMainGroup getgroupinfo(IBaseRoadUser mem)
    {
        UUID id = mem.getGroupId();
        return m_env.groups().parallelStream().filter(g->g.id() ==id).findFirst().get();

    }


    // agent actions
    /**
     * Computes a result, or throws an exception if unable to do so.
     * overload agent-cycle
     * @todo set maximum acceleration rate of pedestrian and car
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @IAgentActionFilter
    @IAgentActionName( name = "calculate/next/position" )
    public IBaseRoadUser nextPosition() throws Exception
    {
        final double l_distance = CVector.subtract( this.getGoalposition(), this.getPosition() ).length();
        final double l_slowingRadius = this.getM_radius() + ( m_maxspeed * 1.25 );
        double l_acefactor = (this.m_currentbehavior == 0) ? 0.5: 0.25;

        if ( this.m_route.size() > 0 )
        {
            //add if intermediate point is passed without reaching it, delete it from route list
            if ( ( this.getType() == 1 && l_distance <= 3*m_pixelpermeter ) || l_distance <= 1*m_pixelpermeter )//10
            {
                // sub-destination nearby
                this.trigger(
                        CTrigger.from(
                                ITrigger.EType.ADDGOAL,
                                CLiteral.from( "reached/destination", Stream.of( CRawTerm.from( this.getGoalposition() ) ) )
                        )
                );
                m_speed = this.m_velocity.length();
                this.m_velocity = new Vector2d(0, 0 );
                this.m_goal = m_route.remove(0 );
                this.setM_numberofgoalpoints(m_route.size());
                double l_speed = this.getVelocity().length() + m_acceleration <= m_maxspeed ? this.getVelocity().length() +
                        m_acceleration : m_maxspeed;

                m_acceleration = m_acceleration + l_acefactor*m_pixelpermeter <= m_pixelpermeter ? m_acceleration + l_acefactor*m_pixelpermeter : m_acceleration ;
                this.accelaration( l_speed );
            }
            else
            {
                double l_speed = this.getVelocity().length() + m_acceleration <= m_maxspeed ? this.getVelocity().length()
                        + m_acceleration : m_maxspeed;
                m_acceleration = m_acceleration + l_acefactor*m_pixelpermeter <= m_pixelpermeter ? m_acceleration + l_acefactor*m_pixelpermeter : m_acceleration ;
                this.accelaration( l_speed );
            }
        }
        else
        {    // Arrival steering behavior
            if ( l_distance <= l_slowingRadius )
            {

                if( this.getVelocity().length() <= 0.001 )
                {
                    this.setSpeed(0);
                    this.trigger(
                            CTrigger.from(
                                    ITrigger.EType.ADDGOAL,
                                    CLiteral.from( "reached/destination", Stream.of( CRawTerm.from( this.getGoalposition() ) ) )
                            ) );
                }
            }
            else
            {

                double l_speed = this.getVelocity().length() + m_acceleration <= m_maxspeed ? this.getVelocity().length() +
                        m_acceleration : m_maxspeed;
                System.out.println( this.getname() +" don't they come here "+l_speed );
                m_acceleration = m_acceleration + l_acefactor*m_pixelpermeter <= m_pixelpermeter ?
                        m_acceleration + l_acefactor*m_pixelpermeter : m_acceleration;
                this.accelaration( l_speed );
            }
        }
        //}//for genetic algo
        if( m_position.getX() > m_env.getWidth() )
        {
            setposX( 0.0 );
        }

        if( m_position.getX() < 0.0 )
        {
            setposX( 0.0 );
        }

        if( m_position.getY() > m_env.getHeight() )
        {
            setposY( 0.0 );
        }

        if( m_position.getY() < 0.0 )
        {
            setposY( 0.0 );
        }

        return this;
    }


    //@IAgentActionFilter
    //@IAgentActionName( name = "car/following" )
    private IBaseRoadUser carFollowing( final IBaseRoadUser p_test )
    {
        Vector2d l_desiredVelocity = CVector.scale( this.m_maxspeed, CVector.direction( this.getGoalposition(), this.getPosition() ) );
        double l_speed = this.getVelocity().length() + m_acceleration <= m_maxspeed ? this.getVelocity().length() + m_acceleration : m_maxspeed;
        if ( p_test.getSpeed() != 0 )
        {
            this.setCarFollowing(1);
            this.setCarFollowingCar( p_test );
            p_test.setCarFollowed(1);
            if ( CVector.distance( this.getPosition(), p_test.getPosition()) <= m_cartocardistance )//32
            {
                l_speed = this.getVelocity().length() /(float)m_decelerationcartocar;
            }
            else
            {
                l_desiredVelocity = CVector.scale( m_maxspeed, CVector.direction(CForce.nextCarFollowing(this, p_test), this.getPosition() ) );
            }
        }
        else
        {
            // fix the scale factor for intermediate goal based on the radius of cars
            this.overtaking( p_test );
            this.beliefbase().remove(CLiteral.from("force", CRawTerm.from( 2.0 ) ) );
            this.beliefbase().add(CLiteral.from("force", CRawTerm.from( 3.0 ) ) );
        }

        Vector2d l_velocity = CVector.truncate( CForce.drivingForce( l_desiredVelocity, this.m_velocity ), this.m_maxforce );
        m_speed = this.m_velocity.length();
        this.m_velocity = CVector.scale( l_speed, CVector.normalize( CVector.add( this.m_velocity, l_velocity ) ) );
        this.m_position = CVector.add( m_position, m_velocity );
        return this;

    }

    //need to check
    public void overtaking( final IBaseRoadUser p_test )
    {
        this.setCurrentlyOvertakingCar( p_test );
        Vector2d l_temp = this.m_goal;
        this.m_goal = CVector.add( p_test.getPosition(), CVector.scale(8*m_pixelpermeter,//10//30
                CVector.directionrotation( CVector.direction( this.getGoalposition(), this.getPosition() ), 180 ) ) );//225
        this.m_route.add(0, CVector.add( this.m_goal, CVector.scale(15*m_pixelpermeter, CVector.direction( l_temp, this.getPosition() ) ) ) );//30
        this.m_route.add( 1, CVector.add( m_route.get(0), CVector.scale(20*m_pixelpermeter,
                CVector.directionrotation( CVector.direction( m_route.get(0), this.m_goal ), 45 ) ) ) );//45, 20
        this.m_route.add(2, l_temp );
    }


    @IAgentActionFilter
    @IAgentActionName( name = "moving" )
    private IBaseRoadUser freelymoving()
    {
        m_speed = this.m_velocity.length();
        Vector2d l_desiredVelocity = CVector.scale( this.m_speed, CVector.direction( this.getGoalposition(), this.getPosition() ) );
        Vector2d l_velocity = CVector.truncate( CForce.drivingForce( l_desiredVelocity, this.m_velocity ), this.m_maxforce );
        this.m_velocity = CVector.scale( this.m_speed, CVector.normalize( CVector.add( this.m_velocity, l_velocity ) ) );
        this.m_position = CVector.add( m_position, m_velocity );
        return this;
    }

    @IAgentActionFilter
    @IAgentActionName( name = "car/stop/moving" )
    private IBaseRoadUser carStopping()
    {
        this.m_currentbehavior = 1;
        Vector2d l_desiredVelocity = CVector.scale( this.m_maxspeed, CVector.direction( this.getGoalposition(), this.getPosition() ) );
        Vector2d l_velocity = CVector.truncate( CForce.drivingForce( l_desiredVelocity, this.m_velocity ), this.m_maxforce );
        System.out.println( "who 2 "+ this.getCurrentlyPlayingWith().get(0).getname());
        if( CVector.distance( this.getPosition(), this.getCurrentlyPlayingWith().get(0).getPosition()) <= m_pedtocardistance )
        {
            m_speed = this.m_velocity.length();
            this.m_velocity = CVector.scale( 0.2, CVector.normalize( CVector.add( this.m_velocity, l_velocity ) ) );
            return this;
        }

        this.decelerating();

        if ( this.m_velocity.length() - m_deceleration < 0.2 )
            return this;

        /*
             * car following behavior
             * if any agent finds other competitive agent in his direction of travel with degree 10 or 350 and
             * the direction of that competitive agent is in the same direction ( approximately )
             * as ego (current) agent, ego agent follows the competitive agent
             */
            double l_distance = 14*m_pixelpermeter;
            double finalL_distance = 14*m_pixelpermeter;
            List<IBaseRoadUser> l_viewedcarlist = m_env.getCarinfo().stream()
                    .filter(j -> !j.equals(this) && finalL_distance >= CVector.distance(this.getPosition(), j.getPosition()))
                    .filter(i ->
                    {
                        double l_angle = CForce.getViewAngle(CVector.direction(this.getGoalposition(), this.getPosition()).x,
                                CVector.direction(this.getGoalposition(), this.getPosition()).y,
                                (i.getPosition().x - this.m_position.x), (i.getPosition().y - this.m_position.y));
                        return l_angle < 10 || l_angle > 350;
                    })
                    .collect(Collectors.toList());

            IBaseRoadUser l_test = null;

            // to get the nearest competitive road user
            if (l_viewedcarlist.size() != 0)

                for (IBaseRoadUser aL_viewedcarlist : l_viewedcarlist)
                {
                    if (CVector.distance(this.getPosition(), aL_viewedcarlist.getPosition()) <= l_distance) {
                        l_test = aL_viewedcarlist;
                        l_distance = CVector.distance(this.getPosition(), aL_viewedcarlist.getPosition());
                    }
                }

            if (l_test != null && this.getCurrentlyOvertakingCar() != l_test)
            {
                if( CVector.distance( this.getPosition(), l_test.getPosition() ) <= m_pedtocardistance)
                {
                    m_speed = this.m_velocity.length();
                    this.m_velocity = CVector.scale( 0.2, CVector.normalize( CVector.add( this.m_velocity, l_velocity ) ) );
                    return this;
                }
                if (l_test.getPosition().equals(l_test.getGoalposition())) {
                    this.overtaking(l_test);
                    return this;
                }
                if ( CVector.direction( this.getGoalposition(), this.getPosition() ).dot(CVector.direction(l_test.getGoalposition(), l_test.getPosition())) > 0.95) {
                    this.carFollowing(l_test);
                    return this;
                }
            }
        m_speed = this.m_velocity.length();
        this.m_velocity = CVector.scale(this.m_velocity.length() - m_deceleration,
                CVector.normalize( CVector.add( this.m_velocity, l_velocity ) ));
        this.m_position = CVector.add( m_position, m_velocity );//
        return this;
    }

    /*
    * new method for deceleration
    * Implemented in 12.02.2019
    * remove m_deceleration from m_speed
    */
    private IBaseRoadUser decelerating()
    {
        /*if ( CVector.distance( this.m_position, this.getCurrentlyPlayingWith()
                .get(0).getPosition())< this.getCurrentlyPlayingWith()
                .get(0).getM_radius() + this.m_radius + 2 * m_pixelpermeter )
            m_deceleration = this.getVelocity().length();*/

        double l_distance = CVector.distance( this.m_position, this.getCurrentlyPlayingWith()
                .get(this.getCurrentlyPlayingWith().size()-1).getPosition()) - m_pedtocardistance;

        //|| l_distance <= this.m_velocity.length()*this.m_velocity.length()*0.2592 + 2

        if ( l_distance <= 0 )
        {
            m_deceleration = this.m_velocity.length()/2;
            return this;
        }

        m_deceleration = ( this.m_velocity.length() * this.m_velocity.length() )/l_distance;
        return this;

    }

    @IAgentActionFilter
    @IAgentActionName( name = "cancel/detour" )
    private IBaseRoadUser cancelDetour()
    {
        this.setBehavior(3);
        if ( !m_route.isEmpty() ) this.m_goal = m_route.remove( 0 );
        return this;
    }

    // @todo later consider multiple competetive users
    @IAgentActionFilter
    @IAgentActionName( name = "pedestrian/stop/moving" )
    private IBaseRoadUser pedestrianStopping()
    {
        System.out.println("speed "+ this.m_speed  );
        Vector2d l_desiredVelocity = CVector.scale( this.m_maxspeed, CVector.direction( this.getGoalposition(), this.getPosition() ) );
        Vector2d l_velocity = CVector.truncate( CForce.drivingForce( l_desiredVelocity, this.m_velocity ), this.m_maxforce );

        if( !this.getCurrentlyPlayingWith().isEmpty() )
        {
            if( !this.getCurrentlyPlayingWith().get(0).getCurrentlyPlayingWith().contains(this) &&
                    !CForce.collisionCheckingfordeceleration(this,this.getCurrentlyPlayingWith().get(0), 5 )  )
            {

                this.trigger(
                        CTrigger.from(
                                ITrigger.EType.ADDGOAL,
                                CLiteral.from("update/belief", CRawTerm.from(3.0))
                        )
                );

                //this.beliefbase().add(CLiteral.from("force", CRawTerm.from(3.0)));
                this.m_currentbehavior = 3;
                this.getCurrentlyPlayingWith().clear();
                return this;
            }

            if ( CVector.distance( this.m_position, this.getCurrentlyPlayingWith().get(0).getPosition() )
                    < this.getCurrentlyPlayingWith().get(0).getM_radius() + this.m_radius + m_pixelpermeter )// 2 * m_pixelpermeter
            {
                m_speed = this.m_velocity.length();
                this.m_velocity = CVector.scale( 0.1, CVector.normalize( CVector.add( this.m_velocity, l_velocity ) ) );
                return this;
            }
        }

        if ( this.getVelocity().length()/2f <= 0.1 )
            return this;
        m_speed = this.m_velocity.length();
        this.m_velocity = CVector.scale( this.getVelocity().length()/2f,
                CVector.normalize( CVector.add( this.m_velocity, l_velocity ) ) );
        this.m_position = CVector.add( m_position, m_velocity );//*/
        return this;
    }



    @IAgentActionFilter
    @IAgentActionName( name = "check/reachability/of/intermediate/goal" )
    private IBaseRoadUser cheakingReachability()
    {
        // later consider multiple competitive users /may be only for car
        // if currently playing with car/ pedestrian make problem for current agent to reach it's intermediate goal it will delete that goal
        if ( this.getType() == 2 )
        {
            double l_angle2 = CForce.getViewAngle(CVector.direction(this.getGoalposition(), this.getPosition()).x, CVector.direction(this.getGoalposition(), this.getPosition()).y,
                    ( this.l_currentlyplayingwith.get(0).getPosition().x - this.m_position.x ), ( this.l_currentlyplayingwith.get(0).getPosition().y - this.m_position.y ) );

            if ( !this.m_route.isEmpty() && ( l_angle2 < 10 || l_angle2 > 350 ) ) this.m_goal = this.m_route.get(0);
        }

        return this;
    }

    //need to redo
    @IAgentActionFilter
    @IAgentActionName( name = "deviate/movement" )
    private IBaseRoadUser deviate()
    {   this.m_currentbehavior = 2;
        if ( m_route.size() == m_numberofgoalpoint ) this.m_route.add(0, this.m_goal );

        Vector2d l_tempgoal = CVector.scale( -m_pedtocaraccelarationdistance, CVector.direction( this.l_currentlyplayingwith.get(0).getGoalposition(),
                        this.l_currentlyplayingwith.get(0).getPosition() ) );
        /*System.out.println( this.l_currentlyplayingwith.get(0).getname()+" deviate "+l_tempgoal );
        Vector2d l_new = new Vector2d( l_tempgoal.y - this.l_currentlyplayingwith.get(0).getM_radius() * CVector.direction( this.l_currentlyplayingwith.get(0).getGoalposition(),
                this.l_currentlyplayingwith.get(0).getPosition() ).x, l_tempgoal.x + this.l_currentlyplayingwith.get(0).getM_radius() * CVector.direction( this.l_currentlyplayingwith.get(0).getGoalposition(),
                this.l_currentlyplayingwith.get(0).getPosition() ).y );*/

        //if ( this.l_currentlyplayingwith.get(0).getCurrentBehavior() == 1 )
        {
            this.m_goal = CVector.add( this.l_currentlyplayingwith.get(0).getPosition(),l_tempgoal );
            return this;
        }

        //this.m_goal = CVector.add( this.l_currentlyplayingwith.get(0).getPosition(), CVector.scale( 0.5, l_tempgoal ) );
        //return this;
    }

    //need to redo
    @IAgentActionFilter
    @IAgentActionName( name = "deviate/movement/withoutgame" )
    private IBaseRoadUser deviate2()
    {
        if ( m_route.size() == m_numberofgoalpoint ) this.m_route.add(0, this.m_goal );

        this.m_goal = CVector.add( this.l_currentlyplayingwith.get(0).getPosition(), CVector.scale( 0.8,
                CVector.scale( -m_pedtocaraccelarationdistance, CVector.direction( this.l_currentlyplayingwith.get(0).getGoalposition(),
                this.l_currentlyplayingwith.get(0).getPosition() ) ) ) );
        return this;
    }

    void setTrue( int p_par )
    {
        m_usercreated = p_par;
    }
    public int getTrue()
    {
       return m_usercreated;
    }
    public void setGroupId( final UUID p_groupid )
    {
        this.m_groupid = p_groupid;
    }

    public UUID getGroupId()
    {
        return  m_groupid;
    }

    public IBaseRoadUser setPedinGroup(final double p_pedinGroup)
    {
        this.m_pedinGroup = p_pedinGroup;
        return this;
    }

    public Vector2d getdesiredvelocity()
    {
        return m_desiredVelocity = CVector.scale(this.m_maxspeed, CVector.direction(this.getGoalposition(), this.getPosition()));
    }

}
