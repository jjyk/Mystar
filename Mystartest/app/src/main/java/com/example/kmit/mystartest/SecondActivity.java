package com.example.kmit.mystartest;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class SecondActivity extends ActionBarActivity implements MyDialog.Communicator{

    private TCPClient mTcpClient;
    connectTask connectT;
    int moduleCount;
    Button buttonModule, buttonCow, buttonOn;
    refresh refresh;
    TextView textView1, textView2, textView3, textView4, textView5, textView6;
    Menu menu;
    private boolean waitAnswer;
    boolean refresher;
    Intent intent;
    GraphView graph;
    String pointsGraph;
    Simulation simulation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresher = true;
        setContentView(R.layout.activity_second);

        connectT = new connectTask();
        connectT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        buttonModule = (Button)findViewById(R.id.buttonModule);
        buttonCow = (Button)findViewById(R.id.buttonCow);
        buttonOn = (Button)findViewById(R.id.buttonOn);
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);
        textView6 = (TextView)findViewById(R.id.textView6);
        simulation = new Simulation();


        moduleCount = 1;
        waitAnswer = false;
        intent = new Intent(SecondActivity.this, MainActivity.class);
        //график
        graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setGridColor(Color.parseColor("#170031ff"));
        graph.setBackgroundColor(Color.parseColor("#170031ff"));
        graph.setTitle("Молокоотдача(л/мин) / время(с)");
        graph.getViewport().setScrollable(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(4);
        graph.getViewport().setMaxX(200);
        pointsGraph = "000";

        // таймер обновляет инфу

        if (refresh == null ) {
            refresh = new refresh();
            refresh.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }


        // долгое нажатие на кнопку ДОЕНИЕ


        buttonOn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//
                if (moduleCount == 1)
                mTcpClient.sendMessage();

                return false;
            }
        });




        // Долгое нажатие на кнопку места

        buttonModule.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                FragmentManager manager = getFragmentManager();
                MyDialog myDialog = new MyDialog();
                myDialog.show(manager, "MyDialog");
                return false;
            }
        });

        // долгое нажатие на кнопку коровы

        buttonCow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

    }

    // =================== ======= меню с кнопками ===========================================================================================

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        this.menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement

        switch (id) {
            case R.id.statusModule:
                toastMaker(1);
                return true;
            case R.id.modeModule:
                return true;
            case R.id.statusMilk:
                return true;
            case R.id.action_exit:
                moveTaskToBack(true);
                super.onDestroy();
                System.runFinalizersOnExit(true);
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ========================= всплывающие сообщения TOAST ================================================================================

    void toastMaker (int number) {
        Toast toast = Toast.makeText(getApplicationContext(), "Статус модуля", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(getApplicationContext());
        catImageView.setImageResource(R.drawable.gray);
        toastContainer.addView(catImageView, 0);


        toast.show();

    }

    //========================== действие после всплывающего окна с модуля ==================================================================

    public void onDialogMessage(boolean title, String message) {
        if (title) {
            if (message != "0") {
                int module = Integer.parseInt(message);
                if (module > 0 & module <= 40) {
                    moduleCount = module;
                    getModuleInfo(moduleCount);
                    pointsGraph = "000";
                    graph.removeAllSeries();
                } else
                    Toast.makeText(this, "Выбирайте номер места от 1 до 40!", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(this, "Введите номер коровы!", Toast.LENGTH_LONG).show();
        }
    }

    //======================== кнопка доение, короткое нажатие ===============================================================================

    public void onClickMilking(View view) {
        //String mess = Integer.toString(moduleCount);
        //if (mess.length() == 1)
        //    mTcpClient.sendMessage("10" + mess + "4000");
        //else
        //    mTcpClient.sendMessage("1" + mess + "4000");
    }


    //======================== сборка и отправка пакета запроса информации по модулю =========================================================

    void getModuleInfo(int moduleNumber) {

            String module = Integer.toString(moduleNumber);
        if (module.length()==1)
            dissasemblyPacket(simulation.getPacketInfo("0" + module));
        else
            dissasemblyPacket(simulation.getPacketInfo(module));

    }

    //======================== влево листать ==================================================================================================

    public void onClickLess(View view) {

        if (waitAnswer == false)
            if (moduleCount>1) {
                moduleCount = moduleCount - 1;
                getModuleInfo(moduleCount);
                pointsGraph = "000";
                graph.removeAllSeries();}
    }

    //======================== вправо листать ==================================================================================================

    public void onClickMore(View view) {
        if (waitAnswer == false)
            if (moduleCount<40){
                moduleCount = moduleCount + 1;
                getModuleInfo(moduleCount);
                pointsGraph = "000";
                graph.removeAllSeries();}
    }

    // ================================= короткие клики на кнопки модуля и коровы ===============================================================

    public void onClickModule(View view) {
    }

    public void onClick_buttonCow(View view) {
    }



    public void onClick15(View view) {
        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);

        startActivity(intent);
    }

    //================= поток тсп клиента ======================================================================================================

    public class connectTask extends AsyncTask<String,byte[],TCPClient> {


        @Override
        protected TCPClient doInBackground(String... message) {
            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(byte[] message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });

            mTcpClient.run();

            //mTcpClient = null;

            return null;
        }


        @Override
        protected void onProgressUpdate(byte[]... values) {
            super.onProgressUpdate(values);
            //20 83 B8 ED 00 01 BB 01 01 08 00
            byte[] check = new byte[11];
            check[0] = (byte) 0x20;
            check[1] = (byte) 0x83;
            check[2] = (byte) 0xB8;
            check[3] = (byte) 0xED;
            check[4] = (byte) 0x00;
            check[5] = (byte) 0x01;
            check[6] = (byte) 0xBB;
            check[7] = (byte) 0x01;
            check[8] = (byte) 0x01;
            check[9] = (byte) 0x08;
            check[10] = (byte) 0x00;
            if (Arrays.equals(check, values[0])) {    //сравнение двух массивов
                simulation.setStatus("01");
                getModuleInfo(moduleCount);
                Log.e("TCP", "S: Принял");
            }



        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

    // ===================== информацию в текствиевы доение ===================================================================================

    void setTextMilking(String message) {
        int min;
        int sec;
        String cownum = message.substring(24, 28);
        if (moduleCount == 1)
            buttonCow.setText("Номер коровы 293");                                       // номер коровы
        else
            buttonCow.setText("Номер коровы " + cownum);

        buttonModule.setText("Место " + Integer.toString(moduleCount));                                       // место
        textView1.setText(message.substring(4, 6) + "," + message.substring(6, 7) + " кг");                   // текущий надой
        textView2.setText(message.substring(7, 9) + "," + message.substring(9, 10) + " кг");                  // ожидаемый надой
        min = Integer.parseInt(message.substring(10,14))/60;
        sec = Integer.parseInt(message.substring(10,14)) - 60*min;
        textView3.setText(Integer.toString(min) +":" + Integer.toString(sec) );                               // время доения
        textView5.setText(message.substring(14, 15) + "," + message.substring(15, 17) + " л/мин");            // текущая молокоотдача
        textView6.setText(message.substring(17, 18) + "," + message.substring(18, 20) + " л/мин");            // средняя молокоотдача
        min = Integer.parseInt(message.substring(20,24))/60;
        sec = Integer.parseInt(message.substring(20,24)) - 60*min;
        textView4.setText(Integer.toString(min) +":" + Integer.toString(sec) );                               // ожидаемое время доения
    }

    //================================== информация в текствиевы промывка =====================================================================

    void setTextWashing(String message, boolean washing) {
        int min;
        int sec;
        buttonModule.setText("Место " + Integer.toString(moduleCount));
        if (washing)
            buttonCow.setText("Идет промывка");
        else
            buttonCow.setText("Готов к промывке");
        min = Integer.parseInt(message.substring(10,14))/60;
        sec = Integer.parseInt(message.substring(10,14)) - 60*min;
        textView1.setText(Integer.toString(min) +":" + Integer.toString(sec) );
        textView2.setText("00:25");
        textView3.setText("");
        textView4.setText("");
        textView5.setText("");
        textView6.setText("");
    }

    // ===================== отрисовка графика ================================================================================================

    void graphDrawing(String message) {
        int time = Integer.parseInt(message.substring(10,14));
        // string pointsGraph = "000";
        String drawing = message.substring(31);


        if (drawing.length() > pointsGraph.length()) {
            DataPoint[] dataPoint = new DataPoint[drawing.length() / 3];
            for (int i = 0; i < drawing.length() / 3; i++) {
                double d = Double.parseDouble(drawing.substring(i * 3, i * 3 + 3));
                d = d / 100;
                dataPoint[i] = new DataPoint(i*5, d);
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoint);

            series.setDrawBackground(true);
            series.setColor(Color.BLUE);
            pointsGraph = drawing;
            graph.addSeries(series);
        }
        else
        if (drawing.length() < pointsGraph.length())
        {
            graph.removeAllSeries();
            pointsGraph = drawing;
        }

    }

    //====================== разборка пришедшего пакета =======================================================================================

    void dissasemblyPacket (String message) {

        if(message.length()>31) {
            Log.e("TCP Client", message);
            // цвет 1 индикатора слева, статус модуля
            switch (message.substring(28,29))
            {
                case "1":           // готовность к доению
                    setTextMilking(message);
                    graphDrawing(message);
                    buttonOn.setBackgroundColor(Color.parseColor("#ff27ff22"));
                    buttonOn.setText("Запустить доение");
                    menu.getItem(0).setIcon(R.drawable.yellow);
                    break;
                case "2":           // массаж
                    setTextMilking(message);
                    graphDrawing(message);
                    buttonOn.setBackgroundColor(Color.RED);
                    buttonOn.setText("Остановить доение");
                    menu.getItem(0).setIcon(R.drawable.pink);
                    break;
                case "3":           // доение
                    setTextMilking(message);
                    graphDrawing(message);
                    buttonOn.setBackgroundColor(Color.RED);
                    buttonOn.setText("Остановить доение");
                    menu.getItem(0).setIcon(R.drawable.green);
                    break;
                case "4":           // сьем доильного аппарата
                    setTextMilking(message);
                    graphDrawing(message);
                    buttonOn.setBackgroundColor(Color.LTGRAY);
                    buttonOn.setText("Подготовка к доению");
                    menu.getItem(0).setIcon(R.drawable.red);
                    break;
                default:
                    menu.getItem(0).setIcon(R.drawable.gray);
                    break;
            }
            // цвет 2 индикатора слева, режим модуля
            switch (message.substring(29,30))
            {
                case "1":
                    menu.getItem(1).setIcon(R.drawable.green);
                    break;
                case "2":
                    menu.getItem(1).setIcon(R.drawable.yellow);
                    break;
                default:
                    menu.getItem(1).setIcon(R.drawable.gray);
                    break;
            }
            // цвет 3 индикатора слева, состояние отборщика проб и молока
            switch (message.substring(30,31))
            {
                case "1":
                    //menuStatusMilk.setIcon(R.drawable.green);
                    break;
                case "2":
                    //menuStatusMilk.setIcon(R.drawable.red);
                    break;
                default:
                    //menuStatusMilk.setIcon(R.drawable.gray);
                    break;
            }

        }

        // разбираем на части

    }

    //=================================== таймер в потоке =======================================================================================

    private class refresh extends AsyncTask<Void, Integer, Void> {

        private Timer timer;
        private int anInt;

        refresh()
        {
            timer = new Timer();
            anInt = 1;
        }



        protected Void doInBackground(Void... params) {

            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    anInt = anInt + 1;
                    publishProgress(anInt);
                }
            }, 1000, 1000);   // ожидание перед стартом, длительность тика
            return null;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(anInt);

            simulation.timerSimulation();
            getModuleInfo(moduleCount);

        }

    }

    //===========================================================================================================================================
    //===========================================================================================================================================

}