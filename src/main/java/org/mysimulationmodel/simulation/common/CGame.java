package org.mysimulationmodel.simulation.common;

import org.mysimulationmodel.simulation.agent.EGroupMode;
import org.mysimulationmodel.simulation.agent.IBaseRoadUser;
import org.mysimulationmodel.simulation.environment.CEnvironment;

import java.util.Random;

/**
 * own speed can also have impact on road user's behavior
 * Helping class for game playing
 * Created by fatema on 23.03.2018.
 */
public class CGame
{
    /**
     * measuring road user's speed
     * if speed of the competative user is higher than their normal speed, ego user prefer to decelerate (-1),
     * otherwise accelerate (1)
     **/

    private static final int m_pixelpermeter = CEnvironment.getpixelpermeter();

    //if this does not work then try with weight with each utility condition or factor
    private static final double m_carcontinue = 4;
    private static final double m_carstop  = 0;
    private static final double m_pedcontinue = 4;
    private static final double m_pedstop = 0;
    private static double m_peddeviate = 1;//1
    private static double m_carstoppeddeviate;//-2
    private static int m_randomization = 3;

    private static void setDeviate( final IBaseRoadUser p_car )
    {
        m_carstoppeddeviate = p_car.getCurrentBehavior() == 1 ? 0 : -2;
    }
    /** for ca-tocar interaction
     * measuring impact of car following another car, on road user's behavior
     **/
    private static double carFollowingCarStop( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getCarfollowingActive() == 1 ) return 1;
        return 0;
    }

    /**
     * measuring impact of car following another car, on road user's behavior
     **/
    private static double carFollowingCarContinue( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getCarfollowingActive() == 1 ) return -1;
        return 0;
    }//<-

    /** need to check from here
     * measuring impact of car following behavior on road user's behavior
     * if a car following another car it's drive at moderate speed normaly, so pedestrian more often get chance to continue
     *
    private static double carFollowingActivePedStop( final IBaseRoadUser p_other )
    {
        if ( p_other.getCarfollowingActive() == 1 ) return -1;
        return 0;
    }

    /**
     * measuring impact of car following behavior on road user's behavior
     * if a car following another car it's drive at moderate speed normaly, so pedestrian more often get chance to continue
     *
    private static double carFollowingActivePedContinue( final IBaseRoadUser p_other )
    {
        if ( p_other.getCarfollowingActive() == 1 ) return 1;
        return 0;
    }*/

    /**
     * measuring impact of car followed behavior on road user's behavior
     * if a car is followed by another car it can be dangerous for pedestrian to deviate
     **/
    private static double carFollowedActivePedDeviate( final IBaseRoadUser p_other )
    {
        if ( p_other.getCarfollowed() == 1 ) return -5;
        return 0;
    }

    /** @todo check later frome here->
     * measuring impact of pedestrian is in group or not on road user's behavior
     * normally if pedestrians is in a group they does not want to deviate
     **/
    private static double pedInGroupPedDeviate( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getPedinGroup() == 1 ) return -1;
        return 0;
    }

    /**
     * measuring impact of pedestrian is in group or not on road user's behavior
     * normally if pedestrians is in a group they have less preference to stop
     **/
    private static double pedInGroupPedStop( final IBaseRoadUser p_self )
    {
        if ( p_self.getPedinGroup() == 1 ) return -1;
        return 0;
    }

    /**
     * measuring impact of pedestrian is in group or not on road user's behavior
     * normally if pedestrians is in a group they hprefer to continue
     **/
    private static double pedInGroupPedContinue( final IBaseRoadUser p_self )
    {
        if ( p_self.getPedinGroup() == 1 ) return 1;
        return 0;
    }

    /**
     * measuring impact of pedestrian is in group or not on road user's behavior
     * normally if pedestrians is in a group, car drivers prefer to let them pass first
     **/
    private static double pedInGroupCarContinue( final IBaseRoadUser p_other )
    {
        if ( p_other.getPedinGroup() == 1 ) return -1;
        return 0;
    }

    /**
     * measuring impact of pedestrian is in group or not on road user's behavior
     * normally if pedestrians is in a group, car drivers prefer to let them pass first
     **/
    private static double pedInGroupCarStop( final IBaseRoadUser p_other )
    {
        if ( p_other.getPedinGroup() == 1 ) return 1;
        return 0;
    }

    /**
     * measuring impact of car driver's mood on road user's behavior
     **/
    static double CarMoodContinue( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getMoodCount() > 3 ) return 1;
        return -1;
    }

    /**
     * measuring impact of car driver's mood on road user's behavior
     **/
    static double CarMoodStop( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getMoodCount() > 3 ) return -1;
        return 1;
    }

    //suhair-group
    private static double pedGroupCarContinue(final IBaseRoadUser p_agent)
    {
        if (p_agent.getPedinGroup()==1 && p_agent.getgroupinfo(p_agent).size()>= 2) return -p_agent.getgroupinfo(p_agent).size();
        return 0;
    }

    private static double pedGroupCarStop(final IBaseRoadUser p_agent)
    {
        return p_agent.getPedinGroup()==1 ? p_agent.getgroupinfo(p_agent).size()>= 2 ? p_agent.getgroupinfo(p_agent).size(): 1 : 0;
    }

    private static double pedGroupContinue(final IBaseRoadUser p_agent)
    {
        if (p_agent.getPedinGroup()==1) return 1;
        return 0;
    }



    /**
     * This method is not correct as I assume, NoSC does not have impact on Pedestrian's behavior (not sure yet)
     * Constant "p_NoSC > 2", need to be calibrated
     * measuring impact of number of simultaneous conflicts (NoSC) on road user's behavior
     **/
    static double NoSEpedContinue( final double p_NoSC )
    {
        if ( p_NoSC >= 2 ) return 1;
        return 0;
    }

    private static double goalnearby( final IBaseRoadUser p_self )
    {
        final double l_distance = CVector.subtract( p_self.getGoalposition(), p_self.getPosition() ).length();
        if ( p_self.getRoute().isEmpty() && ( l_distance <= p_self.getM_radius()* 5 ) )
            return 4;

        return 0;
    }

    /**
     * measuring impact of car following behavior on road user's behavior
     **/
    static double actionProbability( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getMoodCount() > 3 ) return -1;
        return 1;
    }//<-@todo till here

    // @now it starts from here
       private static double speedMeasurementContinue( final IBaseRoadUser p_other )
    {
        if ( p_other.getVelocity().length() < p_other.getMaxSpeed()/3f )
            return (p_other.getType() == 2) ? 2 : 1;
        return -2;
    }

    /**
     * measuring road user's speed
     * if speed of the competative user is higher than their normal speed, ego user prefer to decelerate (1),
     * otherwise other's speed has no impact on ego user's stopping decision
     **/
    private static double speedMeasurementStop( final IBaseRoadUser p_other )
    {
        if ( p_other.getVelocity().length() >= p_other.getMaxSpeed()/2 ) return 2;//1.5f
        return 0;
    }

    private static double carAlreadyDecelerateCarContinue( final IBaseRoadUser p_agent )
    {   //if ( p_agent.getVelocity().length() <= 0.2 )
        if ( p_agent.getCurrentBehavior() == 1 ) return -4;
        return 0;
    }

    // p_agent = other
    private static double carAlreadyDecelerateCarStop( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getCurrentBehavior() == 1 ) return 4;
        return 0;
    }

    private static double carAlreadyDeceleratePedContinue( final IBaseRoadUser p_other )
    {
        if ( p_other.getCurrentBehavior() == 1 ) return 4;
        return 0;
    }

    private static double carAlreadyDeceleratePedStop( final IBaseRoadUser p_other )
    {
        if ( p_other.getCurrentBehavior() == 1 ) return -4;
        return 0;
    }
    /**
     * if competative user is decelerating than it does not make sense for ego user to deviate
     **/
    private static double speedMeasurementdeviate( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getVelocity().length() >= p_agent.getMaxSpeed()/1.5f ) return 2;
        return 0;
    }

    /**
     * measuring impact of car following behavior on road user's behavior
     **/
    private static double carFollowingActiveCarStop( final IBaseRoadUser p_self )
    {
        if ( p_self.getCarfollowingActive() == 1)
        {
            if (CVector.distance(p_self.getCarFollowingCar().getPosition(), p_self.getPosition()) <= 7 * m_pixelpermeter)//10
                return -30;//-3
            return 2;
        }
        return 0;
    }

    /**
     * measuring impact of car following behavior on road user's behavior
     **/
    private static double carFollowingActiveCarContinue( final IBaseRoadUser p_self )
    {
        if ( p_self.getCarfollowingActive() == 1 && (CVector.distance(p_self.getCarFollowingCar().getPosition(),
                p_self.getPosition()) > 10 * m_pixelpermeter))
        {
            return -1;
        }
        return 0;
    }

    /**
     * measuring impact of car driver's mood on road user's behavior
     * randomization
     **/
    private static double CarDriverMood()
    {

        return - m_randomization;
        //return 2 + l_number.nextInt(3);

        //if (new Random().nextInt(100) > 80) { return -1; }
        //return 1;
    }

    //these 3 need to be calibrated
    static double carunabletostop( IBaseRoadUser p_self, IBaseRoadUser p_other )
    {
        if ( CVector.distance( p_self.getPosition(), p_other.getPosition() ) <= 3*m_pixelpermeter && p_self.getVelocity().length() > 0.5*m_pixelpermeter ) return -6;
        return 0;
    }

    static double carunabletostoppedcontinue( IBaseRoadUser p_other, IBaseRoadUser p_self )
    {
        if ( CVector.distance( p_self.getPosition(), p_other.getPosition() ) <= 3*m_pixelpermeter
                && p_other.getVelocity().length() > 0.5*m_pixelpermeter ) return -6;
        return 0;
    }

    /**
     * Constant "p_NoSC > 2", need to be calibrated
     * measuring impact of number of simultaneous conflicts (NoSC) on road user's behavior, pagent = self
     **/
    static double NoSEcarStop( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getNoSC() >= 2 ) return p_agent.getNoSC()-1;
        return 0;
    }

    /**
     * Constant "p_NoSC > 2", need to be calibrated
     * measuring impact of number of simultaneous conflicts (NoSC) on road user's behavior
     **/
    static double NoSEcarContinue( final IBaseRoadUser p_agent )
    {
        if ( p_agent.getNoSC() >= 2 ) return -p_agent.getNoSC()+1;
        return 0;
    }

    // this 3 need to be validated
    static double toomuchdeviation( final IBaseRoadUser p_ped, final IBaseRoadUser p_car )
    {
        if ( CVector.distance( p_ped.getPosition(), p_car.getPosition() ) >= p_car.getM_radius() * 3 ) return -2;
        System.out.println( CVector.distance( p_ped.getPosition(), p_car.getPosition())+" why not "+p_ped.getname());
        return 0;
    }

    private static double leaderinWaitingZoneCarContinue( final IBaseRoadUser p_ped )
    {
        if ( p_ped.getPedinGroup()==1 && p_ped.getgroupinfo(p_ped).mode() == EGroupMode.COORDINATING )
            return 15;
        return 0;
    }

    static double pedabletodeviate( final IBaseRoadUser p_ped, final IBaseRoadUser p_car )
    {
        double l_angle2 = CForce.getViewAngle( CVector.direction( p_car.getGoalposition(), p_car.getPosition() ).x,
                CVector.direction( p_car.getGoalposition(), p_car.getPosition() ).y,
                ( p_ped.getPosition().x - p_car.getPosition().x), ( p_ped.getPosition().y - p_car.getPosition().y ) );

        if ( l_angle2 < 40 || l_angle2 > 320 ) return -20;//45, 315
        if ( ( l_angle2 > 58 && l_angle2 <= 113 ) || ( l_angle2 >= 247 && l_angle2 < 302 ) ) return 10;
        return 0;
    }

    static double pedabletodeviatecarcontinue( final IBaseRoadUser p_ped, final IBaseRoadUser p_car )
    {
        if( p_car.getCurrentBehavior() != 1 )
        {
            double l_angle2 = CForce.getViewAngle( CVector.direction( p_car.getGoalposition(), p_car.getPosition() ).x,
                CVector.direction( p_car.getGoalposition(), p_car.getPosition() ).y,
                ( p_ped.getPosition().x - p_car.getPosition().x), ( p_ped.getPosition().y - p_car.getPosition().y ) );
            if ( ( l_angle2 > 58 && l_angle2 <= 113 ) || ( l_angle2 >= 247 && l_angle2 < 302 ) ) return 5;
            // ped can't stop
            //if ( l_angle2 < 40 || l_angle2 > 320 ) return -5;
        }
        return 0;
    }


    /*
     * dimension 1 or row = player1; dimension 2 or column = player 2
     * 0 = continue, 1 = decelerate, 2 = deviate
     * third dimension: 0 = player1's(Car) utility, 1 = player2's(pedestrian) utility
     */
    public static double[][][] payoffMatrixCalculationCartoPed( final IBaseRoadUser p_self, final IBaseRoadUser p_other, final int p_row, final int p_column,
                                                                int p_randomization )
    {

        m_randomization = p_randomization;

        double[][][] l_payoffMatrix = new double[p_row][p_column][2];

        for( int i=0; i<p_row; i++ )
        {
            for( int j=0; j<p_column; j++ )
            {
                double[] l_utility = utilityCalculationCartoPed( p_self, p_other, i, j );
                l_payoffMatrix[i][j][0] = l_utility[0];
                l_payoffMatrix[i][j][1] = l_utility[1];
            }
        }
        return l_payoffMatrix;

    }


    /*
     * dimension 1 or row = player1; dimension 2 or column = player 2
     * 0 = continue, 1 = decelerate, 2 = deviate
     * third dimension: 0 = player1's utility, 1 = player2's utility
     */
    public static double[][][] payoffMatrixCalculationCartoCar( final IBaseRoadUser p_self, final IBaseRoadUser p_other, int p_randomization )
    {
        m_randomization = p_randomization;

        double l_payoffMatrix[][][] = new double[2][2][2];

        for( int i=0; i<2; i++ )
        {
            for( int j=0; j<2; j++ )
            {
                l_payoffMatrix[i][j][0] = utilityCalculationCartoCar( p_self, p_other, i, j, 0 );
                l_payoffMatrix[i][j][1] = utilityCalculationCartoCar( p_other, p_self, i, j, 1 );
            }
        }

        return l_payoffMatrix;

    }

    //need to make change
    private static double utilityCalculationCartoCar(final IBaseRoadUser p_player, final IBaseRoadUser p_other, final int p_rowstrategyindicator, final int p_columnstrategyindicator, final int p_type )
    {
        if ( p_type == 0 )
        {
            if ( p_rowstrategyindicator == 0 )
            {
                if ( p_columnstrategyindicator == 0 ) return -100;
                else return 2 + speedMeasurementContinue( p_other ) + carFollowingCarContinue( p_other ) + 0 + 0;
            }
            if ( p_rowstrategyindicator == 1 )
            {
                return speedMeasurementStop( p_other ) + carFollowingCarStop( p_other ) + 0 + 0;// + goalnearby( p_player );
            }
        }
        else
        {
            if (  p_columnstrategyindicator == 0 )
            {
                if ( p_rowstrategyindicator == 0 ) return -100;
                else return 2 + speedMeasurementContinue( p_other ) + carFollowingCarContinue( p_other ) + 0 + 0;
            }
            if ( p_columnstrategyindicator == 1 ) return speedMeasurementStop( p_other ) + carFollowingCarStop( p_other ) + 0 + 0;// + goalnearby( p_player ) ;
        }

        return 0;
    }

