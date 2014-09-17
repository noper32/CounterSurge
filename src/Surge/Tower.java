package Surge;

/**
 * Created with IntelliJ IDEA.
 * User: maa
 * Date: 12.08.13
 * Time: 14:28
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
public class Tower {
    private final int WIRES_COUNT=3;
    private Wire[] wires, ropes;
    private Insulation insulator;

    public Tower(Wire[] pWires,Wire[] pRopes,Insulation pInsulator){
        for (int i=0;i<WIRES_COUNT;i++) {
            wires[i] = new Wire(pWires[i]);
        }
        for (int i=0;i<2;i++)
            ropes[i] = new Wire(pRopes[i]);
        insulator = new Insulation(pInsulator);
    }

    public Wire[] getWires(){
        return wires;
    }

    public double getWireMiddleHeight(int pWireIndex){
        return wires[pWireIndex].getMiddleHeight();
    }

    private double FindKRopeWire(double pWireHorDist,double pRopeHorDist, double pWireMiddleHeight, double pRopeMiddleHeight, double pRopeRadius){
        /*Возвращает коэффициент связи провода и троса, о.е.
        pWireHorDist - Расстояние между осью опоры и точкой крепления провода в горизонтальной плоскости
        pRopeHorDist - Расстояние между осью опоры и точкой крепления троса в горизонтальной плоскости
        pWireMiddleHeight - Средняя высота подвеса провода в пролёте
        pRopeMiddleHeight - Средняя высота подвеса троса в пролёте
        pRopeRadius - Радиус троса (с учётом короны или без) */
        double ropeWireHorDist = Math.abs(pWireHorDist-pRopeHorDist);
        double ropeWireD =  Math.hypot(ropeWireHorDist,pWireMiddleHeight+pRopeMiddleHeight);
        double ropeWired = Math.hypot(ropeWireHorDist,pRopeMiddleHeight-pWireMiddleHeight);
        return Math.log(ropeWireD/ropeWired)/Math.log(2*pRopeMiddleHeight/pRopeRadius);}

    private double FindKRopeWire(double pWireHorDist, double pRopeHorDist1, double pRopeHorDist2,double pRopeRopeHorDist, double pWireMiddleHeight, double pRopeMiddleHeight,double pRopeRadius){
        double ropeWireHorDist1 = Math.abs(pWireHorDist-pRopeHorDist1);
        double ropeWireHorDist2 = Math.abs(pWireHorDist-pRopeHorDist2);
        double ropeWireD1 = Math.hypot(ropeWireHorDist1,pWireMiddleHeight+pRopeMiddleHeight);
        double ropeWireD2 = Math.hypot(ropeWireHorDist2,pWireMiddleHeight+pRopeMiddleHeight);
        double ropeRopeD =  Math.hypot(pRopeRopeHorDist,2*pRopeMiddleHeight);
        double ropeWired1 = Math.hypot(ropeWireHorDist1,pRopeMiddleHeight-pWireMiddleHeight);
        double ropeWired2 = Math.hypot(ropeWireHorDist2,pRopeMiddleHeight-pWireMiddleHeight);
        return Math.log(ropeWireD1*ropeWireD2/(ropeWired1*ropeWired2))/Math.log(2*pRopeMiddleHeight*ropeRopeD/(pRopeRadius*pRopeRopeHorDist));
    }
}
