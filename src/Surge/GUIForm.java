package Surge;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: Maxim Antipov
 * Date: 15.07.13
 * Time: 16:22
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
public class GUIForm implements Observer {
    public static final String ver = "Counter Surge v0.5 alpha";
    private JTextField towerHeightTextField;
    private JTextField kTowerRopeTextField;
    private JTextField kTowerWireTextField;
    private JTextField wireAnkHeightTextField1,wireAnkHeightTextField2,wireAnkHeightTextField3,ropeAnkHeightTextField;
    private JTextField dWireTextField1,dWireTextField2,dWireTextField3,dRopeTextField;
    private JTextField wireDiameterTextField;
    private JTextField ropeDiameterTextField;
    private JTextField gndResistanceTextField;
    private JTextField gndResistivityTextField;
    private JTextField wireMinHeightTextField1,wireMinHeightTextField2,wireMinHeightTextField3,ropeMinHeightTextField;
    private JTextField insHeightTextField;
    private JTextField insArmHeightTextField;
    private JPanel GUIForm;
    private JTextField frequencyTextField;
    private JTextField roTextField;
    private JTextField minSurgeTimeTextField, maxSurgeTimeTextField, timeStepTextField;
    private JTextField spanLengthTextField;
    private JButton StartBtn, ExitBtn, AboutBtn;
    private JLabel resultLabel;
    private JTextArea logTextArea;
    private JTextField wireZCoronaTextField;
    private JCheckBox CoronaCheckBox;
    private JTextField insulationCountTextField;
    private JCheckBox doubleRopeCheckBox;
    private JTextField ropeRopeHorDistTextField;
    private JComboBox <String> voltageComboBox;
    private JLabel doubleRopeLabel;
    private JTextField secondRopeHorDistTextField;
    private JLabel secondRopeHorDistLabel;
    private JLabel ropeRopeUnitsLabel;
    private JLabel secondRopeHorDistUnits;
    private JCheckBox doubleLineCheckBox;
    SurgeCounter surgeCounter;

    public GUIForm() {
        StartBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.

                double SurgeDensityGND, insulationLength, insulationCount=0, insulationArmLength, towerHeight, wireRadius, ropeHorDist, ropeHorDist2=0, ropeAnkHeight, ropeMinHeight, ropeRopeHorDist=0, ropeRadius, spanLength, GNDResistance, GNDResistivity, kTower, kWireTower, wireZCorone;
                double[] wireHorDist = {0, 0, 0}, wireAnkHeight = {0, 0, 0}, wireMinHeight = {0, 0, 0};
                int frequency;

                try {
                    wireHorDist[0] = Float.parseFloat(dWireTextField1.getText());
                    wireHorDist[1] = Float.parseFloat(dWireTextField2.getText());
                    wireHorDist[2] = Float.parseFloat(dWireTextField3.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Расстояние от оси опоры до провода указано некорректно");
                    return;
                }
                try {
                    wireMinHeight[0] = Float.parseFloat(wireMinHeightTextField1.getText());
                    wireMinHeight[1] = Float.parseFloat(wireMinHeightTextField2.getText());
                    wireAnkHeight[2] = Float.parseFloat(wireMinHeightTextField3.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Минимальная высота провода в пролёте указана некорректно");
                    return;
                }
                try {
                    wireAnkHeight[0] = Float.parseFloat(wireAnkHeightTextField1.getText());
                    wireAnkHeight[1] = Float.parseFloat(wireAnkHeightTextField2.getText());
                    wireAnkHeight[2] = Float.parseFloat(wireAnkHeightTextField3.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Высота подвеса провода на опоре(траверсы) указана некорректно");
                    return;
                }
                try {
                    SurgeDensityGND = Float.parseFloat(roTextField.getText())*0.05;
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Число грозовых часов указано не корректно");
                    return;
                }
                try {
                    insulationLength = Float.parseFloat(insHeightTextField.getText())/1e3;
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Строительная высота изолятора указана некорректно");
                    return;
                }
                try {
                    insulationCount = Float.parseFloat(insulationCountTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Количество изолтяторов в гирлянде указано некорректно");
                }
                try {
                    insulationArmLength = Float.parseFloat(insArmHeightTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Высота изолятора с арматурой указана некорректно");
                    return;
                }
                Insulation insulator = new Insulation(insulationLength, insulationCount, insulationArmLength);
                try {
                    towerHeight = Float.parseFloat(towerHeightTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Высота опоры указана некорректно");
                    return;
                }
                try {
                    wireRadius = Float.parseFloat(wireDiameterTextField.getText()) / 2e3;
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Радиус провода указан некорректно");
                    return;
                }
                try {
                    ropeHorDist = Float.parseFloat(dRopeTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Расстояние от оси опоры до троса указано некорректно");
                    return;
                }
                try {
                    ropeAnkHeight = Float.parseFloat(ropeAnkHeightTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Высота подвеса троса указана некорректно");
                    return;
                }
                try {
                    ropeMinHeight = Float.parseFloat(ropeMinHeightTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Минимальная высота троса в пролёте указана некорректно");
                    return;
                }

                try {
                    frequency = Integer.parseInt(frequencyTextField.getText());

                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Частота указана некорректно");
                    return;
                }
                try {
                    ropeRadius = Float.parseFloat(ropeDiameterTextField.getText()) / 2e3;
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Радиус троса указан некорректно");
                    return;
                }
                try {
                    spanLength = Float.parseFloat(spanLengthTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Длинна пролёта указана некорректно");
                    return;
                }
                try {
                    GNDResistance = Float.parseFloat(gndResistanceTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Сопротивление заземления указано некорректно");
                    return;
                }
                try {
                    GNDResistivity = Float.parseFloat(gndResistivityTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Удельное сопротивление грунта указано некорректно");
                    return;
                }
                double hSize = 4.2;
                try {
                    kTower = Float.parseFloat(kTowerRopeTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Коэффициент индуктивности опоры до точки подвеса троса указан некорректно");
                    return;
                }
                try {
                    kWireTower = Float.parseFloat(kTowerWireTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Коэффициент индуктивности опоры до точки подвеса проводов указан некорректно");
                    return;
                }
                try {
                    wireZCorone = Float.parseFloat(wireZCoronaTextField.getText());
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null, "Волновое сопротивление коронирующего провода указано некорректно");
                    return;
                }
                ElectricalNetwork E = new ElectricalNetwork(frequency, Integer.parseInt(voltageComboBox.getSelectedItem().toString()));

                if (doubleRopeCheckBox.isSelected()){
                    try{
                        ropeRopeHorDist = Float.parseFloat(ropeRopeHorDistTextField.getText());
                    } catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(null,"Расстояние между тросами указано некорректно");
                    }
                    try{
                        ropeHorDist2 = Float.parseFloat(secondRopeHorDistTextField.getText());
                    }catch (NumberFormatException e1){
                        JOptionPane.showMessageDialog(null,"Расстояние от стойки опоры до второго троса указано некорректно");
                    }
                }
                logTextArea.append("\nЗапуск расчёта...\n");
                startCalculation(insulator,E, SurgeDensityGND, spanLength, towerHeight, wireRadius, wireHorDist, wireAnkHeight, wireMinHeight, ropeRadius, ropeHorDist, ropeAnkHeight, ropeMinHeight, GNDResistance, GNDResistivity, hSize, kTower, kWireTower, wireZCorone, ropeRopeHorDist, CoronaCheckBox.isSelected(), ropeHorDist2, doubleLineCheckBox.isSelected());
                StartBtn.setEnabled(false);
            }
        });

        ExitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                System.exit(0);
            }
        });
        AboutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                JOptionPane.showMessageDialog(null, ver + " (c) 2013. Максим Антипов, Максим Овчинников");
            }
        });
        doubleRopeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                    doubleRopeLabel.setEnabled(doubleRopeCheckBox.isSelected());
                    ropeRopeHorDistTextField.setEnabled(doubleRopeCheckBox.isSelected());
                    ropeRopeUnitsLabel.setEnabled(doubleRopeCheckBox.isSelected());
                    secondRopeHorDistLabel.setEnabled(doubleRopeCheckBox.isSelected());
                    secondRopeHorDistTextField.setEnabled(doubleRopeCheckBox.isSelected());
                    secondRopeHorDistUnits.setEnabled(doubleRopeCheckBox.isSelected());
            }
        });
    }

