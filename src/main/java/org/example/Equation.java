package org.example;


public class Equation extends Thread {
    public
    int n;
    int progress = 0;
    double[][] A;
    double[][] A1;
    double[][] A2;
    double[][] B2;
    double[][] Y3;
    double[][] C2;

    double[][] b;
    double[][] b1;
    double[][] c1;
    double[][] y1;
    double[][] y2;

    boolean useremote;
    double[][] x;

    Remote.Brain remote;

    public Equation(int n, Remote.Brain remote, boolean useremote){
        this.n = n;
        this.remote = remote;
        this.useremote = useremote;
    }

    public void fillC2(){
        for (int i=0; i<this.n; i++){
            for (int j=0; j<this.n; j++){
                this.C2[i][j]=25.0/( Math.pow(i+j+2,3));
            }
        }
    }

    public void fillb(){
        for (int i=0; i<this.n; i++){
            this.b[i][0]=25.0/( Math.pow(i+1,3)*((i+1)%2) + 1*((i+2)%2) );
        }
    }

    public void random(double[][] Matrix) {
        for ( int i=0; i<Matrix.length; i++){
            for (int j=0; j<Matrix[0].length; j++){
                Matrix[i][j] =  Math.random()*40;
            }
        }
    }
    public void init(){
        this.A = new double[n][n];
        this.A1 = new double[n][n];
        this.A2 = new double[n][n];
        this.B2 = new double[n][n];
        this.Y3 = new double[n][n];
        this.C2 = new double[n][n];

        this.b = new double[n][1];
        this.b1 = new double[n][1];
        this.c1 = new double[n][1];
        this.y1 = new double[n][1];
        this.y2 = new double[n][1];

        random(A);
        random(A1);
        random(A2);
        random(B2);
        random(Y3);
        fillC2();


        fillb();
        random(b1);
        random(c1);
        random(y1);
        random(y2);
    }



    public class y1part extends Thread {
        public double[][] A;
        public double[][] b;
        public double[][] Y1;
        public boolean useremote;
        public y1part(double[][] A, double[][] b, boolean userem){
            this.A = A;
            this.b = b;
            this.useremote = userem;
        }

        public void run(){
            if (this.useremote) {
                try {
                    this.Y1 = remote.multiplyMatrices(this.A, this.b);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                this.Y1 = matrixoper.multiplyMatrices(this.A, this.b);
            }
        }
    }

    public class y2part extends Thread {
        public double[][] b1;
        public double[][] c1;
        public double[][] A1;
        public double[][] y2;
        public boolean useremote;
        public y2part(double[][] b1, double[][] c1, double[][] A1, boolean userem){
            this.b1 = b1;
            this.c1 = c1;
            this.A1 = A1;
            this.useremote = userem;
        }
        public void run(){
            if (this.useremote) {
                try {
                    this.y2 = remote.multiplyMatrices(this.A1, remote.addMatrices(this.b1,this.c1));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                this.y2 = matrixoper.multiplyMatrices(this.A1, matrixoper.addMatrices(this.b1,this.c1));
            }

        }
    }

