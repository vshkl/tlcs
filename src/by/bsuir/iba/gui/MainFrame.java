package by.bsuir.iba.gui;

import by.bsuir.iba.core.configuration.Configuration;
import by.bsuir.iba.core.configuration.ConfigurationLoader;
import by.bsuir.iba.core.enumerations.TrafficSchedules;
import by.bsuir.iba.core.logic.States;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main frame of the TLCS app
 *
 * @author Ruslan Ardytski
 * @author Pavel Vashkel
 */
public class MainFrame extends JFrame {
    final ConfigurationLoader configurationLoader = new ConfigurationLoader();
    protected JFrame frame;
    protected Configuration conf;
    protected CrossroadPanel crossroadPanel = new CrossroadPanel();
    protected JPanel optionPanel;
    protected HashMap<Integer, Coordinates> startPoints = new HashMap<>();
    protected File directory = new File("D:\\");
    protected String _path = "";
    protected ScheduledExecutorService executor;
    protected JButton buttonGetConfigFile;
    protected JButton buttonTransportStart;
    protected JButton buttonTransportStop;
    protected JButton buttonConfigurate;
    protected JButton buttonNextState;
    protected JButton goButton;
    protected JTextField textFieldTime;
    protected JCheckBox checkBoxIsUse;
    protected JComboBox<Integer> comboboxOrder;
    protected JComboBox<TrafficSchedules> comboboxTrafficSchedule;
    Set<int[]> stateTreeSetForConfig;
    Iterator<int[]> statesIterator;
    HashMap<Integer, State> statesHashMap = new HashMap<>();
    private boolean isChecked;
    private int time;
    private int tmpConfigState[];
    private int timeDelay = 4;

    /**
     * Sets configs.
     */
    public void setConfigs() {
        int lineIndex;
        boolean isBrick;

        if (_path.equals("")) {
            configurationLoader.setPath("D:\\Java\\TLCS\\resources\\configurations\\TLCS.properties");
        } else {
            configurationLoader.setPath(_path);
        }
        configurationLoader.load();
        conf = configurationLoader.getConfiguration();

        setPoints();

        for (int i = 1; i <= conf.getRoads(); i++) {
            lineIndex = i * 10;
            if (conf.getLeftTurns()[i - 1] != 0) {
                lineIndex++;
                TrafficLine tmpRightLine = new TrafficLine(lineIndex, startPoints.get(lineIndex).x,
                        startPoints.get(lineIndex).y, true);
                crossroadPanel.addTrafficLine(tmpRightLine);
            } else {
                lineIndex++;
                isBrick = true;
                TrafficLine tmpRightLine = new TrafficLine(lineIndex, startPoints.get(lineIndex).x,
                        startPoints.get(lineIndex).y, isBrick, false);
                crossroadPanel.addTrafficLine(tmpRightLine);
            }

            if (conf.getStraight()[i - 1] != 0) {
                lineIndex++;
                TrafficLine tmpRightLine = new TrafficLine(lineIndex, startPoints.get(lineIndex).x,
                        startPoints.get(lineIndex).y, true);
                crossroadPanel.addTrafficLine(tmpRightLine);
            } else {
                lineIndex++;
                isBrick = true;
                TrafficLine tmpRightLine = new TrafficLine(lineIndex, startPoints.get(lineIndex).x,
                        startPoints.get(lineIndex).y, isBrick, false);
                crossroadPanel.addTrafficLine(tmpRightLine);
            }

            if (conf.getRightTurns()[i - 1] != 0) {
                lineIndex++;
                TrafficLine tmpRightLine = new TrafficLine(lineIndex, startPoints.get(lineIndex).x,
                        startPoints.get(lineIndex).y, true);
                crossroadPanel.addTrafficLine(tmpRightLine);
            } else {
                lineIndex++;
                isBrick = true;
                TrafficLine tmpRightLine = new TrafficLine(lineIndex, startPoints.get(lineIndex).x,
                        startPoints.get(lineIndex).y, isBrick, false);
                crossroadPanel.addTrafficLine(tmpRightLine);
            }

            if (conf.getOutputLines()[i - 1] != 0) {
                for (int outLines = 1; outLines <= 3; outLines++) {
                    lineIndex++;
                    isBrick = (conf.getOutputLines()[i - 1] < outLines);
                    TrafficLine tmpRightLine = new TrafficLine(lineIndex, startPoints.get(lineIndex).x,
                            startPoints.get(lineIndex).y, isBrick, false);
                    crossroadPanel.addTrafficLine(tmpRightLine);
                }
            } else {
                for (int outLines = 1; outLines <= 3; outLines++) {
                    lineIndex++;
                    isBrick = true;
                    TrafficLine tmpRightLine = new TrafficLine(lineIndex, startPoints.get(lineIndex).x,
                            startPoints.get(lineIndex).y, isBrick, false);
                    crossroadPanel.addTrafficLine(tmpRightLine);
                }
            }
        }
    }

