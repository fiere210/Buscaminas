/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.autonoma.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @authors Francisco Contreras and Alvaro Albornoz
 */
public class GUI extends JFrame{
    private JButton[][] boton;
    private int[][] matrix; 
    private int[][] bordes;
    private int[][] marcador;
    private int[][] flags;
    private Random rnd;
    private int minas;
    private int tamaño;
    private JPanel minasPnl;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JLabel etiquetaTiempoLbl;
    private JLabel etiquetaMinasLbl;
    private JLabel tiempoLbl;
    private JLabel minasLbl;
    private JMenuBar menuMnb;
    private JMenu juegoMnu;
    private JMenu opcionesMnu;
    private JMenuItem nuevojuegoItm;
    private JMenuItem reiniciarjuegoItm;
    private JMenuItem salirItm;
    private JMenuItem mejoresItm;
    private JMenuItem dificultadItm;
    private Timer tiempo;
    private int segundos = 1;
    private int casillas;
    private boolean comenzar = false;
    private int tipoJuego;
    private BufferedWriter bw;
    private BufferedReader br;
    private BufferedReader br2;
    private boolean mejorTiempo = false;
    private JDialog puntuaciones;
    private ImageIcon banderita;
    private ImageIcon mina;
    
            
    public GUI(){
        inicializarComponentes();
    }
    
