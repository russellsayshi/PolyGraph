import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class Main extends JPanel {
	private static double[][] matrix = new double[300][5];
	private static double[][] gammaVals = new double[1500000][2];
	private static final double Y_MIN=-5,Y_MAX=5,X_MIN=-8,X_MAX=8;

	private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2) {
		g.drawLine((int)((x1-X_MIN)/(X_MAX-X_MIN)*getWidth()), (int)(((-y1)-Y_MIN)/(Y_MAX-Y_MIN)*getHeight()), (int)((x2-X_MIN)/(X_MAX-X_MIN)*getWidth()), (int)(((-y2)-Y_MIN)/(Y_MAX-Y_MIN)*getHeight()));
	}

	private double pgamma(double x) {
		if(x < 0) throw new IllegalArgumentException("pgamma of " + x);
		else if(x == 0) return Double.NaN;
		double delta = 0.00000001;
		double sum = 0;
		for(double t = delta; t < 1000; t += delta) {
			sum += delta * Math.pow(t, x-1) * Math.exp(-t);
			if(t > 100) {
				delta = 1;
			} else if(t > 50) {
				delta = 0.01;
			} else if(t > 10) {
				delta = 0.005;
			} else if(t > 1) {
				delta = 0.00001;
			} else if(t > 0.1) {
				delta = 0.000001;
			}
		}
		return sum;
	}

	private double ngamma(double x) {
		if(x > 0) throw new IllegalArgumentException("ngamma of " + x);
		else if(x == 0) return Double.NaN;
		else if(x <= -1) return 1/x * ngamma(x+1);
		else if(x < 0) return 1/x * pgamma(x+1);
		else throw new RuntimeException("math has broken.");
	}

	private double ogamma2(double x) {
		if(x >= 0) return pgamma(x);
		else return ngamma(x);
	}

	private double gamma(double x) {
		int index = (int)((x + 5.00001) * 100000)-1;
		if(index < 0 || index >= gammaVals.length) {
			return Double.NaN;
		}
		return gammaVals[index][1];
	}

	private double ytop(double y) {
		return ((-y)-Y_MIN)/(Y_MAX-Y_MIN)*getHeight();
	}

	private double xtop(double x) {
		return (x-X_MIN)/(X_MAX-X_MIN)*getWidth();
	}

	private double ptox(double p) {
		return (X_MAX-X_MIN)/getWidth()*p+X_MIN;
	}

	private double ptoy(double p) {
		return -((Y_MAX-Y_MIN)/getHeight()*p+Y_MIN);
	}

	private double fofx(int f, double x) {
		double val = 0;
		for(int i = 0; i < matrix[f].length - 1; i++) {
			val += Math.pow(x - matrix[f][0], i) * matrix[f][i+1];
		}
		return val;
	}

	private static long factorial(long f) {
		long res = 1;
		for(int i = 2; i < f; i++) {
			res *= i;
		}
		return res;
	}

	private double dfdxoffofx(int f, double x, int d) {
		double val = 0;
		for(int i = d; i < matrix[f].length - 1; i++) {
			val += factorial(i)/factorial(i-d) * Math.pow(x - matrix[f][0], i-d) * matrix[f][i+1];
		}
		return val;
	}

	private double dfdxoffofx(int f, double x, double d) {
		double val = 0;
		for(int i = (int)d; i < matrix[f].length - 1; i++) {
			val += gamma(i+1)/gamma(i-d+1) * Math.pow(x - matrix[f][0], i-d) * matrix[f][i+1];
		}
		return val;
	}

	private static double logGamma(double x) {
		double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1) + 24.01409822 / (x + 2) - 1.231739516 / (x + 3) +  0.00120858003 / (x + 4) - 0.00000536382 / (x + 5);
		return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
	}

	private static double ogamma(double x) {
		if(x < 0) System.out.println(x);
		return Math.exp(logGamma(x));
	}

	double deriv = 0;

	@Override
	public void paintComponent(Graphics gOld) {
		Graphics2D g = (Graphics2D)gOld;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(200, 200, 200));
		for(int x = (int)X_MIN; x < X_MAX; x++) {
			drawLine(g, x, Y_MIN, x, Y_MAX);
		}
		for(int y = (int)Y_MIN; y < Y_MAX; y++) {
			drawLine(g, X_MIN, y, X_MAX, y);
		}
		g.setColor(Color.BLACK);
		drawLine(g, X_MIN, 0, X_MAX, 0);
		drawLine(g, 0, Y_MIN, 0, Y_MAX);
		g.setColor(Color.RED);
		for(int x = 0; x < getWidth(); x++) {
		//for(int dx = -100; dx < 100; dx++) {
			for(int f = 0; f < matrix.length; f++) {
				if(f % 10 != 0) continue;
				/*if(Double.isNaN(gamma(ptox(x))) || Double.isInfinite(gamma(ptox(x))) || Double.isInfinite(gamma(ptox(x+1))) || Double.isNaN(gamma(ptox(x+1)))) {
					continue;
				}*/
				//g.drawLine(x, (int)ytop(gamma(ptox(x))), x+1, (int)ytop(gamma(ptox(x+1))));
				//System.out.print("(" + ptox(x) + ", " + gamma(ptox(x)) + "), ");
				//g.drawLine((int)xtop(matrix[f][0]), (int)ytop(dfdxoffofx(f, matrix[f][0]+0.3, deriv)), (int)xtop(matrix[f][0])+1, (int)ytop(dfdxoffofx(f, matrix[f][0]+0.3, deriv)));
				//g.setColor(new Color((f * 130) % 256, (f * 12) % 256, (f * 90) % 256));
				//System.out.println(fofx(0, ptox(x)));
				//g.drawLine(x, (int)ytop(fofx(f, ptox(x))), x+1, (int)ytop(fofx(f, ptox(x+1))));
				g.drawLine(x, (int)ytop(dfdxoffofx(f, ptox(x), deriv)), x+1, (int)ytop(dfdxoffofx(f, ptox(x+1), deriv)));
				//g.drawLine((int)xtop(matrix[f][0]), (int)ytop(-0.1), (int)xtop(matrix[f][0]), (int)ytop(0.1));
			}
		//}
		}
		g.setColor(Color.GREEN);
		g.drawString("deriv: " + deriv, 20, 20);
		deriv += 0.01;
	}

	public static void main(String[] args) throws Exception {
		File gamma = new File("table.txt");
		Scanner gscan = new Scanner(gamma);
		for(int n = 0; n < gammaVals.length; n++) {
			String line = gscan.nextLine();
			String[] parts = line.split(" ");
			gammaVals[n][0] = Double.parseDouble(parts[0]);
			gammaVals[n][1] = Double.parseDouble(parts[1]);
		}
		gscan.close();
		File file = new File("in.txt");
		/*Scanner scan = new Scanner(file);
		for(int n = 0; n < matrix.length; n++) {
			String line = scan.nextLine();
			String[] parts = line.split(" ");
			for(int i = 0; i < matrix[i].length; i++) {
				matrix[n][i] = Double.parseDouble(parts[i]);
			}
		}
		scan.close();*/
		matrix = new double[1][10];
		matrix[0][0] = 0;
		int sign = 1;
		for(int i = 1; i < 10; i++) {
			if(i % 2 == 0) {
				matrix[0][i] = sign * 1/((double)factorial(i));
				sign *= -1;
			}
		}
		JFrame frame = new JFrame("yeet");
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setContentPane(new Main());
		frame.setVisible(true);
	}
}
