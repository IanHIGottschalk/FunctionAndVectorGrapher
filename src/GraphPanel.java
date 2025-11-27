import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {

    private static final int SCALE = 40; // pixels per unit
    private String functionExpr = "x*x";
    private String vectorDir = "3,2";
    private String vectorPos = "";

    public void setFunction(String expr) {
        this.functionExpr = expr;
    }

    public void setVector(String input) {
        if (input.contains(";")) {
            String[] parts = input.split(";");
            vectorDir = parts[0].trim();
            vectorPos = parts.length > 1 ? parts[1].trim() : "";
        } else {
            vectorDir = input.trim();
            vectorPos = "";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAxes(g);
        drawFunction(g);
        drawVector(g);
    }

    // === Drawing Helpers ===

    private void drawAxes(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth(), h = getHeight();
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, toScreenY(0), w, toScreenY(0)); // X-axis
        g2.drawLine(toScreenX(0), 0, toScreenX(0), h); // Y-axis
    }

    private void drawFunction(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.CYAN);
        g2.setStroke(new BasicStroke(2));

        int w = getWidth();
        for (int px = 0; px < w - 1; px++) {
            double x1 = toWorldX(px);
            double x2 = toWorldX(px + 1);
            double y1 = evaluate(functionExpr, x1);
            double y2 = evaluate(functionExpr, x2);

            g2.drawLine(px, toScreenY(y1), px + 1, toScreenY(y2));
        }
    }

    private void drawVector(Graphics g) {
        String[] dir = vectorDir.split(",");
        double dx = Double.parseDouble(dir[0].trim());
        double dy = Double.parseDouble(dir[1].trim());

        double x0 = 0, y0 = 0;
        if (!vectorPos.isEmpty()) {
            String[] pos = vectorPos.split(",");
            x0 = Double.parseDouble(pos[0].trim());
            y0 = Double.parseDouble(pos[1].trim());
        }

        double x1 = x0 + dx;
        double y1 = y0 + dy;

        drawArrow(g, x0, y0, x1, y1, Color.RED);
    }

    private void drawArrow(Graphics g, double x0, double y0, double x1, double y1, Color color) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));

        int sx = toScreenX(x0);
        int sy = toScreenY(y0);
        int ex = toScreenX(x1);
        int ey = toScreenY(y1);

        g2.drawLine(sx, sy, ex, ey);
        drawArrowHead(g2, sx, sy, ex, ey);
    }

    private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double phi = Math.toRadians(25);
        int barb = 10;
        double dx = x2 - x1, dy = y2 - y1;
        double theta = Math.atan2(dy, dx);

        for (int i = 0; i < 2; i++) {
            double rho = theta + (i == 0 ? phi : -phi);
            int x = (int) (x2 - barb * Math.cos(rho));
            int y = (int) (y2 - barb * Math.sin(rho));
            g2.drawLine(x2, y2, x, y);
        }
    }

    // === Coordinate Translations ===

    private int toScreenX(double x) {
        return getWidth() / 2 + (int) (x * SCALE);
    }

    private int toScreenY(double y) {
        return getHeight() / 2 - (int) (y * SCALE);
    }

    private double toWorldX(int px) {
        return (px - getWidth() / 2) / (double) SCALE;
    }

    private double toWorldY(int py) {
        return (getHeight() / 2 - py) / (double) SCALE;
    }

    // === Expression Evaluation ===

    private double evaluate(String expr, double x) {
        return new ExpressionParser(expr, x).parse();
    }

    // === Math Expression Parser ===

    static class ExpressionParser {
        private final String input;
        private int pos = -1, ch;
        private final double x;

        ExpressionParser(String input, double x) {
            this.input = input;
            this.x = x;
            nextChar();
        }

        void nextChar() {
            ch = (++pos < input.length()) ? input.charAt(pos) : -1;
        }

        boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            return parseExpression();
        }

        double parseExpression() {
            double x = parseTerm();
            while (true) {
                if (eat('+')) x += parseTerm();
                else if (eat('-')) x -= parseTerm();
                else return x;
            }
        }

        double parseTerm() {
            double x = parseFactor();
            while (true) {
                if (eat('*')) x *= parseFactor();
                else if (eat('/')) x /= parseFactor();
                else return x;
            }
        }

        double parseFactor() {
            if (eat('+')) return parseFactor();
            if (eat('-')) return -parseFactor();

            double x;
            int startPos = this.pos;

            if (eat('(')) {
                x = parseExpression();
                eat(')');
            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(input.substring(startPos, this.pos));
            } else if (Character.isLetter(ch)) {
                while (Character.isLetter(ch)) nextChar();
                String func = input.substring(startPos, this.pos);
                if (func.equals("x")) {
                    x = this.x;
                } else {
                    x = parseFactor();
                    switch (func) {
                        case "sin": x = Math.sin(x); break;
                        case "cos": x = Math.cos(x); break;
                        case "tan": x = Math.tan(x); break;
                        case "sqrt": x = Math.sqrt(x); break;
                        case "log": x = Math.log(x); break;
                        default: x = 0;
                    }
                }
            } else {
                x = 0;
            }

            if (eat('^')) x = Math.pow(x, parseFactor());

            return x;
        }
    }
}
