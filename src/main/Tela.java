package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Tela extends JPanel {
    // Configurações
    public static final int LARGURA = 800;
    public static final int ALTURA = 600;
    private final double velSimulacao = 1;

    // Câmera
    private int numObjFoco = 6; // Objeto focado
    private double zoom;
    private double offsetX;
    private double offsetY;
    private Point lastMousePos;

    private long ultimoMomento;

    // Simulador
    private final Simulador simulador;
    private final ArrayList<Objeto> objetos;

    public Tela() {
        // Carregar objetos do simulador
        simulador = new Simulador();
        objetos = simulador.objetos;

        // CONTROLES DA CÂMERA

        // Rodinha do mouse
        addMouseWheelListener(e -> {
            double delta = -e.getPreciseWheelRotation();
            double zoomFactor = 1.3;
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
                    // Converte coordenadas da tela para coordenadas do mundo
                    double mouseX = (e.getX() - getWidth() / 2.0) / zoom + offsetX;
                    double mouseY = (e.getY() - getHeight() / 2.0) / zoom + offsetY;

                    // Procura objeto que foi clicado
                    boolean objEncontrado = false;
                    for (Objeto o : objetos) {
                        double dx = mouseX - o.getX();
                        double dy = mouseY - o.getY();
                        double dist2 = dx * dx + dy * dy;

                        if (dist2 <= (o.getRaio()) * (o.getRaio())) {
                            objEncontrado = true;
                            System.out.println("Clique sobre o objeto: " + o.getNome());
                            numObjFoco = objetos.indexOf(o);
                            break;
                        }
                    }

                    if (!objEncontrado) {
                        numObjFoco = -1;
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
                    System.out.println("Seta para direita pressionada. Foco: " + objetos.get(numObjFoco).getNome());

                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) { // Seta para esquerda
                    numObjFoco--;
                    numObjFoco += objetos.size(); // Impede que numObjFoco seja negativo
                    numObjFoco = numObjFoco % objetos.size();
                    System.out.println("Seta para esquerda pressionada. Foco: " + objetos.get(numObjFoco).getNome());
                }

                if (e.getKeyCode() == KeyEvent.VK_1) { // Seta para esquerda
                    zoom = 1e-8;
                }

                if (e.getKeyCode() == KeyEvent.VK_2) { // Seta para esquerda
                    zoom = 40;
                }
            }
        });

        // Fundo
        setBackground(Color.black);

        // Zoom inicial
        zoom = 20 / objetos.get(numObjFoco).getRaio();

        Timer timer = new Timer(0, _ -> {
            // Calcular delta time
            long momentoAtual = System.nanoTime();
            double dt = (momentoAtual - ultimoMomento) / 1e9;
            System.out.println(dt/velSimulacao);
            dt *= velSimulacao;
            ultimoMomento = momentoAtual;

            // Simular um frame
            simulador.simularFisica(dt);

            // Alterar foco da câmera
            if (numObjFoco != -1) {
                offsetX = objetos.get(numObjFoco).getX();
                offsetY = objetos.get(numObjFoco).getY();
            }

            repaint(); // Atualiza o frame
        });
        timer.start(); // Inicia a animação
        ultimoMomento = System.nanoTime();

        // Ajustar foco do JFrame
        setFocusable(true);
        requestFocusInWindow();
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