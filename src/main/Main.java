package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Simulador de Física 2D");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Tela.LARGURA, Tela.ALTURA);
        frame.setResizable(true);
        frame.add(new Simulador());
        frame.setVisible(true);
    }
}
