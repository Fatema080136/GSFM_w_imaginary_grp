package org.mysimulationmodel;

//import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

import com.mysql.cj.xdevapi.Statement;
import org.apache.commons.math3.util.Precision;
import org.mysimulationmodel.simulation.common.CCSVFileReaderForGA;
import org.mysimulationmodel.simulation.common.CSampleOutput;

import javax.json.JsonObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

public class Genetic_Algorithm
{
    static double crossRate = .85;
    static double mutRate = .001;
    static Random rand = new Random();
    static int populationSize = 70;
    static int chromolength=14;
    //static Map< String, ArrayList<String>> m_realdata = CCSVFileReaderForGA.readDataFromCSV();

   /* public static void main(String[] args)
    {
        /*ArrayList<Double> l_totalfitness = new ArrayList();

        for ( int i = 1; i<= 3; i=i+2 )
        {
            ArrayList<Double> l_scenariofitness = new ArrayList();
            ArrayList<CSampleOutput> eachScenarioResult = CMain.runSimulation(1, 3, 3, 0.2,
                    1.4, 0.4, 5, 7,
                    8, 3,14,14, 7, 2, i);
            //System.out.println( "whattt "+ eachScenarioResult.size());
        for (int j=0;j<=eachScenarioResult.size()-1;j++)
        {
            double simulatedX= eachScenarioResult.get(j).m_selfX;
            double simulatedY= eachScenarioResult.get(j).m_selfY;
            double realX = -1;
            double realY = -1;

            ArrayList<String> l_temp = m_realdata.get( new StringBuffer(String.valueOf(eachScenarioResult.get(j).m_timestep))//.replaceAll(".0", "")
                            .append(eachScenarioResult.get(j).m_id.replaceAll("\"","")).toString());
            //System.out.println( "nulllll "+ l_temp );
            if( l_temp != null )
            {
                realX = Double.parseDouble( l_temp.get(0) );
                realY = Double.parseDouble( l_temp.get(1) );
            }
            if ( realX >= 0 && realY >= 0)
            {
                double xDiff = realX - simulatedX;
                double xSqr = Math.pow(xDiff, 2);

                double yDiff = realY - simulatedY;
                double ySqr = Math.pow(yDiff, 2);

                double distance = Math.sqrt(xSqr + ySqr);
                System.out.println(eachScenarioResult.get(j).m_timestep+" t "+eachScenarioResult.get(j).m_id+" dis "+distance);
                l_scenariofitness.add(distance);
                //if( distance != 0.0 ) l_scenariofitness.add( (double)(1 /(float)distance) );
            }
        }
            l_totalfitness.add( average(l_scenariofitness) );
        }
        System.out.println(average( l_totalfitness ));
    }*/

/*

        long startTime = System.nanoTime();
        //Data Map
        ArrayList<chromosome> population;
        ArrayList<chromosome> newPopulation=new ArrayList();
        int gen=0;

        Genetic_Algorithm ga=new Genetic_Algorithm();
        population=ga.generate_Population();
//        System.out.println(ga.getFitnessScore(3,5,10,11));

        // Loop until solution is found
        while(true) {
            // Clear the new pool
            newPopulation.clear();

            // Add to the generations
            gen++;

            // Loop until the pool has been processed
            for(int x=population.size()-1;x>=0;x-=2)
            {
                // Select two members
                chromosome n1 = ga.selectMember(population);
                chromosome n2 = ga.selectMember(population);

                // Cross over and mutate
                n1.crossOver(n2);
                n1.mutate();
                n2.mutate();

                // Rescore the nodes
                try {
                    n1.getFitnessScore();
                    n2.getFitnessScore();
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                // Check to see if either is the solution

                // Add to the new pool
                newPopulation.add(n1);
                newPopulation.add(n2);

            }

            // Add the newPool back to the old pool
            population.addAll(newPopulation);

            if (gen==30) {
                Collections.sort(population);
                population.stream().forEach(g -> System.out.println(g.chromo.toString() +"chromo " +g.score));
                break;
            }


        }
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println( totalTime );

    }

    public ArrayList generate_Population(){
        ArrayList<chromosome> population = new ArrayList<chromosome>();
        for (int i=0; i<populationSize;i++) {
            population.add(new chromosome());
        }

        return population;
    }

    private chromosome selectMember(ArrayList l) {

        // Get the total fitness
        double tot=0.0;
        for (int x=l.size()-1;x>=0;x--) {
            double score = ((chromosome)l.get(x)).score;
            tot+=score;
        }
        double slice = tot*rand.nextDouble();

        // Loop to find the node
        double ttot=0.0;
        for (int x=l.size()-1;x>=0;x--) {
            chromosome node = (chromosome)l.get(x);
            ttot+=node.score;
            if (ttot>=slice) { l.remove(x); return node; }
        }

        return (chromosome)l.remove(l.size()-1);
    }

    //    responsible for creating each chromosome and
    private class chromosome implements Comparable<chromosome>{
        ArrayList<Double> chromo=new ArrayList<Double>();
        double score=0;
        public chromosome(){
            for (int j = 0; j < chromolength; j++) {
                chromo.add(getRandomGene());

            }
//                get the simulated and real position (X,Y) of the car of pedestrian. Use the values to find the scrore
//                Now I am using a dummy value.
            try {
                this.score=getFitnessScore();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        private double getRandomGene(){
            Random ran= new Random();
            return Precision.round(ran.nextDouble(),1)+ran.nextInt(21);
        }

        // Crossover bits
        public final void crossOver(chromosome other) {

            // Should we cross over?
            if (rand.nextDouble() > crossRate) return;

            // Generate a random position
            int pos = rand.nextInt(chromolength);

            // Swap all chars after that position
            for (int x=pos;x<chromolength;x++) {
                // Get our character
                double tmp = chromo.get(x);

                // Swap the chars
                chromo.set(x, other.chromo.get(x));
                other.chromo.set(x, tmp);
            }
        }

        // Mutation
        public final void mutate() {
            for (int x=0;x<chromolength;x++) {
                if (rand.nextDouble()<=mutRate)
                    chromo.set(x, rand.nextDouble()+rand.nextInt(11));
            }
        }

        public double average( ArrayList<Double> p_list )
        {
            double actualTotal = 0;
            for( int i = 1; i<= p_list.size()-1; i++)
            {
                actualTotal = actualTotal + p_list.get(i);
            }

            return actualTotal/(float)p_list.size();
        }

        //For fitness input chromosome and scenario, and output json object contains scenario->time->p/c->position
        public double getFitnessScore() throws SQLException
        {

            ArrayList<Double> l_totalfitness = new ArrayList();

            //simulation running for each scenario sequentially
            IntStream.range( 1, 51)
                   .parallel()
                    .forEach( i ->
            //for ( int i = 1; i<= 40; i++ )
            {    //20 parameters + scenario id
                System.out.println( "scenario id " + i );

                ArrayList<CSampleOutput> eachScenarioResult = CMain.runSimulation(this.chromo.get(0),this.chromo.get(1), this.chromo.get(2),
                        this.chromo.get(3),this.chromo.get(4), this.chromo.get(5),this.chromo.get(6),this.chromo.get(7),
                        this.chromo.get(8),this.chromo.get(9), this.chromo.get(10),this.chromo.get(11),this.chromo.get(12),
                        this.chromo.get(13), i);

                ArrayList<Double> l_scenariofitness = new ArrayList();

                for (int j=0;j<=eachScenarioResult.size()-1;j++)
                {
                    double simulatedX= eachScenarioResult.get(j).m_selfX;
                    double simulatedY= eachScenarioResult.get(j).m_selfY;
                    //double simulatedX= Double.parseDouble( eachScenarioResult.get(j).get("x_axis").toString() );
                    //double simulatedY= Double.parseDouble( eachScenarioResult.get(j).get("y_axis").toString() );
                    double realX = -1;
                    double realY = -1;

                    ArrayList<String> l_temp = //m_realdata.get( new StringBuffer(eachScenarioResult.get(j).get("id").toString().replaceAll("\"",""))
                            //.append(eachScenarioResult.get(j).get("timestep").toString().replaceAll(".0", "")).toString());
                            m_realdata.get( new StringBuffer(String.valueOf(eachScenarioResult.get(j).m_timestep))//.replaceAll(".0", "")
                            .append(eachScenarioResult.get(j).m_id.replaceAll("\"","")).toString());

                    if( l_temp != null )
                    {
                        realX = Double.parseDouble( l_temp.get(0) );
                        realY = Double.parseDouble( l_temp.get(1) );
                    }
                    if ( realX >= 0 && realY >= 0)
                    {
                        double xDiff = realX - simulatedX;
                        double xSqr = Math.pow(xDiff, 2);

                        double yDiff = realY - simulatedY;
                        double ySqr = Math.pow(yDiff, 2);

                        double distance = Math.sqrt(xSqr + ySqr);

                        if( distance != 0.0 ) l_scenariofitness.add( (double)(1 /(float)distance) );
                    }
                }

                l_totalfitness.add( average( l_scenariofitness));
                //l_totalfitness.add( l_scenariofitness.stream().mapToDouble(n -> n).average().getAsDouble() );

                //calculate fitness

            });
            return average( l_totalfitness );
            //return l_totalfitness.stream().mapToDouble(n -> n).average().getAsDouble();
        }

        @Override
        public int compareTo(chromosome o)
        {
            if(this.score>o.score)
                return 1;
            else
                return -1;
        }

    }*/

    /*public static double average( ArrayList<Double> p_list )
    {
        double actualTotal = 0;
        for( int i = 1; i<= p_list.size()-1; i++)
        {
            actualTotal = actualTotal + p_list.get(i);
        }

        return actualTotal/(float)p_list.size();
    }*/
}
