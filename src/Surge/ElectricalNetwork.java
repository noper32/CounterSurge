package Surge;

/**
 * Created with IntelliJ IDEA.
 * User: maa
 * Date: 06.08.13
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 LICENSE:
 This file is part of Counter Surge.

 Counter Surge is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
public class ElectricalNetwork {
    public final int PHASE_COUNT = 3;
    final double PHASE_STEP = Math.toRadians(120);
    private static int[] voltage ={6,10,20,35,110,220,330,500,750,1150};
    private static double[] maxVoltages = {7.2,12,24,40.5,126,252,363,525,787,1200};
    private int frequency, voltageClassIndex;
    private double cyclicFrequency,period,phaseAmplitude;


    private void initializer(int pFrequency, int pVoltageClass){
        frequency = pFrequency;
        cyclicFrequency =  2*Math.PI*frequency;
        period = 1.0/frequency;
        for (int i=0;i< voltage.length;i++){
            if (pVoltageClass==voltage[i]){
                voltageClassIndex=i;
                break;
            }
        }
        phaseAmplitude=maxVoltages[voltageClassIndex]*Math.sqrt(2.0/3.0);
    }

    public ElectricalNetwork(ElectricalNetwork pElectricalNetwork){
        initializer(pElectricalNetwork.getFrequency(),pElectricalNetwork.getVoltageClass());
    }

    public ElectricalNetwork(int pFrequency,int pVoltageClass){
       initializer(pFrequency,pVoltageClass);
    }
    public int getFrequency(){
        return frequency;
    }

    public int getVoltageClass(){
        return voltage[voltageClassIndex];
    }

    public static String[] getVoltageClasses(){
        String s[]=new String[voltage.length];
        for (int i=0;i<voltage.length;i++){
            s[i]=String.valueOf(voltage[i]);
        }
        return s;
    }

    public double getMaxLineVoltage(){
        return maxVoltages[voltageClassIndex];
    }

    public double getPeriod(){
        return period;
    }

    public static int getVoltageClassIndex(int pVoltageClass){
        int index;
        for (index=0;index<voltage.length;index++){
            if (pVoltageClass==voltage[index])
                break;
        }
        return index;
    }

    public double[] getThreePhaseVoltage(double pTime){
        double[] voltage = new double[PHASE_COUNT];
        for (int i=0;i<PHASE_COUNT;i++)
            voltage[i] = phaseAmplitude*Math.sin(cyclicFrequency*pTime+PHASE_STEP*i);
        return voltage;
    }
}
