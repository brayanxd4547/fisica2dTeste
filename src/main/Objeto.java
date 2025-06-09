package main;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Objeto {
    double x, y;
    double vx, vy;
    double m;
    double raio;
    Color cor;

    public Objeto(double x, double y, double vx, double vy, double m, double raio, Color cor) {
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
        if (diametro < 5) {
            diametro = 5;
        }

        g2.setColor(cor);
        Ellipse2D.Double elipse = new Ellipse2D.Double(
                centroX + (x - offsetX) * zoom - raio * zoom,
                centroY + (y - offsetY) * zoom - raio * zoom,
                diametro,
                diametro
        );
        g2.fill(elipse);
    }
}
