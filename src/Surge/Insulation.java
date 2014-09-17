package Surge;

/**
 * Created with IntelliJ IDEA.
 * User: maa
 * Date: 12.07.13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 *
 * Description: This class is model of insulation 220 kV or lower with length lower than 2m.
 * Higher voltage class modeling required correction of insConstructionFactor and insEFactor.
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
public class Insulation {
    private double insulationDischargeVoltage;
    private double insulationArmLength, dischargeVoltageFactor,insulationLength;

    public  Insulation(Insulation pInsulation){
        insulationArmLength = pInsulation.insulationArmLength;
        insulationDischargeVoltage = pInsulation.getDischargeVoltage();
        dischargeVoltageFactor = pInsulation.getDischargeVoltageFactor();
        insulationLength = pInsulation.getInsulationLength();
    }

    public Insulation(double pInsulatorHeight,double pInsulatorCount, double pInsulationArmLength){
        insulationArmLength = pInsulationArmLength;
        insulationLength = pInsulatorCount*pInsulatorHeight;
        insulationDischargeVoltage = 628.1*Math.pow(insulationLength,0.8591);
        double insConstructionFactor = 1, insEFactor =1;
        dischargeVoltageFactor = 340.0*insConstructionFactor*insEFactor*insulationLength; //Статичный кусок формулы разрядного напряжения
    }

    public double getDischargeVoltage(){
        return insulationDischargeVoltage;
    }
    public double getDischargeVoltage(double pTime){
        // Returns insulation discharge voltage in kV from time in us.
        return dischargeVoltageFactor*(1+15.0/(pTime+9.5));
    }
    public double getDischargeVoltageFactor(){
        return dischargeVoltageFactor; //Need for correct object from object construction
    }
    public double getInsulationLength(){
        return insulationLength;
    }
    public double getInsulationArmLength(){
        return insulationArmLength;
    }
}