    public class y3part extends Thread {
        public double[][] B2;
        public double[][] C2;
        public double[][] A2;
        public double[][] y3;
        public boolean useremote;
        public y3part(double[][] B2, double[][] C2, double[][] A2, boolean userem){
            this.B2 = C2;
            this.C2 = C2;
            this.A2 = A2;
            this.useremote = userem;
        }
        public void run(){
            if (this.useremote) {
                try {
                    this.y3 = remote.y3part(B2,C2,A2);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {

                this.y3 = matrixoper.multiplyMatrices(this.A2, matrixoper.addMatrices(this.B2, this.C2));

            }
        }
    }

    public class R1solve extends Thread {
        public double[][] y3;
        public double[][] y1;
        public double[][] r1;
        public boolean useremote;
        public R1solve(double[][] y3, double[][] y1, boolean userem){
            this.y3 = y3;
            this.y1 = y1;
            this.useremote = userem;
        }
        public void run(){
            if (this.useremote) {
                try {
                    this.r1 = remote.r1solve(this.y3,this.y1);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                this.r1 = matrixoper.multiplyMatrices(this.y3, matrixoper.multiplyMatrices(this.y3, this.y3));
                this.r1 = matrixoper.multiplyMatrices(this.r1, this.y1);
                this.r1 = matrixoper.addMatrices(this.r1, this.y1);
            }
        }
    }

    public void printusedmemory(){
        System.out.println("Used memory "+Long.toString(( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576) +" Mb" );
    }
    public class R2solve extends Thread {
        public double[][] y3;
        public double[][] y1;
        public double[][] y2;
        public double[][] r2;
        public boolean useremote;
        public R2solve(double[][] y3, double[][] y1, double[][] y2, boolean userem){
            this.y3 = y3;
            this.y1 = y1;
            this.y2 = y2;
            this.useremote = userem;
        }
        public void run(){
            if (this.useremote) {
                try {
                    this.r2 = remote.multiplyMatrices(this.y1, remote.transposeMatrix(this.y2));
                    this.r2 = remote.multiplyMatrices(this.r2, this.y3);
                    this.r2 = remote.multiplyMatrices(this.r2, this.y1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            else {
                this.r2 = matrixoper.multiplyMatrices(this.y1, matrixoper.transposeMatrix(this.y2));
                this.r2 = matrixoper.multiplyMatrices(this.r2, this.y3);
                this.r2 = matrixoper.multiplyMatrices(this.r2, this.y1);
            }
        }
    }

    public class Leq extends Thread {
        public double[][] y3;
        public double[][] y1;
        public double[][] y2;
        public double[][] l;
        public boolean useremote;
        public Leq(double[][] y3, double[][] y1, double[][] y2, boolean userem){
            this.y3 = y3;
            this.y1 = y1;
            this.y2 = y2;
            this.useremote = userem;
        }
        public void run(){
            if (this.useremote) {
                try {
                    this.l = remote.multiplyMatrices(remote.transposeMatrix(y1), y3);
                    this.l = remote.multiplyMatrices(this.l, y2);
                    this.l = remote.addMatrices(this.l, remote.transposeMatrix(y2));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            else {
                this.l = matrixoper.multiplyMatrices(matrixoper.transposeMatrix(y1), y3);
                this.l = matrixoper.multiplyMatrices(this.l, y2);
                this.l = matrixoper.addMatrices(this.l, matrixoper.transposeMatrix(y2));
            }
        }
    }

    public class Req extends Thread {
        public double[][] R1;
        public double[][] R2;
        public double[][] Req;
        public boolean useremote;
        public Req(double[][] R1, double[][] R2, boolean userem){
            this.R1 = R1;
            this.R2 = R2;
            this.useremote = userem;
        }
        public void run(){
            if (this.useremote) {
                try {
                    this.Req = remote.addMatrices(R1, R2);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                this.Req = matrixoper.addMatrices(R1, R2);
            }
        }
    }

    public class x extends Thread {
        public double[][] Req;
        public double[][] Leq;
        public double[][] xeq;
        public boolean useremote;
        public x(double[][] Req, double[][] Leq){
            this.Req = Req;
            this.Leq = Leq;
        }
        public void run(){
            this.xeq = matrixoper.multiplyMatrices(Leq,Req);
        }
    }


    public void run(){
        long starttime = System.currentTimeMillis();
        this.init();


        y1part y1 = new y1part(A,b, false);
        y1.start();

        y2part y2 = new y2part(b1,c1,A1, false);
        y2.start();


        y3part y3 = new y3part(B2,C2,A2, false);
        y3.start();


        try {

            y1.join();

            y2.join();

            y3.join();
            System.out.println(Long.toString((System.currentTimeMillis()-starttime)/1000) + " секунд - час проходження першої групи." );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



        R2solve r2 = new R2solve(y3.y3,y1.Y1,y2.y2,false);
        r2.start();

        Leq l = new Leq(y3.y3,y1.Y1,y2.y2,false);
        l.start();

        R1solve r1 = new R1solve(y3.y3,y1.Y1, this.useremote);
        r1.start();

        try {
            r1.join();

            r2.join();

            l.join();
            System.out.println(Long.toString((System.currentTimeMillis()-starttime)/1000) + " секунд - час проходження другої групи." );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Req req = new Req(r1.r1,r2.r2, false);
        req.start();
        try {
            req.join();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        x xeq = new x(req.Req,l.l);
        xeq.start();
        try {
            xeq.join();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long endtime = System.currentTimeMillis();
        this.x = xeq.xeq;
        System.out.println(Long.toString((endtime-starttime)/1000) + " секунд - час виконання." );

        printusedmemory();

    }
}
