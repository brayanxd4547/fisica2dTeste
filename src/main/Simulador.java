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
    private double zoom = 1;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point lastMousePos = null;
    private Objeto focoCamera = null;
    private int numObjFoco = 0;

    // Gravidade
    private static final double G = 1e-7; // Recomendado 1e-7 para visualizar órbita planetária

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
            zoom = Math.max(0.00000001, Math.min(zoom, 10000000)); // limita zoom entre 0.1 e 10
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

        // Teclas de seta para direita e esquerda
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
            }
        });

        // Adicionar objetos
        objetos = new ArrayList<>(Arrays.asList(
                /* SISTEMA SOL TERRA LUA*/
                new Objeto(0, 0, 0, 0, 1.98e30, 696350, Color.yellow),  // Sol
                new Objeto(57.9e6, 0, 0, Math.sqrt(G * 1.98e30 / 57.9e6), 3.3e23, 2440, Color.lightGray),  // Mercúrio
                new Objeto(108.2e6, 0, 0, Math.sqrt(G * 1.98e30 / 108.2e6), 4.87e24, 6052, Color.orange),  // Vênus
                new Objeto(149.6e6, 0, 0, Math.sqrt(G * 1.98e30 / 149.6e6), 5.97e24, 6371, Color.blue),  // Terra
                new Objeto(149.6e6+384e3, 0, 0, Math.sqrt(G * 1.98e30 / 149.6e6)+Math.sqrt(G * 5.97e24 / 384e3), 7.35e22, 1737, Color.white), // Lua
                new Objeto(227.9e6, 0, 0, Math.sqrt(G * 1.98e30 / 227.9e6), 6.42e23, 3390, Color.orange),  // Marte
                new Objeto(227.9e6, 0, 0, Math.sqrt(G * 1.98e30 / 227.9e6), 1.9e27, 69911, Color.orange),  // Júpiter



                /*new Objeto(100, 100, 20, 20, 30, 30, Color.BLUE),
                new Objeto(600, 100, -30, 20, 40, 40, Color.ORANGE),
                new Objeto(100, 600, 20, -30, 25, 25, Color.RED),
                new Objeto(600, 600, -25, -25, 50, 50, Color.GREEN),
                new Objeto(300, 50, 0, 35, 20, 20, Color.MAGENTA),
                new Objeto(300, 700, 0, -40, 35, 35, Color.CYAN),
                new Objeto(50, 300, 35, 0, 28, 28, Color.YELLOW),
                new Objeto(700, 300, -35, 0, 45, 45, Color.PINK),
                new Objeto(200, 400, 12, -15, 22, 22, Color.GRAY),
                new Objeto(400, 200, -15, 12, 30, 30, Color.LIGHT_GRAY)*/
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