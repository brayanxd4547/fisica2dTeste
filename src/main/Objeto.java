package main;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Objeto {
    String nome;
    double x, y;
    double vx, vy;
    double m;
    double raio;
    Color cor;

    public Objeto(String nome, double x, double y, double vx, double vy, double m, double raio, Color cor) {
        this.nome = nome;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.m = m;
        this.raio = raio;
        this.cor = cor;
    }

    void mover(double dt) {
        x += vx * dt;
        y += vy * dt;
    }

    void desenhar(Graphics2D g2, double centroX, double centroY, double offsetX, double offsetY, double zoom) {
        double diametro = 2 * raio * zoom;
        Color nCor = cor;

        /*if (diametro < 5) {
            diametro = 5;
            nCor = nCor.darker();
        }*/

        /*Ellipse2D.Double borda = new Ellipse2D.Double(
                centroX + (x - offsetX) * zoom - raio * zoom - 3,
                centroY + (y - offsetY) * zoom - raio * zoom - 3,
                diametro,
                diametro
        );
        g2.fill(borda);*/

        g2.setColor(nCor);
        Ellipse2D.Double circulo = new Ellipse2D.Double(
                centroX + (x - offsetX) * zoom - raio * zoom,
                centroY + (y - offsetY) * zoom - raio * zoom,
                diametro,
                diametro
        );
        g2.fill(circulo);

        g2.setColor(Color.white);
        g2.drawString(
                nome,
                (float) (centroX + (x - offsetX) * zoom + raio * zoom),
                (float) (centroY + (y - offsetY) * zoom)
        );
    }
}
