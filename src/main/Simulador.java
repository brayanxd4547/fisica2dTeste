package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.*;

public class Simulador extends JPanel implements ActionListener {
    // Câmera
    private double zoom = 40;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point lastMousePos = null;
    private Objeto focoCamera = null;
    private int numObjFoco = 0;

    // Gravidade
    private static final double G = 6.6743e-11; // 6.6743e-11

    // Lista de objetos
    ArrayList<Objeto> objetos;

    // Tempo
    Timer timer;
    long ultimoMomento;

    public Simulador() {
        // Controles

        // Rodinha do mouse
        addMouseWheelListener(e -> {
            double delta = -e.getPreciseWheelRotation();
            double zoomFactor = 1.1;
            if (delta > 0) {
                zoom *= zoomFactor;
            } else if (delta < 0) {
                zoom /= zoomFactor;
            }
            zoom = Math.max(1e-15, Math.min(zoom, 1e15)); // limita zoom entre 0.1 e 10
            repaint();
        });

        // Clique do mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // Botão direito
                    // Guarda posição do mouse para arrastar
                    lastMousePos = e.getPoint();
                }

                if (e.getButton() == MouseEvent.BUTTON1) { // Botão esquerdo
                    // Guarda as coordenadas do mouse
                    double centroX = getWidth() / 2.0;
                    double centroY = getHeight() / 2.0;

                    // Converte coordenadas da tela para coordenadas do mundo
                    double mouseX = (e.getX() - centroX) / zoom + offsetX;
                    double mouseY = (e.getY() - centroY) / zoom + offsetY;

                    // Procura objeto que foi clicado
                    boolean objEncontrado = false;
                    for (Objeto o : objetos) {
                        double dx = mouseX - o.x;
                        double dy = mouseY - o.y;
                        double dist2 = dx * dx + dy * dy;

                        if (dist2 <= (o.raio) * (o.raio)) {
                            objEncontrado = true;
                            System.out.println("Clique sobre o objeto: " + o);
                            focoCamera = o;
                            break;
                        }
                    }

                    if (!objEncontrado) {
                        focoCamera = null;
                        System.out.println("Câmera desfocada");
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    lastMousePos = null;
                }
            }
        });

        // Arrastar
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePos != null) {
                    Point current = e.getPoint();
                    offsetX -= (current.x - lastMousePos.x) / zoom;
                    offsetY -= (current.y - lastMousePos.y) / zoom;
                    lastMousePos = current;
                    repaint();
                }
            }
        });

        // Teclas
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) { // Seta para direita
                    numObjFoco++;
                    numObjFoco = numObjFoco % objetos.size(); // Garante que numObjFoco se mantenha na lista
                    focoCamera = objetos.get(numObjFoco);
                    System.out.println("Seta para direita pressionada. Foco: " + focoCamera);

                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) { // Seta para esquerda
                    numObjFoco--;
                    numObjFoco += objetos.size(); // Impede que numObjFoco seja negativo
                    numObjFoco = numObjFoco % objetos.size();
                    focoCamera = objetos.get(numObjFoco);
                    System.out.println("Seta para esquerda pressionada. Foco: " + focoCamera);
                }

                if (e.getKeyCode() == KeyEvent.VK_1) { // Seta para esquerda
                    zoom = 1e-8;
                }

                if (e.getKeyCode() == KeyEvent.VK_2) { // Seta para esquerda
                    zoom = 40;
                }
            }
        });

        // Adicionar objetos
        objetos = new ArrayList<>(Arrays.asList(
                // Sistema Solar
                new Objeto(0, 0, 0, 0, 1.9885e30, 6.9634e8, new Color(255, 255, 255)), // Sol

                new Objeto(5.79e10, 0, 0, Math.sqrt(G * 1.9885e30 / 5.79e10), 3.3011e23, 2.4397e6, new Color(97, 97, 97)), // Mercúrio

                new Objeto(1.082e11, 0, 0, Math.sqrt(G * 1.9885e30 / 1.082e11), 4.8675e24, 6.0518e6, new Color(213, 194, 156)), // Vênus

                new Objeto(1.496e11, 0, 0, Math.sqrt(G * 1.9885e30 / 1.496e11), 5.972e24, 6.371e6, new Color(80, 136, 181)), // Terra
                new Objeto(1.496e11,-6.371e6-2,0,Math.sqrt(G * 1.9885e30 / 1.496e11),0.6,0.3, new Color(255, 128, 0)), // Bola de basquete
                new Objeto(1.496e11,6.371e6+1,0,Math.sqrt(G * 1.9885e30 / 1.496e11),0.3,0.2, new Color(255, 255, 255)), // Bola de futebol
                new Objeto(1.496e11 + 384e6, 0, 0, Math.sqrt(G * 1.9885e30 / 1.496e11) + Math.sqrt(G * 5.972e24 / 384e6), 7.346e22, 1.737e6, new Color(168, 168, 168)), // Lua (Terra)

                new Objeto(2.279e11, 0, 0, Math.sqrt(G * 1.9885e30 / 2.279e11), 6.4171e23, 3.3895e6, new Color(220, 113, 83)), // Marte
                new Objeto(2.279e11 + 9.378e6, 0, 0, Math.sqrt(G * 1.9885e30 / 2.279e11) + Math.sqrt(G * 6.4171e23 / 9.378e6), 1.0659e16, 11.08e3, new Color(193, 187, 177)), // Fobos (Marte)
                new Objeto(2.279e11 + 23.463e6, 0, 0, Math.sqrt(G * 1.9885e30 / 2.279e11) + Math.sqrt(G * 6.4171e23 / 23.463e6), 1.4762e15, 6.2e3, new Color(204, 197, 193)), // Deimos (Marte)

                new Objeto(7.785e11, 0, 0, Math.sqrt(G * 1.9885e30 / 7.785e11), 1.8982e27, 6.9911e7, new Color(205, 179, 155)), // Júpiter
                new Objeto(7.785e11 + 421.8e6, 0, 0, Math.sqrt(G * 1.9885e30 / 7.785e11) + Math.sqrt(G * 1.8982e27 / 421.8e6), 8.9319e22, 1.821e6, new Color(240, 238, 100)), // Io (Júpiter)
                new Objeto(7.785e11 + 670.9e6, 0, 0, Math.sqrt(G * 1.9885e30 / 7.785e11) + Math.sqrt(G * 1.8982e27 / 670.9e6), 4.7998e22, 1.561e6, new Color(239, 229, 215)), // Europa (Júpiter)
                new Objeto(7.785e11 + 1070.4e6, 0, 0, Math.sqrt(G * 1.9885e30 / 7.785e11) + Math.sqrt(G * 1.8982e27 / 1070.4e6), 1.4819e23, 2.631e6, new Color(213, 184, 152)), // Ganimedes (Júpiter)
                new Objeto(7.785e11 + 1882.7e6, 0, 0, Math.sqrt(G * 1.9885e30 / 7.785e11) + Math.sqrt(G * 1.8982e27 / 1882.7e6), 1.076e23, 2.410e6, new Color(181, 144, 108)), // Calisto (Júpiter)

                new Objeto(1.433e12, 0, 0, Math.sqrt(G * 1.9885e30 / 1.433e12), 5.6834e26, 5.8232e7, new Color(251, 232, 198)), // Saturno
                new Objeto(1.433e12 + 1221.87e6, 0, 0, Math.sqrt(G * 1.9885e30 / 1.433e12) + Math.sqrt(G * 5.6834e26 / 1221.87e6), 1.3455e23, 2.575e6, new Color(241, 209, 121)), // Titã (Saturno)
                new Objeto(1.433e12 + 527.1e6, 0, 0, Math.sqrt(G * 1.9885e30 / 1.433e12) + Math.sqrt(G * 5.6834e26 / 527.1e6), 2.3065e21, 763.5e3, new Color(230, 229, 210)), // Reia (Saturno)
                new Objeto(1.433e12 + 237.9e6, 0, 0, Math.sqrt(G * 1.9885e30 / 1.433e12) + Math.sqrt(G * 5.6834e26 / 237.9e6), 1.08e20, 252.1e3, new Color(236, 236, 236)), // Encélado (Saturno)

                new Objeto(2.872e12, 0, 0, Math.sqrt(G * 1.9885e30 / 2.872e12), 8.6810e25, 2.5362e7, new Color(208, 254, 255)), // Urano
                new Objeto(2.872e12 + 1276e6, 0, 0, Math.sqrt(G * 1.9885e30 / 2.872e12) + Math.sqrt(G * 8.6810e25 / 1276e6), 3.529e21, 788.9e3, new Color(220, 209, 191)), // Titânia (Urano)
                new Objeto(2.872e12 + 1170e6, 0, 0, Math.sqrt(G * 1.9885e30 / 2.872e12) + Math.sqrt(G * 8.6810e25 / 1170e6), 3.014e21, 761.4e3, new Color(200, 200, 200)), // Oberon (Urano)

                new Objeto(4.495e12, 0, 0, Math.sqrt(G * 1.9885e30 / 4.495e12), 1.02413e26, 2.4622e7, new Color(148, 222, 244)), // Netuno
                new Objeto(4.495e12 + 354.8e6, 0, 0, Math.sqrt(G * 1.9885e30 / 4.495e12) + Math.sqrt(G * 1.02413e26 / 354.8e6), 2.14e22, 1.352e6, new Color(207, 206, 206)) // Tritão (Netuno)
        ));

        // Inicar temporizador
        timer = new Timer(1000 / Tela.FPS, this);
        timer.start();
        ultimoMomento = System.nanoTime();

        // Fundo preto
        setBackground(Color.BLACK);

        // Ajusta foco do JFrame
        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Calcular delta time
        long momentoAtual = System.nanoTime();
        double dt = (momentoAtual - ultimoMomento) / 1_000_000_000.0;
        ultimoMomento = momentoAtual;

        // Aplicar gravidade e colisão entre pares

        // 1. Calcular forças gravitacionais
        int n = objetos.size();
        for (int i = 0; i < n; i++) {
            Objeto a = objetos.get(i);
            for (int j = i + 1; j < n; j++) {
                Objeto b = objetos.get(j);

                // Calcular distância entre um par
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                double dist2 = dx * dx + dy * dy;

                if (dist2 == 0) continue; // Evitar divisão por zero

                aplicarGravidade(a, b, dx, dy, dist2, dt);
            }
        }

        // 2. Mover objetos após todas as forças aplicadas
        objetos.forEach(o -> o.mover(dt));

        // 3. Detectar e resolver colisões
        for (int i = 0; i < n; i++) {
            Objeto a = objetos.get(i);
            for (int j = i + 1; j < n; j++) {
                Objeto b = objetos.get(j);

                // Calcular distância entre um par
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                double dist2 = dx * dx + dy * dy;
                double somaRaios = a.raio + b.raio;
                if (dist2 <= somaRaios * somaRaios) {
                    colisaoElastica(a, b, dx, dy, dist2);
                }
            }
        }

        // Alterar foco
        if (focoCamera != null) {
            offsetX = focoCamera.x;
            offsetY = focoCamera.y;
        }

        repaint(); // Atualiza o frame
    }

    private void aplicarGravidade(Objeto a, Objeto b, double dx, double dy, double dist2, double dt) {
        double dist = Math.sqrt(dist2);
        double forca = G * a.m * b.m / dist2;
        double ux = dx / dist;
        double uy = dy / dist;

        double ax = -forca * ux / a.m;
        double ay = -forca * uy / a.m;
        double bx = forca * ux / b.m;
        double by = forca * uy / b.m;

        a.vx += ax * dt;
        a.vy += ay * dt;
        b.vx += bx * dt;
        b.vy += by * dt;
    }

    private void colisaoElastica(Objeto a, Objeto b, double dx, double dy, double dist2) {
        double dvx = a.vx - b.vx;
        double dvy = a.vy - b.vy;

        double prodEscalar = dvx * dx + dvy * dy;
        if (prodEscalar >= 0) return;

        double fator = (2 * prodEscalar) / ((a.m + b.m) * dist2);
        double impulsoX = fator * dx;
        double impulsoY = fator * dy;

        a.vx -= impulsoX * b.m;
        a.vy -= impulsoY * b.m;
        b.vx += impulsoX * a.m;
        b.vy += impulsoY * a.m;

        double dist = Math.sqrt(dist2);
        double sobreposicao = (a.raio + b.raio) - dist;
        if (sobreposicao > 0) {
            double nx = dx / dist;
            double ny = dy / dist;

            double massaTotal = a.m + b.m;
            double proporcaoA = b.m / massaTotal;
            double proporcaoB = a.m / massaTotal;

            a.x += nx * sobreposicao * proporcaoA;
            a.y += ny * sobreposicao * proporcaoA;
            b.x -= nx * sobreposicao * proporcaoB;
            b.y -= ny * sobreposicao * proporcaoB;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double centroX = getWidth() / 2.0;
        double centroY = getHeight() / 2.0;

        objetos.forEach(o -> o.desenhar(g2, centroX, centroY, offsetX, offsetY, zoom));
    }
}