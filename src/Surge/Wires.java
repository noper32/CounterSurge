package Surge;

/**
 * Created with IntelliJ IDEA.
 * User: maa
 * Date: 12.07.13
 * Time: 13:52
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
public class Wires {
    public final int WIRES_COUNT = 3;
    public double[] ankHeight, maxHeight, middleHeight, minHeight;
    public double radius;

    Wires(double[] pAnkHeight, double[] pMaxHeight, double[] pMinHeight, double pWireRadius){
        ankHeight = pAnkHeight;
        maxHeight = pMaxHeight;
        minHeight = pMinHeight;
        radius = pWireRadius;

        for (int i=0;i<ankHeight.length;i++)
            middleHeight[i] = LineParameters.FindMiddleHeight(pMaxHeight[i],pMinHeight[i]);
    }
}