    private void inicializarComponentes(){
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Buscaminas By Francisco and Alvaro");
        setResizable(false);
        String[] opciones = {"Fácil (9x9)","Medio(16x16)"};
        int opc = JOptionPane.showOptionDialog(this,"Elija el nivel de dificultad", "Buscaminas",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,opciones,opciones[0]);
        if(opc == JOptionPane.YES_OPTION){
            setSize(400,400);
            tamaño = 9;
            minas = 10;
            tipoJuego = 1;
        }
        else if(opc == JOptionPane.NO_OPTION){
            setSize(700,700);
            tamaño = 16;
            minas = 40;
            tipoJuego = 2;
        }
        else{
            System.exit(0);
        }
        tiempo = new Timer(1000,new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tiempoLbl.setText(String.valueOf(segundos));
                    segundos++;
                }
            });
        
        casillas = (tamaño*tamaño) - minas;
        panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1,BoxLayout.Y_AXIS));
        etiquetaTiempoLbl = new JLabel("Tiempo");
        tiempoLbl = new JLabel("0");
        panel1.add(etiquetaTiempoLbl);
        panel1.add(tiempoLbl);
        
        panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2,BoxLayout.Y_AXIS));
        etiquetaMinasLbl = new JLabel("Minas");
        minasLbl = new JLabel("" + minas);
        panel2.add(etiquetaMinasLbl);
        panel2.add(minasLbl);
        
        panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.CENTER,150,10));
        panel3.add(panel1);
        panel3.add(panel2);
        getContentPane().add(panel3,BorderLayout.NORTH); 
        minasPnl = new JPanel();     
        minasPnl.setLayout(new GridLayout(tamaño,tamaño));
        getContentPane().add(minasPnl,BorderLayout.CENTER);
        boton = new JButton[tamaño][tamaño];
        for(int i = 0; i < tamaño; i++){
            for(int j = 0; j < tamaño; j++){
                JButton btn = new JButton();
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e){
                        botonActionPerformed(e);
                    }
                });
                btn.setBackground(Color.cyan);
                btn.setToolTipText("Hazme click!!!");
                minasPnl.add(btn);
                boton[i][j] = btn;
            }
        }
        
        matrix = new int[tamaño][tamaño];
        bordes = new int[tamaño+2][tamaño+2];
        marcador = new int[tamaño][tamaño];
        flags = new int [tamaño][tamaño];
        for(int i = 0;i < tamaño;i++){
           for(int j = 0; j < tamaño; j++){
              
               marcador[i][j] = 0;  
               
           }
           System.out.println("\n");
        }
        matrix = generarMatriz(tamaño);
        
        for(int i = 0;i < tamaño;i++){
           for(int j = 0; j < tamaño; j++){
              
               System.out.printf("\t" + matrix[i][j]);  
               
           }
           System.out.println("\n");
        }
        menuMnb = new JMenuBar();
        juegoMnu = new JMenu();
        juegoMnu.setMnemonic(KeyEvent.VK_J);
        opcionesMnu = new JMenu();
        opcionesMnu.setMnemonic(KeyEvent.VK_O);
        nuevojuegoItm = new JMenuItem();
        nuevojuegoItm.setMnemonic(KeyEvent.VK_N);
        nuevojuegoItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_MASK));
        reiniciarjuegoItm = new JMenuItem();
        reiniciarjuegoItm.setMnemonic(KeyEvent.VK_R);
        reiniciarjuegoItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
        salirItm = new JMenuItem();
        salirItm.setMnemonic(KeyEvent.VK_S);
        salirItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
        mejoresItm = new JMenuItem();
        mejoresItm.setMnemonic(KeyEvent.VK_M);
        mejoresItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_MASK));
        dificultadItm = new JMenuItem();
        dificultadItm.setMnemonic(KeyEvent.VK_C);
        dificultadItm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        juegoMnu.setText("Juego");
        opcionesMnu.setText("Opciones");
        nuevojuegoItm.setText("Juego nuevo");
        nuevojuegoItm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nuevojuegoActionPerformed(e);
            }
        });
        reiniciarjuegoItm.setText("Reiniciar este juego");
        reiniciarjuegoItm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarjuegoActionPerformed(e);
            }
        });
        salirItm.setText("Salir");
        salirItm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salirActionPerformed(e);
            }
        });
        dificultadItm.setText("Cambiar la dificultad");
        dificultadItm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dificultadActionPerformed(e);
            }
        });
        mejoresItm.setText("Mejores puntuaciones");
        mejoresItm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mejoresActionPerformed(e);
            }
        });
        juegoMnu.add(nuevojuegoItm);
        juegoMnu.add(reiniciarjuegoItm);
        juegoMnu.add(salirItm);
        opcionesMnu.add(dificultadItm);
        opcionesMnu.add(mejoresItm);
        menuMnb.add(juegoMnu);
        menuMnb.add(opcionesMnu);
        this.setJMenuBar(menuMnb);
    
    }
    
    public int[][] generarMatriz(int n){
        int matriz[][] = new int[n][n];
        for(int i = 0;i < n ;i++){
           for(int j = 0; j < n ; j++){
              matriz[i][j] = 0;
           }
        } 
        for(int i = 0;i < n+2 ;i++){
           for(int j = 0; j < n+2 ; j++){
              bordes[i][j] = 0;
           }
        } 
        rnd = new Random();
        int aux = minas;
        while(aux > 0){
            int x = rnd.nextInt(n);
            int y = rnd.nextInt(n);
            if(matriz[x][y] != -1){
                matriz[x][y] = -1;
                bordes[x+1][y+1] = -1;
                aux--;
            }
        }
        for (int i = 0;i < tamaño+2; i++){
            bordes[i][0] = 9;
            bordes[i][tamaño+1] = 9;
            bordes[0][i] = 9;
            bordes[tamaño+1][i] = 9;
        }
        
        int temporal,f,g,a,b,c;
        int p = 0;
        int q = 0;

   //Cuatro vectores de los costados
        for(int i = 0; i < 4; i++){
            a = b = c = 0;
            switch(i){
                case 0://rotacion de 0
                        a++;
                        break;
                case 1://rotacion de 270
                        b++;
                        c--;
                        break;
                case 2://rotacion de 180
                        a--; 
                        break;
                case 3://rotacion de 90
                        b--;
                        c++;
                        break;
                default:
                        continue;                                
           }
           for(int j = 1;j <= n - 2; j++ ){                        
                if(!(matriz[c*j + p][a*j + q] == -1)){                   
                   // 3 an angulos de 90
                    f = 0;
                    g = -1;
                    for(int k = 0;k < 3 ; k++){
                        if(matriz[a*f + c*(j+g) + p][b*f + a*(j+g) + q] == -1)
                            matriz[c*j + p][a*j + q]++;
                        temporal = f;
                        f = -g;
                        g = temporal;
                    }
                   // 2 en angulos de 45 grados 
                    f = 1;
                    g = -1;
                    for(int k = 0;k < 2; k++){
                        if(matriz[a*f + c*(j+g) + p][b*f + a*(j+g) + q] == -1)
                            matriz[c*j + p][a*j + q]++;
                        temporal = f;
                        f = -g;
                        g = temporal;
                    }                           
                }               
            }  
           
            if(!(matriz[p][q] == -1)){                   
                   // 2 an angulos de 90
                f = 1;
                g = 0;
                  
                for(int k = 0;k < 2 ; k++){
                    if(matriz[a*f + c*g + p][b*f + a*g + q] == -1)
                        matriz[p][q]++;
                      
                    temporal = f;
                    f = -g;
                    g = temporal;
                }
                   // 1 en angulo de 45 grados                         
                if(matriz[a + c + p][b + a + q] == -1)
                    matriz[p][q]++;                  
            }      
           //Transladamos el vector translacion
           //Rotamos el vector translacion en 270 grados sentido antihorario
           temporal = p;
           p = (n-1) - q;
           q = temporal;
        }
   //matrix interna    
        for(int i = 1;i < n-1 ;i++){
            for(int j = 1; j < n - 1; j++){
                if(!(matriz[i][j] == -1)){
                    f = -1;
                    g = -1;
                    for(int k = 0;k < 4;k++){
                        if(matriz[i+f][j+g] == -1)
                            matriz[i][j]++;
                        temporal = f;
                        f = -g;
                        g = temporal;                          
                    }
                   f = 0;
                   g = -1;
                   for(int l = 0;l < 4;l++){
                        if(matriz[i+f][j+g] == -1)
                            matriz[i][j]++; 
                        temporal = f;
                        f = -g;
                        g = temporal;                       
                   }
                }    
            }
        }
        return matriz;
    }
    
    public void nuevojuegoActionPerformed(ActionEvent e){
        jugar();
    }
    
    public void reiniciarjuegoActionPerformed(ActionEvent e){
        for(int i = 0;i < tamaño;i++){
           for(int j = 0; j < tamaño; j++){
               boton[i][j].setText("");
               boton[i][j].setIcon(null);
               boton[i][j].setEnabled(true); 
               segundos = 1;
               tiempoLbl.setText("0");
               comenzar = false;
               tiempo.stop();
               switch(tipoJuego){
                   case 1: minas = 10;
                           minasLbl.setText("" + minas);
                           break;
                   case 2: minas = 40;
                           minasLbl.setText("" + minas);
                           break;
               }
           }
        }
    }
    
    public void salirActionPerformed(ActionEvent e){
        System.exit(0);
    }
    
    public void dificultadActionPerformed(ActionEvent e){
        this.dispose();
        GUI frame = new GUI();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public void mejoresActionPerformed(ActionEvent e){
        crearDialogo();
    }
    
    public void crearDialogo(){
        puntuaciones = new JDialog(this,true);
        puntuaciones.setTitle("Mejores puntuaciones");
        puntuaciones.setSize(500,200);
        puntuaciones.setLocationRelativeTo(this);
        puntuaciones.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        
        final JTabbedPane tp = new JTabbedPane();
        JPanel facilpnl1 = new JPanel();
        JPanel facilpnl2 = new JPanel();
        JPanel facil = new JPanel();
        JPanel mediopnl1 = new JPanel();
        JPanel mediopnl2 = new JPanel();
        JPanel medio = new JPanel();
        facil.setLayout(new BoxLayout(facil,BoxLayout.Y_AXIS));
        facilpnl1.setLayout(new GridLayout(5,1));
        JLabel[] facilLbls = new JLabel[5] ;
        medio.setLayout(new BoxLayout(medio,BoxLayout.Y_AXIS));
        mediopnl1.setLayout(new GridLayout(5,1));
        JLabel[] medioLbls = new JLabel[5] ;
        JButton cerrarBtn = new JButton("Cerrar");
        JButton reestablecerBtn = new JButton("Restablecer");
        JButton cerrarBtn2 = new JButton("Cerrar");
        JButton reestablecerBtn2 = new JButton("Restablecer");
        
        try {
            br = new BufferedReader(new FileReader(new File("mejoresTiempos/mtFacil.txt")));
            String str = br.readLine();
            String[] info;
            int i;
            for (i = 0 ; i < 5; i++){
                facilLbls[i] = new JLabel("      (" + (i+1) + ")");
                facilpnl1.add(facilLbls[i]);
            }
            i = 0;
            System.out.println(str);
            while (str != null){
                System.out.println(str);
                info = str.split(":");
                facilLbls[i].setText(facilLbls[i].getText() + "    " +  info[1] + " se demoró " + info[0] + " segundos el dia " + info[2]);
                str = br.readLine();
                i++;
            }
            br.close();
            
            br = new BufferedReader(new FileReader(new File("mejoresTiempos/mtMedio.txt")));
            str = br.readLine();
            for (i = 0 ; i < 5; i++){
                medioLbls[i] = new JLabel("      (" + (i+1) + ")");
                mediopnl1.add(medioLbls[i]);
            }
            i = 0;
            System.out.println(str);
            while (str != null){
                System.out.println(str);
                info = str.split(":");
                medioLbls[i].setText(medioLbls[i].getText() + "    " +  info[1] + " se demoró " + info[0] + " segundos el dia " + info[2]);
                str = br.readLine();
                i++;
            }
            br.close();
                
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            cerrarBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    puntuaciones.dispose();
                }
            });
            reestablecerBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    reestablecerActionPerformed(e, tp.getSelectedIndex());
                }
            });
            cerrarBtn2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    puntuaciones.dispose();
                }
            });
            reestablecerBtn2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    reestablecerActionPerformed(e, tp.getSelectedIndex());
                }
            });
        
        
        
            facilpnl2.add(cerrarBtn);
            facilpnl2.add(reestablecerBtn);
            mediopnl2.add(cerrarBtn2);
            mediopnl2.add(reestablecerBtn2);
            facil.add(facilpnl1);
            facil.add(facilpnl2);
            medio.add(mediopnl1);
            medio.add(mediopnl2);
            tp.addTab("Nivel Fácil",facil);
            tp.addTab("Nivel Medio",medio);
            tp.setMnemonicAt(0, KeyEvent.VK_1);
            tp.setMnemonicAt(1, KeyEvent.VK_2);
            
            puntuaciones.getContentPane().add(tp);
            puntuaciones.setVisible(true);
            
   }
    
    public void reestablecerActionPerformed(ActionEvent e, int b){
       switch(b){
           case 0:  try {
                    bw = new BufferedWriter(new FileWriter(new File("mejoresTiempos/mtFacil.txt")));
                    bw.write("");
                    bw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
       
           case 1:  try {
                    bw = new BufferedWriter(new FileWriter(new File("mejoresTiempos/mtMedio.txt")));
                    bw.write("");
                    bw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
        }   
       
        JOptionPane.showMessageDialog(null, "Mejores tiempos restablecidos correctamente");
        puntuaciones.dispose();
        crearDialogo();
    }
    
    public void ponerColor(int x, int y){
        switch(matrix[x][y]){
            case 1: boton[x][y].setForeground(Color.BLUE);
                    break;
            case 2: boton[x][y].setForeground(Color.gray);
                    break;
            case 3: boton[x][y].setForeground(Color.MAGENTA);
                    break;
            case 4: boton[x][y].setForeground(Color.DARK_GRAY);
                    break;
            case 5: boton[x][y].setForeground(Color.pink);
                    break;
            case 6: boton[x][y].setForeground(Color.orange);
                    break;
            case 7: boton[x][y].setForeground(Color.black);
                    break;
            case 8: boton[x][y].setForeground(Color.red);
                    break;
            default: break;
        }
    }
    
    public void botonActionPerformed(MouseEvent e){
        if(comenzar == false){
            tiempo.start();
            comenzar = true;
        }
        JButton asd = (JButton)e.getSource();
        if(MouseEvent.BUTTON1 == e.getButton()){
            for(int i=0; i<tamaño; i++){
                for(int j=0; j<tamaño; j++){
                    if(flags[i][j] != 1){
                        if(matrix[i][j] == -1){
                            if(boton[i][j] == (JButton)e.getSource()){
                                tiempo.stop();
                                mina = new ImageIcon("Images/mina.jpg");
                                boton[i][j].setIcon(mina);
                                JOptionPane.showMessageDialog(this,"BOOM! Has volado en mil pedazos","Game over",JOptionPane.PLAIN_MESSAGE);
                                jugar();
                            }
                        }
                        else{
                            if(matrix[i][j] == 0){
                                if(boton[i][j] == (JButton)e.getSource()){
                                    marcador[i][j] = 1;
                                    asd.setEnabled(false);
                                    recursiva(i,j);
                                    ganar();
                                }
                            }
                            else{
                                if(boton[i][j] == (JButton)e.getSource()){
                                    marcador[i][j] = 1;
                                    asd.setText("" + matrix[i][j]);
                                    ganar();
                                    ponerColor(i,j);
                                }
                            }
                        }  
                    }
                }
            }
            
            
        }
        if(MouseEvent.BUTTON3 == e.getButton()){
            for(int i=0; i<tamaño; i++){
                for(int j=0; j<tamaño; j++){
                    if(boton[i][j] == asd){
                        if(asd.isEnabled()){
                            if(flags[i][j] == 0 && asd.getText().equals("")){
                                minas--;
                                minasLbl.setText("" + minas);
                                banderita = new ImageIcon("Images/bandera.jpg");
                                asd.setIcon(banderita);
                                flags[i][j] = 1;

                            }
                            else if(asd.getIcon() != null){
                                minas++;
                                minasLbl.setText("" + minas);
                                asd.setIcon(null);
                                flags[i][j] = 0;
                                }
                            }
                    }
                    
                }
            }
         }
         
    }
    
    public void recursiva (int x ,int y){
        int f = -1;
        int g = -1;
        for (int k = 0; k < 8; k++) {
            if (k == 4) {
                f = 0;
                g = -1;
            }
            if (bordes[x + 1 + f][y + 1 + g] == 9) {
            } 
            else if ((bordes[x + 1 + f][y + 1 + g] == 0) && (matrix[x + f][y + g] == 0) && (flags[x+f][y+g] != 1)) {
                if (boton[x + f][y + g].isEnabled()) {
                    marcador[x + f][y + g] = 1;
                    boton[x + f][y + g].setEnabled(false);
                    recursiva(x + f, y + g);
                }

            } else if ((bordes[x + 1 + f][y + 1 + g] == 0) && (matrix[x + f][y + g] != -1) && (matrix[x + f][y + g] != 0) && (flags[x+f][y+g] != 1)) {
                //en este caso es un numero distito de cero y solo se revela esta casilla
                marcador[x + f][y + g] = 1;
                boton[x + f][y + g].setText("" + matrix[x + f][y + g]);
                ponerColor(x + f, y + g);

            }
            int temporal = f;
            f = -g;
            g = temporal;
        }

    }
    
    public void jugar(){
        for(int i = 0;i < tamaño;i++){
           for(int j = 0; j < tamaño; j++){
               matrix[i][j] = 0;
               marcador[i][j] = 0;
               flags[i][j] = 0;
               boton[i][j].setText("");
               boton[i][j].setIcon(null);
               boton[i][j].setEnabled(true); 
               segundos = 1;
               tiempoLbl.setText("0");
               comenzar = false;
               tiempo.stop();
               switch(tipoJuego){
                   case 1: minas = 10;
                           minasLbl.setText("" + minas);
                           break;
                   case 2: minas = 40;
                           minasLbl.setText("" + minas);
                           break;
               }
           }
        }
        for(int i = 0;i < tamaño + 2;i++){
           for(int j = 0; j < tamaño + 2; j++){
               bordes[i][j] = 0;
           }
        }
        matrix = generarMatriz(tamaño);
        
        System.out.println("\n");
        for(int i = 0;i < tamaño;i++){
           for(int j = 0; j < tamaño; j++){
              
               System.out.printf("\t" + matrix[i][j]);  
               
           }
           System.out.println("\n");
        }
    }
    
    public void ganar(){
        int count = 0;
        for(int i = 0; i < tamaño; i++){
            for(int j = 0; j < tamaño; j++){
                System.out.print("" + marcador[i][j]);
                if(marcador[i][j] == 1){
                    count++;
                }
            }
            System.out.println("");
        }
        
        if(count == casillas){
            tiempo.stop();
            JOptionPane.showMessageDialog(this,"Felicidades, has ganado!!!","Victoria",JOptionPane.PLAIN_MESSAGE);
            registrarMejorestiempos();
            jugar();
        }
        System.out.println("" + count);
    }
    
    public void registrarMejorestiempos(){
        switch(tipoJuego){
            case 1: try {
                        br = new BufferedReader(new FileReader(new File ("mejoresTiempos/mtFacil.txt")));
                        String str = br.readLine();
                        if(str == null){
                            Object nick = (String) JOptionPane.showInputDialog(this,"Usted ha alcanzado los mejores tiempos! Ingrese su nombre para registrarlo.","Mejor tiempo!",JOptionPane.PLAIN_MESSAGE,null,null,"Jugador");
                            if(nick != null){
                                bw = new BufferedWriter(new FileWriter(new File ("mejoresTiempos/mtFacil.txt")));
                                while(nick.toString().length() < 3 || "".equals(nick)){
                                    JOptionPane.showMessageDialog(this,"Debe ingresar un mínimo de 3 caracteres",null,JOptionPane.ERROR_MESSAGE);
                                    nick = JOptionPane.showInputDialog(this,"Usted ha alcanzado los mejores tiempos! Ingrese su nombre para registrarlo.","Mejor tiempo!",JOptionPane.PLAIN_MESSAGE,null,null,"Jugador");
                                }
                                bw.write(tiempoLbl.getText() + ":" + nick  + ":" + fechaActual());
                                bw.close();
                            }
                        }
                        else{
                            br2 = new BufferedReader(new FileReader(new File ("mejoresTiempos/mtFacil.txt")));
                            String str2 = br2.readLine();
                            int l = 0;
                            while(str2 != null){
                                l++;
                                if(Integer.parseInt(tiempoLbl.getText()) < Integer.parseInt(str2.substring(0,str2.indexOf(":")))){
                                    mejorTiempo = true;
                                }
                                str2 = br2.readLine();
                            }
                            br2.close();
                            if(mejorTiempo == true || l < 5){
                                Object nick = (String) JOptionPane.showInputDialog(this,"Usted ha alcanzado los mejores tiempos! Ingrese su nombre para registrarlo.","Mejor tiempo!",JOptionPane.PLAIN_MESSAGE,null,null,"Jugador");
                                if(nick != null){
                                    while(nick.toString().length() < 3 || "".equals(nick)){
                                        JOptionPane.showMessageDialog(this,"Debe ingresar un mínimo de 3 caracteres",null,JOptionPane.ERROR_MESSAGE);
                                        nick = JOptionPane.showInputDialog(this,"Usted ha alcanzado los mejores tiempos! Ingrese su nombre para registrarlo.","Mejor tiempo!",JOptionPane.PLAIN_MESSAGE,null,null,"Jugador");

                                    }
                                    String[] registros = new String[l+1];
                                    int i = 0;
                                    while(str != null){
                                        registros[i] = str;
                                        str = br.readLine();
                                        i++;

                                    }
                                    for(String s: registros){
                                        System.out.println(s);
                                    }

                                    registros[i] = (tiempoLbl.getText() + ":" + nick + ":" + fechaActual());
                                    registros = burbuja(registros);
                                    bw = new BufferedWriter(new FileWriter(new File ("mejoresTiempos/mtFacil.txt")));
                                    for(int x = 0 ; x < registros.length ; x++){
                                        if(x < 5){
                                            bw.write(registros[x]);
                                            bw.newLine();
                                        }
                                    }
                                    bw.close();
                                }
                            }
                            
                        }
                        br.close();
            
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
            
            case 2: try {
                        br = new BufferedReader(new FileReader(new File ("mejoresTiempos/mtMedio.txt")));
                        String str = br.readLine();
                        if(str == null){
                            Object nick = (String) JOptionPane.showInputDialog(this,"Usted ha alcanzado los mejores tiempos! Ingrese su nombre para registrarlo.","Mejor tiempo!",JOptionPane.PLAIN_MESSAGE,null,null,"Jugador");
                            if(nick != null){
                                bw = new BufferedWriter(new FileWriter(new File ("mejoresTiempos/mtMedio.txt")));
                                while(nick.toString().length() < 3 || "".equals(nick)){
                                    JOptionPane.showMessageDialog(this,"Debe ingresar un mínimo de 3 caracteres",null,JOptionPane.ERROR_MESSAGE);
                                    nick = JOptionPane.showInputDialog(this,"Usted ha alcanzado los mejores tiempos! Ingrese su nombre para registrarlo.","Mejor tiempo!",JOptionPane.PLAIN_MESSAGE,null,null,"Jugador");
                                }
                                bw.write(tiempoLbl.getText() + ":" + nick  + ":" + fechaActual());
                                bw.close();
                            }
                        }
                        else{
                            
                            br2 = new BufferedReader(new FileReader(new File ("mejoresTiempos/mtMedio.txt")));
                            String str2 = br2.readLine();
                            int l = 0;
                            while(str2 != null){
                                l++;
                                if(Integer.parseInt(tiempoLbl.getText()) < Integer.parseInt(str2.substring(0,str2.indexOf(":")))){
                                    mejorTiempo = true;
                                }
                                str2 = br2.readLine();
                            }
                            br2.close();
                            if(mejorTiempo == true || l < 5){
                                System.out.println("Si entra");
                                Object nick = (String) JOptionPane.showInputDialog(this,"Usted ha alcanzado los mejores tiempos! Ingrese su nombre para registrarlo.","Mejor tiempo!",JOptionPane.PLAIN_MESSAGE,null,null,"Jugador");
                                if(nick != null){
                                    while(nick.toString().length() < 3 || "".equals(nick)){
                                        JOptionPane.showMessageDialog(this,"Debe ingresar un mínimo de 3 caracteres",null,JOptionPane.ERROR_MESSAGE);
                                        nick = JOptionPane.showInputDialog(this,"Usted ha alcanzado los mejores tiempos! Ingrese su nombre para registrarlo.","Mejor tiempo!",JOptionPane.PLAIN_MESSAGE,null,null,"Jugador");

                                    }
                                    String[] registros = new String[l+1];
                                    int i = 0;
                                    while(str != null){
                                        registros[i] = str;
                                        str = br.readLine();
                                        i++;

                                    }
                                    for(String s: registros){
                                        System.out.println(s);
                                    }
                                    System.out.println("" + i);
                                    registros[i] = (tiempoLbl.getText() + ":" + nick + ":" + fechaActual());
                                    for(String s: registros){
                                        System.out.println(s);
                                    }
                                    registros = burbuja(registros);
                                    bw = new BufferedWriter(new FileWriter(new File ("mejoresTiempos/mtMedio.txt")));
                                    for(int x = 0 ; x < registros.length ; x++){
                                        if(x < 5){
                                            bw.write(registros[x]);
                                            bw.newLine();
                                        }
                                    }
                                    bw.close();
                                }
                            }
                            
                        }
                        br.close();
            
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
        }
    }
    
    public String[] burbuja(String[] n){
            String temp;
            System.out.println(n.length);
            for (int i = 1; i < n.length; i++) {
                for (int j = 0; j < (n.length-1); j++) {
                    System.out.println(Integer.parseInt(n[j].substring(0,n[j].indexOf(":"))) + ">" + Integer.parseInt(n[j+1].substring(0,n[j+1].indexOf(":"))));
                    if(Integer.parseInt(n[j].substring(0,n[j].indexOf(":"))) > Integer.parseInt(n[j+1].substring(0,n[j+1].indexOf(":")))){
                        temp = n[j];
                        n[j] = n[j+1];
                        n[j+1] = temp;
                    }
                }
            }
            return n; 
    }
    
    public String fechaActual(){
        Calendar cal = Calendar.getInstance();
        String fecha = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.YEAR);
        return fecha;
    }
    
    public static void main(String[] args){
            GUI frame = new GUI();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
    }
}