//add goalnearby for accelaration too later on
    private static double[] utilityCalculationCartoPed( final IBaseRoadUser p_player1, final IBaseRoadUser p_player2, final int p_rowstrategyindicator, final int p_columnstrategyindicator )
    {
        double[] l_return = new double[2];

        {
            setDeviate( p_player1 );
            if ( p_rowstrategyindicator == 0 )
            {
                if ( p_columnstrategyindicator == 0 ) { l_return[0] = -100; l_return[1] = -100; }

                else
                {
                    // car
                    l_return[0] = m_carcontinue + NoSEcarContinue( p_player1 )+ speedMeasurementContinue( p_player2 )+ pedInGroupCarContinue(p_player2)
                            + CarDriverMood() + carAlreadyDecelerateCarContinue( p_player1 )+ carFollowingActiveCarContinue( p_player1 )
                    + pedabletodeviatecarcontinue(p_player2,p_player1) + leaderinWaitingZoneCarContinue( p_player2 );

                    //pedestrian
                    l_return[1] = ( p_columnstrategyindicator == 1 )? m_pedstop + speedMeasurementStop( p_player1 ) + carAlreadyDeceleratePedStop( p_player1 ) //+ goalnearby( p_player2 )
                    : m_peddeviate + pedabletodeviate(p_player2,p_player1) + speedMeasurementdeviate( p_player1 )
                            + carFollowedActivePedDeviate( p_player1 ) + pedGroupContinue(p_player2);// + toomuchdeviation(p_player2,p_player1);
                }
            }

            else
            {
                l_return[0] =( p_columnstrategyindicator == 1 )? -50 : m_carstop + speedMeasurementStop( p_player2 )
                        + carFollowingActiveCarStop( p_player1 ) + carAlreadyDecelerateCarStop( p_player1 )
                        + carunabletostop(p_player1, p_player2) + pedInGroupCarStop( p_player2 );

                l_return[1] = ( p_columnstrategyindicator == 1 )? -50
                        : ( p_columnstrategyindicator == 0 ) ? m_pedcontinue + speedMeasurementContinue( p_player1 ) + carAlreadyDeceleratePedContinue( p_player1 )
                         + carunabletostoppedcontinue(p_player1, p_player2) + pedeasytodeviate(p_player2,p_player1) + pedGroupContinue(p_player2)
                        : m_carstoppeddeviate + pedabletodeviate(p_player2,p_player1)+ speedMeasurementdeviate( p_player1 )
                        + carFollowedActivePedDeviate( p_player1 );// +toomuchdeviation(p_player2,p_player1);

            }

        }
        return l_return;
    }

    private static double pedeasytodeviate(IBaseRoadUser p_ped, IBaseRoadUser p_car)
    {
        double l_angle2 = CForce.getViewAngle( CVector.direction( p_car.getGoalposition(), p_car.getPosition() ).x,
                CVector.direction( p_car.getGoalposition(), p_car.getPosition() ).y,
                ( p_ped.getPosition().x - p_car.getPosition().x), ( p_ped.getPosition().y - p_car.getPosition().y ) );

        if ( ( l_angle2 > 58 && l_angle2 <= 113 ) || ( l_angle2 >= 247 && l_angle2 < 302 ) ) return -4;
        return 0;
    }
}