    private void startCalculation(Insulation pInsulator, ElectricalNetwork pElNet, double pSurgeDensityGND, double pSpanLength, double pTowerHeight, double pWireRadius, double pWireHorDist[], double pWireInsAnkHeight[], double pWireMinHeight[], double pRopeRadius, double pRopeHorDist, double pRopeAnkHeight, double pRopeMinHeight, double pGNDResistance, double pGNDResistivity, double phSize, double pkRopeTower, double pkWireTower, double pWireZCorone, double pRopeRopeHorDist, boolean pUseCorone, double pRopeHorDist2, boolean pDoubleLine){
        surgeCounter = new SurgeCounter(pInsulator,pElNet, pSurgeDensityGND, pSpanLength, pTowerHeight, pWireRadius, pWireHorDist, pWireInsAnkHeight, pWireMinHeight, pRopeRadius, pRopeHorDist, pRopeAnkHeight, pRopeMinHeight, pGNDResistance, pGNDResistivity, phSize, pkRopeTower, pkWireTower, pWireZCorone,pRopeRopeHorDist, pUseCorone,pRopeHorDist2,pDoubleLine);
        surgeCounter.addObserver(this);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(ver);
        frame.setContentPane(new Surge.GUIForm().GUIForm);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents(){
        voltageComboBox = new JComboBox<String>(ElectricalNetwork.getVoltageClasses());
        voltageComboBox.setSelectedIndex(ElectricalNetwork.getVoltageClassIndex(220));
    }

    @Override
    public void update(Observable o, Object arg) {
        //To change body of implemented methods use File | Settings | File Templates.
        double u2[]=surgeCounter.getResult();
        logTextArea.append("Удельное число отключений от: \n");
        DecimalFormat df = new DecimalFormat("0.000");
        logTextArea.append("1. прорывов молнии: "+df.format(u2[0])+"\n");
        logTextArea.append("2. ударов молнии в опору: "+df.format(u2[1])+"\n");
        logTextArea.append("3. ударов молнии в трос: "+df.format(u2[2])+"\n");
        double res = u2[0]+u2[1]+u2[2];
        logTextArea.append("Результат: " + df.format(res) + "\n");
        resultLabel.setText("Результат: " + String.valueOf(df.format(res)));
        for (int i=0;i<u2.length;i++)
            System.out.print("Unit"+(i+1)+": "+df.format(u2[i])+"\n");
        System.out.println("Summary result: n = " + df.format(res));
        StartBtn.setEnabled(true);
    }
}
