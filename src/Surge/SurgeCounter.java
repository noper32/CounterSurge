package Surge;

import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: Maxim Antipov
 * Date: 13.06.13
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 Description: SurgeCounter encounts surge discharge to tower.
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

public class SurgeCounter extends Observable implements Runnable {
    private static final double CURRENT_MIN = 3; //кА
    private static final int WIRES_COUNT = 3;
    private static final double LIGHT_SPEED = LineParameters.LIGHT_SPEED/1e6; // м/мкс
    private static final double BETA = 0.3;
    private static final double MAIN_DISCHRGE_SPEED = BETA*LIGHT_SPEED;
    private static final double TIME_MIN = 0.7;

    private double gndResistance, groundResistivity, hSize;   //Сопротивление заземления опоры и удельное сопротивление грунта, Ом
    private double ropeAnkHeight, ropeMiddleHeight, ropeRadius, ropeInductance, ropeZGeo, ropeRopeHorDist; //Средняя высота и радиус троса,
    double wireAnkHeight[] = new double[WIRES_COUNT],  wireMiddleHeight[] = new double[WIRES_COUNT], ropeWireHeight[] = new double[WIRES_COUNT];
    private double tHalfSpan;   // Половина времени пробега пролёта, мкс?
    private double towerHeight, kRopeTower,kWireTower; //Высота опоры, м
    private double N, powerArcFactor;   // Число ударов молнии на 100 км длинны линии.
    private double spanLength;
    //----------------------------------------------------------//
    private double timeStep = 0.01; // Шаг расчёта по времени, мкс
    private double timeMax = 10;    // Максимальное время расчёта, мкс
    private double alphaStep = 1;   // Шаг изменения крутизны тока молнии, кА/мкс
    private double alphaMax = 72;  // Максимальное значение крутизны тока молнии, кА/мкс
    private double industrialVoltageStep = 0.002; // Шаг времени напряжения промышленной частоты, с
    private Insulation insulator;
    private ElectricalNetwork ElNet;
    double ropeTowerInductance;
    boolean doubleLine;
    int higestWireIndex=0;
    double wireHorDist[];
    double ropeHorDist , ropeHorDist2 ,wireRadius, wireZCorone;
    boolean useCorone;
    double result[] = new double[3];
    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        result[0]=SurgeToWire(ropeWireHeight[higestWireIndex],ropeAnkHeight,ropeHorDist,wireAnkHeight[higestWireIndex],wireMiddleHeight[higestWireIndex],wireHorDist[higestWireIndex],wireRadius,wireZCorone);
        result[1]=SurgeToTower(wireAnkHeight,ropeAnkHeight,ropeWireHeight,ropeTowerInductance,ropeInductance,wireHorDist,ropeHorDist,ropeHorDist2,wireMiddleHeight,ropeMiddleHeight,useCorone);
        result[2]=SurgeToRope(ropeHorDist,ropeHorDist2,ropeMiddleHeight,ropeRadius,1,wireHorDist,wireAnkHeight,wireMiddleHeight,ropeInductance,ropeTowerInductance,useCorone);
        setChanged();
        notifyObservers();
    }

    public double[] getResult(){
        return result;
    }

    public SurgeCounter(Insulation pInsulator, ElectricalNetwork pElNet, double pSurgeDensityGND, double pSpanLength, double pTowerHeight, double pWireRadius, double pWireHorDist[], double pWireInsAnkHeight[], double pWireMinHeight[], double pRopeRadius, double pRopeHorDist, double pRopeAnkHeight, double pRopeMinHeight, double pGNDResistance, double pGNDResistivity, double phSize, double pkRopeTower, double pkWireTower, double pWireZCorone, double pRopeRopeHorDist, boolean pUseCorone, double pRopeHorDist2, boolean pDoubleLine){
        insulator = new Insulation(pInsulator);
        ElNet = new ElectricalNetwork(pElNet);
        doubleLine = pDoubleLine;
        towerHeight = pTowerHeight;
        groundResistivity = pGNDResistivity;
        ropeMiddleHeight=LineParameters.FindMiddleHeight(pRopeAnkHeight,pRopeMinHeight);
        gndResistance = pGNDResistance;
        groundResistivity = pGNDResistivity;
        ropeRadius = pRopeRadius;
        ropeAnkHeight = pRopeAnkHeight;
        ropeRopeHorDist = pRopeRopeHorDist;
        hSize = phSize;
        spanLength = pSpanLength;
        tHalfSpan = pSpanLength/(2.0*LIGHT_SPEED);
        ropeZGeo = LineParameters.FindAirWireWaveResistance(ropeMiddleHeight,ropeRadius);
        ropeInductance = LineParameters.FindWireInductance(ropeMiddleHeight,pRopeRadius)*pSpanLength*1e6;//Гн
        N = TotalSurgeCount(pSurgeDensityGND,0);
        kRopeTower = pkRopeTower;
        kWireTower = pkWireTower;
        powerArcFactor=FindPowerArcFactor(ElNet.getMaxLineVoltage(),insulator.getInsulationLength());
        wireHorDist = pWireHorDist;
        ropeHorDist = pRopeHorDist;
        ropeHorDist2 = pRopeHorDist2;
        useCorone = pUseCorone;
        wireRadius = pWireRadius;
        wireZCorone = pWireZCorone;
        for(int i=0;i < WIRES_COUNT;i++){
            wireAnkHeight[i] = pWireInsAnkHeight[i] - insulator.getInsulationArmLength();
            ropeWireHeight[i]=pRopeAnkHeight-wireAnkHeight[i];
            wireMiddleHeight[i]=LineParameters.FindMiddleHeight(wireAnkHeight[i],pWireMinHeight[i]);
        }

        double buff = 0.0;
        for (int i=0;i<WIRES_COUNT;i++){
            if (wireAnkHeight[i]> buff)
                buff = wireAnkHeight[i];
        }
        for (int i=0;i<WIRES_COUNT;i++){
            if (wireAnkHeight[i]==buff)
                higestWireIndex=i;
        }
        ropeTowerInductance = kRopeTower * ropeAnkHeight;
        Thread thread = new Thread(this);
        thread.start();
    }

    public double[] execute(Insulation pInsulator, ElectricalNetwork pElNet, double pSurgeDensityGND, double pSpanLength, double pTowerHeight, double pWireRadius, double pWireHorDist[], double pWireInsAnkHeight[], double pWireMinHeight[], double pRopeRadius, double pRopeHorDist, double pRopeAnkHeight, double pRopeMinHeight, double pGNDResistance, double pGNDResistivity, double phSize, double pkRopeTower, double pkWireTower, double pWireZCorone, double pRopeRopeHorDist, boolean pUseCorone, double pRopeHorDist2, boolean pDoubleLine){
        result[0]=SurgeToWire(ropeWireHeight[higestWireIndex],ropeAnkHeight,pRopeHorDist,wireAnkHeight[higestWireIndex],wireMiddleHeight[higestWireIndex],pWireHorDist[higestWireIndex],pWireRadius,pWireZCorone);
        result[1]=SurgeToTower(wireAnkHeight,ropeAnkHeight,ropeWireHeight,ropeTowerInductance,ropeInductance,pWireHorDist,pRopeHorDist,pRopeHorDist2,wireMiddleHeight,ropeMiddleHeight,pUseCorone);
        result[2]=SurgeToRope(pRopeHorDist,pRopeHorDist2,ropeMiddleHeight,ropeRadius,1,pWireHorDist,wireAnkHeight,wireMiddleHeight,ropeInductance,ropeTowerInductance,pUseCorone);
        return result;
    }

    public double SurgeToWire(double pRopeWireHeight, double pRopeAnkHeight,double pRopeHorDist ,double pWireAnkHeight, double pWireMiddleHeight, double pWireHorDist, double pWireRadius, double pWireZCorone){
        /* Возвращает удельное число отключений линии от прорывов молнии через тросовую защиту.
         dischargeGapLength - Длинна разрядного промежутка, м.
         towerHeight - Высота опоры, м.
         ropeWaveResistance - Волновое сопротивление троса, Ом.
         voltageClass - Класс напряжения линии, кВ.
         wireAnkHeight - Высота подвеса провода на опоре (низ гирлянды), м.
         pWireMiddleHeight - Средняя высота провода, м.
         wireRadius - Радиус провода, м.
         ropeAnkHeight - Высота подвеса троса, м.
         pRopeWireHeight - Разность высот подвеса провода и троса на опоре, м.
         ropeWireHorDist - Смещение провода и троса по горизонтали, м.
         currentMin - Минимальный ток молнии, кА.
         N - Число ударов молнии на 100 км линии.*/
       // double insulationDischargeVoltage = 628.1*Math.pow(insulationLength,0.8591);
       // double pWireZCorone = 410; // Debug mode only!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        double ropeWireHorDist = Math.abs(pRopeHorDist - pWireHorDist);
        double current = 2*insulator.getDischargeVoltage()/pWireZCorone;
        double Pi = DischargeRegistrator.PIIntegral(towerHeight,CURRENT_MIN,current);
       // System.out.println("Surge to wire I="+current+" Pi="+Pi);
        double Q0= Math.pow(200,(1+0.1*ElNet.getVoltageClass()/1000));
        double Q=ElNet.getVoltageClass()/(Math.log(2*pWireMiddleHeight/pWireRadius));
        double Du=1+(ElNet.getVoltageClass()/(100*pRopeWireHeight))*Math.pow((0.75*pWireAnkHeight*Q/(pRopeWireHeight*(Q+Q0))),3);
        double alpha = Math.exp((3.2*(ropeWireHorDist/pRopeWireHeight)*Math.sqrt(pRopeAnkHeight/pRopeWireHeight)-9/Du)/Du);
      //  System.out.print("Unit 1 says Palpha="+alpha);
        double ret=N * alpha * Pi * powerArcFactor;
        if (doubleLine)
            ret/= 2;
        return ret;}

    public double SurgeToTower(double pWireAnkHeight[], double pRopeAnkHeight, double pRopeWireHeight[], double pRopeTowerInductance, double pRopeInductance,double pWireHorDist[], double pRopeHorDist, double pRopeHorDist2, double pWireMiddleHeight[], double pRopeMiddleHeight ,boolean pUseCorone){
        /* Возвращает число отключений линии от ударов молнии в опору.
            pWireAnkHeight - Высота подвеса провода на опоре, м
            pRopeAnkHeight - Высота подвеса троаса на опоре, м
            pRopeWireHeight - Вертикальное расстояение между точками подвеса провода и троса, м
           pRopeTowerInductance - Индуктивность опоры до точки подвеса троса
           pRopeInductance - Индуктивность троса
           kRopeWire - Коэффициент
           pWireHorDist - расстояние от провода до стойки опоры
           pRopeHorDist - расстояние от троса до стойки опоры
           pWireMiddleHeight - Средняя высота подвеса провода
           pRopeMiddleHeight - Средняя высота подвеса троса*/
        double tIndustrial=0;
        //double U=0;
        double[] H = new double[WIRES_COUNT];
        double[] wireTowerInductance = new double[WIRES_COUNT];
        DischargeRegistrator Discharge = new DischargeRegistrator();
        Discharge.setData(towerHeight,CURRENT_MIN);
        for (int i=0;i<WIRES_COUNT;i++){
            wireTowerInductance[i] = kWireTower*pWireAnkHeight[i];
            H[i] = pRopeAnkHeight + pWireAnkHeight[i];
        }

        do{
            double [] vIndustrial = ElNet.getThreePhaseVoltage(tIndustrial);
            double alpha = alphaStep;
            Discharge.setFirstDischarge();
            do{
                double towerCurrent = 0, ropeCurrent = 0, time = 0;
                do{
                    double R = gndResistance;//FindSparkingResistance(groundResistivity, gndResistance, hSize, towerCurrent);
                    double surgeCurrent = alpha*time;
                    double mRope = 0.2 * pRopeAnkHeight * (Math.log((MAIN_DISCHRGE_SPEED*time+2*pRopeAnkHeight)/((1+BETA)*2.0*pRopeAnkHeight))+1);
                    double diRopedt;
                    if (time < tHalfSpan)
                        diRopedt = (alpha * (mRope + pRopeTowerInductance)+surgeCurrent*R-2.0*ropeCurrent*(ropeZGeo/2.0+R))/(2.0*pRopeTowerInductance);
                    else
                        diRopedt = (alpha*(mRope+pRopeTowerInductance)+surgeCurrent*R-3.0*ropeCurrent*R)/(pRopeInductance+3.0*pRopeTowerInductance);
                    double diTowerdt=DiTowerdt(alpha,diRopedt);
                    ropeCurrent += diRopedt * timeStep;
                    towerCurrent += diTowerdt * timeStep;
                    double vR = towerCurrent * R;
                    double[] mWire = new double[WIRES_COUNT];
                    double[] kRopeWire = new double[WIRES_COUNT];
                    double[] vInductMagnet = new double[WIRES_COUNT];
                    double[] vInductE = new double[WIRES_COUNT];
                    double[] vInRope = new double[WIRES_COUNT];
                    boolean discharge = false;
                    double uDischarge = insulator.getDischargeVoltage(time);
                    double ropeCoroneRadius = ropeRadius;
                    if (pUseCorone){
                        double zRope = LineParameters.FindAirWireWaveResistance(ropeMiddleHeight,ropeRadius);
                        double zTower = Math.sqrt(Math.pow(R,2) + Math.pow((ropeTowerInductance*diTowerdt),2));
                        double vRope = vR + diTowerdt*ropeTowerInductance;// alpha * tHalfSpan * (zTower*zRope/2.0)/(zTower+zRope/2.0);
                        ropeCoroneRadius = getCoroneRadius(ropeRadius,1,ropeMiddleHeight,vRope);//Works
                    }
                    for (int i=0;i<WIRES_COUNT;i++){
                        mWire[i] = 0.2 * pWireAnkHeight[i] * (Math.log((MAIN_DISCHRGE_SPEED*time+H[i])/((1+BETA)*H[i]))-pRopeWireHeight[i]/(2.0*pWireAnkHeight[i])*Math.log(H[i]/pRopeWireHeight[i])+1.0);
                        if (ropeRopeHorDist==0)
                            kRopeWire[i] = FindKRopeWire(pWireHorDist[i],pRopeHorDist,pWireMiddleHeight[i],pRopeMiddleHeight,ropeCoroneRadius);
                        else
                            kRopeWire[i]=FindKRopeWire(pWireHorDist[i],pRopeHorDist,pRopeHorDist2,ropeRopeHorDist,pWireMiddleHeight[i],pRopeMiddleHeight,ropeCoroneRadius);
                        vInductMagnet[i] = wireTowerInductance[i]*diTowerdt+alpha*mWire[i];
                        vInductE[i] = (alpha*0.1*pWireAnkHeight[i]/BETA)*(1-kRopeWire[i]*pRopeAnkHeight/pWireAnkHeight[i])*Math.log(((MAIN_DISCHRGE_SPEED*time+pRopeAnkHeight)*Math.sqrt((MAIN_DISCHRGE_SPEED*time+H[i])*(MAIN_DISCHRGE_SPEED*time+pRopeWireHeight[i])))/(Math.pow((1+BETA),2)*pRopeAnkHeight*Math.sqrt(pRopeWireHeight[i]*H[i])));
                        vInRope[i] = kRopeWire[i]*(vR+pRopeTowerInductance*diTowerdt+alpha*mRope);
                        if (uDischarge<=vR+vInductMagnet[i]+vInductE[i]-vInRope[i]+vIndustrial[i]){
                            discharge = true;
                            //U=vIndustrial[i];
                        }
                    }
                    if ((time >=TIME_MIN)&(time<=timeMax)&discharge){    //insulation discharge voltage wrong!!!
                        Discharge.addDischarge(alpha,surgeCurrent);
                     /*   System.out.print("Surge to tower U=");
                        System.out.format("%-10.3f",U);
                        System.out.print(" I=");
                        System.out.format("%-10.3f",surgeCurrent);
                        System.out.print(" Tfront=");
                        System.out.format("%-10.3f",time);
                        System.out.print(" Pi=");
                        System.out.format("%-10.3f",Discharge.getPi());
                        System.out.print(" Pai=");
                        System.out.format("%-10.3f%n",Discharge.getPai());
                            */
                        break;}
                    time+=timeStep;
                }while (time <= timeMax);
                alpha +=alphaStep;
            }while (alpha <= alphaMax);
            tIndustrial+=industrialVoltageStep;
        }while (tIndustrial<=ElNet.getPeriod());
        return Discharge.getPSum()*4*N*(ropeAnkHeight/spanLength)*powerArcFactor/(ElNet.getPeriod()/industrialVoltageStep);}

    public double SurgeToRope(double pRopeHorDist,double pRopeHorDist2, double pRopeMiddleHeight, double pRopeRadius,double pRopeN, double pWireHorDist[],double pWireAnkHeight[], double pWireMiddleHeight[], double pRopeInductance, double pRopeTowerInductance,boolean pUseCorone){
        /* Возвращает число отключений линии от удара молнии в трос
          pRopeHorDist - Горизонтальное расстояние от оси опоры до точки подвеса троса, м
          pRopeAnkHeight - Высота подвеса троса, м
          pRopeMiddleHeight - Средняя высота троса в пролёте, м
          pRopeRadius - Радиус троса, м
          pRopeN - Количество составляющих в тросе, шт
          pWireHorDist - Горизонтальное расстояние от оси опорыдо точки подвеса провода, м
          pWireAnkHeight - Высота подвеса провода, м
          pWireMiddleHeight - Средняя высота провода в пролёте, м
          pRopeInductance - Индуктивность троса
          pRopeTowerInductance - Индуктивность опоры до точки подвеса троса, м */
        double w0=Math.exp(Math.log(2*pRopeMiddleHeight/pRopeRadius));/// Выпилить логарфифм с экспонентой
        double [] towerWireInductance = new double [WIRES_COUNT];
        DischargeRegistrator Discharge = new DischargeRegistrator();
        Discharge.setData(towerHeight,CURRENT_MIN);
        for (int i=0;i<WIRES_COUNT;i++)
            towerWireInductance[i] = kWireTower * pWireAnkHeight[i];
        double tIndustrial=0;
        double U=0;
        do{
            double [] vIndustrial = ElNet.getThreePhaseVoltage(tIndustrial);
            double alpha = alphaStep;
            Discharge.setFirstDischarge();
            do{
                double ropeCurrent = 0, surgeCurrent=0, time = 0, towerCurrent=0;
                do{
                    double R = gndResistance; //FindSparkingResistance(groundResistivity, gndResistance, hSize, towerCurrent);
                    double ropeCoroneRadius = ropeRadius;
                    if (pUseCorone){
                        double vRope = alpha*tHalfSpan*ropeZGeo/2;
                        ropeCoroneRadius = getCoroneRadius(ropeRadius,1,ropeMiddleHeight,vRope);  ///DISABLED IN DEBUG MODE CORONE RADIUS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    }
                    double wk=Math.exp(Math.sqrt(Math.log(2*pRopeMiddleHeight/ropeRadius)*Math.log(2*pRopeMiddleHeight/ropeCoroneRadius)));
                    double deltati = tHalfSpan *Math.sqrt((pRopeN*Math.log(w0)/(pRopeN*Math.log(wk)-1))-1);
                    double alphaR = alpha*tHalfSpan/(tHalfSpan+deltati);
                    //ropeCoroneRadius = ropeRadius;///DISABLED IN DEBUG MODE CORONE RADIUS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! //Корректируем радиус короны с учётом alphaR
                    double diRopedt = alphaR* ropeTowerInductance/(2.0*(pRopeInductance+pRopeTowerInductance))+alphaR*time*R/(2.0*(pRopeInductance+pRopeTowerInductance))-ropeCurrent*R/(pRopeInductance+pRopeTowerInductance);   ////Changed L rope to L tower
                    double diTowerdt = alphaR/2.0 - diRopedt;
                    ropeCurrent += diRopedt*timeStep; //Changed from Time to TimeStep
                    surgeCurrent += alphaR*timeStep;  //Changed from Time to TimeStep
                    towerCurrent = surgeCurrent/2.0 - ropeCurrent;
                    double uR=towerCurrent*R;
                    double[] kr = new double[WIRES_COUNT];
                    double[] uInductMagnet = new double[WIRES_COUNT];
                    double uInductRope[] = new double[WIRES_COUNT];
                    double uWire[] = new double[WIRES_COUNT];
                    double uDischarge = insulator.getDischargeVoltage(time);
                    boolean discharge = false;
                    for (int i=0;i<WIRES_COUNT;i++){
                        if (ropeRopeHorDist==0)
                            kr[i] = FindKRopeWire(pWireHorDist[i],pRopeHorDist,pWireMiddleHeight[i],pRopeMiddleHeight,ropeCoroneRadius);
                        else
                            kr[i]=FindKRopeWire(pWireHorDist[i],pRopeHorDist,pRopeHorDist2,ropeRopeHorDist,pWireMiddleHeight[i],pRopeMiddleHeight,ropeCoroneRadius);
                        uInductMagnet[i] = diTowerdt* towerWireInductance[i];
                        uInductRope[i] = kr[i]*(uR+uInductMagnet[i]*ropeTowerInductance/towerWireInductance[i]);
                        uWire[i] = uR+uInductMagnet[i]-uInductRope[i]+vIndustrial[i];
                        U=vIndustrial[i];
                        if (uDischarge<=uWire[i]){
                            discharge = true;
                        }
                    }
                    if ((time >=TIME_MIN)&(time<=timeMax)&discharge){
                      Discharge.addDischarge(alphaR,surgeCurrent);
                        /*
                        System.out.print("Surge to rope U=");
                        System.out.format("%-10.3f",U);
                        System.out.print(" I=");
                        System.out.format("%-10.3f",surgeCurrent);
                        System.out.print(" Tfront=");
                        System.out.format("%-10.3f",time);
                        System.out.print(" Pi=");
                        System.out.format("%-10.3f",Discharge.getPi());
                        System.out.print(" Pai=");
                        System.out.format("%-10.3f%n",Discharge.getPai());
                          */
                        break;}
                    time+=timeStep;
                }while (time<=timeMax);
                alpha +=alphaStep;
            }while (alpha<=alphaMax);
            tIndustrial+=industrialVoltageStep;
        }while (tIndustrial<=ElNet.getPeriod());
        return Discharge.getPSum()*(N-4*N*(ropeAnkHeight/spanLength))*powerArcFactor/(ElNet.getPeriod()/industrialVoltageStep);}

   // private double FindSparkingResistance(double ro, double groundResistance, double S, double towerCurrent){
      /* Корректирует сопротивление с учётом искрообразования в грунте
      ro - удельное сопротивление грунта.
      groundResistance - Сопротивление заземления опоры
      S - Характеристический размер.
      towerCurrent - Ток стекающий по опоре. */
      /*  double impR = groundResistance;
        double eDischarge = 6.9 * Math.log10(ro)-0.6*Math.pow(Math.log10(ro),2)-3.4;
        double P1 = groundResistance*S/ro;
        double P2 = Math.exp(Math.log(0.256 / P1) / 0.354);
        double sparkingCurrent = P2*Math.pow(S,2)*eDischarge/ro;
        if (towerCurrent >= sparkingCurrent){
            P2 = towerCurrent * ro / (Math.pow(S,2)*eDischarge);
            if (P2>45)
                P1=1.829*Math.pow(P2,-0.871);
            else
                P1=0.256*Math.pow(P2,-0.354);
            impR=P1*ro/S;
        }
        return impR;} */

    private double getCoroneRadius(double pRadius,int pWireN,double pWireHeight,double pVoltage){
        final double METERS_TO_SANTIMETERS=100;
        final double CORONE_RADIUS_ACCURACY =METERS_TO_SANTIMETERS* 1.0E-05;
        double aCoroneRadius[] = {0,pRadius*METERS_TO_SANTIMETERS};
        double eRope;
        do{
            aCoroneRadius[0]=aCoroneRadius[1];
            eRope = 33.9*Math.pow(aCoroneRadius[0],0.268);
            aCoroneRadius[1]=pVoltage/(eRope*pWireN*Math.log(2*pWireHeight*METERS_TO_SANTIMETERS/aCoroneRadius[0]));
        }while (aCoroneRadius[1]-aCoroneRadius[0]>=CORONE_RADIUS_ACCURACY);
        double res = aCoroneRadius[1]/METERS_TO_SANTIMETERS;
        if (res<pRadius)
            res = pRadius;
        return res;
    }

    private static double DiTowerdt(double pAlpha,double pdiRopedt){
        return pAlpha-2*pdiRopedt;
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
        return Math.log(ropeWireD/ropeWired)/Math.log(2*pRopeMiddleHeight/pRopeRadius);
    }

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

    private double TotalSurgeCount(double surgeDensityGND, double ropeRopeDist){
        /* Возвращает число ударов молнии на 100 км длины ВЛ.
           surgeDensityGND - Плотность разрядов молнии на землю.
           ropeRopeDist - Расстояние между тросами, м.
           ropeMiddleHeight - Средняя высота подвеса троса, м.*/
        double N100;
        if (ropeMiddleHeight <= 30)
            N100=0.2 * surgeDensityGND * (ropeRopeDist/2.0 + 5*ropeMiddleHeight - 2.0 * Math.pow(ropeMiddleHeight,2)/30.0);
        else
            N100=0.15 * surgeDensityGND * (ropeRopeDist/2.0 + ropeMiddleHeight + 90);
        return N100;}

    private double FindPowerArcFactor(double pMaxLineVoltage,double pInsulationLength){
        final double MAX_ETA = 0.9;
        double eta = (0.92*pMaxLineVoltage/pInsulationLength-6)*0.01;
        if (eta > MAX_ETA)
            eta = MAX_ETA;
        return eta;  //Коэффициент перехода имп. перекрытия в дугу.
    }
}