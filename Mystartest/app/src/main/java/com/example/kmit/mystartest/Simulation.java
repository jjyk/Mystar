package com.example.kmit.mystartest;

import java.util.Random;


public class Simulation {

    String[][] modules;
    Random random;

    Simulation()
    {
        // 41 - модули от 1 до 40 включительно
        // 12 - параметры:
        // 0-текущий надой   _ _._ || 1-ожидаемый надой     _ _._   || 2-время доения     _ _ _ _    || 3-тек.молокоотдача    _._ _
        // 4-ср.молокоотдача _._ _ || 5-ср.время доения     _ _ _ _ || 6-номер коровы     _ _ _ _    || 7-сост.модуля         _
        // 8-режим модуля    _     || 9-сост.отборщика проб _       || 10-график(значение _._ _)     || 11 - сумматор графика
        modules = new String[41][12];

        random = new Random();
        for (int i =0;i<41;i++)
            setInfoModule(i);

    }



    // ==================== сборка пакета =====================================
    public String getPacketInfo (String module)
    {
        int mod = Integer.parseInt(module);
        String variable;
        variable = "1" + module + "1";

        // добавление текущего надоя
        variable = variable + modules[mod][0];
        // добавление ожидаемого надоя
        variable = variable + modules[mod][1];
        // добавление времени доения
        variable = variable + modules[mod][2];
        // добавление текущей молокоотдачи
        variable = variable + modules[mod][3];
        // добавление средней молокоотдачи
        variable = variable + modules[mod][4];
        // добавление среднего времени доения
        variable = variable + modules[mod][5];
        // добавление номера коровы
        variable = variable + modules[mod][6];
        // состояние модуля
        variable = variable + modules[mod][7];
        // режим модуля
        variable = variable + modules[mod][8];
        // состояние отборщика проб
        variable = variable + modules[mod][9];
        // добавление координат
        variable = variable + modules[mod][10];
        return variable;
    }
    //===================== изменение статуса ============================
    public void setStatus(String module)
    {
        int mod = Integer.parseInt(module);
        switch (modules[mod][7])
        {
            case "1":
                modules[mod][7]="2";
                break;
            case "2":
                modules[mod][7]="4";
                break;
            case "3":
                modules[mod][7]="4";
                break;
            case "4":
                setZeroTimerValues(module);
                modules[mod][7]="1";
            default:
                break;
        }

    }

    public void timerSimulation()
    {
        switch (modules[1][7])
        {
            // готовность к доению
            case "1":
                break;
            // массаж
            case "2":
                    modules[1][2] = "0000"; //обнуляем таймер
                    modules[1][7] = "3"; // включаем доение

                break;
            // доение
            case "3":
                timerAdd(1);
                getTimerValues("01");
                break;

            // съем
            case "4":
                break;
        }
    }


    //===================== переменные по таймеру =================

    void getTimerValues (String module)
    {
        int mod = Integer.parseInt(module);
        milkYield(module);
        milkFlow (module);

        if (Integer.parseInt(modules[mod][2])%5==0)
        {
            int a = random.nextInt(100) + 150;
            modules[mod][11] = Integer.toString(Integer.parseInt(modules[mod][11]) + Integer.parseInt(modules[mod][3]));
            modules[mod][10] = modules [mod][10] + Integer.toString(Integer.parseInt(modules[mod][11])/5);
            modules[mod][11] = "0";
        }
        else
        {
            int a = random.nextInt(100) + 150;

            modules[mod][11] = Integer.toString(Integer.parseInt(modules[mod][11]) + Integer.parseInt(modules[mod][3]));
        }

    }

    //======================== обнуление переменных (таймер)============

    void setZeroTimerValues(String module)
    {
        int mod = Integer.parseInt(module);
        modules[mod][0] = "000";
        modules[mod][2] = "0000";
        modules[mod][3] = "000";
        modules[mod][10] = "000";
    }

    // ==================== добавление еденицы к таймеру =====================

