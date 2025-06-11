package main;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Objeto {
    private String nome;
    private double x, y;
    private double velX, velY;
    private double massa;
    private double raio;
    private Color cor;

    public Objeto(String nome, double x, double y, double velX, double velY, double massa, double raio, Color cor) {
        this.nome = nome;
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.massa = massa;
        this.raio = raio;
        this.cor = cor;
    }

    public String getNome() {
        return nome;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelX() {
        return velX;
    }

    public double getVelY() {
        return velY;
    }

    public double getMassa() {
        return massa;
    }

    public double getRaio() {
        return raio;
    }

    public Color getCor() {
        return cor;
    }

    public void setVelX(double velX){
        this.velX = velX;
    }

    public void setVelY(double velY){
        this.velY = velY;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    void mover(double dt) {
        x += velX * dt;
        y += velY * dt;
    }

    void desenhar(Graphics2D g2, double centroX, double centroY, double offsetX, double offsetY, double zoom) {
        double diametro = 2 * raio * zoom;
        Color nCor = cor;

        if (diametro < 5) {
            diametro = 5;
            nCor = nCor.darker();
        }

        g2.setColor(nCor);
        Ellipse2D.Double circulo = new Ellipse2D.Double(
                centroX + (x - offsetX) * zoom - raio * zoom,
                centroY + (y - offsetY) * zoom - raio * zoom,
                diametro,
                diametro
        );
        g2.fill(circulo);

        if (raio * zoom > 0.2) {
            g2.setColor(Color.white);
            g2.drawString(
                    nome,
                    (float) (centroX + (x - offsetX) * zoom + raio * zoom + 10),
                    (float) (centroY + (y - offsetY) * zoom)
            );
        }
    }
}
