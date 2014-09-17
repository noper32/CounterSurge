package Surge;

/**
 * Created with IntelliJ IDEA.
 * User: maa
 * Date: 12.08.13
 * Time: 14:07
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
public class Wire {
    private double middleHeight,radius, wireX;

    public Wire(double pWireX, double pMaxHeight, double pMinHeight,double pRadius){
        wireX = pWireX;
        middleHeight = FindMiddleHeight(pMaxHeight,pMinHeight);
        radius = pRadius;
    }
    public Wire(Wire pWire){
        wireX = pWire.getX();
        middleHeight = pWire.getMiddleHeight();
        radius = pWire.getRadius();
    }

    public double getX(){
        return wireX;
    }
    public double getMiddleHeight(){
        return middleHeight;
    }
    public double getRadius(){
        return radius;
    }

    private static double FindMiddleHeight(double pAnkHeight, double pMinHeight){
        /* Возвращает среднюю высоту подвеса провода по формуле hср=hкр-(2/3)*f, где f=(hкр-hмин) стрела провеса.
            pAnkHeight - максимальная высота подвеса провода
            pMinHeight - минимальная высота провода в пролёте */
        return pAnkHeight - 2*(pAnkHeight-pMinHeight)/3;}
}
