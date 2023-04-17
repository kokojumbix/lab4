package org.example;

import java.rmi.*;
import java.rmi.registry.*;
import java.io.*;
import java.rmi.server.*;
public class Remote
{
    public interface Brain extends java.rmi.Remote    //Це віддалений інтерфейс
    {
        public void Ping() throws Exception;

        public double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) throws Exception;
        public double[][] addMatrices(double[][] firstMatrix, double[][] secondMatrix) throws Exception;
        public double[][] transposeMatrix(double[][] matrix) throws Exception;
        public double[][] r1solve(double[][] y3, double[][] y1) throws Exception;

        public double[][] y3part(double[][] B2, double[][] C2, double[][] A2) throws Exception;

    }
    public class BrainImpl extends UnicastRemoteObject implements Brain
//Це реалізація віддаленого інтерфейсу
    {
        public BrainImpl() throws Exception
        {
        }
        public double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
            int rowFirstMatrix = firstMatrix.length;
            int columnFirstMatrix = firstMatrix[0].length;
            int rowSecondMatrix = secondMatrix.length;
            int columnSecondMatrix = secondMatrix[0].length;

            if (columnFirstMatrix != rowSecondMatrix) {
                throw new IllegalArgumentException("Розмірність матриць не співпадає.");
            }

            double[][] resultMatrix = new double[rowFirstMatrix][columnSecondMatrix];

            for (int i = 0; i < rowFirstMatrix; i++) {
                for (int j = 0; j < columnSecondMatrix; j++) {
                    for (int k = 0; k < columnFirstMatrix; k++) {
                        resultMatrix[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                    }
                }
            }
            System.out.println("Обрахував множення матриць!");
            return resultMatrix;
        }

        public double[][] addMatrices(double[][] firstMatrix, double[][] secondMatrix){

            if ( firstMatrix.length == 1 && firstMatrix[0].length == 1 ){
                double[][] result = new double[secondMatrix.length][secondMatrix[0].length];
                for (int i = 0; i < secondMatrix.length; i++){
                    for(int j = 0; j < secondMatrix[0].length; j++){
                        result[i][j] = secondMatrix[i][j] + firstMatrix[0][0];
                    }
                }
                return result;
            }
            else{
                double[][] result = new double[secondMatrix.length][secondMatrix[0].length];
                for (int i = 0; i < secondMatrix.length; i++){
                    for(int j = 0; j < secondMatrix[0].length; j++){
                        result[i][j] = secondMatrix[i][j] + firstMatrix[i][j];
                    }
                }
                return result;
            }
        }

        public double[][] transposeMatrix(double[][] matrix) {
            int rows = matrix.length;
            int cols = matrix[0].length;
            double[][] resultMatrix = new double[cols][rows];

            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    resultMatrix[i][j] = matrix[j][i];
                }
            }

            return resultMatrix;
        }
        public void Ping() throws Exception
        {
            System.out.println("Pong! :)");
        }

        public double[][] r1solve(double[][] y3, double[][] y1) throws Exception{
            double[][] r1;
            r1 = matrixoper.multiplyMatrices(y3, matrixoper.multiplyMatrices(y3, y3));
            r1 = matrixoper.multiplyMatrices(r1, y1);
            r1 = matrixoper.addMatrices(r1, y1);
            return r1;
        }

        public double[][] y3part(double[][] B2, double[][] C2, double[][] A2) throws Exception{
            return matrixoper.multiplyMatrices(A2, matrixoper.addMatrices(B2,C2));
        }
    }
    public class Host	//Це сервер
    {
        public Registry reg;
        public Host(String HostName,int port) throws Exception
        {
            Registry reg = LocateRegistry.createRegistry(port);	//Порт задає користувач
            reg.rebind(HostName, new BrainImpl());	//Прив'язую сервер до реєстру RMI
            System.out.println("Host "+HostName+" started successfully");
            System.out.println("Press [Enter] to close the Host");
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            keyboard.readLine();	//Сервер завершить роботу при натиску клавіші Enter
        }
    }
    public class Client	//Це клієнт
    {
        public Brain remotingBrain;	//Це інтерфейс що буде посилатися на віддалений клас
        public Client(String address,String HostName,int port) throws Exception
        {
            remotingBrain = (Brain) Naming.lookup("//"+address+":"+port+"/"+HostName);
//З'єднуюся з реєстром RMI і отримую посилання на віддалений серверний об'єкт
            System.out.println("Connection to "+address+":"+port+"/"+HostName+" is succeed");
        }
    }
    public Remote() throws Exception	//Це реалізація інтерфейсу користувача
    {
        System.out.println("Type [1] to start Host");
        System.out.println("Type [2] to start Client");
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        int c = keyboard.readLine().charAt(0); //Вибір запуску клієнта або сервера
        if (c == '1')	//Якщо обрано запуск сервера
        {
            String address = null;
            String name = null;
            int port;
            System.out.print("\t-host name:\t");
            name = keyboard.readLine();	//Зчитую ім'я сервера
            System.out.print("\t-port:\t\t");
            port = Integer.parseInt(keyboard.readLine());	//І номер порта де він буде розміщений
            System.out.println("\tHost is starting ...");
            Host myHost = new Host(name,port);	//Запускаю сервер
        }
        else if (c == '2')	//Якщо обрано запуск клієнта
        {


            String address = null;
            String name = null;
            int port;
            System.out.print("\t-adress:\t");
            address = keyboard.readLine();	//Зчитую адрес де має бути розташований сервер
            System.out.print("\t-host name:\t");
            name = keyboard.readLine();		//Зчитую його ім'я
            System.out.print("\t-port:\t\t");
            port = Integer.parseInt(keyboard.readLine());	//І його порт
            System.out.println("\tClient is starting...");
            Client myClient = new Client(address,name,port);	//Запускаю клієнт
            myClient.remotingBrain.Ping();	//Викликаю просту віддалену процедуру

            System.out.println("\tInput n: ");
            int n = Integer.parseInt(keyboard.readLine());

            System.out.println("\n\nSolving without RMI for first time:");

            new Equation(n, myClient.remotingBrain, false).run();



            System.out.println("\n\nSolving without RMI:");

            Equation r = new Equation(n, myClient.remotingBrain, false);
            r.run();

            System.out.println(r.x[0][0]);

            System.out.println("\n\nSolving with RMI:");

            Equation rx = new Equation(n, myClient.remotingBrain, true);
            rx.run();

            System.out.println(rx.x[0][0]);

            System.out.println("\n\nSolving with RMI at second time:");

            rx = new Equation(n, myClient.remotingBrain, true);
            rx.run();

            System.out.println(rx.x[0][0]);

        }
    }
    public static void main(String[] args) throws Exception
    {
        new Remote();
    }
}