    void timerAdd(int module)
    {
        String variable;
        variable = Integer.toString(Integer.parseInt(modules[module][2]) + 1); 	// текстовое значение таймера
        if (variable.length()>3)	// разборка количества знаков и дописывание спереди нулей
            modules[module][2] = variable;
        else
        if (variable.length()>2)
            modules[module][2] = "0" + variable;
        else
        if (variable.length()>1)
            modules[module][2] = "00" + variable;
        else
            modules[module][2] = "000" + variable;
    }

    //===================== заполнение 0 ячейки (текущий надой) ================

    void milkYield (String module)
    {

        int mod = Integer.parseInt(module);
        int addition;
        int variable;
        if (Integer.parseInt(modules[mod][2])%3 == 0)
        variable =1;
        else
        variable = 0;
        if (Integer.parseInt(modules[mod][2])%30 == 0)
            variable =2;

        if (modules[mod][0] != null)
            addition = Integer.parseInt(modules[mod][0]) + variable;
        else
            addition = variable;

        String var = Integer.toString(addition);
        if (var.length()>2)
            modules[mod][0] = var;
        else
        if (var.length()>1)
            modules[mod][0] = "0" + var;
        else
            modules[mod][0] = "00" + var;
    }

    //===================== заполнение 3 ячейки (текущая молокоотдача) ================

    void milkFlow (String module)
    {
        int mod = Integer.parseInt(module);
        int variable = random.nextInt(100) + 170;
        String var = Integer.toString(variable);
        if (var.length()>2)
            modules[mod][3] = var;
        else
        if (var.length()>1)
            modules[mod][3] = "0" + var;
        else
            modules[mod][3] = "00" + var;
    }
//=====================================================================================
//===================== добавление первичной инфы в ячейку ============================
//=====================================================================================

    void setInfoModule (int mod)
    {
        String module = Integer.toString(mod);
        if (module.length() == 1)
            module = "0" + module;
        modules[mod][7] = "1";
        modules[mod][8] = "1";
        modules[mod][9] = "0";
        modules[mod][0] = "000";
        modules[mod][2] = "0000";
        modules[mod][3] = "000";
        modules[mod][10] = "000";
        modules[mod][11] = "0";
        expectedMilkYield (module);  // 1 ячейка
        averageMilkFlow (module);    // 4 ячейка
        averageMilkingTime (module); // 5 ячейка
        randomCowNumber (module);    // 6 ячейка
    }


//===================== заполнение 1 ячейки (ожидаемый надой) ================

    void expectedMilkYield (String module)
    {
        int mod = Integer.parseInt(module);

        int variable = random.nextInt(40) + 10;
        String var = Integer.toString(variable);
        if (var.length()>2)
            modules[mod][1] = var;
        else
        if (var.length()>1)
            modules[mod][1] = "0" + var;
        else
            modules[mod][1] = "00" + var;
    }

//===================== заполнение 4 ячейки (средняя молокоотдача) ================

    void averageMilkFlow (String module)
    {
        int mod = Integer.parseInt(module);
        int variable = random.nextInt(100) + 150;
        String var = Integer.toString(variable);
        if (var.length()>2)
            modules[mod][4] = var;
        else
        if (var.length()>1)
            modules[mod][4] = "0" + var;
        else
            modules[mod][4] = "00" + var;
    }

//===================== заполнение 5 ячейки (среднее время доения) ================

    void averageMilkingTime (String module)
    {
        int mod = Integer.parseInt(module);
        int variable = random.nextInt(100) + 50;
        String var = Integer.toString(variable);
        if (var.length()>3)
            modules[mod][5] = var;
        else
        if (var.length()>2)
            modules[mod][5] = "0" + var;
        else
        if (var.length()>1)
            modules[mod][5] = "00" + var;
        else
            modules[mod][5] = "000" + var;
    }

//===================== рандумное заполнение 6 ячейки (номер коровы) ================

    void randomCowNumber (String module)
    {
        int mod = Integer.parseInt(module);
        modules[mod][6] = "10" + module;
        modules[1][6] = "0293";

    }
}
