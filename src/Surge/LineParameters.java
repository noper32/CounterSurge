package Surge;

/**
 * Created with IntelliJ IDEA.
 * User: Maxim Antipov
 * Date: 01.07.13
 * Time: 12:04
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
public class LineParameters {
    public static final double LIGHT_SPEED = 299792458;
    public static final double MU0 = 4*Math.PI*Math.pow(10,-7);//Vacuum magnetic permability
    public static final double E0 = 1/(MU0 *Math.pow(LIGHT_SPEED,2));

    public static double FindWireInductance(double pWireHeight, double pWireRadius){
//        Returns Wire inductance
        return (MU0 /(2*Math.PI))*Math.log(2*pWireHeight/pWireRadius);
    }
    public static double FindWireCapacity(double pWireHeight, double pWireRadius){
        return 2*Math.PI*E0/(Math.log(2*pWireHeight/pWireRadius));
    }
    public static double FindAirWireWaveResistance(double pWireHeight, double pWireRadius){
        return 60*Math.log(2*pWireHeight/pWireRadius);
    }
    public static double FindMiddleHeight(double pAnkHeight, double pMinHeight){
        /* Возвращает среднюю высоту подвеса провода по формуле hср=hкр-(2/3)*f, где f=(hкр-hмин) стрела провеса.
            pAnkHeight - максимальная высота подвеса провода
            pMinHeight - минимальная высота провода в пролёте */
        return pAnkHeight - 2*(pAnkHeight-pMinHeight)/3;}

}