    /**
     * Init components.
     */
    public void initComponents() {
        frame = new JFrame("Traffic Light Control System");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        optionPanel = new JPanel();
        optionPanel.setLayout(null);

        // Get config file
        buttonGetConfigFile = new JButton("Get config");
        optionPanel.add(buttonGetConfigFile);
        buttonGetConfigFile.setBounds(50, 50, 150, 25);
        buttonGetConfigFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileOpen = new JFileChooser();
                fileOpen.setCurrentDirectory(directory);
                int result = fileOpen.showOpenDialog(MainFrame.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    directory = fileOpen.getCurrentDirectory();
                    _path = fileOpen.getSelectedFile().getPath();
                    crossroadPanel.trafficLineHashMap.clear();
                    crossroadPanel.removeAll();
                    frame.repaint();
                    setConfigs();
                    frame.repaint();
                }
                if (result == JFileChooser.CANCEL_OPTION) {
                    System.out.println("Canceled");
                }
            }
        });

        // Order combo box
        comboboxOrder = new JComboBox<>();
        optionPanel.add(comboboxOrder);
        comboboxOrder.setBounds(150, 150, 45, 25);
        comboboxOrder.setEnabled(false);
        comboboxOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        // Green time in seconds
        textFieldTime = new JTextField(10);
        optionPanel.add(textFieldTime);
        textFieldTime.setBounds(90, 150, 45, 25);
        textFieldTime.setEnabled(false);

        // Is using check box
        checkBoxIsUse = new JCheckBox();
        optionPanel.add(checkBoxIsUse);
        checkBoxIsUse.setBounds(50, 150, 25, 25);
        checkBoxIsUse.setEnabled(false);
        checkBoxIsUse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBoxIsUse.isSelected()) {
                    textFieldTime.setEnabled(true);
                    comboboxOrder.setEnabled(true);
                    isChecked = true;
                } else {
                    textFieldTime.setEnabled(false);
                    comboboxOrder.setEnabled(false);
                    isChecked = false;
                }

            }
        });

        // Configurate crossroad
        buttonConfigurate = new JButton("Configurate");
        optionPanel.add(buttonConfigurate);
        buttonConfigurate.setBounds(50, 110, 150, 25);
        buttonConfigurate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBoxIsUse.setEnabled(true);
                buttonNextState.setEnabled(true);

                stateTreeSetForConfig = States.getStateTreeSet();
                statesIterator = stateTreeSetForConfig.iterator();
                for (int i = 0; i < stateTreeSetForConfig.size(); i++) {
                    comboboxOrder.addItem(i + 1);
                }

                comboboxOrder.setEnabled(true);
                comboboxOrder.setSelectedIndex(-1);
                if (statesIterator.hasNext()) ;
                tmpConfigState = statesIterator.next();

                crossroadPanel.lightGreenLights(tmpConfigState);


            }
        });

        // Next state
        buttonNextState = new JButton("Next");
        optionPanel.add(buttonNextState);
        buttonNextState.setBounds(50, 185, 150, 25);
        buttonNextState.setEnabled(false);
        buttonNextState.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isChecked) {
                    if (!textFieldTime.getText().equals("")) {
                        time = Integer.parseInt(textFieldTime.getText());
                        State state = new State(tmpConfigState, time);
                        statesHashMap.put((Integer) comboboxOrder.getSelectedItem(), state);
                    }
                    crossroadPanel.lightRedLights(tmpConfigState);
                    if (statesIterator.hasNext()) {
                        tmpConfigState = statesIterator.next();
                    }
                    crossroadPanel.lightGreenLights(tmpConfigState);

                    if (comboboxOrder.getItemCount() == 1) {
                        crossroadPanel.lightRedLights(tmpConfigState);
                        buttonNextState.setEnabled(false);
                        comboboxOrder.removeItemAt(comboboxOrder.getSelectedIndex());
                    } else {
                        comboboxOrder.removeItemAt(comboboxOrder.getSelectedIndex());
                    }
                    comboboxOrder.setSelectedIndex(-1);
                    System.out.println("Число итемов" + comboboxOrder.getItemCount());
                } else {
                    crossroadPanel.lightRedLights(tmpConfigState);
                    if (statesIterator.hasNext()) {
                        tmpConfigState = statesIterator.next();
                    }
                    crossroadPanel.lightGreenLights(tmpConfigState);
                    if (comboboxOrder.getItemCount() == 1) {
                        crossroadPanel.lightRedLights(tmpConfigState);
                        buttonNextState.setEnabled(false);
                        comboboxOrder.removeItemAt(comboboxOrder.getSelectedIndex());
                    } else {
                        comboboxOrder.removeItemAt(comboboxOrder.getItemCount() - 1);
                    }
                }
            }
        });

        goButton = new JButton("Run");
        optionPanel.add(goButton);
        goButton.setBounds(50, 280, 150, 25);
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Собираемя ехать!");
                RunGarland garland = new RunGarland();
                garland.start();
            }
        });

        // Start transport generating
        buttonTransportStart = new JButton("Start transport");
        optionPanel.add(buttonTransportStart);
        buttonTransportStart.setEnabled(true);
        buttonTransportStart.setBounds(50, 455, 150, 25);
        buttonTransportStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateTransport();
            }
        });

        // Stop transport generationg
        buttonTransportStop = new JButton("Stop transport");
        optionPanel.add(buttonTransportStop);
        buttonTransportStop.setEnabled(false);
        buttonTransportStop.setBounds(50, 490, 150, 25);
        buttonTransportStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopTransportGeneration();
            }
        });

        // Choose traffic scheduling
        comboboxTrafficSchedule = new JComboBox<>();
        optionPanel.add(comboboxTrafficSchedule);
        comboboxTrafficSchedule.setBounds(50, 320, 150, 25);
        for (TrafficSchedules trs : TrafficSchedules.values()) {
            comboboxTrafficSchedule.addItem(trs);
        }
        comboboxTrafficSchedule.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (comboboxTrafficSchedule.getItemAt(comboboxTrafficSchedule.getSelectedIndex())) {
                    case BASIC: {
                        timeDelay = 4;
                    }
                    break;
                    case JAM: {
                        timeDelay = 2;
                    }
                    break;
                    case NIGHT: {
                        timeDelay = 6;
                    }
                    break;
                }
            }
        });

        frame.setSize(800, 585);
        frame.setResizable(true);
        frame.getContentPane().setLayout(null);
        frame.getContentPane().add(crossroadPanel);
        crossroadPanel.setBounds(0, 0, 555, 555);
        frame.getContentPane().add(optionPanel);
        optionPanel.setBounds(555, 0, 200, 555);
        frame.setVisible(true);
    }

    private void setPoints() {
        int[] coordinateX = {163, 203, 243, 283, 323, 363, 405, 405, 405, 405, 405, 405, 363, 320, 280, 240, 200,
                160, 120, 120, 120, 120, 120, 120};
        int[] coordinateY = {115, 115, 115, 115, 115, 115, 163, 203, 243, 283, 323, 363, 410, 410, 410, 410, 410,
                410, 363, 320, 280, 240, 200, 160};
        int[] coordinateId = {33, 32, 31, 34, 35, 36, 23, 22, 21, 24, 25, 26, 13, 12, 11, 14, 15, 16, 43, 42, 41, 44,
                45, 46};
        for (int i = 0; i < coordinateX.length; i++) {
            Coordinates tmpCoordinate = new Coordinates(coordinateX[i], coordinateY[i]);
            startPoints.put(coordinateId[i], tmpCoordinate);
        }
    }

    /**
     * Generate transport.
     */
    public void generateTransport() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (int key : crossroadPanel.trafficLineHashMap.keySet()) {
                    TrafficLine tmpLine = crossroadPanel.trafficLineHashMap.get(key);
                    if (tmpLine.hasTrafficLight) {
                        tmpLine.comingCars();
                    }
                }
            }
        }, 4, timeDelay, TimeUnit.SECONDS);
        buttonTransportStart.setEnabled(false);
        buttonTransportStop.setEnabled(true);
        comboboxTrafficSchedule.setEnabled(false);
    }

    /**
     * Stop transport generation.
     */
    public void stopTransportGeneration() {
        executor.shutdown();
        buttonTransportStart.setEnabled(true);
        buttonTransportStop.setEnabled(false);
        comboboxTrafficSchedule.setEnabled(true);
    }

    /**
     * The type Coordinates.
     */
    public class Coordinates {
        int x;
        int y;

        /**
         * Instantiates a new Coordinates.
         *
         * @param _x the _ x
         * @param _y the _ y
         */
        public Coordinates(int _x, int _y) {
            x = _x;
            y = _y;
        }
    }

    /**
     * The type Run garland.
     */
    class RunGarland extends Thread {
        private final Object monitor = new Object();

        @Override
        public void run() {
            boolean fistRun = true;
            synchronized (monitor) {
                int[] nextLights;

                TreeSet<Integer> currentGreen = new TreeSet<>();
                TreeSet<Integer> newGreen = new TreeSet<>();
                TreeSet<Integer> withoutOrange = new TreeSet<>();
                TreeSet<Integer> withOrange = new TreeSet<>();
                TreeSet<Integer> makeGreen = new TreeSet<>();
                TreeSet<Integer> makeRed = new TreeSet<>();

                while (true) {
                    for (int key : statesHashMap.keySet()) {
                        if (fistRun) {
                            MainFrame.State tmpState = statesHashMap.get(key);
                            nextLights = tmpState.greenLights;

                            crossroadPanel.lightYellowLights(nextLights);
                            try {
                                monitor.wait(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            crossroadPanel.lightGreenLights(nextLights);
                            try {
                                monitor.wait(tmpState.greenDelay * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            fistRun = false;
                            for (int nextLight : nextLights) {
                                currentGreen.add(nextLight);
                            }
                        } else {

                            MainFrame.State tmpState = statesHashMap.get(key);
                            nextLights = tmpState.greenLights;

                            for (int nextLight2 : nextLights) {
                                newGreen.add(nextLight2);
                            }

                            for (int nextLight1 : nextLights) {
                                if (currentGreen.contains(nextLight1)) {
                                    withoutOrange.add(nextLight1);
                                }
                            }

                            int[] currentGreenmas = new int[currentGreen.size()];
                            int index = 0;
                            for (Integer i : currentGreen) {
                                currentGreenmas[index++] = i;
                            }

                            for (int currentGreenma : currentGreenmas) {
                                if (!newGreen.contains(currentGreenma)) {
                                    withOrange.add(currentGreenma);
                                } else {
                                    withoutOrange.add(currentGreenma);
                                }
                            }

                            for (int nextLight : nextLights) {
                                if (!currentGreen.contains(nextLight)) {
                                    withOrange.add(nextLight);
                                } else {
                                    withoutOrange.add(nextLight);
                                }
                            }

                            int[] withOrangeMas = new int[withOrange.size()];
                            int index2 = 0;
                            for (Integer i : withOrange) {
                                withOrangeMas[index2++] = i;
                            }
                            crossroadPanel.lightYellowLights(withOrangeMas);
                            try {
                                monitor.wait(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            for (int withOrangeMa : withOrangeMas) {
                                if (newGreen.contains(withOrangeMa)) {
                                    makeGreen.add(withOrangeMa);
                                } else {
                                    makeRed.add(withOrangeMa);
                                }
                            }

                            int[] withoutOrangeMas = new int[withoutOrange.size()];
                            int index3 = 0;
                            for (Integer i : withoutOrange) {
                                withoutOrangeMas[index3++] = i;
                            }

                            for (int withoutOrangeMa : withoutOrangeMas) {
                                makeGreen.add(withoutOrangeMa);
                            }

                            int[] makeRedMas = new int[makeRed.size()];
                            int index4 = 0;
                            for (Integer i : makeRed) {
                                makeRedMas[index4++] = i;
                            }

                            crossroadPanel.lightRedLights(makeRedMas);

                            int[] makeGreenMas = new int[makeGreen.size()];
                            int index5 = 0;
                            for (Integer i : makeGreen) {
                                makeGreenMas[index5++] = i;
                            }


                            crossroadPanel.lightGreenLights(makeGreenMas);

                            currentGreen.clear();
                            withOrange.clear();
                            withoutOrange.clear();
                            newGreen.clear();
                            makeGreen.clear();
                            makeRed.clear();
                            for (int i = 0; i < makeGreenMas.length; i++) {
                                currentGreen.add(nextLights[i]);
                            }

                            try {
                                monitor.wait(tmpState.greenDelay * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                        }


                    }
                }
            }
        }
    }

    private class State {
        int[] greenLights;
        int greenDelay;

        /**
         * Instantiates a new State.
         *
         * @param _greenLights the _ green lights
         * @param _greenDelay  the _ green delay
         */
        public State(int[] _greenLights, int _greenDelay) {
            greenLights = _greenLights;
            greenDelay = _greenDelay;
        }
    }
}
