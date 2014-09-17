package Surge;

/**
 * Created with IntelliJ IDEA.
 * User: Maxim Antipov
 * Date: 10.07.13
 * Time: 15:23
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
public class DischargeRegistrator {
    private boolean First=false;
    private double Pi, Pai,Psum=0, CurrentMin,TowerHeight;

    public void setData(double pTowerHeight,double pCurrentMin){
        TowerHeight = pTowerHeight;
        CurrentMin = pCurrentMin;
    }

    public void setFirstDischarge(){
        First = true;
    }

    public void addDischarge(double pAlpha, double pSurgeCurrent){
        if (First){
            Pai=FindPai(pAlpha);///Fixed
            Pi=PIIntegral(TowerHeight,CurrentMin,pSurgeCurrent);
            First=false;}
        else{
            double PaiCurrent = FindPai(pAlpha);
            Psum+=Pi*(Pai-PaiCurrent);//Fixed
            Pi=PIIntegral(TowerHeight,CurrentMin,pSurgeCurrent);
            Pai=PaiCurrent;}//Fixed
    }
    public double getPSum(){
        return Psum;
    }

    public double getPi(){
        // Для получения промежуточных данных
        return Pi;
    }

    public double getPai(){
        // Для получения промежуточных данных
        return Pai;
    }

    private double FindPai(double pAlpha){
        return Math.exp(-0.08*pAlpha);
    }

    public static double PIIntegral(double pTowerHeight, double currentMin, double current){
        /*Вероятность тока молнии, превышающей значение current
            pTowerHeight - высота опоры, м
            currentMin - минимальный ток молнии, кА
            current - значение тока вероятность превышения которого нужно определить, кА
            iterations - количество разбиения интервала интегрирования средними прямоугольниками */
        final int ITERATIONS = 100000;
        double currenth, sigma;
        if (pTowerHeight <= 20){
            currenth = 20;
            sigma = 0.39;}
        else{
            currenth = 20 + 0.32 * (pTowerHeight - 20);
            sigma = 0.39 - 0.0028 * (pTowerHeight - 20);}
        double delta = (current-currentMin)/ITERATIONS;
        double currenti = currentMin;
        double res = 0;
        for (int i=1;i<=ITERATIONS;i++){
            double currentLoop = (currenti - delta/2.0);
            double forPow = (Math.log10(currentLoop) - Math.log10(currenth) ) / sigma;
            res += (1.0/currentLoop)*Math.exp(-0.5 * Math.pow (forPow,2));
            currenti+=delta;}
        return 1-(Math.log10(Math.E)/(Math.sqrt(2.0*Math.PI)*sigma))*res*delta;}
}